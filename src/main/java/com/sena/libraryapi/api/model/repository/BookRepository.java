package com.sena.libraryapi.api.model.repository;

import com.sena.libraryapi.api.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
