package com.ddkolesnik.ddkapi.model;

import lombok.Data;

import javax.persistence.*;

/**
 * @author Alexandr Stegnin
 */

@Data
@Entity
@Table(name = "USERS")
public class User {

    @Id
    @GeneratedValue
    @Column(name = "id", insertable = false, updatable = false)
    private Long id;

    private String login;

}
