package com.williamcardoso.libraryapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class SwaggerConfig {

    @Bean
    public Docket docket(){//tera todas as configurções necessária para o swagger funcionar
        return new Docket( DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.williamcardoso.libraryapi.api.resource"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo( apiInfo())
                ;

    }
    private ApiInfo apiInfo(){//objeto com informações da API ex: titulo,versão,descrição
        return new ApiInfoBuilder()
                .title("Library API")
                .description("Api do projeto de controle de aluguel de livros")
                .version("1.0")
                .contact(contact())
                .build()
                ;
    }
    private springfox.documentation.service.Contact contact(){//contem as suas  informações
        return new Contact("William Cardoso", "http://github.com/william-cardoso", "williamcardososi@gmail.com");


    }
}
