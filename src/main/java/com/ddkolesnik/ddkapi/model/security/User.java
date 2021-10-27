package com.ddkolesnik.ddkapi.model.security;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Set;

/**
 * @author Alexandr Stegnin
 */

@Data
@Entity
@Table(name = "app_user", schema = "investments", catalog = "investments")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "app_user_generator")
  @SequenceGenerator(name = "app_user_generator", sequenceName = "app_user_id_seq")
  Long id;

  String login;

  String password;

  @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
  @JoinTable(
      name = "user_role",
      joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"),
      foreignKey = @ForeignKey(name = "user_role_to_user"),
      inverseForeignKey = @ForeignKey(name = "user_role_to_role")
  )
  Set<Role> roles;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return roles;
  }

  @Override
  public String getUsername() {
    return login;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return isAccountNonExpired() &&
        isAccountNonLocked() &&
        isCredentialsNonExpired();
  }
}
