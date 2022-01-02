package com.incomecalculator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.Spinner;

import com.google.android.material.navigation.NavigationBarView;
import com.incomecalculator.db.Contract;
import com.incomecalculator.db.DatabaseHelper;
import com.incomecalculator.shifts.Shift;

import java.util.ArrayList;
import java.util.Calendar;
import java.sql.Date;

/**
 * Displays the shifts which were added by the user to the database.
 */
public class ShiftListActivity extends AppCompatActivity {

    private SQLiteDatabase db;
    private Calendar calendar = Calendar.getInstance();

    private String[] months = new String[]{"January", "February", "March", "April", "May",
            "June", "July", "August", "September", "October", "November",
            "December"};
    private ArrayList<Shift> shifts;

    private Spinner monthSpinner;
    private NumberPicker yearPicker;

    private RecyclerView shiftList;
    private ShiftAdapter shiftAdapter;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift_list);

        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        db = dbHelper.getWritableDatabase();

        setupCalendar();
        setupDateSelectors();
        setupShiftList();
    }

    @Override
    protected void onResume() {

        super.onResume();
        updateShiftList();
    }

    //--- Event Listeners ---//

    /**
     * Retrieves all shifts within the selected month and year from the database.
     */
    public void getShifts() {

        Calendar lowerBound = Calendar.getInstance();
        lowerBound.set(yearPicker.getValue(), monthSpinner.getSelectedItemPosition(), 1);

        Calendar upperBound = Calendar.getInstance();
        upperBound.set(yearPicker.getValue(), monthSpinner.getSelectedItemPosition(),
                lowerBound.getActualMaximum(Calendar.DAY_OF_MONTH));

        shifts = Contract.ShiftInformation.getShifts(
                db, lowerBound.getTimeInMillis(), upperBound.getTimeInMillis());
    }

    //--- Helper Methods ---//

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
        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateShiftList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Add years to year picker and set default selection to the current year
        yearPicker = findViewById(R.id.year_picker);
        yearPicker.setMinValue(calendar.getMinimum(Calendar.YEAR));
        yearPicker.setMaxValue(calendar.getMaximum(Calendar.YEAR));
        yearPicker.setValue(calendar.get(Calendar.YEAR));
        yearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                updateShiftList();
            }
        });
    }

    /**
     * Sets up the RecyclerView containing shift information.
     */
    private void setupShiftList() {

        shiftList = findViewById(R.id.shift_list);
        layoutManager = new LinearLayoutManager(this);
        shiftList.setLayoutManager(layoutManager);

        updateShiftList();
    }

    /**
     * Retrieves shifts with the selected dates, and displays them in the
     * RecyclerView containing the list of shifts.
     */
    private void updateShiftList() {

        getShifts();
        shiftAdapter = new ShiftAdapter(shifts, db);
        shiftList.setAdapter(shiftAdapter);
    }
}