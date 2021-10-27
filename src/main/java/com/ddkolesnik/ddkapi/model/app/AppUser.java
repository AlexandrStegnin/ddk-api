package com.ddkolesnik.ddkapi.model.app;

import com.ddkolesnik.ddkapi.model.security.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Alexandr Stegnin
 */

@Data
@Entity
@Table(name = "app_user")
@ToString(exclude = "profile")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppUser {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "app_user_generator")
  @SequenceGenerator(name = "app_user_generator", sequenceName = "app_user_id_seq")
  Long id;

  @Column(name = "login")
  String login;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Column(name = "password")
  String password;

  @ManyToMany
  @JoinTable(name = "user_role",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id"))
  Set<Role> roles = new HashSet<>();

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
  UserProfile profile;

  @Column(name = "kin")
  Integer kin;

  @Column(name = "partner_id")
  Long partnerId;

  public void addRole(Role role) {
    this.roles.add(role);
  }

  public AppUser() {
    this.profile = new UserProfile();
    this.profile.setUser(this);
  }
}
