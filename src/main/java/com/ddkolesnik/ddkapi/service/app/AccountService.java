package com.ddkolesnik.ddkapi.service.app;

import com.ddkolesnik.ddkapi.model.app.Account;
import com.ddkolesnik.ddkapi.model.app.AppUser;
import com.ddkolesnik.ddkapi.repository.app.AccountRepository;
import com.ddkolesnik.ddkapi.util.Constant;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public void createAccount(AppUser user) {
        Account account = new Account();
        account.setAccountNumber(generateAccountNumber(user));
        account.setOwnerId(user.getId());
        accountRepository.save(account);
    }

    private String generateAccountNumber(AppUser user) {
        /*
        первые 5 цифр 00000 (порядковый номер клиента),
        вторые 3 цифры (номер региона),
        далее 4 цифры (порядковый номер объекта),
        далее 2 цифры (порядковый номер подобъекта) - всего 14 символов поллучается
         */
        String clientCode = user.getLogin().substring(Constant.INVESTOR_PREFIX.length());
        String regionNumber = getRegionNumber();
        return clientCode.concat(regionNumber);
    }

    private String getRegionNumber() {
        return "";
    }

}
