package com.ddkolesnik.ddkapi.util;

/**
 * @author Alexandr Stegnin
 */

public enum OwnerType {

    UNDEFINED(0, "Не определён"),
    INVESTOR(1, "Инвестор"),
    FACILITY(2, "Объект"),
    UNDER_FACILITY(3, "Подобъект"),
    ROOM(4, "Помещение");

    private final int id;

    private final String title;

    OwnerType(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}
