package com.example.dinder.Activitys;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.dinder.API.RetrofitAPI;
import com.example.dinder.Datamodel.TierDaten;
import com.example.dinder.R;


import java.util.ArrayList;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Partnersuche extends AppCompatActivity implements View.OnClickListener, LocationListener {

    private LocationManager locationManager;


    String token, selectedProfil;

    Button button;

    EditText text, text2, text3;

    ArrayList<TierDaten> arrayList;

    Intent intent1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partnersuche);
        Button search = findViewById(R.id.button27);
        Button back = findViewById(R.id.button28);
        text = findViewById(R.id.editTextTextPersonName);
        text2 = findViewById(R.id.editTextTextPersonName2);
        text3 = findViewById(R.id.textView50);
        token = getIntent().getStringExtra("key");
        selectedProfil = getIntent().getStringExtra("selectedProfil");
        button = findViewById(R.id.button26);

        arrayList = new ArrayList<>();
        intent1 = new Intent(this, PS_ErgebnisListe.class);

        button.setOnClickListener(this);
        search.setOnClickListener(this);
        back.setOnClickListener(this);
        if (ContextCompat.checkSelfPermission(Partnersuche.this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(Partnersuche.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(Partnersuche.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest
                    .permission.ACCESS_FINE_LOCATION}, 1);

        }


    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button28:
                Intent intent = new Intent(this, StartSeite.class);
                intent.putExtra("key", token);
                intent.putExtra("selectedProfil", selectedProfil);
                startActivity(intent);
                break;
            case R.id.button26:
                getLocation();
                break;
            case R.id.button27:
                //suche();
                intent1.putExtra("key", token);
                intent1.putExtra("selectedProfil", selectedProfil);
                intent1.putExtra("lat", text.getText().toString());
                intent1.putExtra("lg", text2.getText().toString());
                intent1.putExtra("ent", text3.getText().toString());
                startActivity(intent1);
                break;

        }


    }

    @SuppressLint("MissingPermission")
    private void getLocation() {

        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, Partnersuche.this);


    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        text.setText(String.valueOf(location.getLatitude()));
        text2.setText(String.valueOf(location.getLongitude()));

    }


}