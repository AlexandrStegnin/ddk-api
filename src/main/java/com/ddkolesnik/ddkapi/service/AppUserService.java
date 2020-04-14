package com.ddkolesnik.ddkapi.service;

import com.ddkolesnik.ddkapi.dto.app.AppUserDTO;
import com.ddkolesnik.ddkapi.model.Role;
import com.ddkolesnik.ddkapi.model.app.AppUser;
import com.ddkolesnik.ddkapi.repository.AppUserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.UUID;

/**
 * Сервис для управления пользователями
 *
 * @author Alexandr Stegnin
 */

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AppUserService {

    private static final String ROLE_INVESTOR = "ROLE_INVESTOR";

    private static final String INVESTOR_PREFIX = "investor";

    BCryptPasswordEncoder encoder;

    AppUserRepository appUserRepository;

    RoleService roleService;

    /**
     * Обновить данные пользователя
     *
     * @param user - пользователь, которого необходимо обновить
     * @return - обновлённый пользователь
     */
    private AppUser update(AppUser user) {
        log.info("Сохраняем пользователя {}", user);
        return appUserRepository.save(user);
    }

    /**
     * Обновить данные пользователя
     *
     * @param appUserDTO - DTO пользователя
     * @return - обновлённое DTO пользователя
     */
    public AppUserDTO update(AppUserDTO appUserDTO) {
        String login = INVESTOR_PREFIX.concat(appUserDTO.getPartnerCode());
        AppUser user = findByLogin(login);
        if (user == null) {
            user = new AppUser();
            user.setLogin(login);
        } else if (needUpdate(appUserDTO, user)) {
            prepareUser(user, appUserDTO);
            user = update(user);
            BeanUtils.copyProperties(user, appUserDTO);
        }
        return appUserDTO;
    }

    /**
     * Найти пользователя по логину
     *
     * @param login - логин пользователя
     * @return - пользователь
     */
    private AppUser findByLogin(String login) {
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
     * Обновляем пользователя по параметрам
     *
     * @param partnerCode - код инвестора
     * @param lastName - фамилия
     * @param email - email
     * @return - DTO пользователя
     */
    public AppUserDTO update(String partnerCode, String lastName, String email) {
        AppUserDTO userDTO = new AppUserDTO(partnerCode, lastName, email);
        return update(userDTO);
    }

    /**
     * Проверяем необходимость обновления пользователя
     *
     * @param dto - DTO пользователя для обновления
     * @param entity - пользователь из базы данных
     * @return - надо/не надо обновлять
     */
    private boolean needUpdate(AppUserDTO dto, AppUser entity) {
        return !dto.getLastName().equalsIgnoreCase(entity.getLastName()) ||
                !dto.getEmail().equalsIgnoreCase(entity.getEmail());
    }

    /**
     * Подготавливаем пользователя к сохранению
     *
     * @param user - пользователь для сохранения
     * @param dto - DTO на основе которого подготавливается пользователь
     */
    private void prepareUser(AppUser user, AppUserDTO dto) {
        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getLastName() != null && !dto.getLastName().isEmpty()) {
            user.setLastName(dto.getLastName());
        }
        user.addRole(getInvestorRole());
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(generatePassword());
        }
    }
}
