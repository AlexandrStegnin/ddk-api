package com.ddkolesnik.ddkapi.util;

/**
 * @author Alexandr Stegnin
 */

public enum ConcludedWith {

    LEGAL_PERSON(1, "Юр. лицо"),
    NATURAL_PERSON(2, "ФЛ"),
    BUSINESSMAN(3, "ИП"),
    NATURAL_PERSON_PLATFORM(4, "ФЛ-платформа"),
    BUSINESSMAN_PLATFORM(5,  "ИП-платформа"),
    SELF_EMPLOYED_PLATFORM(6, "Самозаняты-платформа");

    private final int id;

    private final String title;

    ConcludedWith(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public static ConcludedWith fromTitle(String title) {
        if (title == null) {
            return LEGAL_PERSON;
        }
        for (ConcludedWith with : values()) {
            if (with.getTitle().equalsIgnoreCase(title)) {
                return with;
            }
        }
        return LEGAL_PERSON;
    }

    public static boolean needCreateCommission(ConcludedWith concludedWith) {
        return concludedWith == NATURAL_PERSON || concludedWith == NATURAL_PERSON_PLATFORM;
    }

}
