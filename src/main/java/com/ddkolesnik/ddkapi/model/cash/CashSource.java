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
@Table(name = "CashSources")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CashSource {

    @Id
    @GeneratedValue
    Long id;

    @Column(name = "CashSource")
    String name;

    @Column(name = "organization_id")
    String organization;
}
