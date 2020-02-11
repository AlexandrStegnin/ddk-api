package com.ddkolesnik.ddkapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.List;

/**
 * @author Alexandr Stegnin
 */

@Data
@Entity
@Table(name = "USERS")
@JsonIgnoreProperties({"id"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Investor {

    @Id
    Long id;

    String login;

    @OneToMany
    @JoinColumn(name = "investorId")
    List<Money> monies;
}
