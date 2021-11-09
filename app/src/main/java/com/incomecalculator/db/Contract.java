package com.incomecalculator.db;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.incomecalculator.shifts.Shift;
import com.incomecalculator.wages.Currency;
import com.incomecalculator.wages.Wage;

import java.sql.Date;
import java.util.ArrayList;

public final class Contract {

    private static final int DEFAULT_ID = 1;

    private Contract() {}

    public static class CurrencyInformation implements BaseColumns {

        public static final String TABLE_NAME = "currencyInformation";

        public static final String COLUMN_NAME_SYMBOL = "symbol";
        public static final String COLUMN_NAME_HAS_SUBUNIT = "hasSubunit";
        public static final String COLUMN_NAME_CURRENCY_IN_SUBUNITS = "currencyInSubunits";

        public static final String SQL_CREATE_TABLE =
                String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY, " +
                                "%s TEXT, %s BOOLEAN, %s INTEGER)",
                        TABLE_NAME, _ID, COLUMN_NAME_SYMBOL, COLUMN_NAME_HAS_SUBUNIT,
                        COLUMN_NAME_CURRENCY_IN_SUBUNITS);

        public static boolean addCurrency(SQLiteDatabase db, String symbol,
                                          boolean hasSubunit, int currencyInSubunits) {

            if (getCurrency(db) != null)
                return updateCurrency(db, symbol, hasSubunit, currencyInSubunits);

            String query = String.format(
                    "INSERT INTO %s (%s, %s, %s) VALUES ('%s', '%b', %d)",
                    TABLE_NAME, COLUMN_NAME_SYMBOL, COLUMN_NAME_HAS_SUBUNIT,
                    COLUMN_NAME_CURRENCY_IN_SUBUNITS, symbol, hasSubunit,
                    currencyInSubunits);

            return executeQuery(db, query);
        }

        public static boolean updateCurrency(SQLiteDatabase db, String symbol,
                                             boolean hasSubunit, int currencyInSubunits) {

            String query = String.format(
                    "UPDATE %s SET %s = '%s', %s = '%b', %s = %d WHERE %s = 1",
                    TABLE_NAME, COLUMN_NAME_SYMBOL, symbol,
                    COLUMN_NAME_HAS_SUBUNIT, hasSubunit,
                    COLUMN_NAME_CURRENCY_IN_SUBUNITS, currencyInSubunits,
                    _ID
            );

            return executeQuery(db, query);
        }

        public static Currency getCurrency(SQLiteDatabase db) {

            String query = String.format("SELECT * FROM %s", TABLE_NAME);

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.getCount() == 0)
                return null;

            cursor.moveToFirst();

            String symbol = cursor.getString(
                    cursor.getColumnIndexOrThrow(COLUMN_NAME_SYMBOL));
            boolean hasSubunit =
                    cursor.getString(cursor.getColumnIndexOrThrow(
                            COLUMN_NAME_HAS_SUBUNIT)).equals("true");
            int valueInSubunits =
                    cursor.getInt(cursor.getColumnIndexOrThrow(
                            COLUMN_NAME_CURRENCY_IN_SUBUNITS));

            cursor.close();

