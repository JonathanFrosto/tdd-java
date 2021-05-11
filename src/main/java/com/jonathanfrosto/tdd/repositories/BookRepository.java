package com.jonathanfrosto.tdd.repositories;

import com.jonathanfrosto.tdd.domain.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
    boolean existsByIsbn(String isbn);
}
