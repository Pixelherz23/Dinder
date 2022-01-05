package com.example.dinder.Datamodel;

import com.google.gson.annotations.SerializedName;

public class TierRequest {

    @SerializedName("profilName")
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public String getAlter() {
        return alter;
    }

    public void setAlter(String alter) {
        this.alter = alter;
    }

    public String getGeschlecht() {
        return geschlecht;
    }

    public void setGeschlecht(String geschlecht) {
        this.geschlecht = geschlecht;
    }

    public String getGrosse() {
        return grosse;
    }

    public void setGrosse(String grosse) {
        this.grosse = grosse;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    @SerializedName("beschreibung")
    String beschreibung;
    @SerializedName("alter")
    String alter;
    @SerializedName("geschlecht")
    String geschlecht;
    @SerializedName("groesse")
    String grosse;
    @SerializedName("tierID")
    String id;
}
