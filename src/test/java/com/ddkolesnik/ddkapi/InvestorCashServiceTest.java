package com.ddkolesnik.ddkapi;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ddkolesnik.ddkapi.configuration.exception.ApiSuccessResponse;
import com.ddkolesnik.ddkapi.dto.cash.InvestorCashDTO;
import com.ddkolesnik.ddkapi.model.app.AppUser;
import com.ddkolesnik.ddkapi.model.app.UserProfile;
import com.ddkolesnik.ddkapi.model.cash.CashSource;
import com.ddkolesnik.ddkapi.model.money.Facility;
import com.ddkolesnik.ddkapi.model.money.Investor;
import com.ddkolesnik.ddkapi.repository.app.AppUserRepository;
import com.ddkolesnik.ddkapi.repository.cash.CashSourceRepository;
import com.ddkolesnik.ddkapi.repository.money.FacilityRepository;
import com.ddkolesnik.ddkapi.repository.money.InvestorRepository;
import com.ddkolesnik.ddkapi.service.cash.InvestorCashService;
import com.ddkolesnik.ddkapi.util.AccountingCode;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Alexandr Stegnin
 */

@Transactional
@SpringBootTest
class InvestorCashServiceTest {

  @Autowired
  private AppUserRepository appUserRepository;

  @Autowired
  private InvestorCashService investorCashService;

  @Autowired
  private CashSourceRepository cashSourceRepository;

  @Autowired
  private InvestorRepository investorRepository;

  @Autowired
  private FacilityRepository facilityRepository;

  @Test
  @Transactional
  void resaleShareTest() {
    createSellerSums();
    createInvestorBuyer();
    InvestorCashDTO dto = new InvestorCashDTO();
    dto.setCashSource(createCashSource());
    dto.setInvestorCode(createInvestorCode());
    dto.setTransactionUUID(UUID.randomUUID().toString());
    dto.setDateGiven(LocalDate.now());
    dto.setGivenCash(BigDecimal.valueOf(1000000));
    dto.setFacility(getFacility());
    dto.setInvestorSellerCode("987");
    dto.setInvestorCode("789");
    dto.setAccountingCode(AccountingCode.RESALE_SHARE.getCode());
    ApiSuccessResponse response = investorCashService.update(dto);
    assertTrue(response.getStatus().is2xxSuccessful());
  }

  private void createSellerSums() {
    for (int i = 0; i < 2; i++) {
      InvestorCashDTO dto = new InvestorCashDTO();
      dto.setCashSource(createCashSource());
      dto.setInvestorCode(createInvestorCode());
      dto.setTransactionUUID(UUID.randomUUID().toString());
      dto.setDateGiven(LocalDate.now());
      dto.setGivenCash(BigDecimal.valueOf(500000));
      dto.setFacility(createFacility());
      investorCashService.update(dto);
    }
  }

  private String createInvestorCode() {
    createAppUser("investor987");
    Investor investor = new Investor();
    investor.setLogin("investor987");
    investorRepository.save(investor);
    return "987";
  }

  private void createAppUser(String login) {
    AppUser appUser = new AppUser();
    appUser.setLogin(login);
    appUser.setPassword("123456");
    UserProfile profile = new UserProfile();
    profile.setUser(appUser);
    appUser.setProfile(profile);
    appUserRepository.save(appUser);
  }

  private String createCashSource() {
    CashSource cashSource = new CashSource();
    cashSource.setName("Наличка ИПООО");
    cashSource.setOrganization("Наличка-Колесник С. В. ИПООО");
    return cashSourceRepository.save(cashSource).getOrganization();
  }

  private void createInvestorBuyer() {
    createAppUser("investor789");
    Investor buyer = new Investor();
    buyer.setLogin("investor789");
    investorRepository.save(buyer);
  }

  @Test
  void createMoneyTest() {
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

  private String createFacility() {
    Facility facility = new Facility();
    facility.setName("Замосквореченская, 987");
    facility.setFullName("99987 Замосквореченская, 987");
    facility.setProjectUUID(UUID.randomUUID().toString());
    facilityRepository.save(facility);
    return facility.getFullName();
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
