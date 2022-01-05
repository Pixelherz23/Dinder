package com.example.dinder.Datamodel;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("profil")
    public String profilName;
    @SerializedName("email")
    public String email;

    public String getProfilName() {
        return profilName;
    }

    public String getEmail() {
        return email;
    }
}
