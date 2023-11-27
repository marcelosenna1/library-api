package com.sena.libraryapi.service.impl;

import com.sena.libraryapi.model.entity.Book;
import com.sena.libraryapi.model.repository.BookRepository;
import com.sena.libraryapi.service.BookService;
import com.sena.libraryapi.exception.BusinessException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository repository;

    public BookServiceImpl(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Book save(Book book) {
        if (repository.existsByIsbn(book.getIsbn())) {
            throw new BusinessException("Isbn já cadastrado.");
        }
        return repository.save(book);
    }

    @Override
    public Optional<Book> getById(Long id) {
        if(id != null) {
            if (repository.findById(id).isPresent()) {
                return repository.findById(id);
            } else {
                throw new BusinessException("Livro inexistente");
            }
        } else {
            throw new IllegalArgumentException("O Id não pode ser nulo");
        }
    }

    @Override
    public void delete(Book book) {
        Optional<Book> foundBook = getById(validaId(book));
        foundBook.ifPresent(repository::delete);
    }

    @Override
    public Book update(Book book) {
        Optional<Book> foundBook = getById(validaId(book));
        foundBook.ifPresent(repository::save);
        return book;
    }

    private static Long validaId(Book book) {
        if (book == null || book.getId() == null) {
            throw new IllegalArgumentException("O Id não pode ser nulo");
        } else {
            return book.getId();
        }
    }
}
