package com.example.dinder.Activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dinder.API.RetrofitAPI;
import com.example.dinder.Datamodel.Profildaten;
import com.example.dinder.R;
import com.google.android.material.button.MaterialButton;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ErgebnisPartner extends AppCompatActivity implements View.OnClickListener {

    TextView textView, textView2, textView3, textView4;
    String profil, token, mail, lat, lg, ent, selectedProfil;

    MaterialButton buttonlike, buttondislike,buttonFriend,buttonBookmark,back;

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ergebnis_partner);

        token = getIntent().getStringExtra("key");
        profil = getIntent().getStringExtra("profil");
        mail = getIntent().getStringExtra("mail");

        selectedProfil = getIntent().getStringExtra("selectedProfil");

        lat = getIntent().getStringExtra("lat");
        lg = getIntent().getStringExtra("lg");
        ent = getIntent().getStringExtra("ent");

        imageView = findViewById(R.id.imageView9);
        textView = findViewById(R.id.textView46);
        textView2 = findViewById(R.id.textView47);
        textView3 = findViewById(R.id.textView48);
        textView4 = findViewById(R.id.textView49);

        buttonlike = findViewById(R.id.button);
        buttondislike = findViewById(R.id.button18);
        back = findViewById(R.id.button102);
        buttonBookmark = findViewById(R.id.button201);
        buttonFriend = findViewById(R.id.button200);



        getProfil();

        back.setOnClickListener(this);
        buttonlike.setOnClickListener(this);
        buttondislike.setOnClickListener(this);
        buttonFriend.setOnClickListener(this);
        buttonBookmark.setOnClickListener(this);

        getPictures(profil, mail, imageView);




    }




    private void getProfil() {
        token = getIntent().getStringExtra("key");
        profil = getIntent().getStringExtra("profil");
        mail = getIntent().getStringExtra("mail");
        Call<Profildaten> arraylist = RetrofitAPI.getService().getProfil(token, profil, mail);


        arraylist.enqueue(new Callback<Profildaten>() {
            @Override
            public void onResponse(Call<Profildaten> call, Response<Profildaten> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        textView.setText(response.body().getProfilname());
                        textView2.setText(response.body().getBeschreibung());
                        textView3.setText(response.body().getPositive());
                        textView4.setText(response.body().getNegative());

                    }
                }

            }

            @Override
            public void onFailure(Call<Profildaten> call, Throwable t) {
                System.out.println("Falsch");
                System.out.println(t.getLocalizedMessage());
                getProfil();

            }
        });

    }

    private void getPictures(String profil, String mail, ImageView imageView) {
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
                System.out.println("Bild nicht geladen");
                getPictures(profil,mail,imageView);
            }


        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button102:
                Intent intent = new Intent(this, PS_ErgebnisListe.class);
                intent.putExtra("key", token);
                intent.putExtra("lat", lat);
                intent.putExtra("lg", lg);
                intent.putExtra("ent", ent);
                startActivity(intent);
                break;
            case R.id.button:
                like();
                break;

            case R.id.button18:
                dislike();
                break;
            case R.id.button200:
                addFriend();
                break;
            case R.id.button201:
                addToBookmark();
                break;
        }

    }

    private void addFriend() {

        Call<Void> call = RetrofitAPI.getService().addFriend(token,selectedProfil,profil,mail);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(ErgebnisPartner.this, "Anfrage wurde geschick", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                System.out.println(t.getLocalizedMessage());

            }
        });


    }

    private void addToBookmark() {

        Call<Void> call = RetrofitAPI.getService().addToBookmark(token, selectedProfil, profil, mail);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(ErgebnisPartner.this, "Profil wurde hinzugefügt", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                System.out.println(t.getLocalizedMessage());

            }
        });

    }

    private void like() {

        Call<Void> call = RetrofitAPI.getService().like(token, profil, mail);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(ErgebnisPartner.this, "Profil wurde gelike", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {


            }
        });

    }

    private void dislike() {

        Call<Void> call = RetrofitAPI.getService().dislike(token, profil, mail);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(ErgebnisPartner.this, "Profil wurde gedislike", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });

    }
}