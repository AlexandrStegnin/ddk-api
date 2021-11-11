package com.ddkolesnik.ddkapi.service;

import com.ddkolesnik.ddkapi.configuration.exception.ApiException;
import com.ddkolesnik.ddkapi.dto.cash.InvestorCashDTO;
import com.ddkolesnik.ddkapi.model.app.Account;
import com.ddkolesnik.ddkapi.model.app.AppUser;
import com.ddkolesnik.ddkapi.model.cash.CashSource;
import com.ddkolesnik.ddkapi.model.log.CashType;
import com.ddkolesnik.ddkapi.model.money.AccountTransaction;
import com.ddkolesnik.ddkapi.model.money.Investor;
import com.ddkolesnik.ddkapi.model.money.Money;
import com.ddkolesnik.ddkapi.repository.app.AccountRepository;
import com.ddkolesnik.ddkapi.repository.app.AppUserRepository;
import com.ddkolesnik.ddkapi.repository.money.MoneyRepository;
import com.ddkolesnik.ddkapi.service.cash.CashSourceService;
import com.ddkolesnik.ddkapi.service.money.AccountTransactionService;
import com.ddkolesnik.ddkapi.service.money.InvestorService;
import com.ddkolesnik.ddkapi.util.OwnerType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.ddkolesnik.ddkapi.util.Constant.INVESTOR_PREFIX;

