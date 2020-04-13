package com.ddkolesnik.ddkapi.service;

import com.ddkolesnik.ddkapi.dto.app.AppUserDTO;
import com.ddkolesnik.ddkapi.model.Role;
import com.ddkolesnik.ddkapi.model.app.AppUser;
import com.ddkolesnik.ddkapi.repository.AppUserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.UUID;

/**
 * @author Alexandr Stegnin
 */

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AppUserService {

    private static final String ROLE_INVESTOR = "ROLE_INVESTOR";

    private static final String INVESTOR_PREFIX = "investor";

    BCryptPasswordEncoder encoder;

    AppUserRepository appUserRepository;

    RoleService roleService;

    public AppUser update(AppUser user) {
        return appUserRepository.save(user);
    }

    public AppUserDTO update(AppUserDTO appUserDTO) {
        AppUser user = new AppUser();
        BeanUtils.copyProperties(appUserDTO, user);
        user.addRole(getInvestorRole());
        user.setPassword(generatePassword());
        user = update(user);
        BeanUtils.copyProperties(user, appUserDTO);
        return appUserDTO;
    }

    @Deprecated
    private AppUser findByLogin(String login) {
        AppUser user = appUserRepository.findByLogin(login);
        if (user == null) {
            throw new EntityNotFoundException("Пользователь с логином = [" + login + "] не найден");
        }
        return appUserRepository.findByLogin(login);
    }

    @Deprecated
    public AppUserDTO findByName(String login) {
        AppUser user = findByLogin(login);
        return new AppUserDTO(user);
    }

    @Deprecated
    public void delete(String login) {
        appUserRepository.delete(findByLogin(login));
    }

    private Role getInvestorRole() {
        return roleService.findByRoleName(ROLE_INVESTOR);
    }

    private String generatePassword() {
        String password = UUID.randomUUID().toString().substring(0, 8);
        return encoder.encode(password);
    }

}
