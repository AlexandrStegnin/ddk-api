package com.ddkolesnik.ddkapi.service;

import com.ddkolesnik.ddkapi.dto.app.AppUserDTO;
import com.ddkolesnik.ddkapi.model.app.AppUser;
import com.ddkolesnik.ddkapi.service.app.AppUserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * @author Alexandr Stegnin
 */

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SendMessageService {

    static String MAIL_APP_TOKEN;

    static String MAIL_APP_BASE_URL;

    static String MAIL_APP_WELCOME_PATH;

    @Value("${spring.mail.app.token}")
    private void setMailAppToken(String value) {
        MAIL_APP_TOKEN = value;
    }


    @Value("${spring.mail.app.base.url}")
    private void setMailAppUrl(String value) {
        MAIL_APP_BASE_URL = value;
    }

    @Value("${spring.mail.app.welcome.path}")
    private void setMailAppWelcomePath(String value) {
        MAIL_APP_WELCOME_PATH = value;
    }

    AppUserService appUserService;

    public void sendMessage(String login) {
        if (MAIL_APP_TOKEN == null || MAIL_APP_TOKEN.isEmpty()) {
            return;
        }
        WebClient webClient = WebClient.create(MAIL_APP_BASE_URL);
        AppUser user = appUserService.findByLogin(login);
        AppUserDTO userDTO = new AppUserDTO();
        userDTO.setEmail(user.getProfile().getEmail());
        webClient.post()
                .uri(MAIL_APP_TOKEN + MAIL_APP_WELCOME_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(userDTO), AppUserDTO.class)
                .retrieve()
                .bodyToMono(Void.class)
                .subscribe();
    }

}
