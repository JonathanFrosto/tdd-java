package com.jonathanfrosto.tdd.services;

import com.jonathanfrosto.tdd.domain.dto.BookDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {

    BookDTO save(BookDTO bookDTO);

    BookDTO getById(Long id);

    void delete(Long id);

    BookDTO update(BookDTO toUpdate);

    Page<BookDTO> find(BookDTO bookDTO, Pageable pageable);

    BookDTO findByIsbn(String isbn);
}
