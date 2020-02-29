package com.example.teniseri_26.db.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = Turniri.TABLE_NAME_USERS)
public class Turniri {

    public static final String TABLE_NAME_USERS = "turnir";

    public static final String FIELD_NAME_ID = "id";
    public static final String FIELD_NAME_NAZIV = "naziv";
    public static final String FIELD_NAME_IMAGE = "image";

    @DatabaseField(columnName = FIELD_NAME_ID, generatedId = true)
    private int id;

    @DatabaseField(columnName = FIELD_NAME_NAZIV)
    private String mNaziv;

    @DatabaseField(columnName = FIELD_NAME_IMAGE)
    private String mImage;

    public Turniri() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getmNaziv() {
        return mNaziv;
    }

    public void setmNaziv(String mNaziv) {
        this.mNaziv = mNaziv;
    }


    public String getmImage() {
        return mImage;
    }

    public void setmImage(String mImage) {
        this.mImage = mImage;
    }


    @Override
    public String toString() {
        return mNaziv;
    }
}
