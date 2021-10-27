package com.ddkolesnik.ddkapi.model.money;

import lombok.Data;

import javax.persistence.*;

/**
 * @author Alexandr Stegnin
 */

@Data
@Entity
@Table(name = "under_facility")
public class UnderFacility {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "under_facility_generator")
  @SequenceGenerator(name = "under_facility_generator", sequenceName = "under_facility_id_seq")
  Long id;

  @Column(name = "name")
  private String name;

}
