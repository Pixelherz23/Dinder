package com.example.dinder.Activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.dinder.Activitys.Accountverwaltung;
import com.example.dinder.R;

public class Mitgliedschaft extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mitgliedschaft);

        Button safe = findViewById(R.id.button15);
        Button back = findViewById(R.id.button16);
        safe.setOnClickListener(this);
        back.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button15) {
            Intent intent1 =new Intent(this, Accountverwaltung.class);
            startActivity(intent1);
        } else if (v.getId() == R.id.button16){
            Intent intent2 = new Intent(this, Accountverwaltung.class);
            startActivity(intent2);
        }
    }
}