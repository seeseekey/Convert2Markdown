package net.seeseekey.convert2markdown.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * The DateTimeUtils class provides utility methods for handling date and time operations.
 */
public class DateTimeUtils {

    private DateTimeUtils() {
    }

    public static LocalDateTime convertToLocalDateTime(Date dateToConvert) {
        return dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}