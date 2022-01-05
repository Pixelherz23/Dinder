package com.example.dinder.Activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dinder.API.RetrofitAPI;
import com.example.dinder.Datamodel.LoginRequest;
import com.example.dinder.Datamodel.LoginResponse;
import com.example.dinder.R;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity implements View.OnClickListener {

    TextInputEditText username, password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        Button login = findViewById(R.id.buttonLogin);
        TextView register = findViewById(R.id.signUpText);
        register.setOnClickListener(this);
        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.signUpText) {
            Intent signUp = new Intent(this, SignUp.class);
            startActivity(signUp);
        }
         else if (TextUtils.isEmpty(username.getText().toString()) || TextUtils.isEmpty(password.getText().toString()) && v.getId() == R.id.buttonLogin ){
            Toast.makeText(Login.this, "Username und Passwort werden benötigt", Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.buttonLogin){
            login();


        }



    }


    public void login() {
        LoginRequest loginRequest = new LoginRequest();

        loginRequest.setUsername(username.getText().toString());
        loginRequest.setPassword(password.getText().toString());


        //loginRequest.setUsername("nacken@gmx.de");
        //loginRequest.setPassword("nacken");

        //loginRequest.setUsername("jj@gmail.de");
        //loginRequest.setPassword("abc");

        String base = loginRequest.getUsername() + ":" + loginRequest.getPassword();

        String authHeader = "Basic " + Base64.encodeToString(base.getBytes(),Base64.NO_WRAP);

        Call<LoginResponse> call = RetrofitAPI.getService().login(authHeader);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()){
                    LoginResponse loginResponse = response.body();
                    if (loginResponse != null) {
                        String token =  loginResponse.getToken();
                        Toast.makeText(Login.this, "Login", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(Login.this, StartSeite.class);
                        i.putExtra("key", token);
                        startActivity(i);


                    }


                } else {
                    Toast.makeText(Login.this, "Passwort oder Username falsch" , Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(Login.this, "Login fehlgeschlagen", Toast.LENGTH_SHORT).show();

            }
        });





    }





}
