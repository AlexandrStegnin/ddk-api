package com.ddkolesnik.ddkapi.service.cash;

import com.ddkolesnik.ddkapi.configuration.exception.ApiException;
import com.ddkolesnik.ddkapi.dto.cash.InvestorCashDTO;
import com.ddkolesnik.ddkapi.model.app.Account;
import com.ddkolesnik.ddkapi.model.app.AppUser;
import com.ddkolesnik.ddkapi.model.cash.CashSource;
import com.ddkolesnik.ddkapi.model.log.CashType;
import com.ddkolesnik.ddkapi.model.log.TransactionLog;
import com.ddkolesnik.ddkapi.model.money.*;
import com.ddkolesnik.ddkapi.repository.app.AccountRepository;
import com.ddkolesnik.ddkapi.repository.app.AppUserRepository;
import com.ddkolesnik.ddkapi.repository.money.MoneyRepository;
import com.ddkolesnik.ddkapi.service.SendMessageService;
import com.ddkolesnik.ddkapi.service.log.TransactionLogService;
import com.ddkolesnik.ddkapi.service.money.AccountTransactionService;
import com.ddkolesnik.ddkapi.service.money.FacilityService;
import com.ddkolesnik.ddkapi.service.money.InvestorService;
import com.ddkolesnik.ddkapi.service.money.UnderFacilityService;
import com.ddkolesnik.ddkapi.util.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.ddkolesnik.ddkapi.util.Constant.COMMISSION_RATE;
import static com.ddkolesnik.ddkapi.util.Constant.INVESTOR_PREFIX;

/**
 * Сервис для работы с проводками из 1С
 *
 * @author Alexandr Stegnin
 */