/**
 * @author Alexandr Stegnin
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ResaleShareService {

  MoneyRepository moneyRepository;
  AppUserRepository appUserRepository;
  AccountTransactionService accountTransactionService;
  InvestorService investorService;
  AccountRepository accountRepository;
  CashSourceService cashSourceService;

  public void resaleShare(InvestorCashDTO dto) {
    if (dto.isDelete()) {
      deleteResale(dto);
    } else {
      List<Money> monies = getByTransactionUUID(dto);
      if (!monies.isEmpty()) {
        throw new ApiException("Обновление проводки перепродажи доли не предусмотрено", HttpStatus.BAD_REQUEST);
      } else {
        createResaleShare(dto);
      }
    }
  }

  private void deleteResale(InvestorCashDTO dto) {
    List<Money> monies = getByTransactionUUID(dto);
    if (monies.isEmpty()) {
      throw new ApiException("Не найдена сумма для удаления", HttpStatus.NOT_FOUND);
    }
    for (Money money : monies) {
      deleteAccountTransactions(money);
      Long sourceMoneyId = releaseRelatedMonies(money);
      releaseMoniesBySource(money);
      moneyRepository.delete(money);
      releaseParentAndDeleteSourceMoney(sourceMoneyId);
    }
  }

  private List<Money> getByTransactionUUID(InvestorCashDTO dto) {
    return moneyRepository.findByTransactionUUID(dto.getTransactionUUID());
  }

  private void createResaleShare(InvestorCashDTO dto) {
    String investorSellerCode = dto.getInvestorSellerCode();
    if (Objects.isNull(investorSellerCode)) {
      throw new ApiException("Не указан код инвестора продавца", HttpStatus.PRECONDITION_FAILED);
    }
    String sellerLogin = INVESTOR_PREFIX.concat(investorSellerCode);
    String buyerLogin = INVESTOR_PREFIX.concat(dto.getInvestorCode());
    AppUser buyer = appUserRepository.findByLogin(buyerLogin);
    if (Objects.isNull(buyer)) {
      throw new ApiException("Не найден инвестор покупатель.", HttpStatus.NOT_FOUND);
    }
    String facility = dto.getFacility();
    List<Money> sellerMonies = moneyRepository.getMoniesByInvestorAndFacility(sellerLogin, facility);

    BigDecimal buyerSum = dto.getGivenCash();
    checkSellerMonies(sellerMonies, buyerSum);
    resaleShare(sellerMonies, buyer, dto);
  }

  private void deleteAccountTransactions(Money money) {
    AccountTransaction transaction = money.getTransaction();
    if (Objects.isNull(transaction)) {
      throw new ApiException("Не найдена транзакция по перепродаже доли", HttpStatus.NOT_FOUND);
    }
    AccountTransaction parentTx = transaction.getParent();
    accountTransactionService.delete(transaction);
    if (Objects.nonNull(parentTx)) {
      accountTransactionService.delete(parentTx);
    }
  }

  @Nullable
  private Long releaseRelatedMonies(Money money) {
    Long sourceMoneyId = money.getSourceMoneyId();
    if (Objects.nonNull(sourceMoneyId)) {
      Money relatedMoney = moneyRepository.findById(sourceMoneyId).orElse(null);
      if (Objects.nonNull(relatedMoney)) {
        relatedMoney.setTypeClosingId(null);
        relatedMoney.setDateClosing(null);
        moneyRepository.save(relatedMoney);
      }
    }
    return sourceMoneyId;
  }

  private void releaseMoniesBySource(Money money) {
    String source = money.getSource();
    if (Objects.nonNull(source)) {
      List<Long> ids = Arrays.stream(source.split("\\|"))
          .map(Long::valueOf)
          .collect(Collectors.toList());
      ids.forEach(id -> {
        Money m = moneyRepository.findById(id).orElse(null);
        if (Objects.nonNull(m)) {
          m.setTypeClosingId(null);
          m.setDateClosing(null);
          moneyRepository.save(m);
        }
      });
    }
  }

  private void releaseParentAndDeleteSourceMoney(Long sourceMoneyId) {
    if (Objects.nonNull(sourceMoneyId)) {
      List<Money> sourceMonies = moneyRepository.findBySource(sourceMoneyId.toString());
      if (sourceMonies.size() == 1) {
        Money sourceMoney = sourceMonies.get(0);
        if (Objects.nonNull(sourceMoney.getSourceMoneyId())) {
          Money parentMoney = moneyRepository.findById(sourceMonies.get(0).getSourceMoneyId()).orElse(null);
          if (Objects.nonNull(parentMoney)) {
            parentMoney.setGivenCash(parentMoney.getGivenCash().add(sourceMoney.getGivenCash()));
            moneyRepository.save(parentMoney);
            moneyRepository.delete(sourceMoney);
          }
        }
      }
    }
  }

  private void checkSellerMonies(List<Money> sellerMonies, BigDecimal buyerSum) {
    BigDecimal sellerSum = sellerMonies.stream()
        .map(Money::getGivenCash)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    if (sellerMonies.isEmpty() || sellerSum.compareTo(buyerSum) < 0) {
      String message = String.format("Недостаточно денег для перепродажи. Сумма продавца = %s", sellerSum.longValue());
      log.error(message);
      throw new ApiException(message, HttpStatus.PRECONDITION_FAILED);
    }
  }

  private void resaleShare(List<Money> sellerMonies, AppUser buyer, InvestorCashDTO dto) {
    BigDecimal buyerSum = dto.getGivenCash();
    Money sellerEqualSum = sellerMonies.stream()
        .filter(money -> money.getGivenCash().compareTo(buyerSum) == 0)
        .findAny()
        .orElse(null);

    if (Objects.isNull(sellerEqualSum)) {
      BigDecimal sellerSum = sellerMonies.stream()
          .map(Money::getGivenCash)
          .reduce(BigDecimal.ZERO, BigDecimal::add);

      if (isBuyerSumAroundSellerSum(buyerSum, sellerSum)) {
        for (Money sellerMoney : sellerMonies) {
          closeSellerAndOpenBuyerMonies(sellerMoney, buyer, dto);
        }
      } else if (isBuyerSumLowerThanSellerSum(buyerSum, sellerSum)) {
        Money sellerBiggestSum = sellerMonies.stream().max(Comparator.comparing(Money::getGivenCash))
            .orElseThrow();
        divideMonies(sellerBiggestSum, buyerSum);
        closeSellerAndOpenBuyerMonies(sellerBiggestSum, buyer, dto);
      }
    } else {
      closeSellerAndOpenBuyerMonies(sellerEqualSum, buyer, dto);
    }
  }

  private boolean isBuyerSumAroundSellerSum(BigDecimal buyerSum, BigDecimal sellerSum) {
    BigDecimal inaccuracy = BigDecimal.valueOf(0.5);
    return buyerSum.compareTo(sellerSum.subtract(inaccuracy)) >= 0 &&
        buyerSum.compareTo(sellerSum.add(inaccuracy)) <= 0;
  }

  private boolean isBuyerSumLowerThanSellerSum(BigDecimal buyerSum, BigDecimal sellerSum) {
    return buyerSum.compareTo(sellerSum) < 0;
  }

  private void closeSellerAndOpenBuyerMonies(Money sellerSum, AppUser buyer, InvestorCashDTO dto) {
    Investor investor = investorService.findByLogin(buyer.getLogin());
    CashSource cashSource = cashSourceService.findByOrganization(dto.getCashSource());

    Money buyMoney = new Money(sellerSum, investor, 4L, dto.getDateGiven(),
        dto.getTransactionUUID(), cashSource);

    String source = sellerSum.getId().toString();
    buyMoney.setSource(source);
    createResaleTransaction(dto, buyMoney, buyer);
    sellerSum.setTypeClosingId(9L);
    sellerSum.setDateClosing(dto.getDateGiven());
    moneyRepository.save(sellerSum);
  }

  private void divideMonies(Money sellerBiggestSum, BigDecimal buyerSum) {
    BigDecimal newSellerSum = sellerBiggestSum.getGivenCash().subtract(buyerSum);
    Money newSellerMoney = new Money(sellerBiggestSum);
    newSellerMoney.setGivenCash(newSellerSum);
    newSellerMoney.setSource(sellerBiggestSum.getId().toString());
    sellerBiggestSum.setGivenCash(buyerSum);
    moneyRepository.save(sellerBiggestSum);
    moneyRepository.save(newSellerMoney);
  }

  private void createResaleTransaction(InvestorCashDTO dto, Money buyMoney, AppUser buyer) {
    Account owner = accountRepository.findByOwnerIdAndOwnerType(buyer.getId(), OwnerType.INVESTOR);
    if (Objects.isNull(owner)) {
      throw new ApiException("Не найден счёт инвестора покупателя", HttpStatus.NOT_FOUND);
    }
    AccountTransaction debitTx = accountTransactionService.createInvestorDebitTransaction(owner, buyMoney,
        CashType.RE_BUY_SHARE, dto.getAccountingCode());
    accountTransactionService.createCreditTransaction(owner, buyMoney, debitTx, CashType.RE_BUY_SHARE);
  }

}
