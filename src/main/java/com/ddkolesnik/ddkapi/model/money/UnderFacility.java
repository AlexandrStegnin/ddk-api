package com.ddkolesnik.ddkapi.model.money;

import com.ddkolesnik.ddkapi.model.AbstractEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Alexandr Stegnin
 */

@Data
@Entity
@Table(name = "under_facility")
@EqualsAndHashCode(callSuper = true)
public class UnderFacility extends AbstractEntity {

    @Column(name = "name")
    private String name;

}
