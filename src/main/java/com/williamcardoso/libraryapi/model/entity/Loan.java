package com.williamcardoso.libraryapi.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Loan {

    private  Long id;
    private String customer;
    private Book book;
    private LocalDate loandate;
    private  Boolean returned;//indicar se o livro foi devolvido

}
