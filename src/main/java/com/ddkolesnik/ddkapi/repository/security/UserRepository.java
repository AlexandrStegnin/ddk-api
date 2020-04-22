package com.ddkolesnik.ddkapi.repository.security;

import com.ddkolesnik.ddkapi.model.security.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Alexandr Stegnin
 */

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByLogin(String login);

}
