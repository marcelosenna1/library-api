package com.sena.libraryapi.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sena.libraryapi.api.dto.LoanDTO;
import com.sena.libraryapi.api.dto.LoanFilterDTO;
import com.sena.libraryapi.api.dto.ReturnedLoanDTO;
import com.sena.libraryapi.exception.BusinessException;
import com.sena.libraryapi.model.entity.Book;
import com.sena.libraryapi.model.entity.Loan;
import com.sena.libraryapi.service.BookService;
import com.sena.libraryapi.service.LoanService;
import com.sena.libraryapi.service.LoanServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class LoanControllerTest {

    static String LOAN_API = "/api/loans";
    @Autowired
    MockMvc mvc;
    @MockBean
    private BookService bookService;
    @MockBean
    private LoanService loanService;

    @Test
    @DisplayName("Deve realizar um emprestimo")
    void createLoanTest() throws Exception {

        LoanDTO dto = LoanDTO.builder().isbn("123").email("marcelo@gmail.com").customer("Sena").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        Book book = Book.builder().id(1L).isbn("123").build();
        given(bookService.getBookByIsbn("123")).willReturn(Optional.of(book));

        Loan loan = Loan.builder().id(1L).customer("Sena").book(book).loanDate(LocalDate.now()).build();
        given(loanService.save(any(Loan.class))).willReturn(loan);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().string("1"));

    }

    @Test
    @DisplayName("Deve retornar erro ao tentar realizar um emprestimo de um livro inexistente")
    void invalidaIsbnCreateLoanTest() throws Exception {

        LoanDTO dto = LoanDTO.builder().isbn("123").customer("Sena").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        given(bookService.getBookByIsbn("123")).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Book not found for passed isbn"));

    }

    @Test
    @DisplayName("Deve retornar erro ao tentar realizar um emprestimo de um livro já emprestado")
    void loanedBookErrorOnCreateLoanTest() throws Exception {

        LoanDTO dto = LoanDTO.builder().isbn("123").customer("Sena").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        Book book = Book.builder().id(1L).isbn("123").build();
        given(bookService.getBookByIsbn("123")).willReturn(Optional.of(book));

        given(loanService.save(any(Loan.class))).willThrow(new BusinessException("Book already loaned"));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Book already loaned"));

    }

    @Test
    @DisplayName("Deve retornar um livro")
    void returnBookTest() throws Exception {

        ReturnedLoanDTO dto = ReturnedLoanDTO.builder().returned(true).build();
        Loan loan = Loan.builder().id(1L).build();
        given(loanService.getById(anyLong())).willReturn(Optional.of(loan));

        String json = new ObjectMapper().writeValueAsString(dto);

        mvc.perform(
                patch(LOAN_API.concat("/1"))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)).andExpect(status().isOk());

        verify(loanService, times(1)).update(loan);
    }

    @Test
    @DisplayName("Deve retornar um erro 404 quando tentar devolver um livro inexistente")
    void returnInexistentBookTest() throws Exception {

        ReturnedLoanDTO dto = ReturnedLoanDTO.builder().returned(true).build();
        String json = new ObjectMapper().writeValueAsString(dto);

        given(loanService.getById(anyLong())).willReturn(Optional.empty());

        mvc.perform(
                patch(LOAN_API.concat("/1"))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)).andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("Deve filtrar empréstimos")
    void findBookTest() throws Exception {

        Long id = 1L;

        Loan loan = LoanServiceTest.createLoan();
        loan.setId(id);
        Book book = Book.builder().id(id).isbn("321").build();
        loan.setBook(book);

        given(loanService.find(any(LoanFilterDTO.class), any(Pageable.class)))
                .willReturn(new PageImpl<Loan>(Arrays.asList(loan), PageRequest.of(0,100), 1));

        String queryString = String.format("?isbn=%s&customer=%s&page=0&size=100",
                book.getIsbn(),
                loan.getCustomer());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(LOAN_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        mvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(100))
                .andExpect(jsonPath("pageable.pageNumber").value(0));


    }


}
