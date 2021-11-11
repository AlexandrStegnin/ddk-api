package com.ddkolesnik.ddkapi.service;

import com.ddkolesnik.ddkapi.dto.cash.InvestorCashDTO;
import com.ddkolesnik.ddkapi.model.app.Account;
import com.ddkolesnik.ddkapi.model.app.AppUser;
import com.ddkolesnik.ddkapi.model.money.AccountTransaction;
import com.ddkolesnik.ddkapi.model.money.Money;
import com.ddkolesnik.ddkapi.repository.app.AccountRepository;
import com.ddkolesnik.ddkapi.repository.app.AppUserRepository;
import com.ddkolesnik.ddkapi.service.log.TransactionLogService;
import com.ddkolesnik.ddkapi.service.mapper.MoneyMapper;
import com.ddkolesnik.ddkapi.service.money.AccountTransactionService;
import com.ddkolesnik.ddkapi.util.AccountingCode;
import com.ddkolesnik.ddkapi.util.DateUtils;
import com.ddkolesnik.ddkapi.util.OwnerType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;

import static com.ddkolesnik.ddkapi.util.Constant.COMMISSION_RATE;
import static com.ddkolesnik.ddkapi.util.Constant.INVESTOR_PREFIX;

/**
 * @author Alexandr Stegnin
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CashingService {

  AccountTransactionService accountTransactionService;
  TransactionLogService transactionLogService;
  AppUserRepository appUserRepository;
  AccountRepository accountRepository;
  MoneyMapper moneyMapper;

  public void cashing(InvestorCashDTO dto) {
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

  private void createCashingTransaction(InvestorCashDTO dto) {
    Money money = moneyMapper.convert(dto);
    money.setDateClosing(dto.getDateGiven());
    AccountingCode accountingCode = AccountingCode.fromCode(dto.getAccountingCode());
    AccountTransaction accountTransaction = accountTransactionService.cashing(money, accountingCode);
    if (Objects.nonNull(accountTransaction)) {
      transactionLogService.cashing(accountTransaction);
    }
  }

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

}
