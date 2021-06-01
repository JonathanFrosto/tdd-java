package com.jonathanfrosto.tdd.repositories;

import com.jonathanfrosto.tdd.domain.entities.Book;
import com.jonathanfrosto.tdd.domain.entities.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    boolean existsByBookAndReturnedFalse(Book book);
}
