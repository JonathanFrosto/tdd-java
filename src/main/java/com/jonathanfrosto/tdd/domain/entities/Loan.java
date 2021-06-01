package com.jonathanfrosto.tdd.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String customer;

    @ManyToOne
    @JoinColumn(name = "id_book")
    private Book book;

    @Column
    private LocalDate loanDate;

    @Column
    private boolean returned;
}
