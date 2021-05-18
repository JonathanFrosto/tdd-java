package com.jonathanfrosto.tdd.services;

import com.jonathanfrosto.tdd.domain.dto.BookDTO;

import java.util.Optional;

public interface BookService {

    BookDTO save(BookDTO bookDTO);

    BookDTO getById(Long id);
}
