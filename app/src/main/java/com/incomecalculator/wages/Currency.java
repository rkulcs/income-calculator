package com.incomecalculator.wages;

import android.database.sqlite.SQLiteDatabase;

import com.incomecalculator.db.Contract;

/**
 * Contains details about a currency, such as its symbol, whether it has
 * a subunit, and it value in that subunit.
 */
public class Currency {

    private String symbol;
    private boolean hasSubunit;
    private int valueInSubunit;

    //--- Constructor ---//

    public Currency(String symbol, boolean hasSubunit, int valueInSubunit) {

        this.symbol = symbol;
        this.hasSubunit = hasSubunit;
        this.valueInSubunit = valueInSubunit;
    }

    //--- Getters and Setters ---//

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {

        if (isValidSymbol(symbol))
            this.symbol = symbol;
    }

    public boolean hasSubunit() {
        return hasSubunit;
    }

    public void setHasSubunit(boolean hasSubunit) {

        if (!hasSubunit)
            valueInSubunit = 0;

        this.hasSubunit = hasSubunit;
    }

    public int getValueInSubunit() {
        return valueInSubunit;
    }

    public void setValueInSubunit(int valueInSubunit) {

        if (hasSubunit && valueInSubunit > 0)
            this.valueInSubunit = valueInSubunit;
    }

    //--- Validation Methods ---//

    public static boolean isValidSymbol(String symbol) {

        for (int i = 0; i < symbol.length(); i++) {
            if (Character.isDigit(symbol.charAt(i)))
                return false;
        }

        return (symbol.length() > 0);
    }

    //--- Database Interaction Methods ---//

    public boolean saveInDatabase(SQLiteDatabase db) {

        if (hasSubunit && valueInSubunit <= 0)
            return false;

        return Contract.Currency.addCurrency(db, symbol, hasSubunit, valueInSubunit);
    }
}
