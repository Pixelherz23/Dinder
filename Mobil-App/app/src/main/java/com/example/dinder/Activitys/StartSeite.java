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
import com.example.dinder.Datamodel.StartElemente;
import com.example.dinder.R;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartSeite extends AppCompatActivity implements View.OnClickListener {


    TextView textView1,textView2,textView3,textView4;
    ImageView imageView, imageView2;

    String token, profil,mail,beschreibung, profil2, mail2,selectedProfil ;
    Button buttonLike,buttonLike2, buttondislike,buttondislike2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startseite);

        Button search = findViewById(R.id.search);
        Button chat = findViewById(R.id.button6);
        Button like = findViewById(R.id.button7);
        Button bookmark = findViewById(R.id.button8);
        Button animalList = findViewById(R.id.button9);
        Button settings = findViewById(R.id.button10);
        imageView = findViewById(R.id.imageView);
        imageView2 = findViewById(R.id.imageView2);

        buttonLike = findViewById(R.id.button2);
        buttonLike2 = findViewById(R.id.button4);

        buttondislike = findViewById(R.id.button3);
        buttondislike2 = findViewById(R.id.button5);

        selectedProfil = getIntent().getStringExtra("selectedProfil");


        textView1 = findViewById(R.id.textView4);
        textView2 = findViewById(R.id.textView5);
        textView3 = findViewById(R.id.textView6);
        textView4 = findViewById(R.id.textView7);
        textView1.setText(profil);
        textView2.setText(beschreibung);


        buttonLike.setOnClickListener(this);
        buttonLike2.setOnClickListener(this);
        buttondislike.setOnClickListener(this);
        buttondislike2.setOnClickListener(this);
        search.setOnClickListener(this);
        chat.setOnClickListener(this);
        like.setOnClickListener(this);
        bookmark.setOnClickListener(this);
        animalList.setOnClickListener(this);
        settings.setOnClickListener(this);

        getMost();

        imageView.refreshDrawableState();
        imageView2.refreshDrawableState();

    }

    @Override
    public void onClick(View v) {
        String token = getIntent().getStringExtra("key");
        switch (v.getId()) {
            case R.id.search:
                Intent intent1 = new Intent(this, Partnersuche.class);
                intent1.putExtra("key", token);
                intent1.putExtra("selectedProfil", selectedProfil);
                startActivity(intent1);
                break;
            case R.id.button6:
                Intent intent2 = new Intent(this, RequestListe.class);
                intent2.putExtra("key", token);
                intent2.putExtra("selectedProfil", selectedProfil);
                startActivity(intent2);
                break;
            case R.id.button7:
                Intent intent3 = new Intent(this, FreundesListe.class);
                intent3.putExtra("key", token);
                intent3.putExtra("selectedProfil", selectedProfil);
                startActivity(intent3);
                break;
            case R.id.button8:
                Intent intent4 = new Intent(this, Merkliste.class);
                intent4.putExtra("selectedProfil", selectedProfil);
                intent4.putExtra("key", token);
                startActivity(intent4);
                break;
            case R.id.button9:
                Intent i = new Intent(StartSeite.this, Tierprofil.class);
                i.putExtra("key", token);
                startActivity(i);
                break;
            case R.id.button10:
                Intent intent6 = new Intent(this, Accountverwaltung.class);
                intent6.putExtra("key", token);
                startActivity(intent6);
                break;
            case R.id.button2:
            case R.id.button3:
                Intent intent7 = new Intent(this, ErgebnisStart.class);
                intent7.putExtra("profil", profil);
                intent7.putExtra("mail", mail);
                intent7.putExtra("key", token);
                intent7.putExtra("selectedProfil", selectedProfil);
                startActivity(intent7);
                break;
            case R.id.button5:
            case R.id.button4:
                Intent intent8 = new Intent(this, ErgebnisStart.class);
                intent8.putExtra("profil", profil2);
                intent8.putExtra("mail", mail2);
                intent8.putExtra("selectedProfil", selectedProfil);
                intent8.putExtra("key", token);
                startActivity(intent8);
                break;

        }

    }

    private synchronized void getPictures(String profil, String mail, ImageView imageView) {
        Call<ResponseBody> call1 = RetrofitAPI.getService().getPic( token,profil,mail);
        call1.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (response.body()!= null) {
                        Bitmap bmp = BitmapFactory.decodeStream(response.body().byteStream());
                        imageView.setImageBitmap(bmp);
                        imageView.refreshDrawableState();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                getPictures(profil,mail,imageView);

            }
        });
    }

    private void getMost() {
        Call<ArrayList<StartElemente>> call = RetrofitAPI.getService().getMost();
        ArrayList<StartElemente> arrayList = new ArrayList<>();
        String token = getIntent().getStringExtra("key");
        this.token = token;

        call.enqueue(new Callback<ArrayList<StartElemente>>() {
            @Override
            public void onResponse(Call<ArrayList<StartElemente>> call, Response<ArrayList<StartElemente>> response) {
                if (response.isSuccessful()) {
                    if (response.body()!=null) {
                        arrayList.addAll(response.body());
                        profil = arrayList.get(0).getName();
                        beschreibung = arrayList.get(0).getBeschreibung();
                        textView1.setText(arrayList.get(0).getName());
                        textView2.setText(arrayList.get(0).getBeschreibung());
                        textView4.setText(arrayList.get(1).getName());
                        textView3.setText(arrayList.get(1).getBeschreibung());
                        profil = arrayList.get(0).getName();
                        mail = arrayList.get(0).getMail();
                        profil2 = arrayList.get(1).getName();
                        mail2 = arrayList.get(1).getMail();
                        getPictures(profil, mail, imageView);
                        getPictures(profil2, mail2, imageView2);
                    }

                    /*Picasso.get().load(baseURL).placeholder(R.drawable.ic_default).into(imageView);
                    try {
                        URL url = new URL(baseURL);
                        Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        imageView.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println(baseURL);

                     */




                }

            }


            @Override
            public void onFailure(Call<ArrayList<StartElemente>> call, Throwable t) {
                getMost();
                System.out.println(t.getLocalizedMessage());

            }
        });


    }


}