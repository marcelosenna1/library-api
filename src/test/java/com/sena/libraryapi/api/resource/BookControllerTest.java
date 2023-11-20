package com.sena.libraryapi.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sena.libraryapi.api.dto.BookDTO;
import com.sena.libraryapi.api.model.Book;
import com.sena.libraryapi.api.service.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
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

        BookDTO dto = new BookDTO()
                .setId(1L)
                .setTitle("Meu livro")
                .setIsbn("123456")
                .setAuthor("Marcelo Sena");

        Book entity = new Book().setId(dto.getId()).setAuthor(dto.getAuthor()).setTitle(dto.getTitle()).setIsbn(dto.getIsbn());
        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(bookService.save(Mockito.any(Book.class))).willReturn(entity);

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
    void createdInvalidBookTest(){

    }

    @Test
    @DisplayName("Deve deletar um livro com sucesso!")
    void deletedBookTest(){

    }
}
