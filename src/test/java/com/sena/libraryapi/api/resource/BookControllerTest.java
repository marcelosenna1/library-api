package com.sena.libraryapi.api.resource;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sena.libraryapi.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sena.libraryapi.api.dto.BookDTO;
import com.sena.libraryapi.model.entity.Book;
import com.sena.libraryapi.service.BookService;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@WebMvcTest (BookController.class)
@AutoConfigureMockMvc
class BookControllerTest {

    static String BOOK_API = "/api/books";
    @Autowired
    MockMvc mvc;
    @MockBean
    BookService bookService;

    @Test
    @DisplayName("Deve criar um livro com sucesso!")
    void createdBookTest() throws Exception {

        BookDTO dto = createNewBook();

        Book entity = new Book().setId(dto.getId()).setAuthor(dto.getAuthor()).setTitle(dto.getTitle()).setIsbn(dto.getIsbn());
        String json = new ObjectMapper().writeValueAsString(dto);

        given(bookService.save(any(Book.class))).willReturn(entity);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").isNotEmpty())
                .andExpect(jsonPath("title").value(dto.getTitle()))
                .andExpect(jsonPath("author").value(dto.getAuthor()))
                .andExpect(jsonPath("isbn").value(dto.getIsbn()));
    }

    @Test
    @DisplayName("Deve deve lançar erro de validação quando não houver dados suficiente para criação do livro")
    void createdInvalidBookTest() throws Exception {

        String json = new ObjectMapper().writeValueAsString(new BookDTO());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(3)));

    }

    @Test
    @DisplayName("Deve lançar erro ao tentar cadastrar um livro com isbn já utilizado por outro.")
    void createBookWithDuplicateIsbn() throws Exception {

        String json = new ObjectMapper().writeValueAsString(createNewBook());
        String mensagemErro = "Isbn já cadastrado.";
        given(bookService.save(any(Book.class))).willThrow( new BusinessException(mensagemErro));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0]").value(mensagemErro));
    }

    @Test
    @DisplayName("Deve obter informações de um livro")
    void getBookDetailsTest() throws Exception {
        Long  id = 1L;

        Book book = Book.builder()
                .id(id)
                .title("Meu livro")
                .isbn("123")
                .author("Marcelo Sena").build();

        given(bookService.getById(id)).willReturn(Optional.of(book));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/"+id))
                .accept(MediaType.APPLICATION_JSON);

        mvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("title").value(book.getTitle()))
                .andExpect(jsonPath("author").value(book.getAuthor()))
                .andExpect(jsonPath("isbn").value(book.getIsbn()));
    }

    @Test
    @DisplayName("Deve retornar resource not found quando o livro procurado não existir")
    void bookNotFoundTest() throws Exception{

        given(bookService.getById(anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        mvc
                .perform(request)
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("Deve deletar um livro com sucesso!")
    void deletedBookTest() throws Exception {

        given(bookService.getById(anyLong())).willReturn(Optional.of(Book.builder().id(1L).build()));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status()
                        .isNoContent());

    }

    @Test
    @DisplayName("Deve retornar resource not found quando não encontrar um livro para deletar!")
    void deletedInexistBookTest() throws Exception {

        given(bookService.getById(anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status()
                        .isNotFound());

    }

    @Test
    @DisplayName("Deve atualizar um livro")
    void updateBookTest() throws Exception{
        Long id = 1L;
        String json = new ObjectMapper().writeValueAsString(createNewBook());
        Book updatingBook = Book.builder().id(1L).isbn("1230").author("Zeus").title("Aventuras").build();

        given(bookService.getById(id)).willReturn(Optional.of(updatingBook));
        given(bookService.update(updatingBook)).willReturn(Book.builder().id(1L).title("Meu livro")
                .isbn("123")
                .author("Marcelo Sena").build());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + 1))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("title").value(createNewBook().getTitle()))
                .andExpect(jsonPath("author").value(createNewBook().getAuthor()))
                .andExpect(jsonPath("isbn").value("123"));

    }
    @Test
    @DisplayName("Deve retornar 404 ao tentar atualizar um livro inexistente")
    void updateInexistBookTest() throws Exception{

        Long id = 1L;
        String json = new ObjectMapper().writeValueAsString(createNewBook());

        given(bookService.getById(anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + 1))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isNotFound());

    }
    private static BookDTO createNewBook() {
        return new BookDTO()
                .setId(1L)
                .setTitle("Meu livro")
                .setIsbn("123")
                .setAuthor("Marcelo Sena");
    }
}
