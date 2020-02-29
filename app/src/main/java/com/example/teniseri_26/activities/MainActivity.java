package com.example.teniseri_26.activities;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;


import com.example.teniseri_26.db.NavigationItem;
import com.example.teniseri_26.R;
import com.example.teniseri_26.tools.ReviewerTools;
import com.example.teniseri_26.service.SimpleReceiver;
import com.example.teniseri_26.service.SimpleService;
import com.example.teniseri_26.db.DatabaseHelper;
import com.example.teniseri_26.db.model.Teniseri;
import com.example.teniseri_26.db.model.Turniri;
import com.example.teniseri_26.dialog.AboutDialog;
import com.example.teniseri_26.fragmenti.DetailFragment;
import com.example.teniseri_26.fragmenti.MasterFragment;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MasterFragment.OnProductSelectedListener {


    private static final int SELECT_PICTURE = 1;

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItemFromDrawer( position );
        }
    }

    private AlertDialog dialog;

    private ArrayList<NavigationItem> navigationItems = new ArrayList<NavigationItem>();
    private CharSequence title;
    private CharSequence drawerTitle;

    List<String> drawerItems;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    ListView drawerList;
    private AlertDialog dijalog;
    private RelativeLayout drawerPane;
    private ActionBarDrawerToggle drawerToggle;
    //private Drawable drawable;

    private SimpleReceiver sync;
    private PendingIntent pendingIntent;
    private AlarmManager manager;

    private SharedPreferences sharedPreferences;
    private String synctime;
    private boolean allowSync;

    private boolean landscapeMode = false;
    private boolean listShown = false;
    private boolean detailShown = false;

    private boolean isLandscape = false;
    private int position = 0;

    private int productId = 0;

    private DatabaseHelper databaseHelper;
    private ImageView preview;
    private String imagePath = null;
    private static final String TAG = "PERMISSIONS";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        if (savedInstanceState != null) {
            this.position = savedInstanceState.getInt( "position" );
        }

        fillData();
        setupToolbar();
        setupDrawer();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate( R.menu.activity_item_detail, menu );
        return super.onCreateOptionsMenu( menu );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                refresh();
                break;
            case R.id.action_add:
                try {
                    addItem();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.action_add_category:
                addCategory();
                break;
        }

        return super.onOptionsItemSelected( item );
    }


    private void fillData() {
        drawerItems = new ArrayList<>();
        drawerItems.add( "Teniseri" );
        drawerItems.add( "Settings" );
        drawerItems.add( "About" );

    }

    private void showDialog() {
        if (dijalog == null) {
            dijalog = new AboutDialog( MainActivity.this ).prepareDialog();
        } else {
            if (dijalog.isShowing()) {
                dijalog.dismiss();
            }
        }
        dijalog.show();
    }

    private void reset() {
        imagePath = "";
        preview = null;
    }


    private void showTeniser() {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        MasterFragment noviMasterFragment = new MasterFragment();
        ft.replace( R.id.fragment1, noviMasterFragment );
//            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();

        if (findViewById( R.id.fragment2 ) != null) {
            isLandscape = true;
            getFragmentManager().popBackStack();

            DetailFragment noviDetailFragment = (DetailFragment) getSupportFragmentManager().findFragmentById( R.id.fragment2 );
            if (noviDetailFragment == null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                noviDetailFragment = new DetailFragment();
                transaction.replace( R.id.fragment2, noviDetailFragment );
//                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.commit();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult( requestCode, resultCode, data );

        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            if (selectedImage != null) {
                Cursor cursor = getContentResolver().query( selectedImage, filePathColumn, null, null, null );
                if (cursor != null) {
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex( filePathColumn[0] );
                    imagePath = cursor.getString( columnIndex );
                    cursor.close();

                    // String picturePath contains the path of selected Image

                    if (preview != null) {
                        preview.setImageBitmap( BitmapFactory.decodeFile( imagePath ) );
                    }

                    Toast.makeText( this, imagePath, Toast.LENGTH_SHORT ).show();
                }
            }
        }
    }

    //da bi dodali podatak u bazu, potrebno je da napravimo objekat klase
    //koji reprezentuje tabelu i popunimo podacima
    private void addItem() throws SQLException {
        final Dialog dialog = new Dialog( this );
        dialog.setContentView( R.layout.dialog_layout );

        Button choosebtn = dialog.findViewById( R.id.choose );
        choosebtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preview = dialog.findViewById( R.id.preview_image );
                selectPicture();
            }
        } );

        final Spinner productsSpinner = dialog.findViewById( R.id.product_category );
        List<Turniri> list = getDatabaseHelper().getTurnirDao().queryForAll();
        ArrayAdapter<Turniri> dataAdapter = new ArrayAdapter<>( this, android.R.layout.simple_spinner_item, list );
        productsSpinner.setAdapter( dataAdapter );
        productsSpinner.setSelection( 0 );

        final EditText productName = dialog.findViewById( R.id.product_name );
        final EditText productDescr = dialog.findViewById( R.id.product_description );
        final EditText productRating = dialog.findViewById( R.id.product_rating );

        Button ok = dialog.findViewById( R.id.ok );
        ok.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String name = productName.getText().toString();
                    String desct = productDescr.getText().toString();
                    float price = Float.parseFloat( productRating.getText().toString() );
                    Turniri turniri = (Turniri) productsSpinner.getSelectedItem();

                    if (name.isEmpty()) {
                        Toast.makeText( MainActivity.this, "Ime teniser ne sme biti prazno", Toast.LENGTH_SHORT ).show();
                        return;
                    }

                    if (desct.isEmpty()) {
                        Toast.makeText( MainActivity.this, "Opis tenisera ne sme biti prazno", Toast.LENGTH_SHORT ).show();
                        return;
                    }

                    if (turniri == null) {
                        Toast.makeText( MainActivity.this, "Kategorija mora biti izabrana", Toast.LENGTH_SHORT ).show();
                        return;
                    }

                    if (preview == null || imagePath == null) {
                        Toast.makeText( MainActivity.this, "Slika mora biti izabrana", Toast.LENGTH_SHORT ).show();
                        return;
                    }

                    Teniseri teniser = new Teniseri();
                    teniser.setmIme( name );
                    teniser.setmBiografija( desct );
                    teniser.setmRating( price );
                    teniser.setmImage( imagePath );
                    teniser.setTurniri( turniri );


                    getDatabaseHelper().getTeniserDao().create( teniser );
                    refresh();
                    Toast.makeText( MainActivity.this, "Tenise je upisan", Toast.LENGTH_SHORT ).show();
                    dialog.dismiss();

                    reset();

                } catch (SQLException e) {
                    e.printStackTrace();

                } catch (NumberFormatException ee) {
                    Toast.makeText( MainActivity.this, "Rating more biti broj", Toast.LENGTH_SHORT ).show();
                }
            }
        } );

        Button cancel = dialog.findViewById( R.id.cancel );
        cancel.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        } );

        if (dataAdapter.isEmpty()) {
            Toast.makeText( MainActivity.this, "Ne postoji ni jedna uneta kategorija. Prvo unestie kategoriju", Toast.LENGTH_SHORT ).show();
        }

        dialog.show();
    }

    private void selectPicture() {
        if (isStoragePermissionGranted()) {
            Intent i = new Intent( Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
            startActivityForResult( i, SELECT_PICTURE );
        }
    }

    private void addCategory() {
        final Dialog dialog = new Dialog( this );
        dialog.setContentView( R.layout.dialog_category_layout );

        Button choosebtn = dialog.findViewById( R.id.choose );
        choosebtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preview = dialog.findViewById( R.id.preview_image );
                selectPicture();
            }
        } );

        final EditText productName = dialog.findViewById( R.id.product_name );

        Button ok = dialog.findViewById( R.id.ok );
        ok.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = productName.getText().toString();

                if (preview == null || imagePath == null) {
                    Toast.makeText( MainActivity.this, "Slika mora biti izabrana", Toast.LENGTH_SHORT ).show();
                    return;
                }

                if (name.isEmpty()) {
                    Toast.makeText( MainActivity.this, "Ime kategorije ne sme biti prazno", Toast.LENGTH_SHORT ).show();
                    return;
                }

                Turniri category = new Turniri();
                category.setmNaziv( name );
                category.setmImage( imagePath );

                try {
                    getDatabaseHelper().getTurnirDao().create( category );
                    refresh();
                    Toast.makeText( MainActivity.this, "Category inserted", Toast.LENGTH_SHORT ).show();
                    dialog.dismiss();

                    reset();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } );

        Button cancel = dialog.findViewById( R.id.cancel );
        cancel.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        } );

        dialog.show();

    }

    private void refresh() {
        ListView listview = findViewById( R.id.lvList );

        if (listview != null) {
            ArrayAdapter<Teniseri> adapter = (ArrayAdapter<Teniseri>) listview.getAdapter();

            if (adapter != null) {
                try {
                    adapter.clear();
                    List<Teniseri> list = getDatabaseHelper().getTeniserDao().queryForAll();

                    adapter.addAll( list );

                    adapter.notifyDataSetChanged();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void setupDrawer() {
        drawerList = findViewById( R.id.left_drawer );
        drawerLayout = findViewById( R.id.drawer_layout );
        drawerPane = findViewById( R.id.drawerPane );

        drawerList.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String title = "Unknown";
                switch (i) {
                    case 0:
                        title = "Teniseri";
                        showTeniser();
                        break;
                    case 1:
                        title = "Settings";
                        Intent settings = new Intent( MainActivity.this, SettingActivity.class );
                        startActivity( settings );
                        break;
                    case 2:
                        title = "About";
                        showDialog();
                        break;

                    default:
                        break;
                }
                drawerList.setItemChecked( i, true );
                setTitle( title );
                drawerLayout.closeDrawer( drawerPane );
            }
        } );
        drawerList.setAdapter( new ArrayAdapter<String>( this, android.R.layout.simple_list_item_1, drawerItems ) );


        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.app_name,
                R.string.app_name
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle( title );
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle( drawerTitle );
                invalidateOptionsMenu();
            }
        };
    }

    public void setupToolbar() {
        toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled( true );
            actionBar.setHomeAsUpIndicator( R.drawable.ic_menu );
            actionBar.setHomeButtonEnabled( true );
            actionBar.show();
        }
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission( Manifest.permission.WRITE_EXTERNAL_STORAGE )
                    == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission( Manifest.permission.READ_EXTERNAL_STORAGE )
                            == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {

                ActivityCompat.requestPermissions( this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 1 );
                return false;
            }
        } else {
            return true;
        }
    }

    private void selectItemFromDrawer(int position) {
        if (position == 0) {

        } else if (position == 1) {
            Intent settings = new Intent( MainActivity.this, SettingActivity.class );
            startActivity( settings );
        } else if (position == 2) {
            if (dialog == null) {
                dialog = new AboutDialog( MainActivity.this ).prepareDialog();
            } else {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }

            dialog.show();
        }

        drawerList.setItemChecked( position, true );
        setTitle( navigationItems.get( position ).getTitle() );
        drawerLayout.closeDrawer( drawerPane );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults );

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED) {
        }
    }


    @Override
    protected void onResume() {

        super.onResume();

        setUpReceiver();
        setUpManager();
    }

    private void setUpReceiver() {
        sync = new SimpleReceiver();

        IntentFilter filter = new IntentFilter();
        filter.addAction( "SYNC_DATA" );
        filter.addAction( "MY_COMMENT" );
        registerReceiver( sync, filter );

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences( this );

        consultPreferences();
    }

    private void setUpManager() {
        Intent intent = new Intent( this, SimpleService.class );
        int status = ReviewerTools.getConnectivityStatus( getApplicationContext() );
        intent.putExtra( "STATUS", status );

        if (allowSync) {
            PendingIntent pintent = PendingIntent.getService( this, 0, intent, 0 );
            AlarmManager alarm = (AlarmManager) getSystemService( Context.ALARM_SERVICE );
            alarm.setRepeating( AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                    ReviewerTools.calculateTimeTillNextSync( Integer.parseInt( synctime ) ),
                    pintent );
        }
    }


    private void consultPreferences() {
        synctime = sharedPreferences.getString( getString( R.string.pref_sync_list ), "1" );
        allowSync = sharedPreferences.getBoolean( getString( R.string.pref_sync ), false );
    }

    @Override
    protected void onPause() {
        if (manager != null) {
            manager.cancel( pendingIntent );
            manager = null;
        }

        if (sync != null) {
            unregisterReceiver( sync );
            sync = null;
        }

        super.onPause();
    }


    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }

    }

    public DatabaseHelper getDatabaseHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper( this, DatabaseHelper.class );
        }
        return databaseHelper;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        super.onSaveInstanceState( savedInstanceState );
        savedInstanceState.putInt( "position", position );
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate( savedInstanceState );

        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged( configuration );

        drawerToggle.onConfigurationChanged( configuration );
    }

    @Override
    public void onProductSelected(int id) {

        productId = id;

        try {
            Teniseri teniseri = getDatabaseHelper().getTeniserDao().queryForId( id );

            if (isLandscape) {
                DetailFragment detailFragment = (DetailFragment) getSupportFragmentManager().findFragmentById( R.id.fragment2 );
                detailFragment.updateTeniser( teniseri );
            } else {
                DetailFragment detailFragment = new DetailFragment();
                detailFragment.setTeniseri( teniseri );
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace( R.id.fragment1, detailFragment, "Detail_Fragment2" );
                ft.setTransition( FragmentTransaction.TRANSIT_FRAGMENT_FADE );
                ft.addToBackStack( "Detail_Fragment2" );
                ft.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public void onBackPressed() {
//
//        if (landscapeMode) {
//            finish();
//        } else if (listShown == true) {
//            finish();
//        } else if (detailShown == true) {
//            getFragmentManager().popBackStack();
//            MasterFragment masterFragment = new MasterFragment();
//            android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
//            ft.replace( R.id.fragment1, masterFragment, "List_Fragment" );
//            ft.setTransition( android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE );
//            ft.commit();
//            listShown = true;
//            detailShown = false;
//        }
//    }
}

