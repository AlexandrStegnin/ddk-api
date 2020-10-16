package com.ddkolesnik.ddkapi.util;

/**
 * @author Alexandr Stegnin
 */

public enum OperationType {

    UNDEFINED(0, "Не определено"),
    DEBIT(1, "Приход"),
    CREDIT(2, "Расход");

    private final int id;

    private final String title;

    OperationType(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public static OperationType fromTitle(String title) {
        for (OperationType value : values()) {
            if (value.title.equalsIgnoreCase(title)) {
                return value;
            }
        }
        return UNDEFINED;
    }

    public static OperationType fromId(int id) {
        for (OperationType value : values()) {
            if (value.id == id) {
                return value;
            }
        }
        return UNDEFINED;
    }

}
