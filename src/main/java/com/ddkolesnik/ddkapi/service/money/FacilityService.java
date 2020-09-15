package com.ddkolesnik.ddkapi.service.money;

import com.ddkolesnik.ddkapi.configuration.exception.ApiException;
import com.ddkolesnik.ddkapi.dto.money.FacilityDTO;
import com.ddkolesnik.ddkapi.model.money.Facility;
import com.ddkolesnik.ddkapi.repository.money.FacilityRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author Alexandr Stegnin
 */

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class FacilityService {

    FacilityRepository facilityRepository;

    public Facility findByFullName(String fullName) {
        Facility facility = facilityRepository.findByFullNameEqualsIgnoreCase(fullName);
        if (Objects.isNull(facility)) {
            throw new ApiException("Объект с названием = [" + fullName + "] не найден", HttpStatus.NOT_FOUND);
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
     * @param dto DTO объект из 1С
     */
    private void updateFacility(Facility facility, FacilityDTO dto) {
        facility.setProjectUUID(dto.getProjectUUID());
        facility.setName(dto.getName());
        facility.setFullName(dto.getName());
        facilityRepository.save(facility);
    }

    /**
     * Создать объект на основе DTO из 1С
     *
     * @param dto DTO из 1С
     */
    private void createFacility(FacilityDTO dto) {
        Facility facility = new Facility();
        facility.setName(dto.getName());
        facility.setFullName(dto.getName());
        facility.setProjectUUID(dto.getProjectUUID());
        facilityRepository.save(facility);
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

}
