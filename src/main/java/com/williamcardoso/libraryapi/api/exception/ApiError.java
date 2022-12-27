package com.williamcardoso.libraryapi.api.exception;

import com.williamcardoso.libraryapi.exception.BusinessEsception;
import org.springframework.validation.BindingResult;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ApiError {

    private List<String> errors;

    public ApiError(BindingResult bindingResult)//Construtor  recebeu os resultados dos erros da validação.
    {
        this.errors = new ArrayList<>();
        bindingResult.getAllErrors()//pegando as mensagens de erro
                .forEach(error -> this.errors.add(error.getDefaultMessage()));

    }

    public ApiError(BusinessEsception ex) {
        this.errors = Arrays.asList(ex.getMessage());//criando um array com apenas um elemento.
    }

    public List<String> getErrors() {
        return errors;
    }

    public ApiError(ResponseStatusException ex){
        this.errors = Arrays.asList(ex.getReason());//menssagem e status

    }
}
