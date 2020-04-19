package com.ddkolesnik.ddkapi.model;

import com.ddkolesnik.ddkapi.model.money.Money;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

/**
 * @author Alexandr Stegnin
 */

@Data
@Entity
@Table(name = "USERS", schema = "pss_projects", catalog = "pss_projects")
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
public class Investor extends AbstractEntity {

    String login;

    @OneToMany(mappedBy = "investor")
    List<Money> monies;
}
