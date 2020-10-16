package com.ddkolesnik.ddkapi.service.money;

import com.ddkolesnik.ddkapi.model.app.Account;
import com.ddkolesnik.ddkapi.model.log.CashType;
import com.ddkolesnik.ddkapi.model.money.AccountTransaction;
import com.ddkolesnik.ddkapi.model.money.Money;
import com.ddkolesnik.ddkapi.repository.money.AccountTransactionRepository;
import com.ddkolesnik.ddkapi.service.app.AccountService;
import com.ddkolesnik.ddkapi.util.OperationType;
import com.ddkolesnik.ddkapi.util.OwnerType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Alexandr Stegnin
 */

@Slf4j
@Service
public class AccountTransactionService {

    private final AccountService accountService;

    private final AccountTransactionRepository accountTransactionRepository;

    public AccountTransactionService(AccountService accountService, AccountTransactionRepository accountTransactionRepository) {
        this.accountService = accountService;
        this.accountTransactionRepository = accountTransactionRepository;
    }

    /**
     * Найти по id суммы
     *
     * @param moneyId
     * @return
     */
    public List<AccountTransaction> findByMoney(Long moneyId) {
        return accountTransactionRepository.findByMoneyId(moneyId);
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
            AccountTransaction creditTx = createCreditTransaction(owner, money);
            createDebitTransaction(creditTx, money);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
        }
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
        debitTx.setMoney(creditTx.getMoney());
        debitTx.setCashType(CashType.CASH_1C);
        debitTx.setCash(creditTx.getMoney().getGivenCash());
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
        creditTx.setMoney(money);
        creditTx.setCashType(CashType.INVESTOR_CASH);
        creditTx.setCash(money.getGivenCash().negate());
        return accountTransactionRepository.save(creditTx);
    }

    /**
     * Обновить сумму транзакции
     *
     * @param money сумма для обновления
     */
    public void updateTransaction(Money money) {
        List<AccountTransaction> transactions = findByMoney(money.getId());
        transactions.forEach(transaction -> {
            switch (transaction.getOperationType()) {
                case DEBIT:
                    transaction.setCash(money.getGivenCash());
                    break;
                case CREDIT:
                    transaction.setCash(money.getGivenCash().negate());
            }
        });

    }

}
