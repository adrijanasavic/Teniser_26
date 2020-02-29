package com.example.teniseri_26.db.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = Teniseri.TABLE_NAME_USERS)
public class Teniseri {

    public static final String TABLE_NAME_USERS = "teniser";

    public static final String FIELD_NAME_ID = "id";
    public static final String FIELD_NAME_IME = "ime";
    public static final String FIELD_NAME_PREZIME = "prezime";
    public static final String FIELD_NAME_BIOGRAFIJA = "biografija";
    public static final String FIELD_NAME_IMAGE = "image";
    public static final String FIELD_NAME_RATING = "rating";
    public static final String FIELD_NAME_DATUM = "datum";
    public static final String FIELD_NAME_TURNIR = "turnir";

    @DatabaseField(columnName = FIELD_NAME_ID, generatedId = true)
    private int mId;

    @DatabaseField(columnName = FIELD_NAME_IME)
    private String mIme;

    @DatabaseField(columnName = FIELD_NAME_PREZIME)
    private String mPrezime;

    @DatabaseField(columnName = FIELD_NAME_BIOGRAFIJA)
    private String mBiografija;

    @DatabaseField(columnName = FIELD_NAME_IMAGE)
    private String mImage;

    @DatabaseField(columnName = FIELD_NAME_RATING)
    private float mRating;

    @DatabaseField(columnName = FIELD_NAME_DATUM)
    private String mDatum;

    @DatabaseField(columnName = FIELD_NAME_TURNIR, foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    private Turniri turniri;


    public Teniseri() {
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getmIme() {
        return mIme;
    }

    public void setmIme(String mIme) {
        this.mIme = mIme;
    }

    public String getmPrezime() {
        return mPrezime;
    }

    public void setmPrezime(String mPrezime) {
        this.mPrezime = mPrezime;
    }

    public String getmBiografija() {
        return mBiografija;
    }

    public void setmBiografija(String mBiografija) {
        this.mBiografija = mBiografija;
    }

    public String getmImage() {
        return mImage;
    }

    public void setmImage(String mImage) {
        this.mImage = mImage;
    }

    public float getmRating() {
        return mRating;
    }

    public void setmRating(float mRating) {
        this.mRating = mRating;
    }

    public String getmDatum() {
        return mDatum;
    }

    public void setmDatum(String mDatum) {
        this.mDatum = mDatum;
    }

    public Turniri getTurniri() {
        return turniri;
    }

    public void setTurniri(Turniri turniri) {
        this.turniri = turniri;
    }

    @Override
    public String toString() {
        return mIme + " " + mPrezime;
    }
}


