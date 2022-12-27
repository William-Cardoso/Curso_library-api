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

import java.util.Optional;

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
        Book book = createNewBook(isbn);
        entityManager.persist(book);//persistindo book na base de dados

        //execução

        boolean existis = repository.existsByIsbn(isbn);

        //verificação

        Assertions.assertThat(existis).isTrue();//org.assertj.core.api.Assertions


    }

    private Book createNewBook(String isbn) {
        return Book.builder().title("Aventuras").author("Fulano").isbn(isbn).build();
    }

    @Test
    @DisplayName("Deve retornar false quando não  existir um livro na base com isbn informado")
    public void returnFalseWhenIsbnDoesntExists(){
        //cenario

        String isbn = "123";

        //execução

        boolean existis = repository.existsByIsbn(isbn);

        //verificação

        Assertions.assertThat(existis).isFalse();//org.assertj.core.api.Assertions


    }
    @Test
    @DisplayName("Deve obter um livro por id.")
    public void findByIdTest(){

        //cenario
        Book book = createNewBook("123");//livro criado sem ID pois aida não foi salvo bo BD
        entityManager.persist(book);

        //execução
        Optional<Book> foundBook = repository.findById(book.getId());//testando o findById um metodo do repositorio

        //verificação
        Assertions.assertThat( foundBook.isPresent()).isTrue();

    }
    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest(){

        Book book = createNewBook("123");

        Book savedBook = repository.save(book);

        Assertions.assertThat( savedBook.getId()).isNotNull();
    }
    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteBookTest(){

        Book book = createNewBook("123");
        entityManager.persist(book);

        Book foundBook = entityManager.find(Book.class, book.getId());//garantindo que o livro estava na base

        repository.delete(foundBook);

        Book deletedBook = entityManager.find(Book.class, book.getId());//verificando se realmente foi deletado.
        Assertions.assertThat(deletedBook).isNull();
    }


}
