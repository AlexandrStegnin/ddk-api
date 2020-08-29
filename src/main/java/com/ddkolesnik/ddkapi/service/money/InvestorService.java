package com.ddkolesnik.ddkapi.service.money;

import com.ddkolesnik.ddkapi.configuration.exception.ApiException;
import com.ddkolesnik.ddkapi.model.money.Investor;
import com.ddkolesnik.ddkapi.repository.money.InvestorRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

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
        if (investor == null) {
            throw new ApiException("Инвестор с логином [" + login + "] не найден", HttpStatus.NOT_FOUND);
        }
        return investor;
    }

}
