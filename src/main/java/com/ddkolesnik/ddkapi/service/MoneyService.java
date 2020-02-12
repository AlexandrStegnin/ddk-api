package com.ddkolesnik.ddkapi.service;

import com.ddkolesnik.ddkapi.dto.MoneyDTO;
import com.ddkolesnik.ddkapi.model.Money;
import com.ddkolesnik.ddkapi.repository.MoneyRepository;
import com.ddkolesnik.ddkapi.specification.MoneySpecification;
import com.ddkolesnik.ddkapi.specification.filter.MoneyFilter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Alexandr Stegnin
 */

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class MoneyService {

    MoneyRepository moneyRepository;

    MoneySpecification specification;

    ModelMapper mapper;

    private List<Money> findAll(MoneyFilter filter) {
        return moneyRepository.findAll(specification.getFilter(filter));
    }

    public List<MoneyDTO> findAllDTO(MoneyFilter filter) {
        return findAll(filter).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private MoneyDTO convertToDTO(Money money) {
        return mapper.map(money, MoneyDTO.class);
    }
}
