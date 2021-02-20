package com.ddkolesnik.ddkapi.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Класс для работы с датами
 *
 * @author Alexandr Stegnin
 */

public class DateUtils {

    private DateUtils() {}

    public static Date convert(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

}
