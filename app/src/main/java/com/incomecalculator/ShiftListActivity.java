package com.incomecalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.Spinner;

import java.util.Calendar;
import java.util.Date;

/**
 * Displays the shifts which were added by the user to the database.
 */
public class ShiftListActivity extends AppCompatActivity {

    private Calendar calendar = Calendar.getInstance();

    private String[] months = new String[]{"January", "February", "March", "April", "May",
            "June", "July", "August", "September", "October", "November",
            "December"};

    private Spinner monthSpinner;
    private NumberPicker yearPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift_list);

        setupCalendar();
        setupDateSelectors();
    }

    /**
     * Sets the calendar's date and time to the current date and time.
     */
    private void setupCalendar() {
        calendar.setTime(new Date(System.currentTimeMillis()));
    }

    /**
     * Sets up the components which allow the user to select the month and
     * year of the shifts to display.
     */
    private void setupDateSelectors() {

        ArrayAdapter<String> monthArrayAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, months);

        // Add months to spinner and set default selection to the current month
        monthSpinner = findViewById(R.id.month_spinner);
        monthSpinner.setAdapter(monthArrayAdapter);
        monthSpinner.setSelection(calendar.get(Calendar.MONTH));

        // Add years to year picker and set default selection to the current year
        yearPicker = findViewById(R.id.year_picker);
        yearPicker.setMinValue(calendar.getMinimum(Calendar.YEAR));
        yearPicker.setMaxValue(calendar.getMaximum(Calendar.YEAR));
        yearPicker.setValue(calendar.get(Calendar.YEAR));
    }
}