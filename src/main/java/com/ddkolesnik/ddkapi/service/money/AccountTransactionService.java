package com.ddkolesnik.ddkapi.service.money;

import com.ddkolesnik.ddkapi.model.app.Account;
import com.ddkolesnik.ddkapi.model.log.CashType;
import com.ddkolesnik.ddkapi.model.money.AccountTransaction;
import com.ddkolesnik.ddkapi.model.money.Money;
import com.ddkolesnik.ddkapi.repository.money.AccountTransactionRepository;
import com.ddkolesnik.ddkapi.repository.money.MoneyRepository;
import com.ddkolesnik.ddkapi.service.app.AccountService;
import com.ddkolesnik.ddkapi.util.OperationType;
import com.ddkolesnik.ddkapi.util.OwnerType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Alexandr Stegnin
 */

@Slf4j
@Service
public class AccountTransactionService {

    private final AccountService accountService;

    private final AccountTransactionRepository accountTransactionRepository;

    private final MoneyRepository moneyRepository;

    public AccountTransactionService(AccountService accountService, AccountTransactionRepository accountTransactionRepository,
                                     MoneyRepository moneyRepository) {
        this.accountService = accountService;
        this.accountTransactionRepository = accountTransactionRepository;
        this.moneyRepository = moneyRepository;
    }

    /**
     * Удалить транзакцию
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
            AccountTransaction creditTx = createCreditTransaction(owner, money);
            createDebitTransaction(creditTx, money);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
        }
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
     * @param money сумма
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
     * @param owner владелец
     * @param money сумма
     */
    private AccountTransaction createCreditTransaction(Account owner, Money money) {
        AccountTransaction creditTx = new AccountTransaction(owner);
        creditTx.setOperationType(OperationType.CREDIT);
        creditTx.setPayer(owner);
        creditTx.setRecipient(owner);
        creditTx.getMonies().add(money);
        creditTx.setCashType(CashType.CASH_1C);
        creditTx.setCash(money.getGivenCash().negate());
        money.setTransaction(creditTx);
        moneyRepository.save(money);
        return accountTransactionRepository.save(creditTx);
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
