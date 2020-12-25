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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private String state = "MATCHING";

    @ManyToOne
    @JoinColumn(name = "acc_tx_id")
    private AccountTransaction transaction;

    public Money(Money old, Double taxRate) {
        this.id = null;
        this.givenCash = old.getGivenCash().multiply(BigDecimal.valueOf((taxRate)));
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

}
