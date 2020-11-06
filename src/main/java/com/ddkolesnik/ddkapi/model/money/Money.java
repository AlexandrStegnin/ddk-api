package com.ddkolesnik.ddkapi.model.money;

import com.ddkolesnik.ddkapi.model.cash.CashSource;
import com.ddkolesnik.ddkapi.util.ShareType;
import lombok.AccessLevel;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author Alexandr Stegnin
 */

@Data
@Entity
@Table(name = "money")
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString(exclude = "investor")
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
}
