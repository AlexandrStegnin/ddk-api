package com.ddkolesnik.ddkapi.service.cash;

import com.ddkolesnik.ddkapi.dto.cash.InvestorCashDTO;
import com.ddkolesnik.ddkapi.model.cash.CashSource;
import com.ddkolesnik.ddkapi.model.log.TransactionLog;
import com.ddkolesnik.ddkapi.model.money.*;
import com.ddkolesnik.ddkapi.repository.money.MoneyRepository;
import com.ddkolesnik.ddkapi.service.SendMessageService;
import com.ddkolesnik.ddkapi.service.log.TransactionLogService;
import com.ddkolesnik.ddkapi.service.money.AccountTransactionService;
import com.ddkolesnik.ddkapi.service.money.FacilityService;
import com.ddkolesnik.ddkapi.service.money.InvestorService;
import com.ddkolesnik.ddkapi.service.money.UnderFacilityService;
import com.ddkolesnik.ddkapi.util.AccountingCode;
import com.ddkolesnik.ddkapi.util.Constant;
import com.ddkolesnik.ddkapi.util.DateUtils;
import com.ddkolesnik.ddkapi.util.ShareType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.ddkolesnik.ddkapi.util.Constant.COMMISSION_RATE;

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

    private static final LocalDate FILTERED_DATE = LocalDate.of(2020, 6, 30);

    MoneyRepository moneyRepository;

    InvestorService investorService;

    FacilityService facilityService;

    CashSourceService cashSourceService;

    SendMessageService messageService;

    TransactionLogService transactionLogService;

    UnderFacilityService underFacilityService;

    AccountTransactionService accountTransactionService;

    /**
     * Создать или обновить проводку, пришедшую из 1С
     *
     * @param dto - DTO объект из 1С
     */
    public void update(InvestorCashDTO dto) {
        if (checkCash(dto)) {
            Money money = moneyRepository.findByTransactionUUID(dto.getTransactionUUID());
            if (dto.isDelete() && money != null) {
                delete(money);
            } else {
                AccountingCode accountingCode = AccountingCode.fromCode(dto.getAccountingCode());
                if (accountingCode != null) {
                    cashing(dto);
                } else {
                    if (money == null) {
                        money = moneyRepository.findMoney(dto.getDateGiven(), dto.getGivenCash(), dto.getFacility(),
                                dto.getCashSource(), Constant.INVESTOR_PREFIX.concat(dto.getInvestorCode()));
                        if (money == null) {
                            money = create(dto);
                            sendMessage(money.getInvestor());
                            transactionLogService.create(money);
                        } else {
                            transactionLogService.update(money);
                            update(money, dto);
                        }
                    } else {
                        transactionLogService.update(money);
                        update(money, dto);
                    }
                }
            }
        }
    }

    /**
     * Вывести деньги по данным из 1С
     *
     * @param dto DTO из 1С
     */
    private void cashing(InvestorCashDTO dto) {
        if (dto.isDelete()) {
            AccountTransaction parent = accountTransactionService.findByTransactionUUID(dto.getTransactionUUID());
            Set<AccountTransaction> child = parent.getChild();
            child.forEach(accountTransactionService::delete);
            accountTransactionService.delete(parent);
        } else {
            AccountTransaction accountTransaction = accountTransactionService.findByTransactionUUID(dto.getTransactionUUID());
            if (accountTransaction == null) {
                createCashingTransaction(dto);
            } else {
                updateCashingTransaction(accountTransaction, dto);
            }
        }
    }

    /**
     * Обновить транзакцию по выводу средств
     *
     * @param accountTransaction транзакция по счёту
     * @param dto                DTO из 1С
     */
    private void updateCashingTransaction(AccountTransaction accountTransaction, InvestorCashDTO dto) {
        BigDecimal cash = accountTransaction.getCash();
        transactionLogService.update(accountTransaction);
        prepareAccountTransaction(accountTransaction, dto);
        Set<AccountTransaction> child = accountTransaction.getChild();
        child.forEach(c -> {
            c.setCash(cash.multiply(COMMISSION_RATE));
            accountTransactionService.update(c);
        });
        accountTransactionService.update(accountTransaction);
    }

    /**
     * Создать транзакцию по выводу средств
     *
     * @param dto DTO из 1C для вывода
     */
    private void createCashingTransaction(InvestorCashDTO dto) {
        Money money = convert(dto);
        money.setDateClosing(dto.getDateGiven());
        AccountTransaction accountTransaction = accountTransactionService.cashing(money);
        if (accountTransaction != null) {
            transactionLogService.cashing(accountTransaction);
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
        moneyRepository.save(money);
        transferMoney(money);
        return money;
    }

    /**
     * Обновить проводку
     *
     * @param money - существующая проводка
     * @param dto   - DTO объект из 1С
     */
    private void update(Money money, InvestorCashDTO dto) {
        prepareMoney(money, dto);
        updateTransaction(money);
        moneyRepository.save(money);
    }

    /**
     * Удалить сумму
     *
     * @param money сумма для удаления
     */
    private void delete(Money money) {
        if (money != null) {
            List<TransactionLog> logs = transactionLogService.findByCash(money);
            List<Money> monies = new ArrayList<>();
            if (money.getTransaction() != null) {
                AccountTransaction parentTx = accountTransactionService.findByParent(money.getTransaction());
                if (parentTx != null) {
                    monies.addAll(parentTx.getMonies());
                    accountTransactionService.delete(parentTx);
                }
                accountTransactionService.delete(money.getTransaction());
            }
            transactionLogService.delete(logs);
            moneyRepository.deleteByTransactionUUID(money.getTransactionUUID());
            moneyRepository.deleteAll(monies);
        }
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
            UnderFacility underFacility = findUnderFacility(facility);
            money.setUnderFacility(underFacility);
        }
        money.setGivenCash(dto.getGivenCash());
        money.setDateGiven(dto.getDateGiven());
        money.setTransactionUUID(dto.getTransactionUUID());
    }

    /**
     * Поготовить к обновлению транзакцию
     *
     * @param accountTransaction существующая транзакция по счёту
     * @param dto                DTO объект из 1С
     */
    private void prepareAccountTransaction(AccountTransaction accountTransaction, InvestorCashDTO dto) {
        accountTransaction.setCash(dto.getGivenCash());
        accountTransaction.setTxDate(DateUtils.convert(dto.getDateGiven()));
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

    /**
     * Найти инвестора по коду, который приходит из 1С
     *
     * @param investorCode - код инвестора
     * @return - найденный инвестор
     */
    private Investor findInvestor(String investorCode) {
        String login = Constant.INVESTOR_PREFIX.concat(investorCode);
        return investorService.findByLogin(login);
    }

    /**
     * Найти объект по имени, который приходит из 1С
     *
     * @param fullName - название объекта
     * @return - найденный объект
     */
    private Facility findFacility(String fullName) {
        return facilityService.findByFullName(fullName);
    }

    /**
     * Найти подобъект по имени с суффиксом "_Целиком"
     *
     * @param facility - объект
     * @return - найденный подобъект
     */
    private UnderFacility findUnderFacility(Facility facility) {
        if (facility != null) {
            String fullName = facility.getName().concat(Constant.UNDER_FACILITY_SUFFIX);
            return underFacilityService.findByName(fullName);
        }
        return null;
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

    /**
     * Проверить подходит ли сумма для внесения на сервер.
     * Временный фильтр. Если дата передачи денег после 30.06.2020 и переданная сумма положительная
     *
     * @param dto DTO денег
     * @return результат проверки
     */
    private boolean checkCash(InvestorCashDTO dto) {
        return dto.getDateGiven().isAfter(FILTERED_DATE);
    }

    /**
     * Переместить деньги по счетам
     *
     * @param money сумма для транзакции
     */
    private void transferMoney(Money money) {
        accountTransactionService.transfer(money);
    }

    /**
     * Обновить сумму транзакции
     *
     * @param money сумма для обновления
     */
    private void updateTransaction(Money money) {
        accountTransactionService.updateTransaction(money);
    }

}
