package com.sena.libraryapi.model.repository;

import com.sena.libraryapi.model.entity.Book;
import com.sena.libraryapi.model.entity.Loan;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static com.sena.libraryapi.model.repository.BookRepositoryTest.createNewBookIsbn;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class LoanRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private LoanRepository repository;

    @Test
    @DisplayName("Deve verificar se existe empréstimo não devolvido para o livro")
    void existsByBookAndNotReturnedTest() {
        Loan loan = createAndPersistLoan();
        Book book = loan.getBook();

        boolean exists = repository.existsByBookAndNotReturned(book);

        assertEquals(true, exists);

    }

    @Test
    @DisplayName("Deve buscar empréstimo pelo isbn do livro ou customer")
    void findByBookIsbnOrCustomerTest() {

        Loan loan = createAndPersistLoan();

        Page<Loan> result = repository.findByBookIsbnOrCustomer("123", "Sena", PageRequest.of(1, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getTotalElements()).isEqualTo(1);

    }

    public Loan createAndPersistLoan() {

        Book book = createNewBookIsbn("123");
        entityManager.persist(book);

        Loan loan = Loan.builder().book(book).customer("Sena").loanDate(LocalDate.now()).build();
        entityManager.persist(loan);

        return loan;
    }


}
