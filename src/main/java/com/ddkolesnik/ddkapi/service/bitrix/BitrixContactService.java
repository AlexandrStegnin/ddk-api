package com.ddkolesnik.ddkapi.service.bitrix;

import com.ddkolesnik.ddkapi.dto.app.AppUserDTO;
import com.ddkolesnik.ddkapi.dto.bitrix.BitrixContactDTO;
import com.ddkolesnik.ddkapi.dto.bitrix.BitrixEmailDTO;
import com.ddkolesnik.ddkapi.model.bitrix.BitrixContact;
import com.ddkolesnik.ddkapi.repository.bitrix.BitrixContactRepository;
import com.ddkolesnik.ddkapi.service.app.AppUserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.ddkolesnik.ddkapi.util.Constant.BITRIX_CONTACT_UPDATE_URL;

/**
 * @author Alexandr Stegnin
 */

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BitrixContactService {

    BitrixContactRepository contactRepository;

    ModelMapper mapper;

    AppUserService userService;

    public BitrixContact findById(String id) {
        return contactRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contact with id = [" + id + "] not found"));
    }

    public List<BitrixContactDTO> findByPartnerCode(String partnerCode) {
        return contactRepository.findByCode(partnerCode)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BitrixContactDTO> findAll() {
        List<BitrixContact> contacts = contactRepository.findAll();
        return contacts
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public HttpStatus updateContacts() {
        WebClient client = WebClient
                .builder()
                .baseUrl(BITRIX_CONTACT_UPDATE_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        ClientResponse response = client.get().exchange()
                .doOnSuccess(clientResponse -> {
                    mergeContacts();
                    log.info("Синхронизация контактов завершена.");
                })
                .doOnError(resp -> log.error("Ошибка: " + resp.getMessage()))
                .block();
        if (Objects.nonNull(response)) {
            return response.statusCode();
        }
        return HttpStatus.BAD_REQUEST;
    }

    private void mergeContacts() {
        List<BitrixContactDTO> contacts = findAll();
        List<AppUserDTO> users = userService.getAllDTO();
        List<AppUserDTO> updatedUsers = new ArrayList<>();
        contacts.forEach(contact -> users.stream()
                .filter(user -> Objects.nonNull(user.getPartnerCode()) &&
                        user.getPartnerCode().endsWith(contact.getCode()))
                .filter(user -> Objects.nonNull(user.getEmail()))
                .forEach(user -> {
                    if (contact.getEmails().size() == 1) {
                        contact.getEmails().stream().findFirst().ifPresent(email -> {
                            if (!user.getEmail().equalsIgnoreCase(email.getEmail())) {
                                user.setEmail(email.getEmail());
                                user.setPartnerCode(contact.getCode());
                                if (!updatedUsers.contains(user)) {
                                    updatedUsers.add(user);
                                }
                            }
                        });
                    } else if (contact.getEmails().size() > 0) {
                        contact.getEmails().stream()
                                .sorted(Comparator.comparing(BitrixEmailDTO::getType))
                                .forEach(email -> {
                                    if (!user.getEmail().equalsIgnoreCase(email.getEmail())) {
                                        user.setEmail(email.getEmail());
                                        user.setPartnerCode(contact.getCode());
                                        if (!updatedUsers.contains(user)) {
                                            updatedUsers.add(user);
                                        }
                                    }
                                });
                    }
                }));
        updatedUsers.forEach(userService::update);
    }

    private BitrixContactDTO convertToDTO(BitrixContact contact) {
        return mapper.map(contact, BitrixContactDTO.class);
    }

}
