package com.ddkolesnik.ddkapi.service.money;

import com.ddkolesnik.ddkapi.configuration.exception.ApiException;
import com.ddkolesnik.ddkapi.model.app.Account;
import com.ddkolesnik.ddkapi.model.log.CashType;
import com.ddkolesnik.ddkapi.model.money.AccountTransaction;
import com.ddkolesnik.ddkapi.model.money.Money;
import com.ddkolesnik.ddkapi.repository.money.AccountTransactionRepository;
import com.ddkolesnik.ddkapi.repository.money.MoneyRepository;
import com.ddkolesnik.ddkapi.service.app.AccountService;
import com.ddkolesnik.ddkapi.util.AccountingCode;
import com.ddkolesnik.ddkapi.util.DateUtils;
import com.ddkolesnik.ddkapi.util.OperationType;
import com.ddkolesnik.ddkapi.util.OwnerType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static com.ddkolesnik.ddkapi.util.Constant.DDK_USER_ID;

/**
 * @author Alexandr Stegnin
 */

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AccountTransactionService {

  AccountTransactionRepository accountTransactionRepository;
  MoneyRepository moneyRepository;
  AccountService accountService;

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
      AccountTransaction investorDebitTx = createInvestorDebitTransaction(owner, money, CashType.CASH_1C, null);
      AccountTransaction creditTx = createCreditTransaction(owner, money, investorDebitTx, CashType.CASH_1C);
      createDebitTransaction(creditTx, money);
    } catch (Exception e) {
      log.error("Произошла ошибка: {}", e.getMessage());
    }
  }

  /**
   * Вывести деньги со счёта по данным из 1С
   *
   * @param money          сумма для вывода
   * @param accountingCode статья проводки
   */
  public AccountTransaction cashing(Money money, AccountingCode accountingCode) {
    Account owner = findByOwnerId(money.getInvestor().getId(), OwnerType.INVESTOR);
    try {
      return createCashingCreditTransaction(owner, money, accountingCode);
    } catch (Exception e) {
      log.error("Произошла ошибка: " + e.getLocalizedMessage());
      return null;
    }
  }

  /**
   * Создать приходную транзакцию инвестору на счёт
   *
   * @param owner          владелец счёта
   * @param money          сумма
   * @param accountingCode код проводки
   * @param cashType       вид денег
   * @return созданную транзакцию
   */
  public AccountTransaction createInvestorDebitTransaction(Account owner, Money money,
                                                           CashType cashType, String accountingCode) {
    checkAvailableAccountTransactionByUUID(money.getTransactionUUID());
    AccountTransaction debitTx = new AccountTransaction(owner);
    debitTx.setOperationType(OperationType.DEBIT);
    debitTx.setPayer(owner);
    debitTx.setRecipient(owner);
    debitTx.getMonies().add(money);
    debitTx.setCashType(cashType);
    debitTx.setCash(money.getGivenCash());
    debitTx.setTxDate(DateUtils.convert(money.getDateGiven()));
    debitTx.setTransactionUUID(money.getTransactionUUID());
    debitTx.setAccountingCode(accountingCode);
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
    debitTx.setTxDate(DateUtils.convert(money.getDateGiven()));
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
  public AccountTransaction createCreditTransaction(Account owner, Money money, AccountTransaction parentTx, CashType cashType) {
    BigDecimal givenCash = money.getGivenCash().negate();
    Account recipient = findByOwnerId(money.getFacility().getId(), OwnerType.FACILITY);
    AccountTransaction creditTx = new AccountTransaction(owner);
    creditTx.setParent(parentTx);
    creditTx.setTxDate(DateUtils.convert(money.getDateGiven()));
    creditTx.setOperationType(OperationType.CREDIT);
    creditTx.setPayer(owner);
    creditTx.setRecipient(recipient);
    creditTx.getMonies().add(money);
    creditTx.setCashType(cashType);
    creditTx.setCash(givenCash);
    creditTx.setAccountingCode(parentTx.getAccountingCode());
    money.setTransaction(creditTx);
    moneyRepository.save(money);
    return accountTransactionRepository.save(creditTx);
  }

  /**
   * Создать расходную операцию при выводе средств
   *
   * @param owner          владелец счёта
   * @param money          сумма к выводу
   * @param accountingCode статья проводки
   * @return созданная транзакция
   */
  private AccountTransaction createCashingCreditTransaction(Account owner, Money money, AccountingCode accountingCode) {
    checkAvailableAccountTransactionByUUID(money.getTransactionUUID());
    BigDecimal givenCash = money.getGivenCash();
    CashType cashType = CashType.CASH_1C_CASHING;
    Account payer = findByOwnerId(DDK_USER_ID, OwnerType.INVESTOR);
    AccountTransaction creditTx = new AccountTransaction(owner);
    creditTx.setTxDate(DateUtils.convert(money.getDateGiven()));
    creditTx.setOperationType(OperationType.CREDIT);
    creditTx.setPayer(payer);
    creditTx.setRecipient(owner);
    creditTx.setCashType(cashType);
    creditTx.setCash(givenCash);
    creditTx.setTransactionUUID(money.getTransactionUUID());
    creditTx.setAccountingCode(accountingCode.getCode());
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
        case DEBIT -> transaction.setCash(money.getGivenCash());
        case CREDIT -> {
          transaction.setCash(money.getGivenCash().negate());
          Account recipient = findByOwnerId(money.getFacility().getId(), OwnerType.FACILITY);
          if (isNotSameRecipient(transaction.getRecipient(), recipient)) {
            transaction.setRecipient(recipient);
          }
        }
      }
      if (Objects.nonNull(transaction.getParent())) {
        transaction.getParent().setCash(money.getGivenCash());
      }
    }
  }

  public AccountTransaction findByParent(AccountTransaction parentTx) {
    return accountTransactionRepository.findByParentId(parentTx.getId());
  }

  /**
   * Найти транзакцию по UUID
   *
   * @param uuid UUID транзакции
   * @return найденная транзакция
   */
  public AccountTransaction findByTransactionUUID(String uuid) {
    List<AccountTransaction> accountTransactions = accountTransactionRepository.findByTransactionUUID(uuid);
    if (accountTransactions.size() > 1) {
      log.error("Найдено более 1 транзакции по UUID {}: {}", uuid, accountTransactions);
      throw new ApiException(String.format("Найдено более 1 транзакции по UUID %s", uuid), HttpStatus.BAD_REQUEST);
    } else if (accountTransactions.isEmpty()) {
      return null;
    }
    return accountTransactions.get(0);
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

  /**
   * Обновить транзакцию по счёту
   *
   * @param accountTransaction транзакция для обновления
   * @return обновлённая транзакция
   */
  public AccountTransaction update(AccountTransaction accountTransaction) {
    return accountTransactionRepository.save(accountTransaction);
  }

  private boolean isNotSameRecipient(Account current, Account found) {
    return !current.getId().equals(found.getId());
  }

  private void checkAvailableAccountTransactionByUUID(String uuid) {
//    if (accountTransactionRepository.existsByTransactionUUID(uuid)) {
//      log.error("Транзакция с UUID {} уже существует. Продолжение операции невозможно", uuid);
//      throw new ApiException(
//          String.format("Транзакция с UUID %s уже существует. Продолжение операции невозможно", uuid),
//          HttpStatus.BAD_REQUEST
//      );
//    }
  }

}
