package com.incomecalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.incomecalculator.db.Contract;
import com.incomecalculator.db.DatabaseHelper;
import com.incomecalculator.wages.Currency;

public class ChangeWageDetailsActivity extends AppCompatActivity {

    SQLiteDatabase db;

    //--- Form Components ---//

    TextInputLayout currencySymbolField;
    CheckBox hasSubunitCheckbox;
    TextInputLayout currencyInSubunitField;
    TextInputLayout rateOfPayField;
    Button submitFormButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_wage_details);

        setupFormComponents();
        openDatabase();
    }

    //--- Event Listeners ---//

    public void submitWageDetails(View view) {

        String currencySymbol =
                currencySymbolField.getEditText().getText().toString().trim();
        boolean hasSubunit = hasSubunitCheckbox.isChecked();

        int currencyInSubunit;

        if (hasSubunit) {
            currencyInSubunit = parseCurrencyInSubunit(
                    currencyInSubunitField.getEditText().getText().toString());
        } else {
            currencyInSubunit = 0;
        }

        String hourlyWage = rateOfPayField.getEditText().getText().toString().trim();

        Currency currency = new Currency(currencySymbol, hasSubunit, currencyInSubunit);

        if (!validateInputs(currencySymbol, hasSubunit, currencyInSubunit, hourlyWage))
            return;

        if (currency.saveInDatabase(db)) {
            finish();
        }
    }

    //--- Input Validation Methods ---//

    private boolean validateInputs(String symbol, boolean hasSubunit,
                                   int currencyInSubunit, String hourlyWage) {

        if (!Currency.isValidSymbol(symbol)) {
            Toast.makeText(this, "Invalid currency symbol", Toast.LENGTH_SHORT);
            return false;
        } else if (hasSubunit && currencyInSubunit <= 0) {
            Toast.makeText(this, "Invalid value in subunit", Toast.LENGTH_SHORT);
            return false;
        } else if (!isValidHourlyWage(hourlyWage)) {
            Toast.makeText(this, "Invalid hourly rate of pay", Toast.LENGTH_SHORT);
            return false;
        }

        return true;
    }

    private boolean isValidHourlyWage(String hourlyWage) {

        int periodCount = 0;

        for (int i = 0; i < hourlyWage.length(); i++) {
            char currentCharacter = hourlyWage.charAt(i);

            if (currentCharacter == '.') {
                periodCount++;

                if (periodCount > 1)
                    return false;
            } else if (!Character.isDigit(currentCharacter)) {
                return false;
            }
        }

        return (hourlyWage.length() > 0);
    }

    //--- Helper Methods ---//

    private int parseCurrencyInSubunit(String input) {

        int value;

        try {
            value = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            value = -1;
        }

        return value;
    }

    private void setupFormComponents() {

        currencySymbolField = findViewById(R.id.currency_symbol_input);
        hasSubunitCheckbox = findViewById(R.id.has_subunit_checkbox);
        currencyInSubunitField = findViewById(R.id.value_in_subunit_input);
        rateOfPayField = findViewById(R.id.hourly_wage_input);
        submitFormButton = findViewById(R.id.update_wage_details_button);

        submitFormButton.setOnClickListener((view) -> submitWageDetails(view));
    }

    private void openDatabase() {

        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        db = dbHelper.getWritableDatabase();
    }
}