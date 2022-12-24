package com.williamcardoso.libraryapi.api.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookDto {
    // classe para representação dos dados de resposta da requisição

    private Long id;
    @NotEmpty//validações
    private String title;
    @NotEmpty
    private String author;
    @NotEmpty
    private String isbn;
}
