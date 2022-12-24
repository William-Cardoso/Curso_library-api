package com.williamcardoso.libraryapi.service.impl;

import com.williamcardoso.libraryapi.exception.BusinessEsception;
import com.williamcardoso.libraryapi.model.entity.Book;
import com.williamcardoso.libraryapi.model.repository.BookRepository;
import com.williamcardoso.libraryapi.service.BookService;
import org.springframework.stereotype.Service;

import java.util.prefs.BackingStoreException;

@Service
public class BookServiceImpl implements BookService {

    private BookRepository repository;

    public BookServiceImpl(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Book save(Book book) {
        if ( repository.existsByIsbn(book.getIsbn())){//exists é qery method do jpa

            throw new BusinessEsception("Isbn já cadastrado.");
        }
        return repository.save(book);
    }
}
