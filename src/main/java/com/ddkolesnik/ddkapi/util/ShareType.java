package com.ddkolesnik.ddkapi.util;

/**
 * @author Alexandr Stegnin
 */

public enum ShareType {

    UNDEFINED(0, "Не определена"),
    MAIN(1, "Основная"),
    OVER(2, "Сверхдоля");

    private final int id;

    private final String title;

    ShareType(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public static ShareType fromId(int id) {
        for (ShareType type : values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        return UNDEFINED;
    }

    public static ShareType fromTitle(String title) {
        for (ShareType type : values()) {
            if (type.getTitle().equalsIgnoreCase(title)) {
                return type;
            }
        }
        return UNDEFINED;
    }

}
