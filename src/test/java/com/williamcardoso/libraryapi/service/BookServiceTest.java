package com.williamcardoso.libraryapi.service;

import com.williamcardoso.libraryapi.exception.BusinessEsception;
import com.williamcardoso.libraryapi.model.entity.Book;
import com.williamcardoso.libraryapi.model.repository.BookRepository;
import com.williamcardoso.libraryapi.service.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {
    //execução de testes unitários

    BookService service;

    @MockBean
    BookRepository repository;

    @BeforeEach
    public void setUp(){
        this.service = new BookServiceImpl(repository);
    }


    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest(){

        //cenário
        Book book = createValidBook();
        Mockito.when( repository.existsByIsbn(Mockito.anyString())).thenReturn(false);
        Mockito.when( repository.save(book))
                .thenReturn(Book.builder()
                        .id(1l).isbn("123")
                        .title("As aventuras")
                        .author("Fulano")
                        .build());
        //execução

        Book savedBook = service.save(book);

        //verificação

        Assertions.assertThat( savedBook.getId()).isNotNull();
        Assertions.assertThat( savedBook.getIsbn()).isEqualTo("123");
        Assertions.assertThat( savedBook.getTitle()).isEqualTo("As aventuras");
        Assertions.assertThat( savedBook.getAuthor()).isEqualTo("Fulano");



    }



    @Test
    @DisplayName("Deve lançar erro de negocio ao tentar salvar um livro com isbn cadastratdo")
    public void shouldNotSaveABookWithDuplicated(){
        //cenario

        Book book = createValidBook();
        Mockito.when( repository.existsByIsbn(Mockito.anyString())).thenReturn(true);//simulando metodo pois o repository esta mocado.

        // execução

        Throwable exception = Assertions.catchThrowable(() -> service.save(book));//quando service chamado sera lançado um exception
                                                                                   // e essa é a forma de testar se foiu lançada
        //verificações

        Assertions.assertThat(exception).isInstanceOf(BusinessEsception.class)
                .hasMessage("Isbn já cadastrado.");
        Mockito.verify(repository,Mockito.never()).save(book);//verifica que nunca o save foi executado



    }

    private Book createValidBook() {
        return Book.builder().isbn("123").author("Fulano").title("As aventuras").build();
    }




}
