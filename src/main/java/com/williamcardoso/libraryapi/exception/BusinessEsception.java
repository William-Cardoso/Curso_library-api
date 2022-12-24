package com.williamcardoso.libraryapi.exception;

public class BusinessEsception extends RuntimeException {
    public BusinessEsception(String mensagem) {
        super(mensagem);
    }
}
