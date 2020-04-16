package com.ddkolesnik.ddkapi.service;

import com.ddkolesnik.ddkapi.dto.app.AppUserDTO;
import com.ddkolesnik.ddkapi.dto.bitrix.BitrixContactDTO;
import com.ddkolesnik.ddkapi.dto.bitrix.BitrixEmailDTO;
import com.ddkolesnik.ddkapi.model.bitrix.BitrixContact;
import com.ddkolesnik.ddkapi.repository.BitrixContactRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    public void mergeContacts() {
        List<BitrixContactDTO> contacts = findAll();
        List<AppUserDTO> users = userService.getAllDTO();
        List<AppUserDTO> updatedUsers = new ArrayList<>();
        contacts.forEach(contact -> users.stream()
                .filter(user -> Objects.nonNull(user.getPartnerCode()) &&
                        user.getPartnerCode().endsWith(contact.getCode()))
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
