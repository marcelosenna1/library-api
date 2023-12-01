package com.sena.libraryapi.service;

import com.sena.libraryapi.model.entity.Book;
import com.sena.libraryapi.model.entity.Loan;
import com.sena.libraryapi.model.repository.LoanRepository;
import com.sena.libraryapi.service.impl.LoanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {

    @MockBean
    private LoanRepository repository;
    private LoanService service;

    @BeforeEach
    void setUp(){
        this.service = new LoanServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um empréstimo")
    void saveLoanTest() {
        Book book = Book.builder().id(1L).build();
        String costumer = "Sena";
        Loan savingLoan = Loan.builder()
                .book(book)
                .costumer(costumer)
                .loanDate(LocalDate.now())
                .build();

        Loan savedLoan = Loan.builder()
                .id(savingLoan.getId())
                .loanDate(LocalDate.now())
                .costumer(costumer)
                .book(book).build();

        when(repository.save(savingLoan)).thenReturn(savedLoan);

        Loan loan = service.save(savingLoan);

        assertEquals(savedLoan.getId(), loan.getId());
        assertEquals(savedLoan.getBook().getId(), loan.getBook().getId());
        assertEquals(savedLoan.getCostumer(), loan.getCostumer());
        assertEquals(savedLoan.getLoanDate(), loan.getLoanDate());
    }
}
