package com.incomecalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.incomecalculator.db.Contract;
import com.incomecalculator.db.DatabaseHelper;
import com.incomecalculator.shifts.Shift;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Displays form for adding a new shift to the database, or editing a saved
 * shift's details.
 */
public class ModifyShiftActivity extends AppCompatActivity {

    public enum Type {
        ADD,
        EDIT
    }

    private SQLiteDatabase db;
    private Serializable type;
    private Shift savedShift;

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
        setContentView(R.layout.activity_modify_shift);

        // Check whether a shift will be added or edited
        type = getIntent().getSerializableExtra("type");

        db = DatabaseHelper.getDatabase(getApplicationContext());

        // Get the details of the shift to edit if needed
        if (Type.EDIT.equals(type)) {
            savedShift = Contract.ShiftInformation.getShift(db,
                    getIntent().getIntExtra("shiftID", 0));

            // Terminate if the given shift is not valid
            if (savedShift == null)
                finish();
        }

        setupFormComponents();
    }

    //--- Event Listeners ---//

    /**
     * Adds the shift with the given details to the database if the form inputs
     * are valid. If the shift is successfully added, the user is informed and
     * the activity is terminated.
     */
    public void submitShiftDetails(View view) {

        Calendar start = getDateTime(startDateField.getEditText().getText().toString(),
                startTimeField.getEditText().getText().toString());
        Calendar end = getDateTime(endDateField.getEditText().getText().toString(),
                endTimeField.getEditText().getText().toString());
        int breakInMinutes = getBreakInMinutes();

        if (!validateInputs(start, end, breakInMinutes))
            return;

        // Try to save the shift in the database
        Shift shift = new Shift(start, end, breakInMinutes);

        if (Type.EDIT.equals(type))
            shift.setID(savedShift.getID());

        boolean successfulModification = Type.ADD.equals(type) ? shift.saveInDatabase(db)
                                                               : shift.updateInDatabase(db);

        String successMessage = Type.ADD.equals(type) ? "Shift added" : "Shift updated";
        String failureMessage = Type.ADD.equals(type) ? "Could not add shift"
                                                      : "Could not update shift";

        if (successfulModification) {
            Toast.makeText(this, successMessage, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, failureMessage, Toast.LENGTH_SHORT).show();
        }
    }

    //--- Helper Methods ---//

    /**
     * Checks if the parsed inputs of the form are valid, and displays an error
     * message if an invalid input is encountered.
     *
     * @return True if the inputs are valid, false otherwise
     */
    private boolean validateInputs(Calendar start, Calendar end, int breakInMinutes) {

        // Validate the shift's start and end dates and times
        if (start == null || end == null || start.after(end)) {
            Toast.makeText(
                    this, "Invalid time period", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate the break's length
        if (!Shift.isValidBreak(start, end, breakInMinutes)) {
            Toast.makeText(this, "Break is too long", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    /**
     * Parses and returns the inputted break in minutes.
     *
     * @return A non-negative break in minutes if the input is valid, -1 otherwise
     */
    private int getBreakInMinutes() {

        int breakInMinutes;

        // Validate the formatting of the break in minutes
        try {
            breakInMinutes = Integer.parseInt(
                    breakInMinutesField.getEditText().getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid break", Toast.LENGTH_SHORT).show();
            return -1;
        }

        return breakInMinutes;
    }

    /**
     * Parses the given date and time Strings, and initializes a Calendar instance
     * with them if they are valid.
     *
     * @return An instance of Calendar with the given date and time if the
     *         date and time Strings are valid, null otherwise
     */
    private Calendar getDateTime(String date, String time) {

        Calendar calendar = Calendar.getInstance();

        // Parse the date and time Strings
        int[] dateComponents = getDateComponents(date);
        int[] timeComponents = getTimeComponents(time);

        // Check that both the date and time have integer components only
        if (dateComponents == null || timeComponents == null)
            return null;

        /* Set the Calendar instance's date and time; return null if the date or time
           components are invalid */
        try {
            calendar.clear();
            calendar.set(dateComponents[0], dateComponents[1], dateComponents[2],
                    timeComponents[0], timeComponents[1]);
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }

        return calendar;
    }

    /**
     * Identifies the integer components of the given String which represents a date
     * in YYYY/MM/DD format.
     *
     * @return An integer array of size 3, where the first entry is the year,
     *         the second entry is the month, and the last entry is the day if
     *         the String contains a valid date, null otherwise
     */
    private int[] getDateComponents(String date) {

        int[] components = getIntegerComponents(date, "/", 3);

        // Adjust month value for Calendar instance representing the date
        if (components != null)
            components[1]--;

        return components;
    }

    /**
     * Identifies the integer components of the given String which represents
     * a time in HH:MM format.
     *
     * @return An integer array where the first entry is the hour, and the last
     *         entry represents the minutes if the given String is a valid time,
     *         null otherwise
     */
    private int[] getTimeComponents(String time) {
        return getIntegerComponents(time, ":", 2);
    }

    /**
     * Gets the integer components of the given String after splitting it
     * at occurrences of the given separator regex.
     *
     * @param value The String value whose integer components will be identified
     * @param separator The value at which the String should be split up
     * @param numComponents The expected number of components
     *
     * @return The integer components of the String all of its components are
     * integers and the number of components matches the expected number of
     * components, null otherwise
     */
    private int[] getIntegerComponents(String value, String separator, int numComponents) {

        String[] stringComponents = value.trim().split(separator);

        // Check if the number of components is equal to the expected value
        if (stringComponents.length != numComponents)
            return null;

        int[] components = new int[numComponents];

        // Attempt to parse all integer components
        try {
            for (int i = 0; i < numComponents; i++) {
                components[i] = Integer.parseInt(stringComponents[i]);
            }
        } catch (NumberFormatException e) {
            return null;
        }

        return components;
    }

    /**
     * Obtains references to all input fields and the submit button of the form,
     * and sets up their event listeners.
     */
    private void setupFormComponents() {

        startDateField = findViewById(R.id.start_date_field);
        startTimeField = findViewById(R.id.start_time_field);
        endDateField = findViewById(R.id.end_date_field);
        endTimeField = findViewById(R.id.end_time_field);
        breakInMinutesField = findViewById(R.id.break_in_minutes_field);
        submitFormButton = findViewById(R.id.submit_shift_button);

        submitFormButton.setOnClickListener((view) -> submitShiftDetails(view));

        // Modify the form if an existing shift is being edited
        if (Type.EDIT.equals(type)) {
            submitFormButton.setText("Update Shift");
            loadShiftData();
        }
    }

    /**
     * If a shift is being edited, load its data into the form.
     */
    private void loadShiftData() {

        if (savedShift == null)
            throw new IllegalStateException("Shift data is unavailable.");

        startDateField.getEditText().setText(savedShift.getStartDateString());
        startTimeField.getEditText().setText(savedShift.getStartTimeString());
        endDateField.getEditText().setText(savedShift.getEndDateString());
        endTimeField.getEditText().setText(savedShift.getEndTimeString());
        breakInMinutesField.getEditText().setText(Integer.toString(savedShift.getBreakInMinutes()));
    }

}