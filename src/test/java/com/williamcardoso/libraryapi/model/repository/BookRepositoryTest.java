package com.williamcardoso.libraryapi.model.repository;

// testes de integração

import com.williamcardoso.libraryapi.model.entity.Book;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest //utilizado anotação para  testes com jpa,com uma instancia do banco.
public class BookRepositoryTest {

    @Autowired
    TestEntityManager entityManager;//utilizado para executar operações do repository na base de dados.

    @Autowired
    BookRepository repository;

    @Test
    @DisplayName("Deve retornar verdadeiro quando existir um livro na base com isbn informado")
    public void returnTrueWhenIsbnExists(){
        //cenario

        String isbn = "123";
        Book book = Book.builder().title("Aventuras").author("Fulano").isbn(isbn).build();
        entityManager.persist(book);//persistindo book na base de dados

        //execução

        boolean existis = repository.existsByIsbn(isbn);

        //verificação

        Assertions.assertThat(existis).isTrue();//org.assertj.core.api.Assertions


    }
    @Test
    @DisplayName("Deve retornar falso quando não  existir um livro na base com isbn informado")
    public void returnFalseWhenIsbnDoesntExists(){
        //cenario

        String isbn = "123";

        //execução

        boolean existis = repository.existsByIsbn(isbn);

        //verificação

        Assertions.assertThat(existis).isFalse();//org.assertj.core.api.Assertions


    }

}
