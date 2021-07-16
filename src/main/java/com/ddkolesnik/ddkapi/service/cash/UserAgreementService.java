package com.ddkolesnik.ddkapi.service.cash;

import static com.ddkolesnik.ddkapi.util.Constant.INVESTOR_PREFIX;

import com.ddkolesnik.ddkapi.configuration.exception.ApiException;
import com.ddkolesnik.ddkapi.dto.cash.UserAgreementDTO;
import com.ddkolesnik.ddkapi.model.app.AppUser;
import com.ddkolesnik.ddkapi.model.cash.UserAgreement;
import com.ddkolesnik.ddkapi.model.money.Facility;
import com.ddkolesnik.ddkapi.model.money.Investor;
import com.ddkolesnik.ddkapi.repository.cash.UserAgreementRepository;
import com.ddkolesnik.ddkapi.service.app.AppUserService;
import com.ddkolesnik.ddkapi.service.money.FacilityService;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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

    public UserAgreement update(UserAgreement userAgreement) {
        return userAgreementRepository.save(userAgreement);
    }

    /**
     * Создать запись о том, с кем заключён договор на основе DTO
     *
     * @param dto DTO записи из Битрикс 24
     * @return созданная запись
     */
    private UserAgreement create(UserAgreementDTO dto) {
        Facility facility = facilityService.findByFullName(dto.getFacility());
        AppUser investor = appUserService.findByLogin(dto.getConcludedFrom());
        if (Objects.isNull(investor)) {
            throw new ApiException("Пользователь [" + dto.getConcludedFrom() + "] не найден", HttpStatus.NOT_FOUND);
        }
        UserAgreement userAgreement = new UserAgreement();
        userAgreement.setFacilityId(facility.getId());
        userAgreement.setConcludedWith(dto.getConcludedWith());
        userAgreement.setTaxRate(dto.getTaxRate());
        userAgreement.setConcludedFrom(investor.getId());
        userAgreement.setOrganization(dto.getOrganization());
        return userAgreementRepository.save(userAgreement);
    }

    /**
     * Обновить информацию о том, с кем заключён договор на основе DTO
     *
     * @param dto DTO из Битрикс 24
     * @return обновлённая информация
     */
    public UserAgreement update(UserAgreementDTO dto) {
        Facility facility = facilityService.findByFullName(dto.getFacility());
        dto.setConcludedFrom(INVESTOR_PREFIX.concat(dto.getConcludedFrom()));
        AppUser investor = appUserService.findByLogin(dto.getConcludedFrom());
        if (Objects.isNull(investor)) {
            throw new ApiException("Пользователь [" + dto.getConcludedFrom() + "] не найден", HttpStatus.NOT_FOUND);
        }
        UserAgreement userAgreement = userAgreementRepository.findByFacilityIdAndConcludedFrom(facility.getId(), investor.getId());
        if (Objects.isNull(userAgreement)) {
            return create(dto);
        }
        userAgreement.setConcludedFrom(investor.getId());
        userAgreement.setTaxRate(dto.getTaxRate());
        userAgreement.setConcludedWith(dto.getConcludedWith());
        userAgreement.setOrganization(dto.getOrganization());
        return userAgreementRepository.save(userAgreement);
    }

    /**
     * Найти инфо о том, с кем заключён договор
     *
     * @param investor инвестор
     * @param facility объект
     * @return найденная информация
     */
    public UserAgreement findByInvestorAndFacility(Investor investor, Facility facility) {
        return userAgreementRepository.findByFacilityIdAndConcludedFrom(facility.getId(), investor.getId());
    }
}
