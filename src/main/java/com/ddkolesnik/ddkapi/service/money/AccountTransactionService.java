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
import com.ddkolesnik.ddkapi.util.ConcludedFrom;
import com.ddkolesnik.ddkapi.util.OperationType;
import com.ddkolesnik.ddkapi.util.OwnerType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

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
        Account owner = accountService.findByOwnerId(money.getInvestor().getId(), OwnerType.INVESTOR);
        if (owner == null) {
            log.error("Не найден счёт пользователя");
            return;
        }
        try {
            createInvestorDebitTransaction(owner, money);
            AccountTransaction creditTx = createCreditTransaction(owner, money, false);
            createDebitTransaction(creditTx, money);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
        }
    }

    /**
     * Вывести деньги со счёта по данным из 1С
     *
     * @param money сумма для вывода
     */
    public Money cashing(Money money) {
        Account owner = accountService.findByOwnerId(money.getInvestor().getId(), OwnerType.INVESTOR);
        if (owner == null) {
            log.error("Не найден счёт пользователя");
            return null;
        }
        try {
            AccountTransaction creditTx = createCreditTransaction(owner, money, true);
            UserAgreement userAgreement = userAgreementService.findByInvestorAndFacility(money.getInvestor(), money.getFacility());
            if (userAgreement == null) {
                throw new ApiException("Не найдена информация \"С кем заключён договор\"", HttpStatus.NOT_FOUND);
            }
            ConcludedFrom concludedFrom = ConcludedFrom.fromTitle(userAgreement.getConcludedFrom());
            if (concludedFrom == ConcludedFrom.NATURAL_PERSON) {
                Money commission = new Money(money, 0.01);
                createCommissionCreditTransaction(money, commission);
                return commission;
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
     */
    private void createInvestorDebitTransaction(Account owner, Money money) {
        AccountTransaction debitTx = new AccountTransaction(owner);
        debitTx.setOperationType(OperationType.DEBIT);
        debitTx.setPayer(owner);
        debitTx.setRecipient(owner);
        debitTx.getMonies().add(money);
        debitTx.setCashType(CashType.CASH_1C);
        debitTx.setCash(money.getGivenCash());
        accountTransactionRepository.save(debitTx);
    }

    /**
     * Создать приходную транзакцию
     *
     * @param creditTx расходная транзакция
     * @param money    сумма
     */
    private void createDebitTransaction(AccountTransaction creditTx, Money money) {
        Account recipient = accountService.findByOwnerId(money.getUnderFacility().getId(), OwnerType.UNDER_FACILITY);
        AccountTransaction debitTx = new AccountTransaction(recipient);
        debitTx.setOperationType(OperationType.DEBIT);
        debitTx.setPayer(creditTx.getOwner());
        debitTx.setRecipient(recipient);
        debitTx.getMonies().add(money);
        debitTx.setCashType(CashType.CASH_1C);
        debitTx.setCash(money.getGivenCash());
        accountTransactionRepository.save(debitTx);
    }

    /**
     * Создать расходную транзакцию по счёту
     *
     * @param owner   владелец
     * @param money   сумма
     * @param cashing признак вывода суммы
     */
    private AccountTransaction createCreditTransaction(Account owner, Money money, boolean cashing) {
        BigDecimal givenCash = money.getGivenCash().negate();
        if (cashing) {
            money.setGivenCash(givenCash);
        }
        Account recipient = accountService.findByOwnerId(money.getFacility().getId(), OwnerType.FACILITY);
        AccountTransaction creditTx = new AccountTransaction(owner);
        creditTx.setOperationType(OperationType.CREDIT);
        creditTx.setPayer(owner);
        creditTx.setRecipient(recipient);
        creditTx.getMonies().add(money);
        creditTx.setCashType(CashType.CASH_1C);
        creditTx.setCash(givenCash);
        money.setTransaction(creditTx);
        moneyRepository.save(money);
        return accountTransactionRepository.save(creditTx);
    }

    /**
     * Создать расходную транзакцию по счёту на сумму комиссии
     *
     * @param money      сумма
     * @param commission сумма комиссии
     */
    public void createCommissionCreditTransaction(Money money, Money commission) {
        Account owner = accountService.findByOwnerId(money.getInvestor().getId(), OwnerType.INVESTOR);
        AccountTransaction creditTx = new AccountTransaction(owner);
        creditTx.setOperationType(OperationType.CREDIT);
        creditTx.setPayer(owner);
        creditTx.setRecipient(owner);
        creditTx.getMonies().add(money);
        creditTx.getMonies().add(commission);
        creditTx.setCashType(CashType.CASH_1C_COMMISSION);
        creditTx.setCash(commission.getGivenCash());
        commission.setTransaction(creditTx);
        moneyRepository.save(commission);
        accountTransactionRepository.save(creditTx);
    }

    /**
     * Обновить сумму транзакции
     *
     * @param money сумма для обновления
     */
    public void updateTransaction(Money money) {
        AccountTransaction transaction = money.getTransaction();
        if (transaction != null) {
            switch (transaction.getOperationType()) {
                case DEBIT:
                    transaction.setCash(money.getGivenCash());
                    break;
                case CREDIT:
                    transaction.setCash(money.getGivenCash().negate());
            }
        }
    }

}
