package com.incomecalculator.shifts;

import android.database.sqlite.SQLiteDatabase;

import com.incomecalculator.db.Contract;

import java.sql.Date;
import java.sql.Time;
import java.util.Calendar;

/**
 * A collection of shift details, such as the start and end dates and times,
 * the number of paid minutes, and the number of unpaid minutes.
 */
public class Shift {

    //--- Instance Variables ---//

    private int id;
    private Date startDate;
    private Time startTime;
    private Date endDate;
    private Time endTime;

    /**
     * The minutes of the shift which are paid.
     */
    private int minutesWorked;

    /**
     * The minutes of the shift which are unpaid.
     */
    private int breakInMinutes;

    //--- Constructors ---//

    public Shift(Calendar start, Calendar end, int breakInMinutes) {

        long startInMillis = start.getTimeInMillis();
        long endInMillis = end.getTimeInMillis();

        this.startDate = new Date(startInMillis);
        this.startTime = new Time(startInMillis);
        this.endDate = new Date(endInMillis);
        this.endTime = new Time(endInMillis);
        this.minutesWorked = ((int) (endInMillis - startInMillis) / 1000 / 60) - breakInMinutes;
        this.breakInMinutes = breakInMinutes;
    }

    public Shift(int id, long start, long end, int breakInMinutes, int minutesWorked) {

        this.id = id;
        this.startDate = new Date(start);
        this.startTime = new Time(start);
        this.endDate = new Date(end);
        this.endTime = new Time(end);
        this.minutesWorked = minutesWorked;
        this.breakInMinutes = breakInMinutes;
    }

    //--- Getters ---//

    public Date getStartDate() {
        return startDate;
    }

    public Time getStartTime() {
        return startTime;
    }

    public Date getEndDate() {
        return endDate;
    }

    public Time getEndTime() {
        return endTime;
    }

    public int getBreakInMinutes() {
        return breakInMinutes;
    }

    //--- Main Methods ---//

    /**
     * Creates a String representation of the shift in the format
     * <DATE> | <START TIME> - <END TIME>.
     */
    public String toString() {
        return String.format("%s | %s - %s", getStartDate(), getStartTime(), getEndTime());
    }

    //--- Validation Methods ---//

    /**
     * Determines if the given break in minutes is valid by checking if it is
     * shorter than the shift's length.
     *
     * @param start The start date and time of the shift
     * @param end The end date and time of the shift
     * @param breakInMinutes The minutes of the shift which are not paid
     *
     * @return True if the break is valid, false otherwise
     */
    public static boolean isValidBreak(Calendar start, Calendar end, int breakInMinutes) {

        long startTime = start.getTimeInMillis();
        long endTime = end.getTimeInMillis();
        int shiftLength = (int) (endTime - startTime) / 1000 / 60;

        return (breakInMinutes < shiftLength);
    }

    //--- Database Interaction Methods ---//

    /**
     * Saves the details of the shift in the given database.
     *
     * @return True if the shift was successfully added, false otherwise
     */
    public boolean saveInDatabase(SQLiteDatabase db) {

        return Contract.ShiftInformation.addShift(db, startDate.getTime(),
                endDate.getTime(), breakInMinutes, minutesWorked);
    }

    public boolean deleteFromDatabase(SQLiteDatabase db) {
        return Contract.ShiftInformation.deleteShift(db, id);
    }
}
