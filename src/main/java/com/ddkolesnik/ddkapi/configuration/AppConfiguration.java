package com.ddkolesnik.ddkapi.configuration;

import com.ddkolesnik.ddkapi.configuration.exception.LogFilter;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.modelmapper.ModelMapper;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.RegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

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
                        .contact(contact));
    }

    @Bean(name = "threadPoolTaskExecutor")
    public Executor threadPoolTaskExecutor() {
        return new ThreadPoolTaskExecutor();
    }

    @SuppressWarnings("rawtypes")
    @Bean
    public FilterRegistrationBean someFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new LogFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(RegistrationBean.HIGHEST_PRECEDENCE); //Must has higher precedence than FilterChainProxy
        return registration;
    }
}
