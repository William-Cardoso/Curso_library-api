package com.williamcardoso.libraryapi.api.resource;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.williamcardoso.libraryapi.api.dto.BookDto;
import com.williamcardoso.libraryapi.exception.BusinessEsception;
import com.williamcardoso.libraryapi.model.entity.Book;
import com.williamcardoso.libraryapi.service.BookService;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = BookController.class)
@AutoConfigureMockMvc
public class BookControllerTest {

    //rota pa a API
    static String BOOK_API = "/api/books";

    @Autowired
    MockMvc mvc;

    @MockBean//implementação apenas para testes por isso foi mocado criando instancia mocada
    BookService service;

    @Test
    @DisplayName("Deve criar um livro com sucesso")
    public void createBookTest() throws Exception {


        //Criando um BookDTO para enviar para a requição

        BookDto dto = createNewBook();
        Book savedBook = Book.builder().id(10l).author("Artur").title("As aventuras").isbn("001").build();

        BDDMockito.given( service.save(Mockito.any(Book.class))).willReturn(savedBook);//simulando service
        String json = new ObjectMapper().writeValueAsString(dto); //converte objeto nesse caso dto de qualquer tipo para json.
                                                                   // esse método devolve uma exceção.


        // montando a requisição
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)//conteudo tipo json
                .accept(MediaType.APPLICATION_JSON)//servidor aceita requisição json
                .content(json);//corpo da requisição

