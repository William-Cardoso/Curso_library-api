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
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
                                                                                   // e essa é a forma de testar se foi lançada com a mensagem especifica
        //verificações

        Assertions.assertThat(exception).isInstanceOf(BusinessEsception.class)
                .hasMessage("Isbn já cadastrado.");
        Mockito.verify(repository,Mockito.never()).save(book);//verifica que nunca o save foi executado



    }

    private Book createValidBook() {

        return Book.builder()
                .isbn("123")
                .author("Fulano")
                .title("As aventuras")
                .build();
    }
    @Test
    @DisplayName(("Deve obter um livro por id"))
    public void getByIdTest(){

        Long id = 1l;
        Book book = createValidBook();
        book.setId(id);//setando id poi o livro criado não possui id e vai buscado com o id.

        Mockito.when( repository.findById(id)).thenReturn(Optional.of(book));

        //execução
        Optional<Book> foundBook = service.getById(id);

         //verificações
        Assertions.assertThat( foundBook.isPresent()).isTrue();
        Assertions.assertThat( foundBook.get().getId()).isEqualTo(id);
        Assertions.assertThat( foundBook.get().getAuthor()).isEqualTo( book.getAuthor());
        Assertions.assertThat( foundBook.get().getIsbn()).isEqualTo( book.getIsbn());
        Assertions.assertThat( foundBook.get().getTitle()).isEqualTo( book.getTitle());
    }

    @Test
    @DisplayName(("Deve retornar vazio ao obter um livro por id quando ele não existe na base"))
    public void bookNotFoundByIdTest(){

        Long id = 1l;

        Mockito.when( repository.findById(id)).thenReturn(Optional.empty());

        //execução
        Optional<Book> book = service.getById(id);

        //verificações
        Assertions.assertThat( book.isPresent()).isFalse();

    }
    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteBookTest(){
        Book book = Book.builder().id(1l).build();

        //execução
       org.junit.jupiter.api.Assertions.assertDoesNotThrow( () -> service.delete(book));

        //verificação
        Mockito.verify( repository, Mockito.times(1)).delete(book);

    }
    @Test
    @DisplayName("Deve ocorrer erro ao tentar deletar um livro inexistente")
    public void deleteInvalidBookTest(){
        Book book = new Book();

        //execução
        org.junit.jupiter.api.Assertions.assertThrows( IllegalArgumentException.class, () -> service.delete(book));//não retorna por isso não tem como fazer assertivas

        //verificação
        Mockito.verify( repository, Mockito.never()).delete(book);//verificando que nunca foi chamado o repository

    }

    @Test
    @DisplayName("Deve ocorrer erro ao tentar atualizar um livro inexistente")
    public void updateInvalidBookTest(){
        Book book = new Book();

        //execução
        org.junit.jupiter.api.Assertions.assertThrows( IllegalArgumentException.class, () -> service.update(book));//não retorna por isso não tem como fazer assertivas

        //verificação
        Mockito.verify( repository, Mockito.never()).save(book);//verificando que nunca foi chamado o repository

    }
    @Test
    @DisplayName("Deve atualizar um livro")
    public void updateBookTest(){
        //cenário
        long id = 1l;

        //livro a atualizar
        Book updatingBook = Book.builder().id(id).build();

        //simulação

        Book updatedBook = createValidBook();
        updatedBook.setId(id);
        Mockito.when( repository.save(updatingBook)).thenReturn(updatedBook);

        //execução

        Book book = service.update(updatingBook);

        //verificação

        Assertions.assertThat( book.getId()).isEqualTo(updatedBook.getId());
        Assertions.assertThat( book.getTitle()).isEqualTo(updatedBook.getTitle());
        Assertions.assertThat( book.getIsbn()).isEqualTo(updatedBook.getIsbn());
        Assertions.assertThat( book.getAuthor()).isEqualTo(updatedBook.getAuthor());

    }
    @Test
    @DisplayName("Deve filtar livros pela propriedades")
    public void findBook(){
        //cenario
        Book book = createValidBook();

        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Book> lista = Arrays.asList(book);
        Page<Book> page = new PageImpl<Book>(lista, pageRequest,1);
        //
        Mockito.when( repository.findAll(Mockito.any(Example.class),Mockito.any(PageRequest.class)))
                .thenReturn(page);

        //execuçção
        Page<Book> result = service.find(book, pageRequest);

        //verificação
        Assertions.assertThat( result.getTotalElements()).isEqualTo(1);
        Assertions.assertThat( result.getContent()).isEqualTo(lista);
        Assertions.assertThat( result.getPageable().getPageNumber()).isEqualTo(0);
        Assertions.assertThat( result.getPageable().getPageSize()).isEqualTo(10);



    }
    @Test
    @DisplayName("Deve obter um livro pelo isbn")
    public void getBookByIsbnTest(){

        String isbn ="1230";
        Mockito.when( repository.findByIsbn(isbn))
                .thenReturn( Optional.of(Book.builder().id(1l).isbn(isbn).build()));//simulando o retorno

        Optional<Book> book = service.getBookByIsbn(isbn);

         Assertions.assertThat( book.isPresent()).isTrue();
         Assertions.assertThat( book.get().getId()).isEqualTo(1l);
         Assertions.assertThat( book.get().getIsbn()).isEqualTo(isbn);

         Mockito.verify( repository, Mockito.times(1)).findByIsbn(isbn);//verificando que o repository foi chamado apenas uma vez

    }



}
