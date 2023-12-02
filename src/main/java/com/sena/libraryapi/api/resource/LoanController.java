package com.sena.libraryapi.api.resource;

import com.sena.libraryapi.api.dto.LoanDTO;
import com.sena.libraryapi.api.dto.ReturnedLoanDTO;
import com.sena.libraryapi.model.entity.Book;
import com.sena.libraryapi.model.entity.Loan;
import com.sena.libraryapi.service.BookService;
import com.sena.libraryapi.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService service;
    private final BookService bookService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody LoanDTO dto) {
        Book book = bookService
                .getBookByIsbn(dto.getIsbn())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found for passed isbn"));
        Loan entity = Loan.builder()
                .book(book)
                .costumer(dto.getCustomer())
                .loanDate(LocalDate.now())
                .build();
        entity = service.save(entity);
        return entity.getId();
    }

    @PatchMapping("{id}")
    public void returnBook(@PathVariable Long id, @RequestBody ReturnedLoanDTO dto) {
        Loan loan = service.getById(id).orElseThrow( ()-> new ResponseStatusException(HttpStatus.NOT_FOUND));
        loan.setReturned(dto.getReturned());
        service.update(loan);
    }

}
