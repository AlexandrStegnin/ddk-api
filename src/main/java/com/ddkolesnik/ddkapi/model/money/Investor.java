package com.ddkolesnik.ddkapi.model.money;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.List;

/**
 * @author Alexandr Stegnin
 */

@Data
@Entity
@Table(name = "app_user", schema = "investments", catalog = "investments")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Investor {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "app_user_generator")
  @SequenceGenerator(name = "app_user_generator", sequenceName = "app_user_id_seq")
  Long id;

  String login;

  @OneToMany(mappedBy = "investor")
  List<Money> monies;
}
