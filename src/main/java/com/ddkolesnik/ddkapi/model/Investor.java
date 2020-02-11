package com.ddkolesnik.ddkapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * @author Alexandr Stegnin
 */

@Data
@Entity
@Table(name = "USERS")
@JsonIgnoreProperties({"id"})
public class Investor {

    @Id
    @GeneratedValue
    @Column(name = "id", insertable = false, updatable = false)
    private Long id;

    private String login;

    @OneToMany
    @JoinColumn(name = "investorId")
    private List<Money> monies;
}
