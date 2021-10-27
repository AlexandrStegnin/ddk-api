package com.ddkolesnik.ddkapi.model.log;

import com.ddkolesnik.ddkapi.model.cash.CashSource;
import com.ddkolesnik.ddkapi.model.money.Facility;
import com.ddkolesnik.ddkapi.model.money.Investor;
import com.ddkolesnik.ddkapi.model.money.Money;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author Alexandr Stegnin
 */

@Data
@Entity
@NoArgsConstructor
@Table(name = "investor_cash_log")
public class InvestorCashLog {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "investor_cash_log_generator")
  @SequenceGenerator(name = "investor_cash_log_generator", sequenceName = "investor_cash_log_id_seq")
  @Column(name = "id")
  private Long id;

  @Column(name = "cash_id")
  private Long cashId;

  @OneToOne
  @JoinColumn(name = "investor_id")
  private Investor investor;

  @OneToOne
  @JoinColumn(name = "facility_id")
  private Facility facility;

  @Column(name = "given_cash")
  private BigDecimal givenCash;

  @Column(name = "date_given_cash")
  private LocalDate dateGiven;

  @OneToOne
  @JoinColumn(name = "cash_source_id")
  private CashSource cashSource;

  @Column(name = "date_closing_invest")
  private LocalDate dateClosing;

  @OneToOne
  @JoinColumn(name = "tx_id")
  private TransactionLog transactionLog;

  @Enumerated
  @Column(name = "instance_of")
  private CashType instanceOf;

  public InvestorCashLog(Money cash, TransactionLog log) {
    this.cashId = cash.getId();
    this.investor = cash.getInvestor();
    this.facility = cash.getFacility();
    this.givenCash = cash.getGivenCash();
    this.dateGiven = cash.getDateGiven();
    this.cashSource = cash.getCashSource();
    this.dateClosing = cash.getDateClosing();
    this.transactionLog = log;
    this.instanceOf = CashType.CASH_1C;
  }

}
