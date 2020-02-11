package com.ddkolesnik.ddkapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * @author Alexandr Stegnin
 */

@Data
@Entity
@Table(name = "InvestorsCash")
@JsonIgnoreProperties({"id"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Money {

    @Id
    @Column(name = "id", insertable = false, updatable = false)
    Long id;

    @Column(name = "givedCash")
    BigDecimal givenCash;

    @Column(name = "facilityId")
    Long facilityId;

    @Column(name = "dateGivedCash")
    LocalDate dateGiven;

    public BigDecimal getGivenCash() {
        return givenCash.setScale(2, RoundingMode.HALF_UP);
    }

}
