package com.ddkolesnik.ddkapi.util;

/**
 * @author Alexandr Stegnin
 */

public enum Region {

    TMN("072", "Тюмень");

    private final String number;

    private final String name;

    Region(String number, String name) {
        this.number = number;
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

}
