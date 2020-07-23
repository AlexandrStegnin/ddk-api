package com.ddkolesnik.ddkapi.model.money;

import com.ddkolesnik.ddkapi.model.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Alexandr Stegnin
 */

@Entity
@Table(name = "under_facility")
public class UnderFacility extends AbstractEntity {

    @Column(name = "name")
    private String name;

}
