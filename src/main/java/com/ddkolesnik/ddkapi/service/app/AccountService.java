package com.ddkolesnik.ddkapi.service.app;

import com.ddkolesnik.ddkapi.dto.money.FacilityDTO;
import com.ddkolesnik.ddkapi.model.app.Account;
import com.ddkolesnik.ddkapi.model.app.AppUser;
import com.ddkolesnik.ddkapi.model.money.Facility;
import com.ddkolesnik.ddkapi.repository.app.AccountRepository;
import com.ddkolesnik.ddkapi.util.Constant;
import com.ddkolesnik.ddkapi.util.OwnerType;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alexandr Stegnin
 */

@Service
@Transactional
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AccountService {

    AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Создать счёт для пользователя (инвестора)
     *
     * @param user пользователь
     */
    public void createAccount(AppUser user) {
        Account account = new Account();
        account.setAccountNumber(generateAccountNumber(user));
        account.setOwnerId(user.getId());
        account.setOwnerType(OwnerType.INVESTOR);
        account.setOwnerName(user.getLogin());
        accountRepository.save(account);
    }

    /**
     * Сгенерировать номер счёта для пользователя
     *
     * @param user пользователь
     * @return сгенерированный номер
     */
    private String generateAccountNumber(AppUser user) {
        /*
        первые 5 цифр 00000 (порядковый номер клиента)
         */
        String clientCode = user.getLogin().substring(Constant.INVESTOR_PREFIX.length());
        String regionNumber = getRegionNumber();
        return clientCode.concat(regionNumber);
    }

    private String getRegionNumber() {
        return "";
    }

    /**
     * Создать счёт для объекта
     *
     * @param facility объект
     */
    public void createAccount(Facility facility) {
        String accountNumber = generateAccountNumber(facility);
        if (accountNumber.isEmpty()) {
            return;
        }
        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setOwnerId(facility.getId());
        account.setOwnerType(OwnerType.FACILITY);
        account.setOwnerName(facility.getFullName());
        accountRepository.save(account);
    }

    /**
     * Проверить номер счёта по реквизитам
     *
     * @param dto объект
     * @return ответ
     */
    public String checkAccountNumber(FacilityDTO dto) {
        if (dto.getName() != null && !dto.getName().isEmpty()) {
            Facility facility = new Facility();
            facility.setName(dto.getName());
            facility.setFullName(dto.getName());
            String accountNumber = generateAccountNumber(facility);
            if (accountNumber.isEmpty()) {
                return null;
            }
            if (accountRepository.existsByAccountNumber(accountNumber)) {
                return String.format("Номер счёта [%s] уже используется. Проверьте правильность введённых данных", accountNumber);
            }
        }
        return null;
    }

    /**
     * Сгенерировать номер счёта для объекта
     *
     * @param facility объект
     * @return сгенерированный номер счёта
     */
    private String generateAccountNumber(Facility facility) {
        /*
            5 или более цифр до пробела (если есть)
        */
        String fullName = facility.getFullName();
        Pattern pattern = Pattern.compile("^\\d{5,}\\s*");
        Matcher matcher = pattern.matcher(fullName);
        String accountNumber = "";
        while (matcher.find()) {
            accountNumber = fullName.substring(matcher.start(), matcher.end()).trim();
        }
        return accountNumber;
    }

    /**
     * Найти счёт по типу и id владельца. При неудачной попытке попытаться создать счёт
     *
     * @param ownerId id владельца
     * @param ownerType тип владельца
     * @return найденный/созданный счёт
     */
    public Account findByOwnerId(Long ownerId, OwnerType ownerType) {
        return accountRepository.findByOwnerIdAndOwnerType(ownerId, ownerType);
    }

}
