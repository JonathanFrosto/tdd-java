package com.jonathanfrosto.tdd.repositories;

import com.jonathanfrosto.tdd.domain.entities.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
class BookRepositoryTest {

    @Autowired
    TestEntityManager testEntityManager;

    @Autowired
    BookRepository bookRepository;

    @Test
    @DisplayName("Success when isbn exists")
    void returnTrueWhenIsbnExists() {
        // Given
        String isbn = "123";

        Book book = Book.builder()
                .author("Jonathan Anthony")
                .name("A alcateia")
                .isbn(isbn)
                .build();

        testEntityManager.persist(book);

        // When
        boolean exists = bookRepository.existsByIsbn(isbn);

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Success when isbn doesn't exists")
    void returnFalseWhenIsbnExists() {
        // Given
        String isbn = "123";

        // When
        boolean exists = bookRepository.existsByIsbn(isbn);

        // Then
        assertThat(exists).isFalse();
    }
}
