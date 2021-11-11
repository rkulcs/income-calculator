package com.incomecalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

public class CalculateIncomeActivity extends AppCompatActivity {

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

        setupFormComponents();
        setupResultDisplays();
    }

    //--- Event Listeners ---//

    public void submitQuery(View view) {


    }

    //--- Helper Methods ---//

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