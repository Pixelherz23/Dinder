package com.example.dinder.Activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.dinder.API.ApiClient;
import com.example.dinder.API.RetrofitAPI;
import com.example.dinder.Activitys.Tierprofil;
import com.example.dinder.Datamodel.TierRequest;
import com.example.dinder.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Tierprofil_erstellen extends AppCompatActivity implements View.OnClickListener {

    TextInputEditText name, beschreibung, alter, geschlecht, grosse, id, bild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tierprofil_erstellen);

        bild = findViewById(R.id.textView27);
        bild.setVisibility(View.INVISIBLE);
        name = findViewById(R.id.textView21);
        beschreibung = findViewById(R.id.textView22);
        alter = findViewById(R.id.textView23);
        geschlecht = findViewById(R.id.textView24);
        grosse = findViewById(R.id.textView25);
        id = findViewById(R.id.textView26);
        String token = getIntent().getStringExtra("key");
        System.out.println("Token: Tier erstelllen " + token);


        Button back = findViewById(R.id.button20);
        Button safe = findViewById(R.id.button19);
        back.setOnClickListener(this);
        safe.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        String token = getIntent().getStringExtra("key");
        if (v.getId() == R.id.button19) {
            saveProfil(createProfil(), token);
            sendImg();
        } else if (v.getId() == R.id.button20) {
            Intent intent2 = new Intent(this, Tierprofil.class);
            intent2.putExtra("key",token);
            startActivity(intent2);
        }
    }

    private void sendImg() {

    }


    public TierRequest createProfil() {
        TierRequest tierRequest = new TierRequest();
        tierRequest.setName(name.getText().toString());
        tierRequest.setBeschreibung(beschreibung.getText().toString());
        tierRequest.setAlter(alter.getText().toString());
        tierRequest.setGeschlecht(geschlecht.getText().toString());
        tierRequest.setGrosse(grosse.getText().toString());
        tierRequest.setId(id.getText().toString());

        return tierRequest;
    }


    public void saveProfil(TierRequest tierRequest, String token) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("profilName", tierRequest.getName());
        jsonObject.addProperty("beschreibung", tierRequest.getBeschreibung());
        JsonObject jsonObject1 = new JsonObject();
        jsonObject1.addProperty("alter", tierRequest.getAlter());
        jsonObject1.addProperty("geschlecht", tierRequest.getGeschlecht());
        jsonObject1.addProperty("groesse", tierRequest.getGrosse());
        JsonObject jsonObject2 = new JsonObject();
        jsonObject2.addProperty("tierID", tierRequest.getId());
        JsonObject jsonObject3 = new JsonObject();
        jsonObject3.add("profilInfo", jsonObject);
        jsonObject3.add("merkmal", jsonObject1);
        jsonObject3.add("Tier", jsonObject2);
        System.out.println("Token: " + token);
        Call<Void> call = RetrofitAPI.getService().createProfil(token, jsonObject3);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Tierprofil_erstellen.this, "Daten wurden gesendet", Toast.LENGTH_SHORT).show();
                    Intent intent1 = new Intent(Tierprofil_erstellen.this, Bild_hochladen.class);
                    intent1.putExtra("key", token);
                    intent1.putExtra("profil", tierRequest.getName());
                    startActivity(intent1);


                } else {
                    Toast.makeText(Tierprofil_erstellen.this, "Daten wurden nicht gesendet" + response.message(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(Tierprofil_erstellen.this, "Daten wurden nicht gesendet" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }


}