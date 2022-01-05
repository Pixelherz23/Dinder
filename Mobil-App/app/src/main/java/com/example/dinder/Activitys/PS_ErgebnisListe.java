package com.example.dinder.Activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dinder.API.RetrofitAPI;
import com.example.dinder.Datamodel.TierDaten;
import com.example.dinder.R;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PS_ErgebnisListe extends AppCompatActivity implements View.OnClickListener {
    String token, lat, lg, ent, profil1, profil2, profil3, beschreibung1, beschreibung2, beschreibung3, mail1, mail2, mail3, selectedProfil;

    ArrayList<TierDaten> arrayList;

    TextView text, text2, text3, text4, text5, text6;

    ImageView imageView, imageView2, imageView3;

    Button buttonProfil1, buttonProfil2, buttonProfil3,buttonStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ps_ergebnis_liste);
        Button back = findViewById(R.id.button29);

        buttonProfil1 = findViewById(R.id.button1002);
        buttonProfil2 = findViewById(R.id.button47);
        buttonProfil3 = findViewById(R.id.button48);
        buttonStart = findViewById(R.id.button34);

        token = getIntent().getStringExtra("key");
        selectedProfil = getIntent().getStringExtra("selectedProfil");
        lat = getIntent().getStringExtra("lat");
        lg = getIntent().getStringExtra("lg");
        ent = getIntent().getStringExtra("ent");


        arrayList = new ArrayList<>();

        text = findViewById(R.id.textView37);
        text2 = findViewById(R.id.textView39);
        text3 = findViewById(R.id.textView40);
        text4 = findViewById(R.id.textView42);
        text5 = findViewById(R.id.textView43);
        text6 = findViewById(R.id.textView45);


        imageView = findViewById(R.id.imageView6);
        imageView2 = findViewById(R.id.imageView7);
        imageView3 = findViewById(R.id.imageView8);
        suche();


        back.setOnClickListener(this);
        buttonProfil1.setOnClickListener(this);
        buttonProfil2.setOnClickListener(this);
        buttonProfil3.setOnClickListener(this);
        buttonStart.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.button29:
                Intent intent = new Intent(this, Partnersuche.class);
                intent.putExtra("key", token);
                intent.putExtra("selectedProfil", selectedProfil);
                startActivity(intent);
                break;
            case R.id.button1002:
                Intent intent1 = new Intent(this, ErgebnisPartner.class);
                intent1.putExtra("lat", lat);
                intent1.putExtra("lg", lg);
                intent1.putExtra("ent", ent);
                intent1.putExtra("key", token);
                intent1.putExtra("selectedProfil", selectedProfil);
                intent1.putExtra("mail", mail1);
                intent1.putExtra("profil", profil1);
                startActivity(intent1);
                break;
            case R.id.button47:

                Intent intent2 = new Intent(this, ErgebnisPartner.class);
                intent2.putExtra("lat", lat);
                intent2.putExtra("lg", lg);
                intent2.putExtra("ent", ent);
                intent2.putExtra("key", token);
                intent2.putExtra("selectedProfil", selectedProfil);
                intent2.putExtra("mail", mail2);
                intent2.putExtra("profil", profil2);
                startActivity(intent2);
                break;
            case R.id.button48:

                Intent intent3 = new Intent(this, ErgebnisPartner.class);
                intent3.putExtra("lat", lat);
                intent3.putExtra("lg", lg);
                intent3.putExtra("ent", ent);
                intent3.putExtra("key", token);
                intent3.putExtra("mail", mail3);
                intent3.putExtra("profil", profil3);
                intent3.putExtra("selectedProfil", selectedProfil);
                startActivity(intent3);
                break;

            case R.id.button34:
                Intent intent4 = new Intent(this,StartSeite.class);
                intent4.putExtra("key", token);
                intent4.putExtra("selectedProfil", selectedProfil);
                startActivity(intent4);
        }

    }

    public synchronized void suche() {
        Call<ArrayList<TierDaten>> call = RetrofitAPI.getService().search(token, lat, lg, ent);

        call.enqueue(new Callback<ArrayList<TierDaten>>() {
            @Override
            public void onResponse(Call<ArrayList<TierDaten>> call, Response<ArrayList<TierDaten>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (response.body().size() < 1) {
                            Toast.makeText(PS_ErgebnisListe.this, "Keine Profile gefunden", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        arrayList.addAll(response.body());
                        if (response.body().size() == 1) {
                            profil1 = arrayList.get(0).getProfilname();
                            beschreibung1 = arrayList.get(0).getBeschreibung();
                            mail1 = arrayList.get(0).getMail();
                            text.setText(profil1);
                            text2.setText(beschreibung1);
                            getPictures(profil1, mail1, imageView);
                            text3.setVisibility(View.INVISIBLE);
                            text4.setVisibility(View.INVISIBLE);
                            text5.setVisibility(View.INVISIBLE);
                            text6.setVisibility(View.INVISIBLE);
                            buttonProfil2.setVisibility(View.INVISIBLE);
                            buttonProfil3.setVisibility(View.INVISIBLE);
                        } else if (response.body().size() ==2){
                            profil1 = arrayList.get(0).getProfilname();
                            profil2 = arrayList.get(1).getProfilname();
                            beschreibung1 = arrayList.get(0).getBeschreibung();
                            beschreibung2 = arrayList.get(1).getBeschreibung();
                            mail1 = arrayList.get(0).getMail();
                            mail2 = arrayList.get(1).getMail();
                            text.setText(profil1);
                            text2.setText(beschreibung1);
                            text3.setText(profil2);
                            text4.setText(beschreibung2);
                            getPictures(profil1, mail1, imageView);
                            getPictures(profil2, mail2, imageView2);
                            buttonProfil3.setVisibility(View.INVISIBLE);
                            text5.setVisibility(View.INVISIBLE);
                            text6.setVisibility(View.INVISIBLE);
                        } else {
                            profil1 = arrayList.get(0).getProfilname();
                            profil2 = arrayList.get(1).getProfilname();
                            profil3 = arrayList.get(2).getProfilname();
                            beschreibung1 = arrayList.get(0).getBeschreibung();
                            beschreibung2 = arrayList.get(1).getBeschreibung();
                            beschreibung3 = arrayList.get(2).getBeschreibung();

                            mail1 = arrayList.get(0).getMail();
                            mail2 = arrayList.get(1).getMail();
                            mail3 = arrayList.get(2).getMail();
                            text.setText(profil1);
                            text2.setText(beschreibung1);
                            text3.setText(profil2);
                            text4.setText(beschreibung2);
                            text5.setText(profil3);
                            text6.setText(beschreibung3);

                            getPictures(profil1, mail1, imageView);
                            getPictures(profil2, mail2, imageView2);
                            getPictures(profil3, mail3, imageView3);

                        }


                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<TierDaten>> call, Throwable t) {
                System.out.println("Fehler : " + t.getLocalizedMessage());
                suche();


            }
        });


    }

    private synchronized void getPictures(String profil, String mail, ImageView imageView) {

        Call<ResponseBody> call1 = RetrofitAPI.getService().getPic(token, profil, mail);
        call1.enqueue(new Callback<ResponseBody>() {
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
                System.out.println("Bild konnte nicht geladen werden");
                getPictures(profil,mail,imageView);

            }
        });
    }

}

