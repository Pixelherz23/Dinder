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
import com.example.dinder.Datamodel.Benutzer;
import com.example.dinder.Datamodel.Profildaten;
import com.example.dinder.R;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Request;
import okhttp3.ResponseBody;
import okio.Timeout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Merkliste extends AppCompatActivity implements View.OnClickListener {

    String token, selectedProfil, profil1, mail1, profil2, mail2, profil3, mail3;
    ArrayList<Benutzer> benutzers;

    TextView textView, textView2, textView3, textView4, textView5, textView6;

    ImageView imageView, imageView2, imageView3;

    Button buttonlike, buttonFriend, buttonlike2, buttonFriend2, buttonlike3, buttonFriend3, buttonRemove1, buttonRemove2, buttonRemove3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merkliste);
        Button back = findViewById(R.id.button29);
        back.setOnClickListener(this);

        textView = findViewById(R.id.textView37);
        textView2 = findViewById(R.id.textView38);

        textView3 = findViewById(R.id.textView40);
        textView4 = findViewById(R.id.textView41);

        textView5 = findViewById(R.id.textView43);
        textView6 = findViewById(R.id.textView44);

        imageView = findViewById(R.id.imageView6);
        imageView2 = findViewById(R.id.imageView7);
        imageView3 = findViewById(R.id.imageView8);

        buttonlike = findViewById(R.id.button38);
        buttonFriend = findViewById(R.id.button53);
        buttonRemove1 = findViewById(R.id.button49);


        buttonlike2 = findViewById(R.id.button41);
        buttonFriend2 = findViewById(R.id.button54);
        buttonRemove2 = findViewById(R.id.button50);


        buttonlike3 = findViewById(R.id.button44);
        buttonFriend3 = findViewById(R.id.button55);
        buttonRemove3 = findViewById(R.id.button51);


        token = getIntent().getStringExtra("key");
        selectedProfil = getIntent().getStringExtra("selectedProfil");
        benutzers = new ArrayList<>();

        buttonlike.setOnClickListener(this);
        buttonFriend.setOnClickListener(this);
        buttonlike2.setOnClickListener(this);
        buttonFriend2.setOnClickListener(this);
        buttonlike3.setOnClickListener(this);
        buttonFriend3.setOnClickListener(this);
        buttonRemove1.setOnClickListener(this);
        buttonRemove2.setOnClickListener(this);
        buttonRemove3.setOnClickListener(this);
        textView.setVisibility(View.INVISIBLE);
        textView2.setVisibility(View.INVISIBLE);
        textView3.setVisibility(View.INVISIBLE);
        textView4.setVisibility(View.INVISIBLE);
        textView5.setVisibility(View.INVISIBLE);
        textView6.setVisibility(View.INVISIBLE);
        buttonFriend.setVisibility(View.INVISIBLE);
        buttonRemove1.setVisibility(View.INVISIBLE);
        buttonRemove2.setVisibility(View.INVISIBLE);
        buttonRemove3.setVisibility(View.INVISIBLE);
        buttonlike.setVisibility(View.INVISIBLE);
        buttonFriend2.setVisibility(View.INVISIBLE);
        buttonlike2.setVisibility(View.INVISIBLE);
        buttonFriend3.setVisibility(View.INVISIBLE);
        buttonlike3.setVisibility(View.INVISIBLE);

        getBenutzer();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button29:
                Intent intent = new Intent(this, StartSeite.class);
                intent.putExtra("key", token);
                intent.putExtra("selectedProfil", selectedProfil);
                startActivity(intent);
                break;
            case R.id.button38:
                like(profil1, mail1);
                break;
            case R.id.button53:
                addFriend(profil1, mail1);
                break;
            case R.id.button41:
                like(profil2, mail2);
                break;
            case R.id.button54:
                addFriend(profil2, mail2);
                break;
            case R.id.button44:
                like(profil3, mail3);
                break;
            case R.id.button55:
                addFriend(profil3, mail3);
                break;
            case R.id.button49:
                remove(profil1, mail1);
                break;
            case R.id.button50:
                remove(profil2, mail2);
                break;
            case R.id.button51:
                remove(profil3, mail3);
                break;
        }
    }

    private void remove(String profil, String mail) {

        Call<Void> call = RetrofitAPI.getService().removeFromBookmark(token, selectedProfil, profil, mail);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Merkliste.this, "Profil wurde entfernt", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });

    }

    public void getBenutzer() {

        Call<ArrayList<Benutzer>> call = RetrofitAPI.getService().getBenutzer(token, selectedProfil);

        call.enqueue(new Callback<ArrayList<Benutzer>>() {
            @Override
            public void onResponse(Call<ArrayList<Benutzer>> call, Response<ArrayList<Benutzer>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null || response.body().size() == 0) {
                        benutzers.addAll(response.body());
                        if (benutzers.size() < 1) {
                            Toast.makeText(Merkliste.this, "Keine Profile vorhanden", Toast.LENGTH_SHORT).show();
                            return;

                        }
                        if (benutzers.size() == 1) {
                            profil1 = benutzers.get(0).getProfilName();
                            mail1 = benutzers.get(0).getEmail();
                            textView.setVisibility(View.VISIBLE);
                            textView2.setVisibility(View.VISIBLE);
                            buttonFriend.setVisibility(View.VISIBLE);
                            buttonlike.setVisibility(View.VISIBLE);
                            buttonRemove1.setVisibility(View.VISIBLE);
                            getProfil(profil1, mail1, textView, textView2, imageView);
                        } else if (benutzers.size() == 2) {
                            profil1 = benutzers.get(0).getProfilName();
                            mail1 = benutzers.get(0).getEmail();
                            profil2 = benutzers.get(1).getProfilName();
                            mail2 = benutzers.get(1).getEmail();
                            getProfil(profil1, mail1, textView, textView2, imageView);
                            getProfil(profil2, mail2, textView3, textView4, imageView2);
                            textView.setVisibility(View.VISIBLE);
                            textView2.setVisibility(View.VISIBLE);
                            buttonFriend.setVisibility(View.VISIBLE);
                            buttonlike.setVisibility(View.VISIBLE);
                            buttonRemove1.setVisibility(View.VISIBLE);
                            buttonRemove2.setVisibility(View.VISIBLE);
                            textView3.setVisibility(View.VISIBLE);
                            textView4.setVisibility(View.VISIBLE);
                            buttonlike2.setVisibility(View.VISIBLE);
                            buttonFriend2.setVisibility(View.VISIBLE);
                        } else if (benutzers.size() >= 3) {
                            profil1 = benutzers.get(0).getProfilName();
                            mail1 = benutzers.get(0).getEmail();
                            profil2 = benutzers.get(1).getProfilName();
                            mail2 = benutzers.get(1).getEmail();
                            profil3 = benutzers.get(2).getProfilName();
                            mail3 = benutzers.get(2).getEmail();
                            textView.setVisibility(View.VISIBLE);
                            buttonRemove1.setVisibility(View.VISIBLE);
                            buttonRemove2.setVisibility(View.VISIBLE);
                            buttonRemove3.setVisibility(View.VISIBLE);
                            textView2.setVisibility(View.VISIBLE);
                            textView3.setVisibility(View.VISIBLE);
                            textView4.setVisibility(View.VISIBLE);
                            textView5.setVisibility(View.VISIBLE);
                            textView6.setVisibility(View.VISIBLE);
                            buttonFriend.setVisibility(View.VISIBLE);
                            buttonlike.setVisibility(View.VISIBLE);
                            buttonFriend2.setVisibility(View.VISIBLE);
                            buttonlike2.setVisibility(View.VISIBLE);
                            buttonFriend3.setVisibility(View.VISIBLE);
                            buttonlike3.setVisibility(View.VISIBLE);
                            getProfil(profil1, mail1, textView, textView2, imageView);
                            getProfil(profil2, mail2, textView3, textView4, imageView2);
                            getProfil(profil3, mail3, textView5, textView6, imageView3);
                        }


                    }
                }


            }

            @Override
            public void onFailure(Call<ArrayList<Benutzer>> call, Throwable t) {
                System.out.println(t.getLocalizedMessage());
                getBenutzer();

            }
        });


    }


    private void getProfil(String profil, String mail, TextView textView, TextView textView2, ImageView imageView) {
        if (profil.isEmpty() || mail.isEmpty()) {
            System.out.println("Daten sind leer");
            return;
        }

        Call<Profildaten> arraylist = RetrofitAPI.getService().getProfil(token, profil, mail);


        arraylist.enqueue(new Callback<Profildaten>() {
            @Override
            public void onResponse(Call<Profildaten> call, Response<Profildaten> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        textView.setText(response.body().getProfilname());
                        textView2.setText(response.body().getBeschreibung());
                        getPictures(response.body().getProfilname(), response.body().getEmail(), imageView);


                    }
                }

            }

            @Override
            public void onFailure(Call<Profildaten> call, Throwable t) {
                System.out.println(t.getLocalizedMessage());
                getProfil(profil,mail,textView,textView2,imageView);

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

    private void like(String profil, String mail) {

        Call<Void> call = RetrofitAPI.getService().like(token, profil, mail);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(Merkliste.this, "Profil wurde gelike", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(Merkliste.this, "Profil wurde nicht gelike", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void addFriend(String profil, String mail) {

        Call<Void> call = RetrofitAPI.getService().addFriend(token, selectedProfil, profil, mail);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(Merkliste.this, "Anfrage wurde geschick", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(Merkliste.this, "Etwas ist schief gelaufen", Toast.LENGTH_SHORT).show();
                System.out.println(t.getLocalizedMessage());

            }
        });


    }
}
