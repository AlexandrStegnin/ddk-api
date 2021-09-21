package com.ddkolesnik.ddkapi.model.money;

import com.ddkolesnik.ddkapi.model.AbstractEntity;
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
@Table(name = "app_user", schema = "investments", catalog = "investments")
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
public class Investor extends AbstractEntity {

    String login;

    @OneToMany(mappedBy = "investor")
    List<Money> monies;
}
