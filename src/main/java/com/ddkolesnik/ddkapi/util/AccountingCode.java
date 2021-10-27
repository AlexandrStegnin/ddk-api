package com.ddkolesnik.ddkapi.util;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * @author Alexandr Stegnin
 */
@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum AccountingCode {

    CASHING("НФ-000027", "Вывод Клиентов"),
    CASHING_BODY("НФ-000184", "Вывод Клиентов Тело"),
    CASHING_COMMISSION("НФ-000167", "Вывод Клиентов Проценты"),
    RESALE_SHARE("НФ-000259", "Перепродажа доли"),
    RESALE_SHARE_DEL("НФ-000039", "Перепродажа (удаление)");

    String code;
    String title;

    public static AccountingCode fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (AccountingCode accountingCode : values()) {
            if (accountingCode.getCode().equalsIgnoreCase(code)) {
                return accountingCode;
            }
        }
        return null;
    }

    public static boolean isCashing(AccountingCode code) {
        return CASHING == code || CASHING_BODY == code || CASHING_COMMISSION == code;
    }

    public static boolean isResale(AccountingCode code) {
        return RESALE_SHARE == code || RESALE_SHARE_DEL == code;
    }

}
