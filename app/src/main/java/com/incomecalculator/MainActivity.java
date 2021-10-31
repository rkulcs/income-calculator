package com.incomecalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.incomecalculator.db.DatabaseHelper;

public class MainActivity extends AppCompatActivity {

    private Button changeWageDetailsButton;
    private Button addShiftButton;
    private Button viewShiftsButton;
    private Button calculateIncomeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeDatabase();
        initializeButtons();
    }

    //--- Activity Launchers ---//

    public void launchChangeWageDetailsActivity(View view) {

        Intent intent = new Intent(this, ChangeWageDetailsActivity.class);
        startActivity(intent);
    }

    public void launchAddShiftActivity(View view) {

        Intent intent = new Intent(this, AddShiftActivity.class);
        startActivity(intent);
    }

    /**
     * Obtains references to all buttons in the main menu, and sets up their
     * event listeners.
     */
    private void initializeButtons() {

        changeWageDetailsButton = findViewById(R.id.change_wage_details_button);
        addShiftButton = findViewById(R.id.add_shift_button);
        viewShiftsButton = findViewById(R.id.view_shifts_button);
        calculateIncomeButton = findViewById(R.id.calculate_income_button);

        changeWageDetailsButton.setOnClickListener((view) -> {
            launchChangeWageDetailsActivity(view);
        });

        addShiftButton.setOnClickListener((view) -> {
            launchAddShiftActivity(view);
        });
    }

    /**
     * Creates a database for the application if it does not have one.
     */
    private void initializeDatabase() {

        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        dbHelper.getReadableDatabase().close();
    }
}