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
import com.incomecalculator.util.DateTimeParser;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Displays form for adding a new shift to the database, or editing a saved
 * shift's details.
 */
public class ModifyShiftActivity extends AppCompatActivity {

    /**
     * The type of operation to be performed on the list of shifts.
     */
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

        Calendar start = DateTimeParser.getDateTime(
                startDateField.getEditText().getText().toString(),
                startTimeField.getEditText().getText().toString());
        Calendar end = DateTimeParser.getDateTime(
                endDateField.getEditText().getText().toString(),
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

        displayStatusMessage(successfulModification);

        if (successfulModification)
            finish();
    }

    //--- Helper Methods ---//

    /**
     * Informs the user about whether the shift was successfully added or updated.
     */
    private void displayStatusMessage(boolean successfulModification) {

        String successMessage = Type.ADD.equals(type) ? "Shift added" : "Shift updated";
        String failureMessage = Type.ADD.equals(type) ? "Could not add shift"
                : "Could not update shift";

        if (successfulModification)
            Toast.makeText(this, successMessage, Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, failureMessage, Toast.LENGTH_SHORT).show();
    }

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