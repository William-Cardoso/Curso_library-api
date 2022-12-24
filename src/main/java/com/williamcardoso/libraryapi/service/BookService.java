package com.williamcardoso.libraryapi.service;

import com.williamcardoso.libraryapi.model.entity.Book;

// criando interface para facilitar os testes
public interface BookService {


    Book save(Book book);

}
