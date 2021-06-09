package com.jonathanfrosto.tdd.controllers;

import com.jonathanfrosto.tdd.domain.dto.LoanDTO;
import com.jonathanfrosto.tdd.domain.dto.LoanFilterDTO;
import com.jonathanfrosto.tdd.services.LoanService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("loans")
public class LoanController {

    LoanService loanService;

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

    @GetMapping
    public ResponseEntity<Page<LoanDTO>> find(LoanFilterDTO filterDTO, Pageable pageable) {
        return ResponseEntity.ok(loanService.find(filterDTO, pageable));
    }
}
