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
import com.incomecalculator.wages.Wage;

/**
 * Displays form for changing the hourly rate of pay and currency used for
 * calculating income.
 */
public class ChangeWageDetailsActivity extends AppCompatActivity {

    private SQLiteDatabase db;
    private Wage wage;
    private Currency currency;

    //--- Form Components ---//

    private TextInputLayout currencySymbolField;
    private CheckBox hasSubunitCheckbox;
    private TextInputLayout currencyInSubunitField;
    private TextInputLayout rateOfPayField;
    private Button submitFormButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_wage_details);

        db = DatabaseHelper.getDatabase(getApplicationContext());
        currency = Contract.CurrencyInformation.getCurrency(db);
        wage = Contract.WageInformation.getWage(db);

        setupFormComponents();
    }

    //--- Event Listeners ---//

    /**
     * Submits the details of the hourly rate of pay and currency used if the
     * inputs are valid. If the wage and currency are successfully updated,
     * the activity terminates.
     */
    public void submitWageDetails(View view) {

        String currencySymbol =
                currencySymbolField.getEditText().getText().toString().trim();
        boolean hasSubunit = hasSubunitCheckbox.isChecked();

        int currencyInSubunit;

        // Get the value of the currency in its subunit if it has one
        if (hasSubunit) {
            currencyInSubunit = parseCurrencyInSubunit(
                    currencyInSubunitField.getEditText().getText().toString());
        } else {
            currencyInSubunit = 0;
        }

        String hourlyWage = rateOfPayField.getEditText().getText().toString().trim();

        if (!validateInputs(currencySymbol, hasSubunit, currencyInSubunit, hourlyWage))
            return;

        currency = new Currency(currencySymbol, hasSubunit, currencyInSubunit);
        wage = new Wage(hourlyWage, currency);

        if (currency.saveInDatabase(db) && wage.saveInDatabase(db)) {
            Toast.makeText(
                    this, "Wage details updated", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    //--- Input Validation Methods ---//

    /**
     * Validates the form's inputs, and displays an error message upon
     * encountering an invalid input.
     *
     * @return True if all inputs are valid, false otherwise
     */
    private boolean validateInputs(String symbol, boolean hasSubunit,
                                   int currencyInSubunit, String hourlyWage) {

        if (!Currency.isValidSymbol(symbol)) {
            Toast.makeText(
                    this, "Invalid currency symbol", Toast.LENGTH_SHORT).show();
            return false;
        } else if (hasSubunit && currencyInSubunit <= 0) {
            Toast.makeText(
                    this, "Invalid value in subunit", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Wage.isValidHourlyRateString(hourlyWage, hasSubunit, currencyInSubunit)) {
            Toast.makeText(
                    this, "Invalid hourly rate of pay", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    //--- Helper Methods ---//

    /**
     * Converts the given value of the currency in its subunit to an integer.
     *
     * @return A non-negative integer if the given String is valid, -1 otherwise
     */
    private int parseCurrencyInSubunit(String input) {

        int value;

        try {
            value = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            value = -1;
        }

        return value;
    }

    /**
     * Obtains references to all input fields and the submit button of the form,
     * and sets up their event listeners.
     *
     * If the database contains wage and currency data, it is loaded into the
     * form.
     */
    private void setupFormComponents() {

        currencySymbolField = findViewById(R.id.currency_symbol_input);
        hasSubunitCheckbox = findViewById(R.id.has_subunit_checkbox);
        currencyInSubunitField = findViewById(R.id.value_in_subunit_input);
        rateOfPayField = findViewById(R.id.hourly_wage_input);
        submitFormButton = findViewById(R.id.update_wage_details_button);

        submitFormButton.setOnClickListener((view) -> submitWageDetails(view));

        if (currency != null) {
            currencySymbolField.getEditText().setText(currency.getSymbol());

            if (currency.hasSubunit())
                hasSubunitCheckbox.setChecked(true);

            currencyInSubunitField.getEditText().setText(
                    Integer.toString(currency.getValueInSubunit()));
        }

        if (wage != null) {
            rateOfPayField.getEditText().setText(wage.getHourlyRateString());
        }
    }
}