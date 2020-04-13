package com.ddkolesnik.ddkapi.repository;

import com.ddkolesnik.ddkapi.model.app.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Alexandr Stegnin
 */

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    AppUser findByLogin(String login);

}
