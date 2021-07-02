package com.ddkolesnik.ddkapi.model.cash;

import java.time.LocalDateTime;
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
     * С кем заключён договор (ЮЛ/ФЛ)
     */
    @Column(name = "concluded_with")
    String concludedWith;

    /**
     * От кого заключён договор (id инвестора)
     */
    @Column(name = "concluded_from")
    Long concludedFrom;

    /**
     * Налоговая ставка (%)
     */
    @Column(name = "tax_rate")
    Double taxRate;

    /**
     * От кого заключён договор (название организации)
     */
    @Column(name = "organization")
    String organization;

    @Column(name = "modified_time")
    LocalDateTime modifiedTime;

    @PreUpdate
    public void preUpdate() {
        this.modifiedTime = LocalDateTime.now();
    }
}
