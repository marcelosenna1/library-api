package com.sena.libraryapi.api.resource;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sena.libraryapi.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    @DisplayName("Deve deletar um livro com sucesso!")
    void deletedBookTest(){

    }

    private static BookDTO createNewBook() {
        return new BookDTO()
                .setId(1L)
                .setTitle("Meu livro")
                .setIsbn("123")
                .setAuthor("Marcelo Sena");
    }
}
