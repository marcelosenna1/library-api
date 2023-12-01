package com.sena.libraryapi.model.repository;

import com.sena.libraryapi.model.entity.Book;
import com.sena.libraryapi.service.BookService;
import com.sena.libraryapi.service.LoanService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository repository;

    @MockBean
    private LoanService loanService;


    @Test
    @DisplayName("Deve retornar verdadeiro quando existir um livro na base com o isbn informado")
    void returnTrueWhenIsbnExists() {

        String isbn = "123";
        Book book = createNewBookIsbn(isbn);
        entityManager.persist(book);

        boolean exists = repository.existsByIsbn(isbn);

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve retornar false quando não existir um livro na base com o isbn informado")
    void returnFalseWhenIsbnExists() {

        String isbn = "123";

        boolean exists = repository.existsByIsbn(isbn);

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Deve obter um livro por ID")
    void findByIdTest() {

        Book book = createNewBookIsbn("123");

        entityManager.persist(book);

        Optional<Book> foundBook = repository.findById(book.getId());

        assertThat(foundBook.isPresent()).isTrue();

    }

    @Test
    @DisplayName("Deve salvar um livro")
    void saveBookTest() {
        Book book = createNewBookIsbn("123");

        Optional<Book> savedBook = Optional.of(repository.save(book));

        assertNotNull(savedBook.get());
        assertNotNull(savedBook.get().getId());
        assertNotNull(savedBook.get().getAuthor());
        assertNotNull(savedBook.get().getTitle());
        assertNotNull(savedBook.get().getIsbn());
    }

    @Test
    @DisplayName("Deve deletar um livro")
    void deleteBookTest() {
        Book book = createNewBookIsbn("123");
        entityManager.persist(book);

        Book foundBook = entityManager.find(Book.class, book.getId());
        repository.delete(foundBook);

        Book deletedBook = entityManager.find(Book.class, book.getId());

        assertNull(deletedBook);
    }

    private static Book createNewBookIsbn(String isbn) {
        return Book.builder().title("Code Sena").author("Marcelo Sena").isbn(isbn).build();
    }
}
