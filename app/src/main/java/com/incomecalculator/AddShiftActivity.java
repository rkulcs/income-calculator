package com.incomecalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.incomecalculator.db.DatabaseHelper;
import com.incomecalculator.shifts.Shift;

import java.util.Calendar;
import java.sql.Date;
import java.sql.Time;

public class AddShiftActivity extends AppCompatActivity {

    private SQLiteDatabase db;

    //--- Form Components ---//

    private TextInputLayout startDateField;
    private TextInputLayout startTimeField;
    private TextInputLayout endDateField;
    private TextInputLayout endTimeField;
    private TextInputLayout breakInMinutesField;
    private Button submitFormButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shift);

        db = DatabaseHelper.getDatabase(getApplicationContext());

        setupFormComponents();
    }

    //--- Event Listeners ---//

    public void submitShiftDetails(View view) {

        Calendar start = getDateTime(startDateField.getEditText().getText().toString(),
                startTimeField.getEditText().getText().toString());
        Calendar end = getDateTime(endDateField.getEditText().getText().toString(),
                endTimeField.getEditText().getText().toString());

        if (start == null || end == null || start.after(end)) {
            Toast.makeText(this, "Invalid date(s)", Toast.LENGTH_SHORT).show();
            return;
        }

        int breakInMinutes;

        try {
            breakInMinutes = Integer.parseInt(
                    breakInMinutesField.getEditText().getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid break", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidBreak(start, end, breakInMinutes)) {
            Toast.makeText(this, "Break is too long", Toast.LENGTH_SHORT).show();
            return;
        }

        Shift shift = new Shift(start, end, breakInMinutes);

        if (shift.saveInDatabase(db)) {
            Toast.makeText(this, "Shift added", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Could not add shift", Toast.LENGTH_SHORT).show();
        }

    }

    //--- Validation Methods ---//

    private boolean isValidBreak(Calendar start, Calendar end, int breakInMinutes) {

        long startTime = start.getTimeInMillis();
        long endTime = end.getTimeInMillis();
        int shiftLength = (int) (endTime - startTime) / 1000 / 60;

        return (breakInMinutes < shiftLength);
    }

    //--- Helper Methods ---//

    private Calendar getDateTime(String date, String time) {

        Calendar calendar = Calendar.getInstance();

        int[] dateComponents = getDateComponents(date);
        int[] timeComponents = getTimeComponents(time);

        if (dateComponents == null || timeComponents == null)
            return null;

        try {
            calendar.clear();
            calendar.set(dateComponents[0], dateComponents[1], dateComponents[2],
                    timeComponents[0], timeComponents[1]);
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }

        return calendar;
    }

    private int[] getDateComponents(String date) {

        int[] components = getIntegerComponents(date, "/", 3);

        // Adjust month value for Calendar instance representing the date
        if (components != null)
            components[1]--;

        return components;
    }

    private int[] getTimeComponents(String time) {
        return getIntegerComponents(time, ":", 2);
    }

    private int[] getIntegerComponents(String value, String separator, int numComponents) {

        String[] stringComponents = value.split(separator);

        if (stringComponents.length != numComponents)
            return null;

        int[] components = new int[numComponents];

        try {
            for (int i = 0; i < numComponents; i++) {
                components[i] = Integer.parseInt(stringComponents[i]);
            }
        } catch (NumberFormatException e) {
            return null;
        }

        return components;
    }

    private void setupFormComponents() {

        startDateField = findViewById(R.id.start_date_field);
        startTimeField = findViewById(R.id.start_time_field);
        endDateField = findViewById(R.id.end_date_field);
        endTimeField = findViewById(R.id.end_time_field);
        breakInMinutesField = findViewById(R.id.break_in_minutes_field);
        submitFormButton = findViewById(R.id.submit_shift_button);

        submitFormButton.setOnClickListener((view) -> submitShiftDetails(view));
    }
}