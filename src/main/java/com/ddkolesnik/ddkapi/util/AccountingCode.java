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

    public boolean isCashing() {
        return this == CASHING || this == CASHING_BODY || this == CASHING_COMMISSION;
    }

    public boolean isResale() {
        return this == RESALE_SHARE || this == RESALE_SHARE_DEL;
    }

}
