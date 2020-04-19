package com.ddkolesnik.ddkapi.service;

import com.ddkolesnik.ddkapi.dto.InvestorCashDTO;
import com.ddkolesnik.ddkapi.model.Facility;
import com.ddkolesnik.ddkapi.model.Investor;
import com.ddkolesnik.ddkapi.model.money.Money;
import com.ddkolesnik.ddkapi.repository.MoneyRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * @author Alexandr Stegnin
 */

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class InvestorCashService {

    private static final String INVESTOR_PREFIX = "investor";

    MoneyRepository moneyRepository;

    InvestorService investorService;

    FacilityService facilityService;

    /**
     * Создать или обновить проводку, пришедшую из 1С
     *
     * @param dto - DTO объект из 1С
     * @return - созданная проводка
     */
    public Money update(InvestorCashDTO dto) {
        Money money = moneyRepository.findByTransactionUUID(dto.getTransactionUUID());
        if (Objects.isNull(money)) {
            return create(dto);
        } else {
            return update(money, dto);
        }
    }

    /**
     * Создать проводку
     *
     * @param dto - DTO объект из 1С
     * @return - созданная проводка
     */
    private Money create(InvestorCashDTO dto) {
        Money money = convertDto(dto);
        return moneyRepository.save(money);
    }

    /**
     * Обновить проводку
     *
     * @param money - существующая проводка
     * @param dto - DTO объект из 1С
     * @return - обновлённая проводка
     */
    private Money update(Money money, InvestorCashDTO dto) {
        prepareMoney(money, dto);
        return moneyRepository.save(money);
    }

    /**
     * Поготовить к обновлению проводку
     *
     * @param money - существующая проводка
     * @param dto - DTO объект из 1С
     */
    private void prepareMoney(Money money, InvestorCashDTO dto) {
        if (!money.getFacility().getName().equalsIgnoreCase(dto.getFacility())) {
            Facility facility = findFacility(dto.getFacility());
            money.setFacility(facility);
        }
        money.setGivenCash(dto.getGivenCash());
        money.setDateGiven(dto.getDateGiven());
    }

    /**
     * Конвертировать DTO в проводку
     *
     * @param dto - DTO объект из 1С
     * @return - подготовленная к сохранению проводка
     */
    private Money convertDto(InvestorCashDTO dto) {
        Money money = new Money();

        Investor investor = findInvestor(dto.getInvestorCode());
        money.setInvestor(investor);

        Facility facility = findFacility(dto.getFacility());
        money.setFacility(facility);

        money.setDateGiven(dto.getDateGiven());
        money.setGivenCash(dto.getGivenCash());
        money.setTransactionUUID(dto.getTransactionUUID());
        return money;
    }

    /**
     * Найти инвестора по коду, который приходит из 1С
     *
     * @param investorCode - код инвестора
     * @return - найденный инвестор
     */
    private Investor findInvestor(String investorCode) {
        String login = INVESTOR_PREFIX.concat(investorCode);
        return investorService.findByLogin(login);
    }

    /**
     * Найти объект по имени, который приходит из 1С
     *
     * @param name - название объекта
     * @return - найденный инвестор
     */
    private Facility findFacility(String name) {
        return facilityService.findByName(name);
    }

}
