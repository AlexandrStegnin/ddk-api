package com.ddkolesnik.ddkapi;

import com.ddkolesnik.ddkapi.configuration.exception.ApiSuccessResponse;
import com.ddkolesnik.ddkapi.dto.cash.InvestorCashDTO;
import com.ddkolesnik.ddkapi.model.cash.CashSource;
import com.ddkolesnik.ddkapi.model.money.Facility;
import com.ddkolesnik.ddkapi.model.money.Investor;
import com.ddkolesnik.ddkapi.repository.cash.CashSourceRepository;
import com.ddkolesnik.ddkapi.repository.money.FacilityRepository;
import com.ddkolesnik.ddkapi.repository.money.InvestorRepository;
import com.ddkolesnik.ddkapi.service.cash.InvestorCashService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Alexandr Stegnin
 */

@Transactional
@SpringBootTest
public class InvestorCashServiceTest {

    @Autowired
    private InvestorCashService investorCashService;

    @Autowired
    private CashSourceRepository cashSourceRepository;

    @Autowired
    private InvestorRepository investorRepository;

    @Autowired
    private FacilityRepository facilityRepository;

    @Test
    public void createMoneyTest() {
        InvestorCashDTO dto = getDTO();
        ApiSuccessResponse response = investorCashService.update(dto);
        assertTrue(response.getStatus().is2xxSuccessful());
    }

    private InvestorCashDTO getDTO() {
        InvestorCashDTO dto = new InvestorCashDTO();
        dto.setCashSource(getCashSource());
        dto.setInvestorCode(getInvestorCode());
        dto.setTransactionUUID(UUID.randomUUID().toString());
        dto.setDateGiven(LocalDate.now());
        dto.setGivenCash(BigDecimal.valueOf(777777));
        dto.setFacility(getFacility());
        return dto;
    }

    private String getCashSource() {
        List<CashSource> cashSources = cashSourceRepository.findAll();
        assertTrue(cashSources.size() > 0);
        return cashSources.get(0).getOrganization();
    }

    private String getInvestorCode() {
        List<Investor> investors = investorRepository.findAll()
                .stream()
                .filter(investor -> investor.getLogin().matches("\\w+\\d+"))
                .collect(Collectors.toList());
        assertFalse(investors.isEmpty());
        return extractInvestorCode(investors.get(0));
    }

    private String extractInvestorCode(Investor investor) {
        return investor.getLogin().replaceAll("\\D+", "");
    }

    private String getFacility() {
        List<Facility> facilities = facilityRepository.findAll();
        assertFalse(facilities.isEmpty());
        return facilities.get(0).getFullName();
    }

}
