package com.ddkolesnik.ddkapi.util;

/**
 * @author Alexandr Stegnin
 */

public enum ConcludedWith {

    LEGAL_PERSON(1, "Юр. лицо"),
    NATURAL_PERSON(2, "Физ. лицо"),
    BUSINESSMAN(3, "ИП");

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

}
