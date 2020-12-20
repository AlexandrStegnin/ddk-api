package com.ddkolesnik.ddkapi.service.cash;

import com.ddkolesnik.ddkapi.model.cash.UserAgreement;
import com.ddkolesnik.ddkapi.repository.cash.UserAgreementRepository;
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

}
