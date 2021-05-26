package com.ddkolesnik.ddkapi.service.money;

import com.ddkolesnik.ddkapi.configuration.exception.ApiException;
import com.ddkolesnik.ddkapi.dto.money.FacilityDTO;
import com.ddkolesnik.ddkapi.model.app.Account;
import com.ddkolesnik.ddkapi.model.money.Facility;
import com.ddkolesnik.ddkapi.repository.money.FacilityRepository;
import com.ddkolesnik.ddkapi.service.app.AccountService;
import com.ddkolesnik.ddkapi.util.OwnerType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alexandr Stegnin
 */

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class FacilityService {

    FacilityRepository facilityRepository;

    AccountService accountService;

    public Facility findByFullName(String fullName) {
        Facility facility = facilityRepository.findByFullNameEqualsIgnoreCase(fullName);
        if (facility == null) {
            String message = String.format("Объект с названием [%s] не найден", fullName);
            log.error(message);
            throw new ApiException(message, HttpStatus.NOT_FOUND);
        }
        return facility;
    }

    /**
     * Обновить или создать объект на основе DTO из 1С
     *
     * @param dto DTO объекта из 1С
     */
    public void update(FacilityDTO dto) {
        if (dto.getProjectUUID() == null || dto.getProjectUUID().isEmpty()) {
            throw new ApiException("Не задан идентификатор объекта", HttpStatus.BAD_REQUEST);
        }
        Facility facility = findFacility(dto);
        if (facility != null) {
            updateFacility(facility, dto);
        } else {
            createFacility(dto);
        }
    }

    /**
     * Обновить объект на основе DTO из 1С
     *
     * @param facility объект для обновления
     * @param dto      DTO объект из 1С
     */
    private void updateFacility(Facility facility, FacilityDTO dto) {
        facility.setProjectUUID(dto.getProjectUUID());
        facility.setName(prepareName(dto));
        facility.setFullName(dto.getName());
        facilityRepository.save(facility);
        Account account = accountService.findByOwnerId(facility.getId(), OwnerType.FACILITY);
        if (account == null) {
            accountService.createAccount(facility);
        }
    }

    /**
     * Создать объект на основе DTO из 1С
     *
     * @param dto DTO из 1С
     */
    private void createFacility(FacilityDTO dto) {
        if (accountService.checkAccountNumber(dto) != null) {
            throw new ApiException("Номер договора уже используется.", HttpStatus.BAD_REQUEST);
        }
        Facility facility = new Facility();
        facility.setName(prepareName(dto));
        facility.setFullName(dto.getName());
        facility.setProjectUUID(dto.getProjectUUID());
        facilityRepository.save(facility);
        accountService.createAccount(facility);
    }

    /**
     * Найти объект по идентификатору из 1С
     *
     * @param projectUUID идентификатор из 1С
     * @return найденный объект
     */
    public Facility findByProjectUUID(String projectUUID) {
        return facilityRepository.findByProjectUUID(projectUUID);
    }

    /**
     * Найти объект в базе данных
     *
     * @param dto DTO из 1С
     * @return найденный объект
     */
    private Facility findFacility(FacilityDTO dto) {
        Facility facility = findByProjectUUID(dto.getProjectUUID());
        if (facility != null) {
            return facility;
        }
        facility = facilityRepository.findByFullNameEqualsIgnoreCase(dto.getName());
        if (facility != null) {
            return facility;
        }
        return facilityRepository.findByNameEqualsIgnoreCase(dto.getName());
    }

    /**
     * Взять из названия объекта всё после первых 5+ чисел
     *
     * @param dto DTO для получения имени объекта
     * @return результат обработки
     */
    private String prepareName(FacilityDTO dto) {
        String name = dto.getName();
        String result = name;
        Pattern pattern = Pattern.compile("^\\d{5,}\\.?\\d?\\s+");
        Matcher matcher = pattern.matcher(name);
        if (matcher.find()) {
            result = name.substring(matcher.group().length());
        }
        return result;
    }

}
