package com.williamcardoso.libraryapi.api.resource;


import com.williamcardoso.libraryapi.api.dto.BookDto;
import com.williamcardoso.libraryapi.api.exception.ApiErrors;
import com.williamcardoso.libraryapi.exception.BusinessEsception;
import com.williamcardoso.libraryapi.model.entity.Book;
import com.williamcardoso.libraryapi.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/books")
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

   //Forma de tratar exception que acontecem na API e mapeia exception pra retorno.
    //MethodArgumentNotValidException é lançado toda vez que se tenta validar um objeto.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleValidationException(MethodArgumentNotValidException ex){

        BindingResult bindingResult = ex.getBindingResult();//resultado da validação que ocorreu,vai conter todas as mensagens de erro
       // bindingResult.getAllErrors();// todos os erros que aconteceram na validação.
        return new ApiErrors(bindingResult);

    }
    @ExceptionHandler(BusinessEsception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleBusinessException(BusinessEsception ex){
        return new ApiErrors(ex);
    }
}
