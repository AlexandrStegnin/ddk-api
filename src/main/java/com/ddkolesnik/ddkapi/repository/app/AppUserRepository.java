package com.ddkolesnik.ddkapi.repository.app;

import com.ddkolesnik.ddkapi.model.security.Role;
import com.ddkolesnik.ddkapi.model.app.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * @author Alexandr Stegnin
 */

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    @Query("SELECT au FROM AppUser au WHERE au.login = :login")
    AppUser findByLogin(@Param("login") String login);

    List<AppUser> findByRolesIn(Set<Role> roles);

}
