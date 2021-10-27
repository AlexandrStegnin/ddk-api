package com.ddkolesnik.ddkapi.model.cash;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Сущность для хранения инфо о том, с кем заключён договор
 *
 * @author Alexandr Stegnin
 */

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_agreement")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAgreement {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_agreement_generator")
  @SequenceGenerator(name = "user_agreement_generator", sequenceName = "user_agreement_id_seq")
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
