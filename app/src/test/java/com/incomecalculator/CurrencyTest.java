package com.incomecalculator;

import org.junit.Test;
import static org.junit.Assert.*;

import com.incomecalculator.wages.Currency;

public class CurrencyTest {

    @Test
    public void isValidSymbol_isCorrect() {

        assertEquals(true, Currency.isValidSymbol("$"));
        assertEquals(true, Currency.isValidSymbol("CA$"));
        assertEquals(false, Currency.isValidSymbol(""));
        assertEquals(false, Currency.isValidSymbol(" "));
        assertEquals(false, Currency.isValidSymbol("0$"));
    }
}
