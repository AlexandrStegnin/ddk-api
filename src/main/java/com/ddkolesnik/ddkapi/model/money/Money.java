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
@Table(name = "InvestorsCash")
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString(exclude = "investor")
public class Money {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "GivedCash")
    BigDecimal givenCash;

    @OneToOne
    @JoinColumn(name = "FacilityId")
    Facility facility;

    @Column(name = "DateGivedCash")
    LocalDate dateGiven;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "InvestorId")
    Investor investor;

    @Column(name = "DateClosingInvest")
    LocalDate dateClosing;

    @Column(name = "transaction_uuid")
    String transactionUUID;

    @OneToOne
    @JoinColumn(name = "CashSourceId")
    CashSource cashSource;

    @OneToOne
    @JoinColumn(name = "UnderFacilityId")
    UnderFacility underFacility;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "ShareType")
    ShareType shareType;

}
