package com.ddkolesnik.ddkapi.repository;

import com.ddkolesnik.ddkapi.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Alexandr Stegnin
 */

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByTitle(String title);

}
