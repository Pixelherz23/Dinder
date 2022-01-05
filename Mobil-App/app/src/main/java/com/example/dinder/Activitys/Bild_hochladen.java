package com.example.dinder.Activitys;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.dinder.API.RetrofitAPI;
import com.example.dinder.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Bild_hochladen extends AppCompatActivity implements View.OnClickListener {

    private Button bnChoose, bnUpload;
    private ImageView img;
    private static final int IMG_Request = 777;
    private Bitmap bitmap;
    public String profil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bild_hochladen);

        img = findViewById(R.id.IVPreviewImage);
        bnChoose = findViewById(R.id.BSelectImage);
        bnUpload = findViewById(R.id.BUploadImage);
        profil =getIntent().getStringExtra("profil");
        bnChoose.setOnClickListener(this);
        bnUpload.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.BSelectImage:
                selectImage();
                break;



            case R.id.BUploadImage:
                uploadImage();
                break;
        }
    }


    private void uploadImage() {

        String token = getIntent().getStringExtra("key");
        //File file = new File(imageToString());

        /*MultipartBody.Part filepart = MultipartBody.Part.createFormData("file", file.getName(),
                RequestBody.create(MediaType.parse("image/*"), file));

         */

        String image =imageToString();
        //System.out.println("Token " + token);
        //System.out.println("image " + image);



        RequestBody responseBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("image", image)
                .addFormDataPart("profilName", "ZoomDog2")
                .build();



        Call<Void> call = RetrofitAPI.getService().sendImg(token, profil, responseBody);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()){
                    Toast.makeText(Bild_hochladen.this, "Bild wurde gesendet", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Bild_hochladen.this, Tierprofil.class );
                    intent.putExtra("key",token);
                    startActivity(intent);

                }

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(Bild_hochladen.this, "Bild wurde nicht gesendet", Toast.LENGTH_SHORT).show();
                Toast.makeText(Bild_hochladen.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMG_Request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==IMG_Request && resultCode==RESULT_OK&& data!=null) {

            Uri path = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                img.setImageBitmap(bitmap);
                img.setVisibility(View.VISIBLE);
                bnUpload.setEnabled(true);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    private String imageToString() {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,40,byteArrayOutputStream);
        byte[] imgByte = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgByte,Base64.DEFAULT);

    }
}
