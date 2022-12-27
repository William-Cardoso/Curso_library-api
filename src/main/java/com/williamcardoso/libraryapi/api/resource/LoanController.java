package com.williamcardoso.libraryapi.api.resource;

import com.williamcardoso.libraryapi.api.dto.LoanDto;
import com.williamcardoso.libraryapi.model.entity.Book;
import com.williamcardoso.libraryapi.model.entity.Loan;
import com.williamcardoso.libraryapi.service.BookService;
import com.williamcardoso.libraryapi.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor//enjeção e criação porem com as variaveis como final
public class LoanController {

    private final LoanService service;
    private final BookService bookService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody LoanDto dto){

        Book book = bookService.getBookByIsbn(dto.getIsbn())
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.BAD_REQUEST,"Book not found for passed isbn"));
        Loan entity = Loan.builder()
                .book(book)
                .customer(dto.getCustomer())
                .loandate(LocalDate.now())
                .build()
                ;
        entity = service.save(entity);
        return entity.getId();

    }
}
