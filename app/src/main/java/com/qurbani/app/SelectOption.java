package com.qurbani.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SelectOption extends AppCompatActivity {

    private Button btnBookShare,btnViewAllShare;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_option);
        findViewById(R.id.btnBookShare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SelectOption.this, MainActivity.class));
            }
        });
        findViewById(R.id.btnViewAllShare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SelectOption.this, AllListActivity.class));
            }
        });
    }
}