package com.jonathanfrosto.tdd.controllers;

import com.jonathanfrosto.tdd.domain.dto.BookDTO;
import com.jonathanfrosto.tdd.domain.dto.LoanDTO;
import com.jonathanfrosto.tdd.services.BookService;
import com.jonathanfrosto.tdd.services.LoanService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("book")
public class BookController {

    BookService bookService;
    private LoanService loanService;

    public BookController(BookService bookService, LoanService loanService) {
        this.bookService = bookService;
        this.loanService = loanService;
    }

    @PostMapping
    public ResponseEntity<BookDTO> createBook(@RequestBody @Valid BookDTO bookDTO) {
        return ResponseEntity.ok(bookService.save(bookDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> findBookById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(bookService.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        bookService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDTO> update(@PathVariable("id") Long id,
                                          @RequestBody BookDTO bookDTO) {
        return ResponseEntity.ok(bookService.update(bookDTO));
    }

    @GetMapping
    public ResponseEntity<Page<BookDTO>> find(BookDTO bookDTO, Pageable pageRequest) {
        return ResponseEntity.ok(bookService.find(bookDTO, pageRequest));
    }

    @GetMapping("/{id}/loans")
    public ResponseEntity<Page<LoanDTO>> findLoansByBook(@PathVariable("id") Long id,
                                                         Pageable pagaRequest) {
        return ResponseEntity.ok(loanService.findByBook(id, pagaRequest));
    }
}
