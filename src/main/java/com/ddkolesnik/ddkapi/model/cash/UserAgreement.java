package com.ddkolesnik.ddkapi.model.cash;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

/**
 * Сущность для хранения инфо о том, с кем заключён договор
 *
 * @author Alexandr Stegnin
 */

@Data
@Entity
@Table(name = "user_agreement")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAgreement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    /**
     * ID объекта
     */
    @Column(name = "facility_id")
    Long facilityId;

    /**
     * С кем заключён договор (id инвестора)
     */
    @Column(name = "concluded_with")
    Long concludedWith;

    /**
     * От кого заключён договор (Юр лицо/Физ лицо)
     */
    @Column(name = "concluded_from")
    String concludedFrom;

    /**
     * Налоговая ставка (%)
     */
    @Column(name = "tax_rate")
    Double taxRate;

}