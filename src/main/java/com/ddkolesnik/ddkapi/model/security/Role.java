package com.ddkolesnik.ddkapi.model.security;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

import static com.ddkolesnik.ddkapi.util.Constant.ROLE_PREFIX;

/**
 * @author Alexandr Stegnin
 */

@Data
@Entity
@Table(name = "app_role", schema = "investments", catalog = "investments")
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Role implements GrantedAuthority {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "app_role_log_generator")
  @SequenceGenerator(name = "app_role_log_generator", sequenceName = "app_role_log_id_seq")
  private Long id;

  @Column(name = "name")
  private String title;

  @Override
  public String getAuthority() {
    return title.startsWith(ROLE_PREFIX) ? title : ROLE_PREFIX + title;
  }

  @PrePersist
  public void setRole() {
    if (!title.trim().toUpperCase().startsWith(ROLE_PREFIX)) title = ROLE_PREFIX + title.trim().toUpperCase();
    else title = title.trim().toUpperCase();
  }

  public Role(GrantedAuthority authority) {
    this.title = authority.getAuthority();
  }
}
