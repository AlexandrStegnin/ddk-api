package com.ddkolesnik.ddkapi.service.cash;

import com.ddkolesnik.ddkapi.dto.cash.UserAgreementDTO;
import com.ddkolesnik.ddkapi.model.app.AppUser;
import com.ddkolesnik.ddkapi.model.cash.UserAgreement;
import com.ddkolesnik.ddkapi.model.money.Facility;
import com.ddkolesnik.ddkapi.repository.cash.UserAgreementRepository;
import com.ddkolesnik.ddkapi.service.app.AppUserService;
import com.ddkolesnik.ddkapi.service.money.FacilityService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

/**
 * @author Alexandr Stegnin
 */

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserAgreementService {

    UserAgreementRepository userAgreementRepository;

    FacilityService facilityService;

    AppUserService appUserService;

    public UserAgreement findById(Long id) {
        return userAgreementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Информация о заключённом договоре не найдена"));
    }

    public UserAgreement create(UserAgreement userAgreement) {
        return userAgreementRepository.save(userAgreement);
    }

    public UserAgreement update(UserAgreement userAgreement) {
        return userAgreementRepository.save(userAgreement);
    }

    public void delete(UserAgreement userAgreement) {
        userAgreementRepository.delete(userAgreement);
    }

    /**
     * Создать запись о том, с кем заключён договор на основе DTO
     *
     * @param dto DTO записи из Битрикс 24
     * @return созданная запись
     */
    public UserAgreement create(UserAgreementDTO dto) {
        Facility facility = facilityService.findByFullName(dto.getFacility());
        AppUser investor = appUserService.findByLogin(dto.getConcludedWith());
        UserAgreement userAgreement = new UserAgreement();
        userAgreement.setFacilityId(facility.getId());
        userAgreement.setConcludedWith(investor.getId());
        userAgreement.setTaxRate(dto.getTaxRate());
        userAgreement.setConcludedFrom(dto.getConcludedFrom());
        return userAgreementRepository.save(userAgreement);
    }

}
