package com.jonathanfrosto.tdd.repositories;

import com.jonathanfrosto.tdd.domain.entities.Book;
import com.jonathanfrosto.tdd.domain.entities.Loan;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
class LoanRepositoryTest {

    @Autowired
    TestEntityManager testEntityManager;

    @Autowired
    LoanRepository loanRepository;

    @Test
    @DisplayName("Loan not returned test")
    void testQueryMethod() {

        Book book = Book.builder()
                .name("A alcateia")
                .isbn("123")
                .author("Jonathan")
                .build();
        testEntityManager.persist(book);

        Loan loan = Loan.builder().book(book).returned(false).build();
        testEntityManager.persist(loan);

        boolean exists = loanRepository.existsByBookAndReturnedFalse(book);

        Assertions.assertThat(exists).isTrue();
    }
}
