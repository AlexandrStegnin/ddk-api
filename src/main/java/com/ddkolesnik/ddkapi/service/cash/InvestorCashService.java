package com.ddkolesnik.ddkapi.service.cash;

import com.ddkolesnik.ddkapi.configuration.exception.ApiSuccessResponse;
import com.ddkolesnik.ddkapi.dto.cash.InvestorCashDTO;
import com.ddkolesnik.ddkapi.model.cash.CashSource;
import com.ddkolesnik.ddkapi.model.log.TransactionLog;
import com.ddkolesnik.ddkapi.model.money.*;
import com.ddkolesnik.ddkapi.repository.money.MoneyRepository;
import com.ddkolesnik.ddkapi.service.CashingService;
import com.ddkolesnik.ddkapi.service.ResaleShareService;
import com.ddkolesnik.ddkapi.service.SendMessageService;
import com.ddkolesnik.ddkapi.service.log.TransactionLogService;
import com.ddkolesnik.ddkapi.service.mapper.MoneyMapper;
import com.ddkolesnik.ddkapi.service.money.AccountTransactionService;
import com.ddkolesnik.ddkapi.service.money.FacilityService;
import com.ddkolesnik.ddkapi.service.money.InvestorService;
import com.ddkolesnik.ddkapi.service.money.UnderFacilityService;
import com.ddkolesnik.ddkapi.util.AccountingCode;
import com.ddkolesnik.ddkapi.util.Constant;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
  
  private static final String SUCCESSFUL_SAVED = "Данные успешно сохранены";
  private static final LocalDate FILTERED_DATE = LocalDate.of(2020, 6, 30);

  MoneyRepository moneyRepository;
  InvestorService investorService;
  FacilityService facilityService;
  CashSourceService cashSourceService;
  SendMessageService messageService;
  TransactionLogService transactionLogService;
  UnderFacilityService underFacilityService;
  AccountTransactionService accountTransactionService;
  ResaleShareService resaleShareService;
  CashingService cashingService;
  MoneyMapper moneyMapper;

  public ApiSuccessResponse update(InvestorCashDTO dto) {
    if (isBeforeFilteredDate(dto)) {
      return new ApiSuccessResponse(HttpStatus.PRECONDITION_FAILED, "Старая проводка, операция невозможна");
    }
    AccountingCode code = AccountingCode.fromCode(dto.getAccountingCode());
    List<Money> monies = getByTransactionUUID(dto);
    if (isMoneyToDelete(dto, code, monies)) {
      monies.forEach(this::delete);
      return new ApiSuccessResponse(HttpStatus.OK, SUCCESSFUL_SAVED);
    }
    if (AccountingCode.isResale(code)) {
      resaleShareService.resaleShare(dto);
      return new ApiSuccessResponse(HttpStatus.OK, SUCCESSFUL_SAVED);
    }
    if (AccountingCode.isCashing(code)) {
      cashingService.cashing(dto);
      return new ApiSuccessResponse(HttpStatus.OK, SUCCESSFUL_SAVED);
    }
    Money money = monies.isEmpty() ? null : monies.get(0);
    if (Objects.nonNull(money)) {
      transactionLogService.update(money);
      update(money, dto);
      return new ApiSuccessResponse(HttpStatus.OK, SUCCESSFUL_SAVED);
    }
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
    log.info("Проводка успешно обновлена [{}]", dto);
    return new ApiSuccessResponse(HttpStatus.OK, SUCCESSFUL_SAVED);
  }

  private boolean isMoneyToDelete(InvestorCashDTO dto, AccountingCode code, List<Money> monies) {
    return dto.isDelete() && !monies.isEmpty() && Objects.isNull(code);
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
    Money money = moneyMapper.convert(dto);
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

  /**
   * Поготовить к обновлению проводку
   *
   * @param money - существующая проводка
   * @param dto   - DTO объект из 1С
   */
  private void prepareMoney(Money money, InvestorCashDTO dto) {
    if (!money.getFacility().getFullName().equalsIgnoreCase(dto.getFacility())) {
      Facility facility = findFacility(dto.getFacility());
      money.setFacility(facility);
      UnderFacility underFacility = findUnderFacility(facility);
      money.setUnderFacility(underFacility);
    }
    money.setGivenCash(dto.getGivenCash());
    money.setDateGiven(dto.getDateGiven());
    money.setTransactionUUID(dto.getTransactionUUID());
    if (!dto.getCashSource().equalsIgnoreCase(money.getCashSource().getOrganization())) {
      CashSource cashSource = findCashSource(dto.getCashSource());
      money.setCashSource(cashSource);
    }
    if (!dto.getInvestorCode().equalsIgnoreCase(extractInvestorCode(money))) {
      Investor investor = findInvestor(dto.getInvestorCode());
      money.setInvestor(investor);
    }
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
  private boolean isBeforeFilteredDate(InvestorCashDTO dto) {
    boolean isAfterFilteredDate = Objects.nonNull(dto.getDateGiven())
        && dto.getDateGiven().isAfter(FILTERED_DATE);
    if (!isAfterFilteredDate) {
      log.info("Сумма не прошла первичную проверку {}", dto);
    }
    return !isAfterFilteredDate;
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

  private String extractInvestorCode(Money money) {
    return money.getInvestor().getLogin().replaceAll("\\D+", "");
  }

  private List<Money> getByTransactionUUID(InvestorCashDTO dto) {
    return moneyRepository.findByTransactionUUID(dto.getTransactionUUID());
  }

}
