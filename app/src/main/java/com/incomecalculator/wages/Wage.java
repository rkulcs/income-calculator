package com.incomecalculator.wages;

import android.database.ContentObservable;
import android.database.sqlite.SQLiteDatabase;

import com.incomecalculator.db.Contract;

/**
 * Represents details about the hourly wage, such as the hourly rate of pay and
 * the currency in which the wage is paid.
 */
public class Wage {

    //--- Instance Variables ---//

    /**
     * The hourly rate of pay in the currency or its subunit if it has one.
     */
    int hourlyRate;

    /**
     * String representation of the hourly rate in terms of the currency used.
     */
    String hourlyRateString;

    /**
     * The currency in which the wage is paid.
     */
    Currency currency;

    //--- Constructors ---//

    public Wage(String hourlyRateString, Currency currency) {

        this.currency = currency;
        this.hourlyRateString = hourlyRateString;
        this.hourlyRate = calculateHourlyRate(hourlyRateString);
    }

    public Wage(int hourlyRate, Currency currency) {

        this.currency = currency;
        this.hourlyRate = hourlyRate;
        this.hourlyRateString = createHourlyRateString(hourlyRate);
    }

    //--- Getters ---//

    public int getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(int hourlyRate) {

        this.hourlyRate = hourlyRate;
        this.hourlyRateString = createHourlyRateString(hourlyRate);
    }

    public String getHourlyRateString() {
        return hourlyRateString;
    }

    public void setHourlyRateString(String hourlyRateString) {

        this.hourlyRateString = hourlyRateString;
        this.hourlyRate = calculateHourlyRate(hourlyRateString);
    }

    public Currency getCurrency() {
        return currency;
    }

    //--- Validation Methods ---//

    /**
     * Checks whether the given hourly rate of pay String is valid based on
     * its currency's subunit's value.
     */
    public static boolean isValidHourlyRateString(String hourlyRateString,
                                                  boolean hasSubunit, int valueInSubunits) {

        // The number of digits allowed after the decimal point
        int maxDigitsAfterPoint = hasSubunit ?
                Integer.toString(valueInSubunits - 1).length() : 0;
        int digitsAfterPointFound = 0;

        // Keeps track of whether a decimal point is found in the String
        boolean decimalPointFound = false;

        for (int i = 0; i < hourlyRateString.length(); i++) {
            char currentCharacter = hourlyRateString.charAt(i);

            if (Character.isDigit(currentCharacter) && decimalPointFound) {
                digitsAfterPointFound++;

                if (digitsAfterPointFound > maxDigitsAfterPoint)
                    return false;
            } else if (currentCharacter == '.') {
                if (decimalPointFound)
                    return false;
                else
                    decimalPointFound = true;
            } else if (!Character.isDigit(currentCharacter)) {
                return false;
            }
        }

        return true;
    }

    public boolean isValidHourlyRateString(String hourlyRateString) {

        return isValidHourlyRateString(hourlyRateString, currency.hasSubunit(),
                currency.getValueInSubunit());
    }

    //--- Database Interaction Methods ---//

    public boolean saveInDatabase(SQLiteDatabase db) {

        return Contract.WageInformation.addWage(db, hourlyRate);
    }

    //--- Helper Methods ---//

    /**
     * Calculates the hourly rate of pay in terms of the currency used, or in
     * terms of the currency's subunit if it has one.
     */
    private int calculateHourlyRate(String hourlyRateString) {

        if (!isValidHourlyRateString(hourlyRateString))
            throw new IllegalArgumentException("Invalid hourly rate.");

        if (!currency.hasSubunit())
            return Integer.parseInt(hourlyRateString);

        // Split value at the decimal point
        String[] components = hourlyRateString.split("\\.");

        components[1] = padWithZeroes(components[1]);

        int hourlyRate = Integer.parseInt(components[0]) * currency.getValueInSubunit()
                + Integer.parseInt(components[1]);

        return hourlyRate;
    }

    /**
     * Creates a String which represents the hourly rate of pay in terms of
     * both the currency and its subunit (if it has one).
     */
    private String createHourlyRateString(int hourlyRate) {

        if (!currency.hasSubunit())
            return Integer.toString(hourlyRate);

        String units = Integer.toString(hourlyRate / currency.getValueInSubunit());
        String subunits = Integer.toString(hourlyRate % currency.getValueInSubunit());
        subunits = padWithZeroes(subunits);

        return units + "." + subunits;
    }

    /**
     * Pads the end of the subunit component of a String with zeroes if needed.
     */
    private String padWithZeroes(String value) {

        // Get the expected number of digits after the decimal point
        int digitsAfterPoint = Integer.toString(
                currency.getValueInSubunit() - 1).length();

        while (value.length() < digitsAfterPoint)
            value += "0";

        return value;
    }
}
