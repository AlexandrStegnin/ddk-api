package com.ddkolesnik.ddkapi.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * @author Alexandr Stegnin
 */

@Data
@MappedSuperclass
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AbstractEntity {

    @Id
    Long id;

}
