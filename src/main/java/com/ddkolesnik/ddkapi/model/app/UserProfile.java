package com.ddkolesnik.ddkapi.model.app;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * @author Alexandr Stegnin
 */

@Data
@Entity
@Table(name = "user_profile")
@NoArgsConstructor
public class UserProfile {

  @Id
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id")
  @MapsId
  private AppUser user;

  @Column(name = "last_name")
  private String lastName;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "patronymic")
  private String patronymic;

  private String email;

}
