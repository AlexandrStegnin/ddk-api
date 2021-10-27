package com.ddkolesnik.ddkapi.model.money;

import com.ddkolesnik.ddkapi.model.app.Account;
import com.ddkolesnik.ddkapi.model.log.CashType;
import com.ddkolesnik.ddkapi.util.OperationType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Alexandr Stegnin
 */

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"parent", "child"})
@ToString(exclude = {"parent", "child"})
@Table(name = "account_transaction")
public class AccountTransaction {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_transaction_generator")
  @SequenceGenerator(name = "account_transaction_generator", sequenceName = "account_transaction_id_seq")
  @Column(name = "id")
  private Long id;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "creation_time")
  private Date creationTime;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "modified_time")
  private Date modifiedTime;

  @PrePersist
  public void prePersist() {
    if (this.creationTime == null) {
      this.creationTime = new Date();
    }
  }

  @PreUpdate
  public void preUpdate() {
    this.modifiedTime = new Date();
  }

  @ManyToOne
  @JoinColumn(name = "parent_acc_tx_id")
  private AccountTransaction parent;

  @OneToMany(mappedBy = "parent", orphanRemoval = true)
  private Set<AccountTransaction> child;

  @Column(name = "tx_date")
  private Date txDate = new Date();

  @Enumerated(EnumType.ORDINAL)
  @Column(name = "operation_type")
  private OperationType operationType;

  @ManyToOne
  @JoinColumn(name = "payer_account_id")
  private Account payer;

  @OneToOne
  @JoinColumn(name = "owner_account_id")
  private Account owner;

  @ManyToOne
  @JoinColumn(name = "recipient_account_id")
  private Account recipient;

  @OneToMany(mappedBy = "transaction")
  private Set<Money> monies = new HashSet<>();

  @Enumerated(EnumType.ORDINAL)
  @Column(name = "cash_type_id")
  private CashType cashType;

  @Column(name = "blocked")
  private boolean blocked = false;

  @Column(name = "cash")
  private BigDecimal cash;

  @Column(name = "transaction_uuid")
  private String transactionUUID;

  @Column(name = "accounting_code")
  private String accountingCode;

  public AccountTransaction(Account owner) {
    this.owner = owner;
  }
}
