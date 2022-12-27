package com.williamcardoso.libraryapi.api;

import com.williamcardoso.libraryapi.api.exception.ApiError;
import com.williamcardoso.libraryapi.exception.BusinessEsception;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice//configurações globais para erros de toda API
public class ApplicationControllerAdvice {

    //Forma de tratar exception que acontecem na API e mapeia exception pra retorno.
    //MethodArgumentNotValidException é lançado toda vez que se tenta validar um objeto.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(MethodArgumentNotValidException ex){

        BindingResult bindingResult = ex.getBindingResult();//resultado da validação que ocorreu,vai conter todas as mensagens de erro
        // bindingResult.getAllErrors();// todos os erros que aconteceram na validação.
        return new ApiError(bindingResult);

    }
    @ExceptionHandler(BusinessEsception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBusinessException(BusinessEsception ex){
        return new ApiError(ex);
    }


    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity handleResponseStatusException(ResponseStatusException ex){
        return new ResponseEntity( new ApiError(ex),ex.getStatus());//retorno objeto e Status
    }

}
