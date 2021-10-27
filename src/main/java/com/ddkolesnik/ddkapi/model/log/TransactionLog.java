package com.ddkolesnik.ddkapi.model.log;

import com.ddkolesnik.ddkapi.model.money.AccountTransaction;
import com.ddkolesnik.ddkapi.model.money.Money;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static com.ddkolesnik.ddkapi.util.Constant.CREATOR_1C;

/**
 * Класс для хранения информации и работы с транзакциями по операциям
 *
 * @author Alexandr Stegnin
 */

@Data
@Entity
@Table(name = "transaction_log")
public class TransactionLog {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_log_generator")
  @SequenceGenerator(name = "transaction_log_generator", sequenceName = "transaction_log_id_seq")
  @Column(name = "id")
  private Long id;

  @Column(name = "created_by")
  private String createdBy;

  @Column(name = "tx_date")
  private Date txDate;

  @ManyToMany
  @JoinTable(name = "tx_log_inv_cash",
      joinColumns = {@JoinColumn(name = "tx_id", referencedColumnName = "id")},
      inverseJoinColumns = @JoinColumn(name = "cash_id", referencedColumnName = "id"))
  private Set<Money> investorsCashes;

  @ManyToMany
  @JoinTable(name = "tx_log_acc_tx",
      joinColumns = {@JoinColumn(name = "tx_id", referencedColumnName = "id")},
      inverseJoinColumns = @JoinColumn(name = "acc_tx_id", referencedColumnName = "id"))
  private Set<AccountTransaction> accountTransactions = new HashSet<>();

  @Enumerated(EnumType.STRING)
  @Column(name = "transaction_type")
  private TransactionType type;

  @Column(name = "rollback_enabled")
  private boolean rollbackEnabled;

  @OneToOne
  @JsonIgnore
  @JoinColumn(name = "blocked_from")
  private TransactionLog blockedFrom;

  @PrePersist
  public void prePersist() {
    this.txDate = new Date();
    this.createdBy = CREATOR_1C;
  }

  public void addAccountTransaction(AccountTransaction accountTransaction) {
    this.accountTransactions.add(accountTransaction);
  }

}
