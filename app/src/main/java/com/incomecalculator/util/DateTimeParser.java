package com.incomecalculator.util;

import java.util.Calendar;

/**
 * A collection of methods for parsing Strings representing dates and times.
 */
public final class DateTimeParser {

    /**
     * Parses the given date and time Strings, and initializes a Calendar instance
     * with them if they are valid.
     *
     * @return An instance of Calendar with the given date and time if the
     *         date and time Strings are valid, null otherwise
     */
    public static Calendar getDateTime(String date, String time) {

        Calendar calendar = Calendar.getInstance();

        // Parse the date and time Strings
        int[] dateComponents = getDateComponents(date);
        int[] timeComponents = getTimeComponents(time);

        // Check that both the date and time have integer components only
        if (dateComponents == null || timeComponents == null)
            return null;

        /* Set the Calendar instance's date and time; return null if the date or time
           components are invalid */
        try {
            calendar.clear();
            calendar.set(dateComponents[0], dateComponents[1], dateComponents[2],
                    timeComponents[0], timeComponents[1]);
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }

        return calendar;
    }

    /**
     * Identifies the integer components of the given String which represents a date
     * in YYYY/MM/DD format.
     *
     * @return An integer array of size 3, where the first entry is the year,
     *         the second entry is the month, and the last entry is the day if
     *         the String contains a valid date, null otherwise
     */
    public static int[] getDateComponents(String date) {

        int[] components = getIntegerComponents(date, "/", 3);

        // Adjust month value for Calendar instance representing the date
        if (components != null)
            components[1]--;

        return components;
    }

    /**
     * Identifies the integer components of the given String which represents
     * a time in HH:MM format.
     *
     * @return An integer array where the first entry is the hour, and the last
     *         entry represents the minutes if the given String is a valid time,
     *         null otherwise
     */
    public static int[] getTimeComponents(String time) {
        return getIntegerComponents(time, ":", 2);
    }

    /**
     * Gets the integer components of the given String after splitting it
     * at occurrences of the given separator regex.
     *
     * @param value The String value whose integer components will be identified
     * @param separator The value at which the String should be split up
     * @param numComponents The expected number of components
     *
     * @return The integer components of the String all of its components are
     * integers and the number of components matches the expected number of
     * components, null otherwise
     */
    public static int[] getIntegerComponents(String value, String separator, int numComponents) {

        String[] stringComponents = value.trim().split(separator);

        // Check if the number of components is equal to the expected value
        if (stringComponents.length != numComponents)
            return null;

        int[] components = new int[numComponents];

        // Attempt to parse all integer components
        try {
            for (int i = 0; i < numComponents; i++) {
                components[i] = Integer.parseInt(stringComponents[i]);
            }
        } catch (NumberFormatException e) {
            return null;
        }

        return components;
    }

}
