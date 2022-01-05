package com.example.dinder.Activitys;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dinder.API.RetrofitAPI;
import com.example.dinder.R;
import com.example.dinder.Datamodel.UserRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUp extends AppCompatActivity implements View.OnClickListener {

    EditText firstname, lastname, email, passwort, birthday, institution;
    Button btnRegister;
    TextView login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        firstname = findViewById(R.id.editTextTextPersonName9);
        lastname = findViewById(R.id.editTextTextPersonName10);
        email = findViewById(R.id.editTextTextPersonName11);
        passwort = findViewById(R.id.editTextTextPersonName12);
        birthday = findViewById(R.id.editTextTextPersonName13);
        institution = findViewById(R.id.editTextTextPersonName14);


        login = findViewById(R.id.loginText);
        btnRegister = findViewById(R.id.buttonSignUp);
        btnRegister.setOnClickListener(this);
        login.setOnClickListener(this);


    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.loginText) {
            Intent signUp = new Intent(this, Login.class);
            startActivity(signUp);
        } else if (firstname.getText().toString().isEmpty() || lastname.getText().toString().isEmpty() || email.getText().toString().isEmpty() || birthday.getText().toString().isEmpty() ||
                passwort.getText().toString().isEmpty() ) {
            Toast.makeText(SignUp.this, "Alle Felder werden benötigt", Toast.LENGTH_SHORT).show();
            return;
        }

        if (v.getId() == R.id.buttonSignUp) {
            saveuser(createRequest());
        }}


        public UserRequest createRequest() {
            UserRequest userRequest = new UserRequest();
            userRequest.setFirstname(firstname.getText().toString());
            userRequest.setLastname(lastname.getText().toString());
            userRequest.setEmail(email.getText().toString());
            userRequest.setPasswort(passwort.getText().toString());
            userRequest.setBirthday(birthday.getText().toString());
            userRequest.setInstitution(institution.getText().toString());


            return userRequest;

        }

        public void saveuser(UserRequest userRequest) {
        Call<Void> userResponseCall = RetrofitAPI.getService().createPost(userRequest.getFirstname(), userRequest.getLastname(),userRequest.getEmail()
        , userRequest.getPasswort(), userRequest.getBirthday(), userRequest.getInstitution());
        userResponseCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()){
                    Toast.makeText(SignUp.this, "Daten wurden gesendet", Toast.LENGTH_SHORT).show();
                    firstname.setText("");
                    lastname.setText("");
                    email.setText("");
                    passwort.setText("");
                    birthday.setText("");
                    institution.setText("");

                } else {
                    Toast.makeText(SignUp.this, "Daten wurden nicht gesendet" +response.message(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

                Toast.makeText(SignUp.this, "Irgendwas lief schief"+ t.getLocalizedMessage() , Toast.LENGTH_SHORT).show();

            }
        });

        }









/*
            if (v.getId() == R.id.buttonSignUp) {
                Intent login = new Intent(this, Login.class);
                startActivity(login);
            } else if (v.getId() == R.id.loginText) {
                Intent signUp = new Intent(this, Login.class);
                startActivity(signUp);
            }





    }

    private void postData() {
        fname = firstname.getText().toString();
        lname = lastname.getText().toString();
        emailaddresse = email.getText().toString();
        pwort = passwort.getText().toString();
        birth = birthday.getText().toString();
        insti = institution.getText().toString();


        Retrofit retrofit = new Retrofit.Builder().
                baseUrl("http://91.63.154.118:5000").
                addConverterFactory(GsonConverterFactory.create())
                .build();

        SignUpApi retrofitAPI = retrofit.create(SignUpApi.class);

        Datamodel datamodel = new Datamodel(fname, lname, emailaddresse, pwort, insti, birth);

        Call<Datamodel> call = retrofitAPI.createPost(datamodel);

        call.enqueue(new Callback<Datamodel>() {
            @Override
            public void onResponse(Call<Datamodel> call, Response<Datamodel> response) {
                System.out.println("Body " + response.body());
                System.out.println("Massage " + response.message());
                System.out.println("Succes " + response.isSuccessful());
                if (response.isSuccessful()) {
                    Toast.makeText(SignUp.this, "Daten wurden gesendet", Toast.LENGTH_SHORT).show();

                    firstname.setText("");
                    lastname.setText("");
                    passwort.setText("");
                    email.setText("");
                    birthday.setText("");
                    institution.setText("");

                } else {
                    Toast.makeText(SignUp.this, "Daten wurden nicht gesendet", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<Datamodel> call, Throwable t) {
                Toast.makeText(SignUp.this, "Failed " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        try {
            JSONObject param = new JSONObject();
            param.put("firstname", firstname.getText().toString());
            param.put("lastname", lastname.getText().toString());
            param.put("email", email.getText().toString());
            param.put("password", password.getText().toString());
            param.put("birthday", birthday.getText().toString());
            param.put("institution", institution.getText().toString());

            Call<JSONObject> call = retrofitAPI.createPost(param);
            call.enqueue((Callback<JSONObject>) this);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Datamodel datamodel = new Datamodel(fname,lname,emailAdresse,pword, instution, bday);

        Call<JSONObject> call = retrofitAPI.createPost(param);



        call.enqueue(new Callback<Datamodel>() {
            @Override
            public void onResponse(Call<Datamodel> call, Response<Datamodel> response) {
                System.out.println(response.message());
                if (response.isSuccessful()) {
                    Toast.makeText(SignUp.this, "Daten wurden gesendet", Toast.LENGTH_SHORT).show();

                    firstname.setText("");
                    lastname.setText("");
                    password.setText("");
                    email.setText("");
                    birthday.setText("");
                    institution.setText("");

                } else {
                    Toast.makeText(SignUp.this, "Daten wurden nicht gesendet", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<Datamodel> call, Throwable t) {

                Toast.makeText(SignUp.this, "Failed " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }


**/

}