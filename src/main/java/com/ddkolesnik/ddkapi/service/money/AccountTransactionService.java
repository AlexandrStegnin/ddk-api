package com.ddkolesnik.ddkapi.service.money;

import com.ddkolesnik.ddkapi.configuration.exception.ApiException;
import com.ddkolesnik.ddkapi.model.app.Account;
import com.ddkolesnik.ddkapi.model.cash.UserAgreement;
import com.ddkolesnik.ddkapi.model.log.CashType;
import com.ddkolesnik.ddkapi.model.money.AccountTransaction;
import com.ddkolesnik.ddkapi.model.money.Money;
import com.ddkolesnik.ddkapi.repository.money.AccountTransactionRepository;
import com.ddkolesnik.ddkapi.repository.money.MoneyRepository;
import com.ddkolesnik.ddkapi.service.app.AccountService;
import com.ddkolesnik.ddkapi.service.cash.UserAgreementService;
import com.ddkolesnik.ddkapi.util.ConcludedWith;
import com.ddkolesnik.ddkapi.util.OperationType;
import com.ddkolesnik.ddkapi.util.OwnerType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static com.ddkolesnik.ddkapi.util.Constant.COMMISSION_RATE;
import static com.ddkolesnik.ddkapi.util.Constant.DDK_USER_ID;

/**
 * @author Alexandr Stegnin
 */

@Slf4j
@Service
public class AccountTransactionService {

    private final AccountService accountService;

    private final AccountTransactionRepository accountTransactionRepository;

    private final MoneyRepository moneyRepository;

    private final UserAgreementService userAgreementService;

    public AccountTransactionService(AccountService accountService, AccountTransactionRepository accountTransactionRepository,
                                     MoneyRepository moneyRepository, UserAgreementService userAgreementService) {
        this.accountService = accountService;
        this.accountTransactionRepository = accountTransactionRepository;
        this.moneyRepository = moneyRepository;
        this.userAgreementService = userAgreementService;
    }

    /**
     * Удалить транзакцию
     *
     * @param transaction транзакция
     */
    public void delete(AccountTransaction transaction) {
        accountTransactionRepository.delete(transaction);
    }

    /**
     * Переместить суммы на счета клиентов
     *
     * @param money сумма для трансфера суммы
     */
    public void transfer(Money money) {
        Account owner = findByOwnerId(money.getInvestor().getId(), OwnerType.INVESTOR);
        try {
            AccountTransaction investorDebitTx = createInvestorDebitTransaction(owner, money);
            AccountTransaction creditTx = createCreditTransaction(owner, money, investorDebitTx);
            createDebitTransaction(creditTx, money);
        } catch (Exception e) {
            log.error("Произошла ошибка: {}", e.getMessage());
        }
    }

    /**
     * Вывести деньги со счёта по данным из 1С
     *
     * @param money сумма для вывода
     */
    public AccountTransaction cashing(Money money) {
        Account owner = findByOwnerId(money.getInvestor().getId(), OwnerType.INVESTOR);
        try {
            AccountTransaction parentTx = createCashingCreditTransaction(owner, money);
            UserAgreement userAgreement = userAgreementService.findByInvestorAndFacility(money.getInvestor(), money.getFacility());
            if (userAgreement == null) {
                throw new ApiException("Не найдена информация \"С кем заключён договор\"", HttpStatus.NOT_FOUND);
            }
            ConcludedWith concludedWith = ConcludedWith.fromTitle(userAgreement.getConcludedWith());
            if (concludedWith == ConcludedWith.NATURAL_PERSON) {
                Money commission = new Money(money, COMMISSION_RATE);
                return createCommissionCreditTransaction(money, commission, parentTx);
            }
        } catch (Exception e) {
            log.error("Произошла ошибка: " + e.getLocalizedMessage());
            return null;
        }
        return null;
    }

    /**
     * Создать приходную транзакцию инвестору на счёт
     *
     * @param owner владелец счёта
     * @param money сумма
     * @return созданную транзакцию
     */
    private AccountTransaction createInvestorDebitTransaction(Account owner, Money money) {
        AccountTransaction debitTx = new AccountTransaction(owner);
        debitTx.setOperationType(OperationType.DEBIT);
        debitTx.setPayer(owner);
        debitTx.setRecipient(owner);
        debitTx.getMonies().add(money);
        debitTx.setCashType(CashType.CASH_1C);
        debitTx.setCash(money.getGivenCash());
        debitTx.setTxDate(convertDate(money.getDateGiven()));
        return accountTransactionRepository.save(debitTx);
    }

