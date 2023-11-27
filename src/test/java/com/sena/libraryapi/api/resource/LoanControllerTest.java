package com.sena.libraryapi.api.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sena.libraryapi.api.dto.LoanDTO;
import com.sena.libraryapi.model.entity.Book;
import com.sena.libraryapi.model.entity.Loan;
import com.sena.libraryapi.service.BookService;
import com.sena.libraryapi.service.LoanService;
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

import javax.swing.text.html.Option;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = LoanController.class)
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

        LoanDTO dto = LoanDTO.builder().isbn("123").customer("Sena").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        Book book = Book.builder().id(1L).isbn("123").build();
        given(bookService.getBookByIsbn("123")).willReturn(Optional.of(book));

        Loan loan = Loan.builder().id(1L).costumer("Sena").book(book).loanDate(LocalDate.now()).build();
        given(loanService.save(any(Loan.class))).willReturn(loan);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(1L));

    }

}
