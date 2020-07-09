package com.ddkolesnik.ddkapi.service.app;

import com.ddkolesnik.ddkapi.dto.app.AppUserDTO;
import com.ddkolesnik.ddkapi.model.app.AppUser;
import com.ddkolesnik.ddkapi.model.security.Role;
import com.ddkolesnik.ddkapi.repository.app.AppUserRepository;
import com.ddkolesnik.ddkapi.service.security.RoleService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Сервис для управления пользователями
 *
 * @author Alexandr Stegnin
 */

@Slf4j
@Service
@Transactional
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AppUserService {

    private static final String ROLE_INVESTOR = "ROLE_INVESTOR";

    private static final String INVESTOR_PREFIX = "investor";

    BCryptPasswordEncoder encoder;

    AppUserRepository appUserRepository;

    RoleService roleService;

    AccountService accountService;

    public AppUserService(BCryptPasswordEncoder encoder, AppUserRepository appUserRepository, RoleService roleService, AccountService accountService) {
        this.encoder = encoder;
        this.appUserRepository = appUserRepository;
        this.roleService = roleService;
        this.accountService = accountService;
    }

    /**
     * Обновить данные пользователя
     *
     * @param user - пользователь, которого необходимо обновить
     */
    private void update(AppUser user) {
        appUserRepository.save(user);
    }

    /**
     * Обновить данные пользователя
     *
     * @param appUserDTO - DTO пользователя
     */
    public void update(AppUserDTO appUserDTO) {
        String login = INVESTOR_PREFIX.concat(appUserDTO.getPartnerCode());
        AppUser user = findByLogin(login);
        if (user == null) {
            appUserDTO.setPartnerCode(login);
            createUser(appUserDTO);
        } else if (needUpdate(appUserDTO, user)) {
            prepareUser(user, appUserDTO);
            update(user);
        }
    }

    private void createUser(AppUserDTO appUserDTO) {
        AppUser user = new AppUser();
        user.setLogin(appUserDTO.getPartnerCode());
        prepareUser(user, appUserDTO);
        update(user);
        accountService.createAccount(user);
    }

    /**
     * Найти пользователя по логину
     *
     * @param login - логин пользователя
     * @return - пользователь
     */
    public AppUser findByLogin(String login) {
        return appUserRepository.findByLogin(login);
    }

    @Deprecated
    public void delete(String login) {
        appUserRepository.delete(findByLogin(login));
    }

    /**
     * Получить из базы роль "ИНВЕСТОР"
     *
     * @return - роль
     */
    private Role getInvestorRole() {
        return roleService.findByRoleName(ROLE_INVESTOR);
    }

    /**
     * Сгенерировать пароль для пользователя
     *
     * @return - сгенерированный пароль
     */
    private String generatePassword() {
        String password = UUID.randomUUID().toString().substring(0, 8);
        return encoder.encode(password);
    }

    /**
     * Проверяем необходимость обновления пользователя
     *
     * @param dto - DTO пользователя для обновления
     * @param entity - пользователь из базы данных
     * @return - надо/не надо обновлять
     */
    private boolean needUpdate(AppUserDTO dto, AppUser entity) {
        return !dto.getLastName().equalsIgnoreCase(entity.getProfile().getLastName()) ||
                !dto.getEmail().equalsIgnoreCase(entity.getProfile().getEmail());
    }

    /**
     * Подготавливаем пользователя к сохранению
     *
     * @param user - пользователь для сохранения
     * @param dto - DTO на основе которого подготавливается пользователь
     */
    private void prepareUser(AppUser user, AppUserDTO dto) {
        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            user.getProfile().setEmail(dto.getEmail());
        }
        if (dto.getLastName() != null && !dto.getLastName().isEmpty()) {
            user.getProfile().setLastName(dto.getLastName());
        }
        user.addRole(getInvestorRole());
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(generatePassword());
        }
    }

    public List<AppUserDTO> getAllDTO() {
        Role role = getInvestorRole();
        return appUserRepository.findByRolesIn(Collections.singleton(role))
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private AppUserDTO convertToDTO(AppUser user) {
        return new AppUserDTO(user.getLogin(), user.getProfile().getLastName(), user.getProfile().getEmail());
    }

}
