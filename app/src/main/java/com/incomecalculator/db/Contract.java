package com.incomecalculator.db;

import android.provider.BaseColumns;

public final class Contract {

    private Contract() {}

    public static class Currency implements BaseColumns {

        public static final String TABLE_NAME = "currencyInformation";

        public static final String COLUMN_NAME_SYMBOL = "symbol";
        public static final String COLUMN_NAME_HAS_SUBUNIT = "hasSubunit";
        public static final String COLUMN_NAME_CURRENCY_IN_SUBUNITS = "currencyInSubunits";

        public static final String SQL_CREATE_TABLE =
                String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY, " +
                                "%s TEXT, %s BOOLEAN, %s INTEGER)",
                        TABLE_NAME, _ID, COLUMN_NAME_SYMBOL, COLUMN_NAME_HAS_SUBUNIT,
                        COLUMN_NAME_CURRENCY_IN_SUBUNITS);
    }

    public static class WageInformation implements BaseColumns {

        public static final String TABLE_NAME = "wageInformation";

        public static final String COLUMN_NAME_HOURLY_RATE = "hourlyRate";
        public static final String COLUMN_NAME_CURRENCY = "currency";

        public static final String SQL_CREATE_TABLE =
                String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY, " +
                        "%s INTEGER, %s INTEGER, FOREIGN KEY (%s) REFERENCES %s(%s))",
                        TABLE_NAME, _ID, COLUMN_NAME_HOURLY_RATE, COLUMN_NAME_CURRENCY,
                        COLUMN_NAME_CURRENCY, Currency.TABLE_NAME, Currency._ID);
    }

    public static class Shift implements BaseColumns {

        public static final String TABLE_NAME = "shifts";

        public static final String COLUMN_NAME_START_DATETIME = "startDateTime";
        public static final String COLUMN_NAME_END_DATETIME = "endDateTime";
        public static final String COLUMN_BREAK_IN_MINUTES = "breakInMinutes";
        public static final String COLUMN_MINUTES_WORKED = "minutesWorked";
        public static final String COLUMN_WAGE_INFORMATION = "wageDetails";

        public static final String SQL_CREATE_TABLE =
                String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY, " +
                                "%s DATETIME, %s DATETIME, %s INTEGER, %s INTEGER, " +
                                "%s INTEGER, FOREIGN KEY (%s) REFERENCES %s(%s))",
                        TABLE_NAME, _ID, COLUMN_NAME_START_DATETIME,
                        COLUMN_NAME_END_DATETIME, COLUMN_BREAK_IN_MINUTES,
                        COLUMN_MINUTES_WORKED, COLUMN_WAGE_INFORMATION,
                        COLUMN_WAGE_INFORMATION, WageInformation.TABLE_NAME,
                        WageInformation._ID);
    }
}
