package com.example.dinder.Datamodel;

import com.google.gson.annotations.SerializedName;

public class Profildaten {

    @SerializedName("profilName")
    public String profilname;
    @SerializedName("beschreibung")
    public String beschreibung;
    @SerializedName("bewertungPositiv")
    public String positive;
    @SerializedName("bewertungNegativ")
    public String negative;
    @SerializedName("konto_email")
    public String email;

    public String getEmail() {
        return email;
    }

    public String getProfilname() {
        return profilname;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public String getPositive() {
        return positive;
    }

    public String getNegative() {
        return negative;
    }
}
