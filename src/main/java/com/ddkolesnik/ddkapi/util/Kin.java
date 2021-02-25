package com.ddkolesnik.ddkapi.util;

/**
 * @author Alexandr Stegnin
 */

public enum Kin {

    KIN(1, "Родственник"),
    NO_KIN(2, "Не родственник"),
    SPOUSE(3, "Супруг/супруга");

    private final int id;

    private final String title;

    Kin(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public static Kin fromTitle(String title) {
        for (Kin kin : values()) {
            if (kin.getTitle().equalsIgnoreCase(title)) {
                return kin;
            }
        }
        return null;
    }

    public static Kin fromId(Integer id) {
        if (id == null) {
            return null;
        }
        for (Kin kin : values()) {
            if (kin.getId() == id) {
                return kin;
            }
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

}
