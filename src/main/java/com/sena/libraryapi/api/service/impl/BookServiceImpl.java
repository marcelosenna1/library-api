package com.sena.libraryapi.api.service.impl;

import com.sena.libraryapi.api.model.Book;
import com.sena.libraryapi.api.model.repository.BookRepository;
import com.sena.libraryapi.api.service.BookService;
import com.sena.libraryapi.exception.BusinessException;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {

    private BookRepository repository;

    public BookServiceImpl(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Book save(Book book) {
        if(repository.existsByIsbn(book.getIsbn())){
            throw new BusinessException("Isbn já cadastrado.");
        }
        return repository.save(book);
    }
}
