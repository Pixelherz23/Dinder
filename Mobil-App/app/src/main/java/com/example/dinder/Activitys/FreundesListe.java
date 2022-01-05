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
import com.example.dinder.Datamodel.Profildaten;
import com.example.dinder.Datamodel.User;
import com.example.dinder.R;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FreundesListe extends AppCompatActivity implements View.OnClickListener {

    String token, profil, profil2, profil3, selectedProfil ,mail,mail2,mail3;

    ArrayList<User> arrayList;

    TextView textView, textView2, textView3;

    Button button, button1, button2,back;

    ImageView imageView,imageView2,imageView3;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freundesliste);
        token = getIntent().getStringExtra("key");
        selectedProfil = getIntent().getStringExtra("selectedProfil");

        arrayList = new ArrayList<>();

        textView = findViewById(R.id.textView55);
        textView2 = findViewById(R.id.textView56);
        textView3 = findViewById(R.id.textView57);

        imageView = findViewById(R.id.imageView10);
        imageView2 = findViewById(R.id.imageView11);
        imageView3 = findViewById(R.id.imageView12);

        button = findViewById(R.id.button31);
        button1 = findViewById(R.id.button32);
        button2 = findViewById(R.id.button33);

        back = findViewById(R.id.button30);


        textView2.setVisibility(View.INVISIBLE);
        textView.setVisibility(View.INVISIBLE);
        textView3.setVisibility(View.INVISIBLE);
        button.setVisibility(View.INVISIBLE);
        button1.setVisibility(View.INVISIBLE);
        button2.setVisibility(View.INVISIBLE);



        button.setOnClickListener(this);
        button2.setOnClickListener(this);
        button1.setOnClickListener(this);
        back.setOnClickListener(this);

        getFriends();




    }

    private void getFriends() {

        Call<ArrayList<User>> call = RetrofitAPI.getService().getFriends(token, selectedProfil);

        call.enqueue(new Callback<ArrayList<User>>() {
            @Override
            public void onResponse(Call<ArrayList<User>> call, Response<ArrayList<User>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        arrayList.addAll(response.body());
                        if (arrayList.size() < 1) {
                            Toast.makeText(FreundesListe.this, "Keine Freunde vorhanden", Toast.LENGTH_SHORT).show();
                        }
                        if (arrayList.size() == 1) {
                            profil = arrayList.get(0).getProfilName();
                            mail = arrayList.get(0).getEmail();
                            textView.setText(profil);
                            textView.setVisibility(View.VISIBLE);
                            button.setVisibility(View.VISIBLE);
                            textView2.setVisibility(View.INVISIBLE);
                            textView3.setVisibility(View.INVISIBLE);
                            button2.setVisibility(View.INVISIBLE);
                            button1.setVisibility(View.INVISIBLE);
                            getProfil(profil,mail,imageView);

                        } else if (arrayList.size() == 2) {
                            profil = arrayList.get(0).getProfilName();
                            mail = arrayList.get(0).getEmail();
                            System.out.println(mail);
                            textView.setText(profil);
                            textView.setVisibility(View.VISIBLE);
                            profil2 = arrayList.get(1).getProfilName();
                            mail2 = arrayList.get(1).getEmail();
                            textView2.setText(profil2);
                            textView2.setVisibility(View.VISIBLE);
                            button.setVisibility(View.VISIBLE);
                            button1.setVisibility(View.VISIBLE);
                            button2.setVisibility(View.INVISIBLE);
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
                            button.setVisibility(View.VISIBLE);
                            button1.setVisibility(View.VISIBLE);
                            button2.setVisibility(View.VISIBLE);
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
                getFriends();

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
                System.out.println("Falsch");
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

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.button31:
                removeFriend(profil,mail);
                break;

            case R.id.button32:
                removeFriend(profil2,mail2);
                break;
            case R.id.button33:
                removeFriend(profil3,mail3);
                break;
            case R.id.button30:
                Intent intent = new Intent(this, StartSeite.class);
                intent.putExtra("key", token);
                intent.putExtra("selectedProfil",selectedProfil );
                startActivity(intent);
        }

    }

    private void removeFriend(String profil,String mail) {

        Call<Void> call = RetrofitAPI.getService().deleteFriendorRequest(token,selectedProfil,profil,mail);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(FreundesListe.this, "Freund wurde entfernt", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {


            }
        });


    }
}