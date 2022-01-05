package com.example.dinder.Datamodel;

import com.google.gson.annotations.SerializedName;

public class TierDaten {

    @SerializedName("profilname")
    private String profilname;
    @SerializedName("discription")
    private String beschreibung;
    @SerializedName("id")
    private String id;
    @SerializedName("mail")
    private String mail;
    @SerializedName("negative")
    private String negative;
    @SerializedName("positive")
    private String positive;
    @SerializedName("rememberListID")
    private String rememberListID;
    @SerializedName("likeListID")
    private String likeListID;

    public String getId() {
        return id;
    }

    public String getMail() {
        return mail;
    }

    public String getNegative() {
        return negative;
    }

    public String getPositive() {
        return positive;
    }

    public String getRememberListID() {
        return rememberListID;
    }

    public String getLikeListID() {
        return likeListID;
    }

    public String getProfilname() {
        return profilname;
    }

    public String getBeschreibung() {
        return beschreibung;
    }
}
