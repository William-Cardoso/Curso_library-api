package com.williamcardoso.libraryapi.service;

import com.williamcardoso.libraryapi.exception.BusinessEsception;
import com.williamcardoso.libraryapi.model.entity.Book;
import com.williamcardoso.libraryapi.model.entity.Loan;
import com.williamcardoso.libraryapi.model.repository.LoanRepository;
import com.williamcardoso.libraryapi.service.impl.LoanServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {

    @MockBean
     LoanRepository repository;

     LoanService service;

     @BeforeEach
     public void setup(){
         this.service = new LoanServiceImpl(repository);
     }

    @Test
    @DisplayName("Deve salvar um empréstimo")
    public void saveLoanTest(){

        Book book = Book.builder().id(1l).build();
        String customer = "Fulano";

        Loan savingLoan =
                Loan.builder()//livro para ser salvo
                .book(book)
                .customer(customer)
                .loandate(LocalDate.now())
                .build();
        Loan savedLoan = Loan.builder()//emprestimo salvo
                .id(1l)
                .book(book)
                .customer(customer)
                .loandate(LocalDate.now())
                .build();

        Mockito.when( repository.existsByBookAndNotReturned(book)).thenReturn(false); // retorno de um mock boolean é retornado false deixando explicito
        Mockito.when( repository.save(savingLoan)).thenReturn( savedLoan );

        Loan loan = service.save( savingLoan );

        Assertions.assertThat( loan.getId()).isEqualTo( savedLoan.getId());
        Assertions.assertThat( loan.getBook().getId()).isEqualTo( savedLoan.getBook().getId());
        Assertions.assertThat( loan.getCustomer()).isEqualTo( savedLoan.getCustomer());
        Assertions.assertThat( loan.getLoandate()).isEqualTo( savedLoan.getLoandate());


    }
    @Test
    @DisplayName("Deve laçar erro de negócio ao salvar um emprestimo com livro já emprestado")
    public void loanedBookSaveTest(){

        Book book = Book.builder().id(1l).build();
        String customer = "Fulano";

        Loan savingLoan =
                Loan.builder()//livro para ser salvo
                        .book(book)
                        .customer(customer)
                        .loandate(LocalDate.now())
                        .build();

        Mockito.when( repository.existsByBookAndNotReturned(book)).thenReturn(true);


        Throwable exception = Assertions.catchThrowable(() -> service.save(savingLoan));//capturando exceção

        Assertions.assertThat( exception).isInstanceOf(BusinessEsception.class).hasMessage("Book already loaned");

        Mockito.verify( repository, Mockito.never()).save(savingLoan);


    }
}