            return new Currency(symbol, hasSubunit, valueInSubunits);
        }
    }

    public static class WageInformation implements BaseColumns {

        public static final String TABLE_NAME = "wageInformation";

        public static final String COLUMN_NAME_HOURLY_RATE = "hourlyRate";
        public static final String COLUMN_NAME_CURRENCY = "currency";

        public static final String SQL_CREATE_TABLE =
                String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY, " +
                        "%s INTEGER, %s INTEGER, FOREIGN KEY (%s) REFERENCES %s(%s))",
                        TABLE_NAME, _ID, COLUMN_NAME_HOURLY_RATE, COLUMN_NAME_CURRENCY,
                        COLUMN_NAME_CURRENCY, CurrencyInformation.TABLE_NAME,
                        CurrencyInformation._ID);

        public static Wage getWage(SQLiteDatabase db) {

            String query = String.format("SELECT * FROM %s", TABLE_NAME);

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.getCount() == 0)
                return null;

            cursor.moveToFirst();

            int hourlyRate = cursor.getInt(cursor.getColumnIndexOrThrow(
                    COLUMN_NAME_HOURLY_RATE));
            Currency currency = CurrencyInformation.getCurrency(db);

            if (currency == null)
                return null;

            cursor.close();

            return new Wage(hourlyRate, currency);
        }

        public static boolean addWage(SQLiteDatabase db, int hourlyRate) {

            if (getWage(db) != null)
                return updateWage(db, hourlyRate);

            String query = String.format(
                    "INSERT INTO %s (%s, %s) VALUES (%d, %d)",
                    TABLE_NAME, COLUMN_NAME_HOURLY_RATE, COLUMN_NAME_CURRENCY,
                    hourlyRate, DEFAULT_ID);

            return executeQuery(db, query);
        }

        public static boolean updateWage(SQLiteDatabase db, int hourlyRate) {

            String query = String.format(
                    "UPDATE %s SET %s = %d WHERE %s = 1",
                    TABLE_NAME, COLUMN_NAME_HOURLY_RATE, hourlyRate, _ID
            );

            return executeQuery(db, query);
        }
    }

    public static class ShiftInformation implements BaseColumns {

        public static final String TABLE_NAME = "shifts";

        public static final String COLUMN_NAME_START_DATETIME = "startDateTime";
        public static final String COLUMN_NAME_END_DATETIME = "endDateTime";
        public static final String COLUMN_NAME_BREAK_IN_MINUTES = "breakInMinutes";
        public static final String COLUMN_NAME_MINUTES_WORKED = "minutesWorked";
        public static final String COLUMN_NAME_WAGE_INFORMATION = "wageDetails";

        public static final String SQL_CREATE_TABLE =
                String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY, " +
                                "%s LONG, %s LONG, %s INTEGER, %s INTEGER, " +
                                "%s INTEGER, FOREIGN KEY (%s) REFERENCES %s(%s))",
                        TABLE_NAME, _ID, COLUMN_NAME_START_DATETIME,
                        COLUMN_NAME_END_DATETIME, COLUMN_NAME_BREAK_IN_MINUTES,
                        COLUMN_NAME_MINUTES_WORKED, COLUMN_NAME_WAGE_INFORMATION,
                        COLUMN_NAME_WAGE_INFORMATION, WageInformation.TABLE_NAME,
                        WageInformation._ID);

        public static boolean addShift(SQLiteDatabase db, long start, long end, int breakInMinutes,
                                       int minutesWorked) {

            String query = String.format(
                    "INSERT INTO %s (%s, %s, %s, %s, %s) VALUES ('%s', '%s', %d, %d, %d)",
                    TABLE_NAME, COLUMN_NAME_START_DATETIME, COLUMN_NAME_END_DATETIME,
                    COLUMN_NAME_BREAK_IN_MINUTES, COLUMN_NAME_MINUTES_WORKED,
                    COLUMN_NAME_WAGE_INFORMATION,
                    start, end, breakInMinutes, minutesWorked, DEFAULT_ID
            );

            return executeQuery(db, query);
        }

        public static boolean updateShift(SQLiteDatabase db, Shift shift) {

            String query = String.format(
                    "UPDATE %s SET %s = %d, %s = %d, %s = %d, %s = %d WHERE %s = %d",
                    TABLE_NAME, COLUMN_NAME_START_DATETIME, shift.getStartDate().getTime(),
                    COLUMN_NAME_END_DATETIME, shift.getEndDate().getTime(),
                    COLUMN_NAME_BREAK_IN_MINUTES, shift.getBreakInMinutes(),
                    COLUMN_NAME_MINUTES_WORKED, shift.getMinutesWorked(),
                    _ID, shift.getID());

            return executeQuery(db, query);
        }

        public static boolean deleteShift(SQLiteDatabase db, int id) {

            String query = String.format("DELETE FROM %s WHERE %s = %d",
                    TABLE_NAME, _ID, id);

            return executeQuery(db, query);
        }

        public static Shift getShift(SQLiteDatabase db, int id) {

            String query = String.format("SELECT * FROM %s WHERE %s = %d",
                    TABLE_NAME, _ID, id);

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.getCount() == 0)
                return null;

            cursor.moveToNext();
            return createShiftInstance(cursor);
        }

        /**
         * Retrieves all shifts from the table within the specified date and time range.
         *
         * @param lowerBound The smallest date and time value that a shift can have
         * @param upperBound The largest date and time value that a shift can have
         */
        public static ArrayList<Shift> getShifts(SQLiteDatabase db, long lowerBound, long upperBound) {

            String query = String.format(
                    "SELECT * FROM %s WHERE %s >= %d AND %s <= %d",
                    TABLE_NAME, COLUMN_NAME_START_DATETIME, lowerBound,
                    COLUMN_NAME_START_DATETIME, upperBound);

            Cursor cursor = db.rawQuery(query, null);

            ArrayList<Shift> shifts = new ArrayList<>();

            while (cursor.moveToNext()) {
                shifts.add(createShiftInstance(cursor));
            }

            return shifts;
        }

        /**
         * Creates a new instance of Shift based on the data in the current row
         * of the given cursor.
         */
        public static Shift createShiftInstance(Cursor cursor) {

            int id = cursor.getInt(cursor.getColumnIndexOrThrow(_ID));
            long start = cursor.getLong(cursor.getColumnIndexOrThrow(
                    COLUMN_NAME_START_DATETIME));
            long end = cursor.getLong(cursor.getColumnIndexOrThrow(
                    COLUMN_NAME_END_DATETIME));
            int breakInMinutes = cursor.getInt(cursor.getColumnIndexOrThrow(
                    COLUMN_NAME_BREAK_IN_MINUTES));
            int minutesWorked = cursor.getInt(cursor.getColumnIndexOrThrow(
                    COLUMN_NAME_MINUTES_WORKED));

            return new Shift(id, start, end, breakInMinutes, minutesWorked);
        }
    }

    private static boolean executeQuery(SQLiteDatabase db, String query) {

        try {
            db.execSQL(query);
        } catch (SQLException e) {
            return false;
        }

        return true;
    }
}
