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

public class ErgebnisStart extends AppCompatActivity implements View.OnClickListener {

    TextView textView, textView2, textView3, textView4;
    String profil, token, mail, selectedProfil;

    MaterialButton buttonlike, buttondislike, buttonFriend, buttonMark;

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ergebnis);

        token = getIntent().getStringExtra("key");
        profil = getIntent().getStringExtra("profil");
        mail = getIntent().getStringExtra("mail");
        selectedProfil = getIntent().getStringExtra("selectedProfil");

        imageView = findViewById(R.id.imageView9);

        textView = findViewById(R.id.textView46);
        textView2 = findViewById(R.id.textView47);
        textView3 = findViewById(R.id.textView48);
        textView4 = findViewById(R.id.textView49);
        buttonlike = findViewById(R.id.button);
        buttondislike = findViewById(R.id.button18);
        buttonMark = findViewById(R.id.buttonMerk);
        buttonFriend = findViewById(R.id.buttonFriend);

        MaterialButton back = findViewById(R.id.button102);

        System.out.println("Token: " + token + " Profil: " + profil + "Mail: " + mail);
        System.out.println("Selected Profil : " + selectedProfil);

        getProfil();

        back.setOnClickListener(this);
        buttonlike.setOnClickListener(this);
        buttondislike.setOnClickListener(this);
        buttonMark.setOnClickListener(this);
        buttonFriend.setOnClickListener(this);

        getPictures(profil, mail, imageView);


    }


    private void getProfil() {
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
        System.out.println("Profil : " + profil);
        System.out.println("Mail : " + mail);
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
                Intent intent = new Intent(this, StartSeite.class);
                intent.putExtra("key", token);
                intent.putExtra("selectedProfil", selectedProfil);
                startActivity(intent);
                break;
            case R.id.button:
                like();
                break;

            case R.id.button18:
                dislike();
                break;
            case R.id.buttonMerk:
                addToBookmark();
                break;
            case R.id.buttonFriend:
                addFriend();
                break;

        }

    }

    private void addFriend() {

        Call<Void> call = RetrofitAPI.getService().addFriend(token,selectedProfil,profil,mail);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(ErgebnisStart.this, "Anfrage wurde geschick", Toast.LENGTH_SHORT).show();

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
                Toast.makeText(ErgebnisStart.this, "Profil wurde hinzugefügt", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ErgebnisStart.this, "Profil wurde gelike", Toast.LENGTH_SHORT).show();

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
                Toast.makeText(ErgebnisStart.this, "Profil wurde gedislike", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {


            }
        });

    }


}