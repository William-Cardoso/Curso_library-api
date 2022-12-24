package com.williamcardoso.libraryapi.api.resource;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.williamcardoso.libraryapi.api.dto.BookDto;
import com.williamcardoso.libraryapi.exception.BusinessEsception;
import com.williamcardoso.libraryapi.model.entity.Book;
import com.williamcardoso.libraryapi.service.BookService;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
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
    //criado para criar um livro
    private BookDto createNewBook() {
        return BookDto.builder().author("Artur").title("As aventuras").isbn("001").build();
    }


}
