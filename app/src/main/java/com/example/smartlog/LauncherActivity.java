package com.example.smartlog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        nothingSet();
    }

    public void nothingSet() {
        SQLiteQueries myDB = new SQLiteQueries(LauncherActivity.this);
        boolean notSet = myDB.serverNotSEt();
        myDB.close();
        if (notSet) {
            startActivity(new Intent(LauncherActivity.this, Fresh_Start.class));
        } else{
            startActivity(new Intent(LauncherActivity.this, BaseActivity.class));
        }
    }
}