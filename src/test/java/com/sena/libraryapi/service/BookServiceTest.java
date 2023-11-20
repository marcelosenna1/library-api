package com.sena.libraryapi.service;

import com.sena.libraryapi.api.model.Book;
import com.sena.libraryapi.api.model.repository.BookRepository;
import com.sena.libraryapi.api.service.BookService;
import com.sena.libraryapi.api.service.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    BookService service;
    @MockBean
    BookRepository repository;


    @BeforeEach
    void setUp(){
        this.service = new BookServiceImpl(repository);
    }


    @Test
    @DisplayName("Deve salvar um livro")
    void saveBookTest(){

        Book book = Book.builder()
                .id(1L)
                .isbn("123")
                .author("Marcelo")
                .title("Para mim")
                .build();

        when(repository.save(book)).thenReturn(book);

        Book savedBook = service.save(book);

        assertThat(savedBook.getId()).isNotNull();
        assertEquals("123", savedBook.getIsbn());
        assertEquals("Marcelo", savedBook.getAuthor());
        assertEquals("Para mim", savedBook.getTitle());
    }

}
