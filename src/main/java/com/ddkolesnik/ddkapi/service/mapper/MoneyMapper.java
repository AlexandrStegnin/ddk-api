package com.ddkolesnik.ddkapi.service.mapper;

import com.ddkolesnik.ddkapi.dto.cash.InvestorCashDTO;
import com.ddkolesnik.ddkapi.model.cash.CashSource;
import com.ddkolesnik.ddkapi.model.money.Facility;
import com.ddkolesnik.ddkapi.model.money.Investor;
import com.ddkolesnik.ddkapi.model.money.Money;
import com.ddkolesnik.ddkapi.model.money.UnderFacility;
import com.ddkolesnik.ddkapi.service.cash.CashSourceService;
import com.ddkolesnik.ddkapi.service.money.FacilityService;
import com.ddkolesnik.ddkapi.service.money.InvestorService;
import com.ddkolesnik.ddkapi.service.money.UnderFacilityService;
import com.ddkolesnik.ddkapi.util.Constant;
import com.ddkolesnik.ddkapi.util.ShareType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author Alexandr Stegnin
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class MoneyMapper {

  InvestorService investorService;
  FacilityService facilityService;
  CashSourceService cashSourceService;
  UnderFacilityService underFacilityService;

  public Money convert(InvestorCashDTO dto) {
    Money money = new Money();

    Investor investor = findInvestor(dto.getInvestorCode());
    money.setInvestor(investor);

    Facility facility = findFacility(dto.getFacility());
    money.setFacility(facility);
    UnderFacility underFacility = findUnderFacility(facility);
    money.setUnderFacility(underFacility);

    money.setShareType(ShareType.MAIN);

    CashSource cashSource = findCashSource(dto.getCashSource());
    money.setCashSource(cashSource);

    money.setDateGiven(dto.getDateGiven());
    money.setGivenCash(dto.getGivenCash());
    money.setTransactionUUID(dto.getTransactionUUID());
    return money;
  }

  private Investor findInvestor(String investorCode) {
    String login = Constant.INVESTOR_PREFIX.concat(investorCode);
    return investorService.findByLogin(login);
  }

  private Facility findFacility(String fullName) {
    return facilityService.findByFullName(fullName);
  }

  private UnderFacility findUnderFacility(Facility facility) {
    if (Objects.nonNull(facility)) {
      String fullName = facility.getName().concat(Constant.UNDER_FACILITY_SUFFIX);
      return underFacilityService.findByName(fullName);
    }
    return null;
  }

  private CashSource findCashSource(String organization) {
    return cashSourceService.findByOrganization(organization);
  }

}
