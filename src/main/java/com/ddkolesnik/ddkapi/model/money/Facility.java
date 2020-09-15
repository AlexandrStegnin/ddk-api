package com.ddkolesnik.ddkapi.model.money;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

/**
 * @author Alexandr Stegnin
 */

@Data
@Entity
@Table(name = "facility")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Facility {

    @Id
    @TableGenerator(name = "facilitySeqStore", table = "SEQ_STORE",
            pkColumnName = "SEQ_NAME", pkColumnValue = "FACILITY.ID.PK",
            valueColumnName = "SEQ_VALUE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "facilitySeqStore")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    String name;

    @Column(name = "full_name")
    String fullName;

    @Column(name = "city")
    String city;

    @Column(name = "project_uuid")
    String projectUUID;
    
}
