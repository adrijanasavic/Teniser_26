package com.example.teniseri_26.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.teniseri_26.db.model.Teniseri;
import com.example.teniseri_26.db.model.Turniri;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    //TODO: Dajemo ime bazi
    private static final String DATABASE_NAME = "ormlite.db";
    //TODO: I pocetnu verziju baze. Obicno krece od 1
    private static final int DATABASE_VERSION = 1;


    private Dao<Teniseri, Integer> mTeniserDao = null;
    private Dao<Turniri, Integer> mTurnirDao = null;

    // TODO: Potreban je dodatni konstruktor zbog pravilne inicijalizacije biblioteke
    public DatabaseHelper(Context context) {
        super( context, DATABASE_NAME, null, DATABASE_VERSION );
    }

    // TODO: Prilikom kreiranja baze potrebno je da pozovemo odgovarajuce metode biblioteke
    // TODO: Prilikom kreiranja moramo pozvati TableUtil.createTable za svaku tabelu koju imamo
    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {

        try {
            TableUtils.createTable( connectionSource, Teniseri.class );
            TableUtils.createTable( connectionSource, Turniri.class );
        } catch (SQLException | java.sql.SQLException e) {
            throw new RuntimeException( e );
        }
    }


    // TODO: Kada zelimo da izmrenimo tabele, moramo pozvati TableUtil.dropTable za sve tabele koje iammo
    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {


        try {
            TableUtils.dropTable( connectionSource, Teniseri.class, true );
            TableUtils.dropTable( connectionSource, Turniri.class, true );
            onCreate( database, connectionSource );
        } catch (SQLException | java.sql.SQLException e) {
            throw new RuntimeException( e );

        }
    }

    //TODO: jedan Dao objekat sa kojim komuniciramo. Ukoliko imamo vise tabela
    //TODO: potrebno je napraviti Dao objekat za svaku tabelu
    public Dao<Teniseri, Integer> getTeniserDao() throws java.sql.SQLException {
        if (mTeniserDao == null) {
            mTeniserDao = getDao( Teniseri.class );
        }
        return mTeniserDao;
    }

    public Dao<Turniri, Integer> getTurnirDao() throws java.sql.SQLException {
        if (mTurnirDao == null) {
            mTurnirDao = getDao( Turniri.class );
        }
        return mTurnirDao;
    }


    //TODO: Obavezno prilikom zatvaranja rada sa bazom osloboditi resurse
    @Override
    public void close() {
        mTeniserDao = null;
        mTurnirDao = null;
        super.close();
    }
}