    /**
     * Создать приходную транзакцию
     *
     * @param creditTx расходная транзакция
     * @param money    сумма
     */
    private void createDebitTransaction(AccountTransaction creditTx, Money money) {
        Account recipient = findByOwnerId(money.getUnderFacility().getId(), OwnerType.UNDER_FACILITY);
        AccountTransaction debitTx = new AccountTransaction(recipient);
        debitTx.setOperationType(OperationType.DEBIT);
        debitTx.setPayer(creditTx.getOwner());
        debitTx.setRecipient(recipient);
        debitTx.getMonies().add(money);
        debitTx.setCashType(CashType.CASH_1C);
        debitTx.setCash(money.getGivenCash());
        debitTx.setTxDate(convertDate(money.getDateGiven()));
        debitTx.setParent(creditTx);
        accountTransactionRepository.save(debitTx);
    }

    /**
     * Создать расходную транзакцию по счёту
     *
     * @param owner    владелец
     * @param money    сумма
     * @param parentTx родительская транзакция
     */
    private AccountTransaction createCreditTransaction(Account owner, Money money, AccountTransaction parentTx) {
        BigDecimal givenCash = money.getGivenCash().negate();
        CashType cashType = CashType.CASH_1C;
        Account recipient = findByOwnerId(money.getFacility().getId(), OwnerType.FACILITY);
        AccountTransaction creditTx = new AccountTransaction(owner);
        creditTx.setParent(parentTx);
        creditTx.setTxDate(convertDate(money.getDateGiven()));
        creditTx.setOperationType(OperationType.CREDIT);
        creditTx.setPayer(owner);
        creditTx.setRecipient(recipient);
        creditTx.getMonies().add(money);
        creditTx.setCashType(cashType);
        creditTx.setCash(givenCash);
        money.setTransaction(creditTx);
        moneyRepository.save(money);
        return accountTransactionRepository.save(creditTx);
    }

    /**
     * Создать расходную операцию при выводе средств
     *
     * @param owner    владелец счёта
     * @param money    сумма к выводу
     * @return созданная транзакция
     */
    private AccountTransaction createCashingCreditTransaction(Account owner, Money money) {
        BigDecimal givenCash = money.getGivenCash();
        CashType cashType = CashType.CASH_1C_CASHING;
        Account payer = findByOwnerId(DDK_USER_ID, OwnerType.INVESTOR);
        AccountTransaction creditTx = new AccountTransaction(owner);
        creditTx.setTxDate(convertDate(money.getDateGiven()));
        creditTx.setOperationType(OperationType.CREDIT);
        creditTx.setPayer(payer);
        creditTx.setRecipient(owner);
        creditTx.setCashType(cashType);
        creditTx.setCash(givenCash);
        return accountTransactionRepository.save(creditTx);
    }

    /**
     * Создать расходную транзакцию по счёту на сумму комиссии
     *
     * @param money      сумма
     * @param commission сумма комиссии
     */
    public AccountTransaction createCommissionCreditTransaction(Money money, Money commission, AccountTransaction parentTx) {
        Account owner = findByOwnerId(money.getInvestor().getId(), OwnerType.INVESTOR);
        Account payer = findByOwnerId(DDK_USER_ID, OwnerType.INVESTOR);
        AccountTransaction creditTx = new AccountTransaction(owner);
        creditTx.setTxDate(convertDate(money.getDateGiven()));
        creditTx.setOperationType(OperationType.CREDIT);
        creditTx.setPayer(payer);
        creditTx.setRecipient(owner);
        creditTx.setCashType(CashType.CASH_1C_COMMISSION);
        creditTx.setCash(commission.getGivenCash());
        creditTx.setParent(parentTx);
        return accountTransactionRepository.save(creditTx);
    }

    /**
     * Обновить сумму транзакции
     *
     * @param money   сумма для обновления
     * @param cashing признак вывода денег
     */
    public void updateTransaction(Money money, boolean cashing) {
        AccountTransaction transaction = money.getTransaction();
        if (transaction != null) {
            switch (transaction.getOperationType()) {
                case DEBIT:
                    transaction.setCash(money.getGivenCash());
                    break;
                case CREDIT:
                    if (cashing) {
                        transaction.setCash(money.getGivenCash());
                    } else {
                        transaction.setCash(money.getGivenCash().negate());
                    }
            }
        }
    }

    public AccountTransaction findByParent(AccountTransaction parentTx) {
        return accountTransactionRepository.findByParentId(parentTx.getId());
    }

    /**
     * Конвертировать LocalDate в java.util.Date
     *
     * @param date дата
     * @return конвертированная дата
     */
    private Date convertDate(LocalDate date) {
        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Найти счёт по id и типу владельца
     *
     * @param ownerId   id владельца
     * @param ownerType тип владельца
     * @return найденный счёт
     */
    private Account findByOwnerId(Long ownerId, OwnerType ownerType) {
        Account account = accountService.findByOwnerId(ownerId, ownerType);
        if (account == null) {
            String msg = String.format("Не найден счёт [%s %d]", ownerType.getTitle(), ownerId);
            log.error(msg);
            throw new ApiException(msg, HttpStatus.NOT_FOUND);
        }
        return account;
    }

}
