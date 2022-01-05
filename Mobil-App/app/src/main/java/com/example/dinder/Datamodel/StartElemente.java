package com.example.dinder.Datamodel;

import com.google.gson.annotations.SerializedName;

public class StartElemente {


    public String getName() {
        return name;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public double getId() {
        return id;
    }

    public String getMail() {
        return mail;
    }

    public double getNegative() {
        return negative;
    }

    public double getPositive() {
        return positive;
    }

    @SerializedName("profilname")
    private String name;
    @SerializedName("discription")
    private String beschreibung;
    @SerializedName("id")
    private double id;
    @SerializedName("mail")
    private String mail;
    @SerializedName("negative")
    private double negative;
    @SerializedName("positive")
    private double positive;
}
