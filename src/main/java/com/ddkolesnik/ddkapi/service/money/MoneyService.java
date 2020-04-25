package com.ddkolesnik.ddkapi.service.money;

import com.ddkolesnik.ddkapi.dto.money.MoneyDTO;
import com.ddkolesnik.ddkapi.dto.bitrix.BitrixContactDTO;
import com.ddkolesnik.ddkapi.model.money.Money;
import com.ddkolesnik.ddkapi.model.money.Money_;
import com.ddkolesnik.ddkapi.repository.money.MoneyRepository;
import com.ddkolesnik.ddkapi.service.bitrix.BitrixContactService;
import com.ddkolesnik.ddkapi.specification.MoneySpecification;
import com.ddkolesnik.ddkapi.specification.filter.MoneyFilter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Alexandr Stegnin
 */

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MoneyService {

    final MoneyRepository moneyRepository;

    final MoneySpecification specification;

    final ModelMapper mapper;

    final BitrixContactService bitrixContactService;

    List<BitrixContactDTO> bitrixContacts;

    private List<Money> findAll(MoneyFilter filter) {
        int page = 0;
        int limit = 50;
        if (filter != null) {
            if (filter.getLimit() == 0) {
                filter.setLimit(50);
            }
            limit = filter.getLimit();
            if (filter.getOffset() > 0) {
                page = filter.getOffset() / limit;
            } else {
                page = 0;
            }
        }
        Pageable pageable = PageRequest.of(page, limit, Sort.Direction.ASC, Money_.DATE_GIVEN);
        return moneyRepository.findAll(specification.getFilter(filter), pageable).getContent();
    }

    public List<MoneyDTO> findAllDTO(MoneyFilter filter) {
        bitrixContacts = new ArrayList<>(bitrixContactService.findAll());
        return findAll(filter).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private MoneyDTO convertToDTO(Money money) {
        MoneyDTO dto = mapper.map(money, MoneyDTO.class);
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(dto.getInvestor());
        if (matcher.find()) {
            BitrixContactDTO contactDTO = bitrixContacts
                    .stream()
                    .filter(contact -> contact.getCode().equalsIgnoreCase(matcher.group()))
                    .findFirst().orElse(null);
            dto.setBitrixInfo(contactDTO);
        }
        return dto;
    }
}
