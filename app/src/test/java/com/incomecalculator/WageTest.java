package com.incomecalculator;

import org.junit.Test;
import static org.junit.Assert.*;

import com.incomecalculator.wages.Currency;
import com.incomecalculator.wages.Wage;

public class WageTest {

    @Test
    public void calculateHourlyRate_isCorrect() {

        Currency dollar = new Currency("$", true, 100);
        Wage wage1 = new Wage("7.65", dollar);
        Wage wage2 = new Wage("8.2", dollar);

        assertEquals(765, wage1.getHourlyRate());
        assertEquals(820, wage2.getHourlyRate());

        Currency currency = new Currency("c", true, 60);
        Wage wage3 = new Wage("8.70", currency);

        assertEquals(550, wage3.getHourlyRate());

        Currency subunitless = new Currency("s", false, 0);
        Wage wage4 = new Wage("230", subunitless);

        assertEquals(230, wage4.getHourlyRate());
    }
}
