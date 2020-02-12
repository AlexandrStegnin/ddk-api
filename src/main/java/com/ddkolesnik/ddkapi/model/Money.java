package com.ddkolesnik.ddkapi.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author Alexandr Stegnin
 */

@Data
@Entity
@Table(name = "InvestorsCash", schema = "pss_projects", catalog = "pss_projects")
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
public class Money extends AbstractEntity {

    @Column(name = "givedCash")
    BigDecimal givenCash;

    @OneToOne
    @JoinColumn(name = "facilityId")
    Facility facility;

    @Column(name = "dateGivedCash")
    LocalDate dateGiven;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "investorId")
    Investor investor;

    @Column(name = "dateClosingInvest")
    LocalDate dateClosing;

}
