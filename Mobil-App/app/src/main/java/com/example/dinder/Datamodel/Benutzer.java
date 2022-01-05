package com.example.dinder.Datamodel;

import com.google.gson.annotations.SerializedName;

public class Benutzer {

    @SerializedName("profilName")
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
