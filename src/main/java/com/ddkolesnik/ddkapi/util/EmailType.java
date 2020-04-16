package com.ddkolesnik.ddkapi.util;

/**
 * @author Alexandr Stegnin
 */

public enum EmailType {

    WORK("WORK"),
    HOME("HOME");

    private final String title;

    EmailType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
