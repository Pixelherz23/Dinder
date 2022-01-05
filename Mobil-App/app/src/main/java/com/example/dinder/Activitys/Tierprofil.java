package com.example.dinder.Activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Entity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dinder.API.RetrofitAPI;
import com.example.dinder.Datamodel.TierDaten;
import com.example.dinder.R;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Tierprofil extends AppCompatActivity implements View.OnClickListener {

    String token, beschreibung, beschreibung2, beschreibung3, profil, profil2, profil3, switch1, switch2, switch3;

    ImageView imageView, imageView2, imageView3;

    TextView text, text1, text2, text3, text4, text5;

    SwitchCompat switchCompat, switchCompat2, switchCompat3;

    Button setting1, setting2, setting3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tierprofil);
        Button back = findViewById(R.id.button21);
        Button newAnimal = findViewById(R.id.button22);
        setting1 = findViewById(R.id.button23);
        setting2 = findViewById(R.id.button24);
        setting3 = findViewById(R.id.button25);
        text = findViewById(R.id.textView29);
        text1 = findViewById(R.id.textView30);
        text2 = findViewById(R.id.textView31);
        text3 = findViewById(R.id.textView32);
        text4 = findViewById(R.id.textView33);
        text5 = findViewById(R.id.textView34);
        imageView = findViewById(R.id.imageView3);
        imageView2 = findViewById(R.id.imageView4);
        imageView3 = findViewById(R.id.imageView5);

        switchCompat = findViewById(R.id.switch3);
        switchCompat2 = findViewById(R.id.switch4);
        switchCompat3 = findViewById(R.id.switch5);

        token = getIntent().getStringExtra("key");
        back.setOnClickListener(this);
        newAnimal.setOnClickListener(this);
        setting1.setOnClickListener(this);
        setting2.setOnClickListener(this);
        setting3.setOnClickListener(this);

        text.setVisibility(View.INVISIBLE);
        text1.setVisibility(View.INVISIBLE);
        switchCompat.setVisibility(View.INVISIBLE);
        imageView.setVisibility(View.INVISIBLE);
        setting1.setVisibility(View.INVISIBLE);

        text2.setVisibility(View.INVISIBLE);
        text3.setVisibility(View.INVISIBLE);
        setting2.setVisibility(View.INVISIBLE);
        switchCompat2.setVisibility(View.INVISIBLE);
        imageView2.setVisibility(View.INVISIBLE);

        text4.setVisibility(View.INVISIBLE);
        text5.setVisibility(View.INVISIBLE);
        setting3.setVisibility(View.INVISIBLE);
        switchCompat3.setVisibility(View.INVISIBLE);
        imageView3.setVisibility(View.INVISIBLE);


        getTiere();


    }

    @Override
    public void onClick(View v) {


        if (v.getId() == R.id.button21) {
            Intent intent2 = new Intent(this, StartSeite.class);
            intent2.putExtra("key", token);
            if (switch1 != null) {
                intent2.putExtra("selectedProfil", switch1);
            } else if (switch2 != null) {
                intent2.putExtra("selectedProfil", switch2);
            } else if (switch3 != null) {
                intent2.putExtra("selectedProfil", switch3);
            }
            startActivity(intent2);


        } else if (v.getId() == R.id.button22) {
            Intent in = new Intent(Tierprofil.this, Tierprofil_erstellen.class);
            in.putExtra("key", token);
            startActivity(in);
        }
    }

    public synchronized void getTiere() {
        Call<ArrayList<TierDaten>> call = RetrofitAPI.getService().getTiere(token);
        ArrayList<TierDaten> arrayList = new ArrayList<>();

        call.enqueue(new Callback<ArrayList<TierDaten>>() {
            @Override
            public void onResponse(Call<ArrayList<TierDaten>> call, Response<ArrayList<TierDaten>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        arrayList.addAll(response.body());
                        if (arrayList.size() < 1) {
                            Toast.makeText(Tierprofil.this, "Keine Profile vorhanden", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (arrayList.size() == 1) {
                            profil = arrayList.get(0).getProfilname();
                            beschreibung = arrayList.get(0).getBeschreibung();
                            text.setText(profil);
                            text1.setText(beschreibung);
                            getPicutures(profil, imageView);
                            text.setVisibility(View.VISIBLE);
                            text1.setVisibility(View.VISIBLE);
                            switchCompat.setVisibility(View.VISIBLE);
                            imageView.setVisibility(View.VISIBLE);
                            setting1.setVisibility(View.VISIBLE);


                        } else if (arrayList.size() == 2) {
                            profil = arrayList.get(0).getProfilname();
                            profil2 = arrayList.get(1).getProfilname();
                            beschreibung = arrayList.get(0).getBeschreibung();
                            beschreibung2 = arrayList.get(1).getBeschreibung();
                            text.setText(profil);
                            text2.setText(profil2);
                            text1.setText(beschreibung);
                            text3.setText(beschreibung2);
                            getPicutures(profil, imageView);
                            getPicutures(profil2, imageView2);
                            text.setVisibility(View.VISIBLE);
                            text1.setVisibility(View.VISIBLE);
                            switchCompat.setVisibility(View.VISIBLE);
                            imageView.setVisibility(View.VISIBLE);
                            setting1.setVisibility(View.VISIBLE);
                            text2.setVisibility(View.VISIBLE);
                            text3.setVisibility(View.VISIBLE);
                            setting2.setVisibility(View.VISIBLE);
                            switchCompat2.setVisibility(View.VISIBLE);
                            imageView2.setVisibility(View.VISIBLE);
                        } else {
                            profil = arrayList.get(0).getProfilname();
                            profil2 = arrayList.get(1).getProfilname();
                            profil3 = arrayList.get(2).getProfilname();
                            beschreibung = arrayList.get(0).getBeschreibung();
                            beschreibung2 = arrayList.get(1).getBeschreibung();
                            beschreibung3 = arrayList.get(2).getBeschreibung();
                            text.setText(profil);
                            text2.setText(profil2);
                            text4.setText(profil3);
                            text1.setText(beschreibung);
                            text3.setText(beschreibung2);
                            text5.setText(beschreibung3);
                            text.setVisibility(View.VISIBLE);
                            text1.setVisibility(View.VISIBLE);
                            switchCompat.setVisibility(View.VISIBLE);
                            imageView.setVisibility(View.VISIBLE);
                            setting1.setVisibility(View.VISIBLE);

                            text2.setVisibility(View.VISIBLE);
                            text3.setVisibility(View.VISIBLE);
                            setting2.setVisibility(View.VISIBLE);
                            switchCompat2.setVisibility(View.VISIBLE);
                            imageView2.setVisibility(View.VISIBLE);

                            text4.setVisibility(View.VISIBLE);
                            text5.setVisibility(View.VISIBLE);
                            setting3.setVisibility(View.VISIBLE);
                            switchCompat3.setVisibility(View.VISIBLE);
                            imageView3.setVisibility(View.VISIBLE);
                            getPicutures(profil, imageView);
                            getPicutures(profil2, imageView2);
                            getPicutures(profil3, imageView3);

                        }

                        //JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));
                        //System.out.println(jsonObject);


                        //System.out.println("Json"+ jsonObject);
                        //System.out.println(jsonObject.getJSONObject("0").getString("beschreibung"));

                        //System.out.println(jsonObject1.getJSONObject(String.valueOf(0)));
                    }


                }


            }

            @Override
            public void onFailure(Call<ArrayList<TierDaten>> call, Throwable t) {
                System.out.println("Fehler " + t.getLocalizedMessage());
                getTiere();

            }
        });

    }

    public synchronized void getPicutures(String profilname, ImageView imageView) {

        Call<ResponseBody> call = RetrofitAPI.getService().getPicProfil(token, profilname);


        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Bitmap bmp = BitmapFactory.decodeStream(response.body().byteStream());
                        imageView.setImageBitmap(bmp);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                System.out.println("Fehler " + t.getLocalizedMessage());
                getPicutures(profilname,imageView);
            }
        });


    }

    public void deleteProfil(String profilname) {


    }


    public void checkSwitch(View view) {
        if (switchCompat.isChecked()) {
            switch1 = profil;
        } else if (switchCompat2.isChecked()) {
            switch2 = profil2;
        } else if (switchCompat3.isChecked()) {
            switch3 = profil3;

        }
    }


}