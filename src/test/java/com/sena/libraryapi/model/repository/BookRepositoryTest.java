package com.sena.libraryapi.model.repository;

import com.sena.libraryapi.model.entity.Book;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository repository;

    @Test
    @DisplayName("Deve retornar verdadeiro quando existir um livro na base com o isbn informado")
    void returnTrueWhenIsbnExists(){

        String isbn = "123";
        Book book = Book.builder().title("Code Sena").author("Marcelo Sena").isbn(isbn).build();
        entityManager.persist(book);

        boolean exists = repository.existsByIsbn(isbn);

        Assertions.assertThat(exists).isTrue();
    } @Test
    @DisplayName("Deve retornar false quando não existir um livro na base com o isbn informado")
    void returnFalseWhenIsbnExists(){

        String isbn = "123";

        boolean exists = repository.existsByIsbn(isbn);

        Assertions.assertThat(exists).isFalse();
    }
}