@Slf4j
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

    AppUserRepository appUserRepository;

    AccountRepository accountRepository;

    /**
     * Создать или обновить проводку, пришедшую из 1С
     *
     * @param dto - DTO объект из 1С
     */
    public void update(InvestorCashDTO dto) {
        if (checkCash(dto)) {
            AccountingCode code = AccountingCode.fromCode(dto.getAccountingCode());
            Money money = moneyRepository.findByTransactionUUID(dto.getTransactionUUID());
            if (dto.isDelete() && Objects.nonNull(money) && Objects.isNull(code)) {
                delete(money);
            } else {
                if (AccountingCode.isResale(code)) {
                    resaleShare(dto);
                } else if (AccountingCode.isCashing(code)) {
                    cashing(dto);
                } else {
                    if (Objects.isNull(money)) {
                        money = moneyRepository.findMoney(dto.getDateGiven(), dto.getGivenCash(), dto.getFacility(),
                                dto.getCashSource(), Constant.INVESTOR_PREFIX.concat(dto.getInvestorCode()));
                        if (Objects.isNull(money)) {
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
            if (Objects.isNull(accountTransaction)) {
                createCashingTransaction(dto);
            } else {
                updateCashingTransaction(accountTransaction, dto);
            }
        }
    }

    /**
     * Провести перепродажу долю по данным из 1С
     *
     * @param dto DTO для перепродажи
     */
    private void resaleShare(InvestorCashDTO dto) {
        if (dto.isDelete()) {
            deleteResale(dto);
        } else {
            Money money = moneyRepository.findByTransactionUUID(dto.getTransactionUUID());
            if (Objects.nonNull(money)) {
                throw new ApiException("Обновление проводки перепродажи доли не предусмотрено", HttpStatus.BAD_REQUEST);
            } else {
                createResaleShare(dto);
            }
        }
    }

    /**
     * Создать проводку по перепродаже доли
     *
     * @param dto DTO суммы для перепродажи
     */
    private void createResaleShare(InvestorCashDTO dto) {
        BigDecimal fromCash = dto.getGivenCash().subtract(BigDecimal.valueOf(0.5));
        BigDecimal toCash = dto.getGivenCash().add(BigDecimal.valueOf(0.5));
        String investorSellerCode = dto.getInvestorSellerCode();
        if (Objects.isNull(investorSellerCode)) {
            throw new ApiException("Не указан код инвестора продавца", HttpStatus.PRECONDITION_FAILED);
        }
        String login = INVESTOR_PREFIX.concat(investorSellerCode);
        AppUser buyer = appUserRepository.findByLogin(INVESTOR_PREFIX.concat(dto.getInvestorCode()));
        if (Objects.isNull(buyer)) {
            throw new ApiException("Не найден инвестор покупатель.", HttpStatus.NOT_FOUND);
        }
        Money openedMoney = moneyRepository.findMoneyAround(fromCash, toCash, dto.getFacility(), login);
        if (Objects.nonNull(openedMoney)) {
            Investor investor = investorService.findByLogin(buyer.getLogin());
            CashSource cashSource = findCashSource(dto.getCashSource());
            Money buyMoney = new Money(openedMoney, investor, 4L, dto.getDateGiven(),
                    dto.getTransactionUUID(), cashSource);
            createResaleTransaction(dto, buyMoney, buyer);
            openedMoney.setTypeClosingId(9L);
            openedMoney.setDateClosing(dto.getDateGiven());
            moneyRepository.save(openedMoney);
        } else {
            log.error("Недостаточно денег для перепродажи: {}", dto);
            throw new ApiException("Недостаточно денег для перепродажи", HttpStatus.PRECONDITION_FAILED);
        }
    }

    /**
     * Создать транзакции по счетам клиентов
     *  @param dto DTO суммы для перепродажи
     * @param buyMoney сумма у инвестора покупателя
     * @param buyer инвестор покупатель
     */
    private void createResaleTransaction(InvestorCashDTO dto, Money buyMoney, AppUser buyer) {
        Account owner = accountRepository.findByOwnerIdAndOwnerType(buyer.getId(), OwnerType.INVESTOR);
        if (Objects.isNull(owner)) {
            throw new ApiException("Не найден счёт инвестора покупателя", HttpStatus.NOT_FOUND);
        }
        AccountTransaction debitTx = accountTransactionService.createInvestorDebitTransaction(owner, buyMoney,
                CashType.RE_BUY_SHARE, dto.getAccountingCode());
        accountTransactionService.createCreditTransaction(owner, buyMoney, debitTx, CashType.RE_BUY_SHARE);
    }

    /**
     * Удалить проводку перепродажи, пришедшую через 1С
     *
     * @param dto DTO для удаления
     */
    private void deleteResale(InvestorCashDTO dto) {
        Money money = moneyRepository.findByTransactionUUID(dto.getTransactionUUID());
        Money relatedMoney;
        if (Objects.isNull(money)) {
            throw new ApiException("Не найдена сумма для удаления", HttpStatus.NOT_FOUND);
        }
        AccountTransaction transaction = money.getTransaction();
        if (Objects.isNull(transaction)) {
            throw new ApiException("Не найдена транзакция по перепродаже доли", HttpStatus.NOT_FOUND);
        }
        AccountTransaction parentTx = transaction.getParent();
        accountTransactionService.delete(transaction);
        if (Objects.nonNull(parentTx)) {
            accountTransactionService.delete(parentTx);
        }
        Long sourceMoneyId = money.getSourceMoneyId();
        if (Objects.nonNull(sourceMoneyId)) {
            relatedMoney = moneyRepository.findById(sourceMoneyId).orElse(null);
            if (Objects.nonNull(relatedMoney)) {
                relatedMoney.setTypeClosingId(null);
                relatedMoney.setDateClosing(null);
                moneyRepository.save(relatedMoney);
            }
        }
        moneyRepository.delete(money);
    }

    /**
     * Обновить транзакцию по выводу средств
     *
     * @param accountTransaction транзакция по счёту
     * @param dto                DTO из 1С
     */
    private void updateCashingTransaction(AccountTransaction accountTransaction, InvestorCashDTO dto) {
        BigDecimal cash = dto.getGivenCash();
        transactionLogService.update(accountTransaction);
        prepareAccountTransaction(accountTransaction, dto);
        AccountingCode accountingCode = AccountingCode.fromCode(dto.getAccountingCode());
        Set<AccountTransaction> child = accountTransaction.getChild();
        child.forEach(c -> {
            c.setCash(cash.multiply(COMMISSION_RATE));
            c.setAccountingCode(accountingCode.getCode());
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
        AccountingCode accountingCode = AccountingCode.fromCode(dto.getAccountingCode());
        AccountTransaction accountTransaction = accountTransactionService.cashing(money, accountingCode);
        if (Objects.nonNull(accountTransaction)) {
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
        if (Objects.nonNull(money)) {
            List<TransactionLog> logs = transactionLogService.findByCash(money);
            List<Money> monies = new ArrayList<>();
            AccountTransaction transaction = money.getTransaction();
            if (Objects.nonNull(transaction)) {
                AccountTransaction parentTx = accountTransactionService.findByParent(transaction);
                if (Objects.nonNull(parentTx)) {
                    monies.addAll(parentTx.getMonies());
                    accountTransactionService.delete(parentTx);
                }
                accountTransactionService.delete(transaction);
            }
            if (Objects.nonNull(logs)) {
                transactionLogService.delete(logs);
            }
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
        AccountingCode accountingCode = AccountingCode.fromCode(dto.getAccountingCode());
        accountTransaction.setAccountingCode(accountingCode.getCode());
        if (!accountTransaction.getOwner().getOwnerName().equalsIgnoreCase(INVESTOR_PREFIX.concat(dto.getInvestorCode()))) {
            AppUser investor = appUserRepository.findByLogin(INVESTOR_PREFIX.concat(dto.getInvestorCode()));
            Account account = accountRepository.findByOwnerIdAndOwnerType(investor.getId(), OwnerType.INVESTOR);
            accountTransaction.setOwner(account);
        }
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
        if (Objects.nonNull(facility)) {
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
     * Временный фильтр. Если дата передачи денег после 30.06.2020
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
