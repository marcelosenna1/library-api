package com.sena.libraryapi.service;

import com.sena.libraryapi.exception.BusinessException;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {

    @MockBean
    private LoanRepository repository;
    private LoanService service;

    @BeforeEach
    void setUp() {
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

    @Test
    @DisplayName("Deve lançar erro de negócio ao salvar um empréstimo com livro já emprestado")
    void loanedBookSaveTest() {
        Book book = Book.builder().id(1L).build();
        String costumer = "Sena";
        Loan savingLoan = Loan.builder()
                .book(book)
                .costumer(costumer)
                .loanDate(LocalDate.now())
                .build();
        when(repository.existsByBookAndNotReturned(book)).thenReturn(true);

        Throwable exception = catchThrowable(() -> service.save(savingLoan));

        assertThat(exception).isInstanceOf(BusinessException.class).hasMessage("Book already loaned");

        verify(repository, never()).save(savingLoan);


    }

    @Test
    @DisplayName("Deve obter as informações de um empréstimo pelo ID")
    void getLoanDetailTest() {
        Loan loan = createLoan();
        Long id = 1L;
        loan.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(loan));

        Optional<Loan> result = service.getById(id);

        assertTrue(result.isPresent());
        assertEquals(loan.getId(), result.get().getId());
        assertEquals(loan.getCostumer(), result.get().getCostumer());
        assertEquals(loan.getBook(), result.get().getBook());
        assertEquals(loan.getLoanDate(), result.get().getLoanDate());

        verify(repository).findById(id);
    }

    @Test
    @DisplayName("Deve atualizar um empréstimo")
    void updateLoanTest(){
        Loan loan = createLoan();
        loan.setId(1L);
        loan.setReturned(true);

        when(repository.save(loan)).thenReturn(loan);

        Loan updated = service.update(loan);

        assertTrue(updated.getReturned());
    }


    public Loan createLoan() {

        Book book = Book.builder().id(1L).build();
        String costumer = "Sena";
        Loan savingLoan = Loan.builder()
                .book(book)
                .costumer(costumer)
                .loanDate(LocalDate.now())
                .build();

        return Loan.builder()
                .id(savingLoan.getId())
                .loanDate(LocalDate.now())
                .costumer(costumer)
                .book(book).build();
    }
}