package com.ddkolesnik.ddkapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Alexandr Stegnin
 */

@Data
@Entity
@Table(name = "FACILITYES")
@JsonIgnoreProperties({"id"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Facility {

    @Id
    Long id;

    @Column(name = "facility")
    String name;

}
