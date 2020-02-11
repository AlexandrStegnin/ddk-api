package com.ddkolesnik.ddkapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import static com.ddkolesnik.ddkapi.util.Constants.UNKNOWN_FACILITY;

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
    Long id;

    @Column(name = "givedCash")
    BigDecimal givenCash;

    @OneToOne
    @JoinColumn(name = "facilityId")
    Facility facility;

    @Column(name = "dateGivedCash")
    LocalDate dateGiven;

    public BigDecimal getGivenCash() {
        return givenCash.setScale(2, RoundingMode.HALF_UP);
    }

    public String getFacility() {
        return facility != null ? facility.getName() : UNKNOWN_FACILITY;
    }
}
