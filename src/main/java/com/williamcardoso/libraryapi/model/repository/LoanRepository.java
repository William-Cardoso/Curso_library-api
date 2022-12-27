package com.williamcardoso.libraryapi.model.repository;

import com.williamcardoso.libraryapi.model.entity.Book;
import com.williamcardoso.libraryapi.model.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    boolean existsByBookAndNotReturned(Book book);
}
