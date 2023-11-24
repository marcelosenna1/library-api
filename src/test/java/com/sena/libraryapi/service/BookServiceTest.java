package com.sena.libraryapi.service;

import com.sena.libraryapi.model.entity.Book;
import com.sena.libraryapi.model.repository.BookRepository;
import com.sena.libraryapi.service.impl.BookServiceImpl;
import com.sena.libraryapi.exception.BusinessException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    BookService service;
    @MockBean
    BookRepository repository;


    @BeforeEach
    void setUp() {
        this.service = new BookServiceImpl(repository);
    }


    @Test
    @DisplayName("Deve salvar um livro")
    void saveBookTest() {

        Book book = createValidBook();

        when(repository.save(book)).thenReturn(book);

        Book savedBook = service.save(book);

        assertThat(savedBook.getId()).isNotNull();
        assertEquals("123", savedBook.getIsbn());
        assertEquals("Marcelo", savedBook.getAuthor());
        assertEquals("Para mim", savedBook.getTitle());
    }

    @Test
    @DisplayName("Deve lançar erro de negócio ao tentar salvar um livro com isbn duplicado")
    void shouldNotSaveABookWithDuplicatedISBN() {

        Book book = createValidBook();
        when(repository.existsByIsbn(anyString())).thenReturn(true);

        Throwable exception = Assertions.catchException(() -> service.save(book));

        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Isbn já cadastrado.");
        verify(repository, never()).save(book);
    }

    private static Book createValidBook() {
        return Book.builder()
                .id(1L)
                .isbn("123")
                .author("Marcelo")
                .title("Para mim")
                .build();
    }

}
