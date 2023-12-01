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
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
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


    @Test
    @DisplayName("Deve obter um livro por Id")
    void getBookByIdTest() {
        Long id = 1L;
        Book book = createValidBook();
        book.setId(id);
        when(repository.findById(id)).thenReturn(Optional.of(book));

        Optional<Book> foundBook = service.getById(id);

        assertThat(foundBook.isPresent()).isTrue();
        assertThat(foundBook.get().getId()).isEqualTo(id);
        assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
        assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
    }

    @Test
    @DisplayName("Deve retornar vazio caso tente obter um livro por Id e ele não exista")
    void BookNotFoundByIdTest() {
        Book book = new Book();

        assertThrows(IllegalArgumentException.class, () -> service.getById(book.getId()));

        verify(repository, never()).findById(book.getId());
    }

    @Test
    @DisplayName("Deve deletar um livro existente")
    void deleteBookTest() {
        Book book = createValidBook();

        when(repository.findById(anyLong())).thenReturn(Optional.of(book));

        service.delete(book);

        verify(repository, times(1)).delete(book);
    }

    @Test
    @DisplayName("Deve mostrar erro ao tentar deletar um livro inexistente")
    void deleteInvalidBookTest() {
        Book book = new Book();

        assertThrows(IllegalArgumentException.class, () -> service.delete(book));

        verify(repository, never()).delete(book);
    }

    @Test
    @DisplayName("Deve atualizar um livro existente")
    void UpdateBookTest() {
        Book book = createValidBook();

        when(repository.findById(anyLong())).thenReturn(Optional.of(book));

        Book updated = service.update(book);

        verify(repository, times(1)).save(book);

        assertNotNull(updated);
        assertNotNull(updated.getId());
        assertNotNull(updated.getTitle());
        assertNotNull(updated.getIsbn());
        assertNotNull(updated.getAuthor());
    }

    @Test
    @DisplayName("Deve mostrar erro ao tentar atualizar um livro inexistente")
    void updateInvalidBookTest() {
        Book book = new Book();

        assertThrows(IllegalArgumentException.class, () -> service.update(book));

        verify(repository, never()).save(book);
    }

    @Test
    @DisplayName("Deve filtrar livros pelas propriedades")
    void findBookTest() {
        Book book = createValidBook();

        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Book> list = Arrays.asList(book);
        Page<Book> page = new PageImpl<>(list, pageRequest, 1);
        when(repository.findAll(any(Example.class), any(PageRequest.class)))
                .thenReturn(page);

        Page<Book> result = service.find(book, pageRequest);

        assertEquals(1, result.getTotalElements());
        assertEquals(list, result.getContent());
        assertEquals(0, result.getPageable().getPageNumber());
        assertEquals(10, result.getPageable().getPageSize());

    }
    @Test
    @DisplayName("Deve obter um livro pelo isbn")
    void getBookIsbnTest(){
        String isbn = "123";
        when(repository.findByIsbn(isbn)).thenReturn(Optional.of(Book.builder().id(1L).isbn(isbn).build()));
        Optional<Book> book = service.getBookByIsbn(isbn);


        assertTrue(book.isPresent());
        assertEquals(isbn, book.get().getIsbn());
        assertEquals(1L, book.get().getId());

        verify(repository, times(1)).findByIsbn(isbn);
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
