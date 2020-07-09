package com.ddkolesnik.ddkapi.service.cash;

import com.ddkolesnik.ddkapi.dto.cash.InvestorCashDTO;
import com.ddkolesnik.ddkapi.model.cash.CashSource;
import com.ddkolesnik.ddkapi.model.money.Facility;
import com.ddkolesnik.ddkapi.model.money.Investor;
import com.ddkolesnik.ddkapi.model.money.Money;
import com.ddkolesnik.ddkapi.repository.money.MoneyRepository;
import com.ddkolesnik.ddkapi.service.SendMessageService;
import com.ddkolesnik.ddkapi.service.log.TransactionLogService;
import com.ddkolesnik.ddkapi.service.money.FacilityService;
import com.ddkolesnik.ddkapi.service.money.InvestorService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Сервис для работы с проводками из 1С
 *
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

    CashSourceService cashSourceService;

    SendMessageService messageService;

    TransactionLogService transactionLogService;

    /**
     * Создать или обновить проводку, пришедшую из 1С
     *
     * @param dto - DTO объект из 1С
     */
    public void update(InvestorCashDTO dto) {
        Money money = moneyRepository.findByTransactionUUID(dto.getTransactionUUID());
        if (null == money) {
            money = create(dto);
            sendMessage(money.getInvestor());
            transactionLogService.create(money);
        } else {
            transactionLogService.update(money);
            update(money, dto);
        }
    }

    /**
     * Отправка сообщения пользователю
     *
     * @param investor - инвестор
     */
    @Async
    protected void sendMessage(final Investor investor) {
        if (isFirstInvestment(investor.getId())) {
            messageService.sendMessage(investor.getLogin());
        }
    }

    /**
     * Создать проводку
     *
     * @param dto - DTO объект из 1С
     * @return - созданная сумма
     */
    private Money create(InvestorCashDTO dto) {
        Money money = convert(dto);
        return moneyRepository.save(money);
    }

    /**
     * Обновить проводку
     *
     * @param money - существующая проводка
     * @param dto   - DTO объект из 1С
     */
    private void update(Money money, InvestorCashDTO dto) {
        prepareMoney(money, dto);
        moneyRepository.save(money);
    }

    /**
     * Поготовить к обновлению проводку
     *
     * @param money - существующая проводка
     * @param dto   - DTO объект из 1С
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
    private Money convert(InvestorCashDTO dto) {
        Money money = new Money();

        Investor investor = findInvestor(dto.getInvestorCode());
        money.setInvestor(investor);

        Facility facility = findFacility(dto.getFacility());
        money.setFacility(facility);

        CashSource cashSource = findCashSource(dto.getCashSource());
        money.setCashSource(cashSource);

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
     * @param fullName - название объекта
     * @return - найденный инвестор
     */
    private Facility findFacility(String fullName) {
        return facilityService.findByFullName(fullName);
    }

    /**
     * Найти источник денег по имени, которое приходит из 1С
     *
     * @param organization - название источника
     * @return - найденный источник
     */
    private CashSource findCashSource(String organization) {
        return cashSourceService.findByOrganization(organization);
    }

    /**
     * Проверить первое вложение инвестора или нет
     *
     * @param investorId - id инвестора
     * @return - первое вложение или нет
     */
    private boolean isFirstInvestment(Long investorId) {
        Long count = moneyRepository.countByInvestorIdAndDateClosingIsNull(investorId);
        return count == 1;
    }
}
