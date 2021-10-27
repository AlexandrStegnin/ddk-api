package com.ddkolesnik.ddkapi.model.cash;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

/**
 * Источник денег
 *
 * @author Alexandr Stegnin
 */

@Data
@Entity
@Table(name = "cash_source")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CashSource {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cash_source_generator")
  @SequenceGenerator(name = "cash_source_generator", sequenceName = "cash_source_id_seq")
  Long id;

  @Column(name = "name")
  String name;

  @Column(name = "organization_id")
  String organization;
}
