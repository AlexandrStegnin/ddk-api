package com.ddkolesnik.ddkapi.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Alexandr Stegnin
 */

@Configuration
public class AppConfiguration {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        Contact contact = new Contact();
        contact.email("alexandr.stegnin@mail.ru");
        contact.name("Alexandr Stegnin");
        return new OpenAPI()
                .components(new Components())
                .info(new Info().title("Документация по API Доходного Дома Колесникъ")
                        .description("Для доступа к информации необходимо отправить POST запрос " +
                                "в формате JSON с логином и паролем на www.api.ddkolesnik.com/auth")
                        .contact(contact));
    }
}
