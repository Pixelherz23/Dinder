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
import com.example.dinder.Datamodel.User;
import com.example.dinder.R;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestListe extends AppCompatActivity implements View.OnClickListener {
    String token, profil, profil2, profil3, selectedProfil,mail,mail2,mail3;

    ArrayList<User> arrayList;

    TextView textView, textView2, textView3;

    Button buttonaccecpt, buttonaccecpt1, buttonaccecpt2, buttonremove,buttonremove1,buttonremove2;

    ImageView imageView,imageView2,imageView3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requestliste);

        token = getIntent().getStringExtra("key");
        selectedProfil = getIntent().getStringExtra("selectedProfil");

        arrayList = new ArrayList<>();

        textView = findViewById(R.id.textView55);
        textView2 = findViewById(R.id.textView56);
        textView3 = findViewById(R.id.textView57);

        imageView = findViewById(R.id.imageView10);
        imageView2 = findViewById(R.id.imageView11);
        imageView3 = findViewById(R.id.imageView12);

        buttonaccecpt = findViewById(R.id.button31);
        buttonaccecpt1 = findViewById(R.id.button32);
        buttonaccecpt2 = findViewById(R.id.button33);

        buttonremove = findViewById(R.id.button52);
        buttonremove1 = findViewById(R.id.button56);
        buttonremove2 = findViewById(R.id.button57);

        textView2.setVisibility(View.INVISIBLE);
        textView.setVisibility(View.INVISIBLE);
        textView3.setVisibility(View.INVISIBLE);
        buttonaccecpt.setVisibility(View.INVISIBLE);
        buttonaccecpt1.setVisibility(View.INVISIBLE);
        buttonaccecpt2.setVisibility(View.INVISIBLE);
        buttonremove.setVisibility(View.INVISIBLE);
        buttonremove1.setVisibility(View.INVISIBLE);
        buttonremove2.setVisibility(View.INVISIBLE);


        Button back = findViewById(R.id.button30);
        back.setOnClickListener(this);
        buttonremove.setOnClickListener(this);
        buttonremove2.setOnClickListener(this);
        buttonremove1.setOnClickListener(this);
        buttonaccecpt.setOnClickListener(this);
        buttonaccecpt1.setOnClickListener(this);
        buttonaccecpt2.setOnClickListener(this);
        getRequest();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button30:
                Intent intent1 = new Intent(this, StartSeite.class);
                intent1.putExtra("key", token);
                intent1.putExtra("selectedProfil", selectedProfil);
                startActivity(intent1);
                break;
            case R.id.button31:
                accecpt(profil,mail);
                break;
            case R.id.button32:
                accecpt(profil2,mail2);
                break;
            case R.id.button33:
                accecpt(profil3,mail3);
                break;
            case R.id.button52:
                deleteRequest(profil,mail);
                break;
            case R.id.button56:
                deleteRequest(profil2,mail2);
                break;
            case R.id.button57:
                deleteRequest(profil3,mail3);
                break;
        }
    }

    private void accecpt(String profil, String mail) {

        Call<Void> call = RetrofitAPI.getService().accecptFriend(token, selectedProfil,profil,mail);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(RequestListe.this, "Anfrage wurde akzeptiert", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(RequestListe.this, "Anfrage wurde nicht Akzeptiert", Toast.LENGTH_SHORT).show();
                System.out.println(t.getLocalizedMessage());

            }
        });


    }


    public void getRequest() {

        Call<ArrayList<User>> call = RetrofitAPI.getService().getRequest(token, selectedProfil);

        call.enqueue(new Callback<ArrayList<User>>() {
            @Override
            public void onResponse(Call<ArrayList<User>> call, Response<ArrayList<User>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        arrayList.addAll(response.body());
                        if (arrayList.size() < 1) {
                            Toast.makeText(RequestListe.this, "Keine Anfragen vorhanden", Toast.LENGTH_SHORT).show();
                        }
                        if (arrayList.size() == 1) {
                            profil = arrayList.get(0).getProfilName();
                            mail = arrayList.get(0).getEmail();
                            textView.setText(profil);
                            textView.setVisibility(View.VISIBLE);
                            buttonaccecpt.setVisibility(View.VISIBLE);
                            buttonremove.setVisibility(View.VISIBLE);
                            textView2.setVisibility(View.INVISIBLE);
                            textView3.setVisibility(View.INVISIBLE);
                            buttonaccecpt1.setVisibility(View.INVISIBLE);
                            buttonaccecpt2.setVisibility(View.INVISIBLE);
                            buttonremove1.setVisibility(View.INVISIBLE);
                            buttonremove2.setVisibility(View.INVISIBLE);
                            getProfil(profil,mail,imageView);

                        } else if (arrayList.size() == 2) {
                            profil = arrayList.get(0).getProfilName();
                            mail = arrayList.get(0).getEmail();
                            textView.setText(profil);
                            textView.setVisibility(View.VISIBLE);
                            profil2 = arrayList.get(1).getProfilName();
                            mail2 = arrayList.get(1).getEmail();
                            textView2.setText(profil2);
                            textView2.setVisibility(View.VISIBLE);
                            buttonremove.setVisibility(View.VISIBLE);
                            buttonremove1.setVisibility(View.VISIBLE);
                            buttonremove2.setVisibility(View.INVISIBLE);
                            buttonaccecpt.setVisibility(View.VISIBLE);
                            buttonaccecpt2.setVisibility(View.VISIBLE);
                            buttonaccecpt1.setVisibility(View.INVISIBLE);
                            textView3.setVisibility(View.INVISIBLE);
                            getProfil(profil,mail,imageView);
                            getProfil(profil2,mail2,imageView2);
                        } else if (arrayList.size() >=3){

                            profil = arrayList.get(0).getProfilName();
                            profil2 = arrayList.get(1).getProfilName();
                            profil3 = arrayList.get(2).getProfilName();
                            mail = arrayList.get(0).getEmail();
                            mail2 = arrayList.get(1).getEmail();
                            mail3 = arrayList.get(2).getEmail();
                            textView.setText(profil);
                            textView2.setText(profil2);
                            textView3.setText(profil3);
                            textView2.setVisibility(View.VISIBLE);
                            textView.setVisibility(View.VISIBLE);
                            textView3.setVisibility(View.VISIBLE);
                            buttonaccecpt.setVisibility(View.VISIBLE);
                            buttonaccecpt2.setVisibility(View.VISIBLE);
                            buttonaccecpt1.setVisibility(View.VISIBLE);
                            buttonremove.setVisibility(View.VISIBLE);
                            buttonremove1.setVisibility(View.VISIBLE);
                            buttonremove2.setVisibility(View.VISIBLE);
                            getProfil(profil,mail,imageView);
                            getProfil(profil2,mail2,imageView2);
                            getProfil(profil3,mail3,imageView3);

                        }

                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<User>> call, Throwable t) {
                System.out.println(t.getLocalizedMessage());
                getRequest();

            }
        });
    }

    private void getProfil(String profil, String mail, ImageView imageView) {
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
                        getPictures(response.body().getProfilname(), response.body().getEmail(), imageView);


                    }
                }

            }

            @Override
            public void onFailure(Call<Profildaten> call, Throwable t) {
                System.out.println(t.getLocalizedMessage());
                getProfil(profil,mail,imageView);

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
                getPictures(profil,mail,imageView);
            }


        });
    }

    public void deleteRequest(String profil,String mail){

        Call<Void> call = RetrofitAPI.getService().deleteFriendorRequest(token,selectedProfil,profil,mail);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(RequestListe.this, "Anfrage wurde entfernt", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });



    }
}