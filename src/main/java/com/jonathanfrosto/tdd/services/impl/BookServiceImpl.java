package com.jonathanfrosto.tdd.services.impl;

import com.jonathanfrosto.tdd.domain.dto.BookDTO;
import com.jonathanfrosto.tdd.domain.entities.Book;
import com.jonathanfrosto.tdd.exceptions.BusinessException;
import com.jonathanfrosto.tdd.repositories.BookRepository;
import com.jonathanfrosto.tdd.services.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class BookServiceImpl implements BookService {

    BookRepository bookRepository;
    ModelMapper modelMapper;

    public BookServiceImpl(BookRepository bookRepository, ModelMapper modelMapper) {
        this.bookRepository = bookRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public BookDTO save(BookDTO bookDTO) {
        if (bookRepository.existsByIsbn(bookDTO.getIsbn())) {
            throw new BusinessException("Duplicated isbn", 409);
        }

        Book entity = modelMapper.map(bookDTO, Book.class);
        return modelMapper.map(bookRepository.save(entity), BookDTO.class);
    }

    @Override
    public BookDTO getById(Long id) {
        return bookRepository.findById(id)
                .map(entity -> modelMapper.map(entity, BookDTO.class))
                .orElseThrow(() -> new BusinessException("Book not found", 404));
    }
}
