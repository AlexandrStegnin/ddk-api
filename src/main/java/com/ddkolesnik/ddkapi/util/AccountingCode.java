package com.ddkolesnik.ddkapi.util;

/**
 * @author Alexandr Stegnin
 */

public enum AccountingCode {

    CASHING("НФ-000027", "Вывод Клиентов"),
    CASHING_BODY("НФ-000184", "Вывод Клиентов Тело"),
    CASHING_COMMISSION("НФ-000167", "Вывод Клиентов Проценты");

    private final String code;

    private final String title;

    AccountingCode(String code, String title) {
        this.code = code;
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

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

}
