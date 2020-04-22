package com.ddkolesnik.ddkapi.model.money;

import com.ddkolesnik.ddkapi.model.AbstractEntity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Alexandr Stegnin
 */

@Data
@Entity
@Table(name = "FACILITYES", schema = "pss_projects", catalog = "pss_projects")
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
public class Facility extends AbstractEntity {

    @Column(name = "facility")
    String name;

}
