package com.sena.libraryapi.model.repository;

import com.sena.libraryapi.model.entity.Book;
import com.sena.libraryapi.model.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    boolean existsByBookAndNotReturned(Book book);
}
