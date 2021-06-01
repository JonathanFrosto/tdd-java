package com.jonathanfrosto.tdd.services.impl;

import com.jonathanfrosto.tdd.domain.dto.LoanDTO;
import com.jonathanfrosto.tdd.domain.dto.LoanFilterDTO;
import com.jonathanfrosto.tdd.domain.entities.Loan;
import com.jonathanfrosto.tdd.exceptions.BusinessException;
import com.jonathanfrosto.tdd.repositories.BookRepository;
import com.jonathanfrosto.tdd.repositories.LoanRepository;
import com.jonathanfrosto.tdd.services.LoanService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public class LoanServiceImpl implements LoanService {

    private final ModelMapper modelMapper;
    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;

    public LoanServiceImpl(ModelMapper modelMapper,
                           LoanRepository loanRepository,
                           BookRepository bookRepository) {
        this.modelMapper = modelMapper;
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public LoanDTO save(LoanDTO loan) {
        var book = bookRepository
                .findByIsbn(loan.getIsbn())
                .orElseThrow(() -> new BusinessException("Book not found", 404));

        if (loanRepository.existsByBookAndReturnedFalse(book)) {
            throw new BusinessException("Book already loaned", 409);
        }

        Loan entity = Loan.builder()
                .customer(loan.getCustomer())
                .book(book)
                .loanDate(LocalDate.now())
                .returned(false)
                .build();

        return modelMapper.map(loanRepository.save(entity), LoanDTO.class);
    }

    @Override
    public void giveBackBook(Long id) {
        Loan entity = loanRepository
                .findById(id)
                .orElseThrow(() -> new BusinessException("Loan not found", 404));

        entity.setReturned(true);
        loanRepository.save(entity);
    }

    @Override
    public Page<LoanDTO> find(LoanFilterDTO loanFilterDTO, Pageable pageable) {
        Loan entityExample = modelMapper.map(loanFilterDTO, Loan.class);

        Page<Loan> pageEntities = loanRepository.findAll(Example.of(entityExample), pageable);

        List<LoanDTO> dtos = modelMapper.map(pageEntities.getContent(), new TypeToken<List<LoanDTO>>() {
        }.getType());

        return new PageImpl<>(dtos, pageable, pageEntities.getTotalElements());
    }
}
