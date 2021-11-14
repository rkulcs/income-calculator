package com.incomecalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.incomecalculator.db.Contract;
import com.incomecalculator.db.DatabaseHelper;
import com.incomecalculator.util.DateTimeParser;

import java.util.Calendar;
import com.incomecalculator.wages.Currency;
import com.incomecalculator.wages.Wage;

public class CalculateIncomeActivity extends AppCompatActivity {

    SQLiteDatabase db;

    //--- Form Components ---//

    private TextInputLayout minimumDateInput;
    private TextInputLayout maximumDateInput;
    private Button calculateIncomeButton;

    //--- Result Displays ---//

    private TextView expectedIncomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_income);

        db = DatabaseHelper.getDatabase(getApplicationContext());

        setupFormComponents();
        setupResultDisplays();
    }

    //--- Event Listeners ---//

    public void submitQuery(View view) {

        Calendar start = DateTimeParser.getDate(
                minimumDateInput.getEditText().getText().toString(), true);
        long min = start.getTimeInMillis();

        Calendar end = DateTimeParser.getDate(
                maximumDateInput.getEditText().getText().toString(), false);
        long max = end.getTimeInMillis();

        if (start.after(end)) {
            Toast.makeText(this, "Invalid range of dates", Toast.LENGTH_SHORT).show();
            return;
        }

        // Display result
        expectedIncomeText.setText("Expected income: " + calculateIncome(min, max));
    }

    //--- Helper Methods ---//

    /**
     * Calculates the income over the given time period, and returns a String
     * representation of this value.
     */
    private String calculateIncome(long min, long max) {

        int minutesWorked = Contract.ShiftInformation.getMinutesWorked(db, min, max);
        Wage wage = Contract.WageInformation.getWage(db);

        // Get the number of full hours worked
        int hoursWorked = minutesWorked / 60;

        // Get the remaining minutes worked
        int remainingMinutes = minutesWorked % 60;

        // Calculate income for the full hours worked
        int income = hoursWorked * wage.getHourlyRate();

        // Add income for remaining minutes worked; this value is rounded down
        income += (((double) remainingMinutes) / 60.0) * wage.getHourlyRate();

        return wage.getCurrency().getSymbol() + wage.createHourlyRateString(income);
    }

    /**
     * Retrieves references to the form's components.
     */
    private void setupFormComponents() {

        minimumDateInput = findViewById(R.id.minimum_date_field);
        maximumDateInput = findViewById(R.id.maximum_date_field);
        calculateIncomeButton = findViewById(R.id.calculate_expected_income_button);

        calculateIncomeButton.setOnClickListener((view) -> submitQuery(view));
    }

    /**
     * Retrieves references to the components which display the query's results.
     */
    private void setupResultDisplays() {
        expectedIncomeText = findViewById(R.id.expected_income_text);
    }
}