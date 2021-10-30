package com.incomecalculator.shifts;

import android.os.Build;

import java.time.LocalTime;
import java.util.Date;

public class Shift {

    //--- Instance Variables ---//

    private Date start;
    private Date end;
    private int breakInMinutes;

    //--- Constructor ---//

    public Shift(Date start, Date end, int breakInMinutes) {

        this.start = start;
        this.end = end;
        this.breakInMinutes = breakInMinutes;
    }

    //--- Validation Methods ---//

    public boolean isValidBreakInMinutes(int breakInMinutes) {
        throw new UnsupportedOperationException();
    }

}
