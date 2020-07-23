package com.ddkolesnik.ddkapi.model.app;

import com.ddkolesnik.ddkapi.model.security.Role;
import lombok.AccessLevel;
import lombok.Data;
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
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "login")
    String login;

    @Column(name = "password")
    String password;

    @ManyToMany
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    Set<Role> roles = new HashSet<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    UserProfile profile;

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public AppUser() {
        this.profile = new UserProfile();
        this.profile.setUser(this);
    }
}
