package com.jonathanfrosto.tdd.services.impl;

import com.jonathanfrosto.tdd.domain.dto.BookDTO;
import com.jonathanfrosto.tdd.domain.entities.Book;
import com.jonathanfrosto.tdd.exceptions.BusinessException;
import com.jonathanfrosto.tdd.repositories.BookRepository;
import com.jonathanfrosto.tdd.services.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Service
public class BookServiceImpl implements BookService {

    public static final String BOOK_NOT_FOUND = "Book not found";
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
                .orElseThrow(() -> new BusinessException(BOOK_NOT_FOUND, 404));
    }

    @Override
    public void delete(Long id) {
        if ( id == null ) {
            throw new IllegalArgumentException("Book id can't be null");
        }

        Optional<Book> entity = bookRepository.findById(id);

        if (entity.isEmpty()) {
            throw new BusinessException(BOOK_NOT_FOUND, 404);
        }

        bookRepository.delete(entity.get());
    }

    @Override
    public BookDTO update(BookDTO bookDTO) {
        if ( bookDTO == null || bookDTO.getId() == null) {
            throw new IllegalArgumentException("Book id can't be null");
        }

        Book entity = bookRepository
                .findById(bookDTO.getId())
                .orElseThrow(() -> new BusinessException(BOOK_NOT_FOUND, 404));

        entity.setAuthor(bookDTO.getAuthor());
        entity.setName(bookDTO.getName());
        entity.setIsbn(bookDTO.getIsbn());

        bookRepository.save(entity);

        return bookDTO;
    }
}
