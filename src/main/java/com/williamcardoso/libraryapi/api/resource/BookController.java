package com.williamcardoso.libraryapi.api.resource;


import com.williamcardoso.libraryapi.api.dto.BookDto;
import com.williamcardoso.libraryapi.model.entity.Book;
import com.williamcardoso.libraryapi.service.BookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
@Api("Book API")//anotações Swagger
//@RequiredArgsConstructor//cria contrutor com as variavel e ja injeta
public class BookController {

    //private final BookService service; se for usar a anotação @RequiredArgsConstructor

    private BookService service;
    private ModelMapper modelMapper;

    public BookController(BookService service, ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)// toda requisição feita com sucesso retorna 200
                                      // por isso colocado a anotação created para o codigo 201.
    @ApiOperation("Create a book")//estilizando o Swagger
    public BookDto create(@RequestBody @Valid BookDto dto){ //boa pratica receber e retornar sempre um dto

//        Book entity = Book.builder()// transformando dto em entitade
//                .author(dto.getAuthor())
//                .title(dto.getTitle())
//                .isbn(dto.getIsbn())
//                .build();
        Book entity = modelMapper.map(dto, Book.class);

        entity = service.save(entity);//salvando entidade no BD.

//        return BookDto.builder() //devolvendo objeto salvo e com ID
//                        .id(entity.getId())
//                        .author(entity.getAuthor())
//                        .title(entity.getTitle())
//                        .isbn(entity.getIsbn())
//                        .build();
        return modelMapper.map(entity, BookDto.class);

    }


    @GetMapping("{id}")
    @ApiOperation("Obtains a book details by id")
    public BookDto get(@PathVariable Long id){

//        Book book = service.getById(id).get();//get no final pois e retornado um option
//
//        return modelMapper.map(book, BookDto.class);

        return service
                .getById(id)
                .map( book -> modelMapper.map(book, BookDto.class))
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND)); //exception ja disponibilizada pelo spring com codigo de status.
    }
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation("Deletes a book by id")
    @ApiResponses({ @ApiResponse( code = 204, message = "Book succesfuly deleted")})//alterando codigos no swagger
    public void delete( @PathVariable Long id){
         Book book = service.getById(id).orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
         service.delete(book);

    }

    @PutMapping("{id}")
    @ApiOperation("updates a book")
    public BookDto update( @PathVariable Long id, BookDto dto){
        return service.getById(id).map( book  -> {//colocado entre chaves para atualizar um objeto

        book.setAuthor(dto.getAuthor());
        book.setTitle(dto.getTitle());
        book = service.update(book);
        return modelMapper.map(book, BookDto.class);})
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND));


    }
    @GetMapping
    @ApiOperation("Find books by params")
    public Page<BookDto> find (BookDto dto, Pageable pageRequest){

        Book filter = modelMapper.map(dto, Book.class);
        Page<Book> result = service.find(filter, pageRequest);
        List<BookDto> list = result.getContent().stream()  //metodo getContent que trás os registros lista.
                .map(entity -> modelMapper.map(entity, BookDto.class))//retornando um stream de dto
                .collect(Collectors.toList());
        return new PageImpl<BookDto>(list, pageRequest,result.getTotalElements());//(conteudo,pagina atual,total de elementos)


    }

}
