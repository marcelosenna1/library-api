package com.sena.libraryapi.service.impl;

import com.sena.libraryapi.exception.BusinessException;
import com.sena.libraryapi.model.entity.Loan;
import com.sena.libraryapi.model.repository.LoanRepository;
import com.sena.libraryapi.service.LoanService;

public class LoanServiceImpl implements LoanService {

    private final LoanRepository repository;

    public LoanServiceImpl(LoanRepository repository) {
        this.repository = repository;
    }

    @Override
    public Loan save(Loan loan) {
        if(repository.existsByBookAndNotReturned(loan.getBook())){
            throw new BusinessException("Book already loaned");
        }
        return repository.save(loan);
    }
}
