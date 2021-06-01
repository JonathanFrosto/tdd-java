package com.jonathanfrosto.tdd.controllers;

import com.jonathanfrosto.tdd.domain.dto.LoanDTO;
import com.jonathanfrosto.tdd.services.LoanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping
    public ResponseEntity<LoanDTO> saveLoan(@RequestBody @Valid LoanDTO body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(loanService.save(body));
    }

    @PatchMapping("/{id}")
    public void returnBookFromLoan(@PathVariable Long id) {
        loanService.giveBackBook(id);
    }
}
