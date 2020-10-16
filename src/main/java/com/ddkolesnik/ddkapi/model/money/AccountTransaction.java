package com.ddkolesnik.ddkapi.model.money;

import com.ddkolesnik.ddkapi.model.app.Account;
import com.ddkolesnik.ddkapi.model.log.CashType;
import com.ddkolesnik.ddkapi.util.OperationType;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Alexandr Stegnin
 */

@Data
@Entity
@NoArgsConstructor
@Table(name = "account_transaction")
public class AccountTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @PreUpdate
    public void preUpdate() {
        this.modifiedTime = new Date();
    }

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

    @OneToOne
    @JoinColumn(name = "money_id")
    private Money money;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "cash_type_id")
    private CashType cashType;

    @Column(name = "blocked")
    private boolean blocked = false;

    @Column(name = "cash")
    private BigDecimal cash;

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

    public AccountTransaction(Account owner) {
        this.owner = owner;
    }
}
