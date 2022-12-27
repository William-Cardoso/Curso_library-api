package com.williamcardoso.libraryapi.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.williamcardoso.libraryapi.api.dto.LoanDto;
import com.williamcardoso.libraryapi.exception.BusinessEsception;
import com.williamcardoso.libraryapi.model.entity.Book;
import com.williamcardoso.libraryapi.model.entity.Loan;
import com.williamcardoso.libraryapi.service.BookService;
import com.williamcardoso.libraryapi.service.LoanService;
import org.hamcrest.Matchers;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.Optional;
import java.util.regex.Matcher;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = LoanController.class)//quando temos mais de um controller
@AutoConfigureMockMvc
public class LoanControllerTest {

    static final String LOAN_API = "/api/loans"; //url

    @Autowired
    MockMvc mvc;

    @MockBean
    BookService bookService;//mocando serviço

    @MockBean
    private LoanService loandService;

    @Test@DisplayName("Deve realizar um emprestimo")
    public void createLoanTest() throws Exception{

        LoanDto dto = LoanDto.builder().isbn("123").customer("Fulano").build();//fulanop ira pedir livro emprestado com isbn 123blz
        String json = new ObjectMapper().writeValueAsString(dto);//convertente objeto dto em json


        Book book = Book.builder().id(1l).isbn("123").build();

        BDDMockito.given( bookService.getBookByIsbn("123"))
                .willReturn(Optional.of(book));//retornando livro que estara na base de dados

        Loan loan = Loan.builder().id(1l).customer("Fulano").book(book).loandate(LocalDate.now()).build();
        BDDMockito.given( loandService.save(Mockito.any(Loan.class))).willReturn(loan);


        //montando a requisição
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform( request)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect( MockMvcResultMatchers.content().string("1"))//não usou o jsonPath pois esta sendo retornado um id.
                ;

    }
    @Test
    @DisplayName("Deve retornar erro ao tentar fazer emprestimo de um livro inexistente.")
    public void invalidIsbnCreateLoanTEst() throws Exception{

        LoanDto dto = LoanDto.builder().isbn("123").customer("Fulano").build();//fulanop ira pedir livro emprestado com isbn 123blz
        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given( bookService.getBookByIsbn("123"))
                .willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform( request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect( MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(1)))//array como Json
                .andExpect( MockMvcResultMatchers.jsonPath("errors[0]").value("Book not found for passed isbn"))

        ;

    }
    @Test
    @DisplayName("Deve retornar erro ao tentar fazer emprestimo de um livro emprestado.")
    public void loanedBookErrorOnCreateLoanTest() throws Exception{

        LoanDto dto = LoanDto.builder().isbn("123").customer("Fulano").build();//fulanop ira pedir livro emprestado com isbn 123blz
        String json = new ObjectMapper().writeValueAsString(dto);

        Book book = Book.builder().id(1l).isbn("123").build();

        BDDMockito.given( bookService.getBookByIsbn("123"))
                .willReturn(Optional.of(book));//retornando livro que estara na base de dados

        BDDMockito.given( loandService.save(Mockito.any(Loan.class)))
                .willThrow( new BusinessEsception("Book already loaned"));


        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform( request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect( MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(1)))//array como Json
                .andExpect( MockMvcResultMatchers.jsonPath("errors[0]").value("Book already loaned"))

        ;

    }


}
