package com.jonathanfrosto.tdd.services;

import com.jonathanfrosto.tdd.domain.dto.BookDTO;

public interface BookService {

    BookDTO save(BookDTO bookDTO);
}
