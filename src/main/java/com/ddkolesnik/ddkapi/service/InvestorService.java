package com.ddkolesnik.ddkapi.service;

import com.ddkolesnik.ddkapi.configuration.exception.ApiException;
import com.ddkolesnik.ddkapi.model.Investor;
import com.ddkolesnik.ddkapi.repository.InvestorRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Objects;

/**
 * @author Alexandr Stegnin
 */

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class InvestorService {

    InvestorRepository investorRepository;

    public Investor findById(Long id) {
        return investorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with ID = [" + id + "] not found"));
    }

    public Investor findByLogin(String login) {
        Investor investor = investorRepository.findByLogin(login);
        if (Objects.isNull(investor)) {
            throw new ApiException("Инвестор с логином [" + login + "] не найден", HttpStatus.NOT_FOUND);
        }
        return investor;
    }

}
