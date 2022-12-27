package com.williamcardoso.libraryapi.service.impl;

import com.williamcardoso.libraryapi.exception.BusinessEsception;
import com.williamcardoso.libraryapi.model.entity.Loan;
import com.williamcardoso.libraryapi.model.repository.LoanRepository;
import com.williamcardoso.libraryapi.service.LoanService;

public class LoanServiceImpl implements LoanService {

    private LoanRepository repository;

    public LoanServiceImpl(LoanRepository repository) {
        this.repository = repository;
    }

    @Override
    public Loan save(Loan loan) {
        if( repository.existsByBookAndNotReturned( loan.getBook())){
            throw  new BusinessEsception("Book already loaned");
        }

        return repository.save(loan);
    }
}
