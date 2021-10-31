package com.incomecalculator.shifts;

import android.database.sqlite.SQLiteDatabase;

import com.incomecalculator.db.Contract;

import java.sql.Date;
import java.sql.Time;
import java.util.Calendar;

public class Shift {

    //--- Instance Variables ---//

    private Date startDate;
    private Time startTime;
    private Date endDate;
    private Time endTime;
    private int minutesWorked;
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

    //--- Database Interaction Methods ---//

    public boolean saveInDatabase(SQLiteDatabase db) {

        return Contract.ShiftInformation.addShift(db, startDate.toString(), startTime.toString(),
                endDate.toString(), endTime.toString(), breakInMinutes, minutesWorked);
    }
}
