package com.ddkolesnik.ddkapi.model.app;

import com.ddkolesnik.ddkapi.util.OwnerType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

/**
 * @author Alexandr Stegnin
 */

@Data
@Entity
@Table(name = "account")
@ToString(of = {"id", "accountNumber"})
@EqualsAndHashCode(of = {"id", "accountNumber"})
public class Account {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_generator")
  @SequenceGenerator(name = "account_generator", sequenceName = "account_id_seq")
  @Column(name = "id")
  private Long id;

  @Column(name = "account_number")
  private String accountNumber;

  @Column(name = "owner_id")
  private Long ownerId;

  @Enumerated(EnumType.STRING)
  @Column(name = "owner_type")
  private OwnerType ownerType;

  @Column(name = "owner_name")
  private String ownerName;
}
