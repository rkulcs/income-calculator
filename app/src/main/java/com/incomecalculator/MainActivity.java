package com.incomecalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.incomecalculator.db.DatabaseHelper;

public class MainActivity extends AppCompatActivity {

    public DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(getApplicationContext());
        dbHelper.getReadableDatabase();
    }
}