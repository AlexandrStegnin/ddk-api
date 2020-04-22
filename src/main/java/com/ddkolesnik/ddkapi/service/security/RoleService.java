package com.ddkolesnik.ddkapi.service.security;

import com.ddkolesnik.ddkapi.model.security.Role;
import com.ddkolesnik.ddkapi.repository.security.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

/**
 * @author Alexandr Stegnin
 */

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RoleService {

    RoleRepository roleRepository;

    public Role findByRoleName(String roleName) {
        return roleRepository.findByTitle(roleName);
    }

}
