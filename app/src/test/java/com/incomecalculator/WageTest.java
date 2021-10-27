package com.incomecalculator;

import org.junit.Test;
import static org.junit.Assert.*;

import com.incomecalculator.wages.Currency;
import com.incomecalculator.wages.Wage;

public class WageTest {

    Currency dollar = new Currency("$", true, 100);
    Currency currency = new Currency("c", true, 60);
    Currency subunitless = new Currency("s", false, 0);

    @Test
    public void calculateHourlyRate_isCorrect() {

        Wage wage1 = new Wage("7.65", dollar);
        assertEquals(765, wage1.getHourlyRate());

        Wage wage2 = new Wage("8.2", dollar);
        assertEquals(820, wage2.getHourlyRate());

        Wage wage3 = new Wage("8.70", currency);
        assertEquals(550, wage3.getHourlyRate());

        Wage wage4 = new Wage("230", subunitless);
        assertEquals(230, wage4.getHourlyRate());
    }

    @Test
    public void createHourlyRateString_isCorrect() {

        Wage wage1 = new Wage(550, currency);
        assertEquals("9.10", wage1.getHourlyRateString());

        Wage wage2 = new Wage(1000, dollar);
        assertEquals("10.00", wage2.getHourlyRateString());

        Wage wage3 = new Wage(789, subunitless);
        assertEquals("789", wage3.getHourlyRateString());

        Wage wage4 = new Wage(7893, dollar);
        assertEquals("78.93", wage4.getHourlyRateString());
    }
}
