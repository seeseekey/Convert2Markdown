package net.seeseekey.wordpress2markdown;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class Utils {

    public static LocalDateTime convertToLocalDateTime(Date dateToConvert) {

        return dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}