        // executando a requisição
        mvc
                .perform(request)//executando a requisição
                .andExpect( MockMvcResultMatchers.status().isCreated() )//verificação
                .andExpect( MockMvcResultMatchers.jsonPath("id").isNotEmpty())//verifica se o json de resposta esta igual ao verificado.
                .andExpect( MockMvcResultMatchers.jsonPath("title").value(dto.getTitle()))
                .andExpect( MockMvcResultMatchers.jsonPath("author").value(dto.getAuthor()) )
                .andExpect( MockMvcResultMatchers.jsonPath("isbn").value(dto.getIsbn()))
                ;




    }



    //teste para validar a integridade
    @Test
    @DisplayName("Deve lançar erro de validação quando não houver dados suficiente para criação do livro")
    public void createInvalidBookTest() throws Exception{

        String json = new ObjectMapper().writeValueAsString(new BookDto());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)//conteudo tipo json
                .accept(MediaType.APPLICATION_JSON)//servidor aceita requisição json
                .content(json);//corpo da requisição


        mvc
                .perform(request)//executando a requisição
                .andExpect( MockMvcResultMatchers.status().isBadRequest() )//verificação
                .andExpect( MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(3)))//o retorno é um array de objetos

        ;

    }
    //teste para validação de regra de negocio
    @Test
    @DisplayName("Deve lançar erro ao tentar cadastrar um livro com isbn já utilizado por outro")
    public void createBookWithDuplicatedIsbn() throws Exception {

        BookDto dto = createNewBook();

        String json = new ObjectMapper().writeValueAsString(dto);
        String mensagemErro = "Isbn já cadastrado";
        BDDMockito.given( service.save(Mockito.any(Book.class)))//chamando o service
                .willThrow(new BusinessEsception(mensagemErro));//quando o isbn ja cadastrado laça uma exceção.
                                                                                // erro regra de negócio.
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)//conteudo tipo json
                .accept(MediaType.APPLICATION_JSON)//servidor aceita requisição json
                .content(json);//corpo da requisição

        mvc
                .perform(request)
                .andExpect( MockMvcResultMatchers.status().isBadRequest())
                .andExpect( MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(1)))
                .andExpect( MockMvcResultMatchers.jsonPath("errors[0]").value(mensagemErro))
                ;

    }
    //busca de livros

    @Test
    @DisplayName("Deve Obter informações de um livro")
    public void getBookDetailsTest() throws Exception {
        // cenario ou given BDD
        Long id = 1l;

        Book book = Book.builder()
                .id(id)
                .title( createNewBook().getTitle())
                .author(createNewBook().getAuthor())
                .isbn(createNewBook().getIsbn())
                .build();
        BDDMockito.given( service.getById(id)).willReturn(Optional.of(book));

        //  /api/books/1
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + id)) // "/api/books/1"
                .accept(MediaType.APPLICATION_JSON);//por ser um GET não tem corpo por isso não tem o contenType.

        mvc
                .perform(request)
                .andExpect( MockMvcResultMatchers.status().isOk())
                .andExpect( MockMvcResultMatchers.jsonPath("id").value(id))//verifica se o json de resposta esta igual ao verificado.
                .andExpect( MockMvcResultMatchers.jsonPath("title").value(createNewBook().getTitle()))
                .andExpect( MockMvcResultMatchers.jsonPath("author").value(createNewBook().getAuthor()) )
                .andExpect( MockMvcResultMatchers.jsonPath("isbn").value(createNewBook().getIsbn()))
                ;


    }
    @Test
    @DisplayName("Deve retornar resource not found quando o livro procurado não existir")
    public void bookNotFoundTest() throws Exception{

        BDDMockito.given( service.getById(Mockito.anyLong())).willReturn(Optional.empty());//objeto retornado vazio

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + 1)) // "/api/books/1"
                .accept(MediaType.APPLICATION_JSON)
                ;
        mvc
                .perform(request)
                .andExpect( MockMvcResultMatchers.status().isNotFound());

    }
    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteBookTest() throws Exception {
        // Criado para deletar pois um livro tem que existir
        BDDMockito.given( service.getById(Mockito.anyLong()))
                .willReturn(Optional.of(Book.builder().id(1l).build()));//somente com o id para deletar

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 1)) // "/api/books/1"
                .accept(MediaType.APPLICATION_JSON)
                ;

        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
    @Test
    @DisplayName("Deve retornar resource not found quando não encontar o livro para deletar um livro")
    public void deleteInexistentBookTest() throws Exception {

        BDDMockito.given( service.getById(Mockito.anyLong()))
                .willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 1)) // "/api/books/1"
                .accept(MediaType.APPLICATION_JSON)
                ;

        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("Deve atualizar um livro")
    public void updateBookTest() throws Exception{

        Long id = 1l ;//id para o livro que sera atualizado

        String json = new ObjectMapper().writeValueAsString(createNewBook());

        //book para atualizar
        Book updatingBook = Book.builder().id(1l).title("some title").author(" some author").isbn("321").build();
        BDDMockito.given( service.getById(id))
                .willReturn(Optional.of(updatingBook));//buscando o livro
        //book atualizado
        Book updatedBook = Book.builder().id(id).author("Artur").title("As aventuras").isbn("321").build();
        BDDMockito.given(service.update(updatingBook)).willReturn(updatedBook);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + 1)) // "/api/books/1"
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json)
                ;

        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect( MockMvcResultMatchers.jsonPath("id").value(id))//verifica se o json de resposta esta igual ao verificado.
                .andExpect( MockMvcResultMatchers.jsonPath("title").value(createNewBook().getTitle()))
                .andExpect( MockMvcResultMatchers.jsonPath("author").value(createNewBook().getAuthor()) )
                .andExpect( MockMvcResultMatchers.jsonPath("isbn").value("321"))
        ;
    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar atualizar um livro inexistente")
    public void updateInexistentBookTest() throws Exception{

        String json = new ObjectMapper().writeValueAsString(createNewBook());


        BDDMockito.given( service.getById(Mockito.anyLong()))
                .willReturn(Optional.empty());//buscando o livro

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + 1)) // "/api/books/1"
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json)
                ;

        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }
    @Test
    @DisplayName("Deve filtar livros")
    public void findBookTest() throws Exception {

        Long id = 1l;
        Book book = Book.builder() //livro para o retorno
                .id(id)
                .title( createNewBook().getTitle())
                .author( createNewBook().getAuthor())
                .isbn( createNewBook().getIsbn())
                .build();

        BDDMockito.given( service.find(Mockito.any(Book.class), Mockito.any(Pageable.class)))
                .willReturn( new PageImpl<Book>(Arrays.asList(book), PageRequest.of(0,100),1));
        //PageImpl implementação do Pageable retornando um objeto de pagina(apagina objeto encontrado)
        //Arrays.aslist  criando um array quando recebe um varargs(content)
        //Pagerequest (qual página ,qantidades de elementos) e total de registro que tem na pesquisa

        //como fazer a pesquisa no get "/api/books?"

        String queryString = String.format("?title=%s&author=%s&page=0&size=100",
                book.getTitle(), book.getAuthor());//query string



        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat(queryString)) // "/api/books/1"
                .accept(MediaType.APPLICATION_JSON)
                ;
         mvc
                 .perform( request)
                 .andExpect( MockMvcResultMatchers.status().isOk())
                 .andExpect( MockMvcResultMatchers.jsonPath("content", Matchers.hasSize(1)))
                 .andExpect( MockMvcResultMatchers.jsonPath( "totalElements").value(1))//quantidade de elementos
                 .andExpect( MockMvcResultMatchers.jsonPath("pageable.pageSize").value(100))//tamanho da página
                 .andExpect( MockMvcResultMatchers.jsonPath("pageable.pageNumber").value(0));//numero da página
    }





    //método para criar um livro
    private BookDto createNewBook() {
        return BookDto.builder().author("Artur").title("As aventuras").isbn("001").build();
    }


}
