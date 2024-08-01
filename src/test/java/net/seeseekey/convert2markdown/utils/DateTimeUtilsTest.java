package net.seeseekey.convert2markdown.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The DateTimeUtilsTest class presents JUnit tests for the DateTimeUtils utility class.
 */

class DateTimeUtilsTest {

    /**
     * Test for the convertToLocalDateTime method of the DateTimeUtils class.
     * The test verifies that the method correctly converts Date objects to LocalDateTime instances.
     */
    @Test
    void testConvertToLocalDateTime() {

        // Initialize a date.
        Date sampleDate = new Date();

        // Convert the date to local date time using ZonedDateTime.
        LocalDateTime expectedLocalDateTime = ZonedDateTime.ofInstant(sampleDate.toInstant(), ZoneId.systemDefault()).toLocalDateTime();

        // Convert the date to local date time using the method to be tested.
        LocalDateTime actualLocalDateTime = DateTimeUtils.convertToLocalDateTime(sampleDate);

        // Check that the actual result is what is expected.
        assertEquals(expectedLocalDateTime, actualLocalDateTime);
    }
}