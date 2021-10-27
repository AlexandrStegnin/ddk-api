package com.ddkolesnik.ddkapi.model.money;

import com.ddkolesnik.ddkapi.model.cash.CashSource;
import com.ddkolesnik.ddkapi.util.ShareType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * @author Alexandr Stegnin
 */

@Data
@Entity
@NoArgsConstructor
@Table(name = "money")
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString(exclude = {"investor", "transaction"})
@EqualsAndHashCode(exclude = "transaction")
public class Money {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "money_generator")
  @SequenceGenerator(name = "money_generator", sequenceName = "money_id_seq")
  Long id;

  @Column(name = "given_cash")
  BigDecimal givenCash;

  @OneToOne
  @JoinColumn(name = "facility_id")
  Facility facility;

  @Column(name = "date_given")
  LocalDate dateGiven;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "investor_id")
  Investor investor;

  @Column(name = "date_closing")
  LocalDate dateClosing;

  @Column(name = "transaction_uuid")
  String transactionUUID;

  @OneToOne
  @JoinColumn(name = "cash_source_id")
  CashSource cashSource;

  @OneToOne
  @JoinColumn(name = "under_facility_id")
  UnderFacility underFacility;

  @Enumerated(EnumType.ORDINAL)
  @Column(name = "share_type")
  ShareType shareType;

  @Column(name = "state")
  String state = "MATCHING";

  @ManyToOne
  @JoinColumn(name = "acc_tx_id")
  AccountTransaction transaction;

  @Column(name = "type_closing_id")
  Long typeClosingId;

  @Column(name = "new_cash_detail_id")
  Long newCashDetailId;

  @Column(name = "real_date_given")
  LocalDate realDateGiven;

  @Column(name = "source_id")
  Long sourceMoneyId;

  @Column(name = "source")
  String source;

  public Money(Money old, BigDecimal taxRate) {
    this.id = null;
    this.givenCash = old.getGivenCash().multiply(taxRate);
    this.facility = old.getFacility();
    this.dateGiven = old.getDateGiven();
    this.investor = old.getInvestor();
    this.dateClosing = old.getDateClosing();
    this.transactionUUID = UUID.randomUUID().toString();
    this.cashSource = old.getCashSource();
    this.underFacility = old.getUnderFacility();
    this.shareType = old.getShareType();
    this.state = old.getState();
    this.transaction = old.getTransaction();
  }

  public Money(Money old, Investor buyer, Long newCashDetailId, LocalDate realDateGiven,
               String transactionUUID, CashSource cashSource) {
    this.givenCash = old.getGivenCash();
    this.facility = old.getFacility();
    this.dateGiven = old.getDateGiven();
    this.transactionUUID = transactionUUID;
    this.cashSource = cashSource;
    this.underFacility = old.getUnderFacility();
    this.shareType = old.getShareType();
    this.transaction = old.getTransaction();
    this.investor = buyer;
    this.newCashDetailId = newCashDetailId;
    this.realDateGiven = realDateGiven;
    this.sourceMoneyId = old.getId();
    this.source = old.getId().toString();
  }

  public Money(Money old) {
    this.id = null;
    this.givenCash = null;
    this.facility = old.getFacility();
    this.dateGiven = old.getDateGiven();
    this.investor = old.getInvestor();
    this.dateClosing = old.getDateClosing();
    this.transactionUUID = UUID.randomUUID().toString();
    this.cashSource = old.getCashSource();
    this.underFacility = old.getUnderFacility();
    this.shareType = old.getShareType();
    this.state = old.getState();
    this.transaction = old.getTransaction();
    this.investor = old.getInvestor();
    this.newCashDetailId = old.getNewCashDetailId();
    this.realDateGiven = old.getRealDateGiven();
    this.sourceMoneyId = old.getId();
    this.source = old.getId().toString();
    this.typeClosingId = old.getTypeClosingId();
  }

}
