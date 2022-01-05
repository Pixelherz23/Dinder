package com.example.dinder.Activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.dinder.R;

public class Accountverwaltung extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountverwaltung);


        Button back = findViewById(R.id.button11);
        Button daten = findViewById(R.id.button12);
        Button mitgliedschaft = findViewById(R.id.button13);
        Button abmelden = findViewById(R.id.button14);

        back.setOnClickListener(this);
        daten.setOnClickListener(this);
        mitgliedschaft.setOnClickListener(this);
        abmelden.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button11) {
            Intent intent1= new Intent(this, StartSeite.class);
            startActivity(intent1);
        } else if (v.getId() == R.id.button12) {
            Intent intent2= new Intent(this, Benutzerdaten.class);
            startActivity(intent2);
        }
        else if (v.getId() == R.id.button13) {
            Intent intent3 = new Intent(this, Mitgliedschaft.class);
            startActivity(intent3);
        }
        else if (v.getId() == R.id.button14) {
            Intent intent4 = new Intent(this, Login.class);
            startActivity(intent4);
        }
    }
}