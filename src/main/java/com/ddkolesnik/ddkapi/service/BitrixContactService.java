package com.ddkolesnik.ddkapi.service;

import com.ddkolesnik.ddkapi.dto.bitrix.BitrixContactDTO;
import com.ddkolesnik.ddkapi.model.bitrix.BitrixContact;
import com.ddkolesnik.ddkapi.repository.BitrixContactRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Alexandr Stegnin
 */

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BitrixContactService {

    BitrixContactRepository contactRepository;

    ModelMapper mapper;

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
        return contactRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private BitrixContactDTO convertToDTO(BitrixContact contact) {
        return mapper.map(contact, BitrixContactDTO.class);
    }

}
