package com.example.teniseri_26.fragmenti;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;


import com.example.teniseri_26.activities.MainActivity;
import com.example.teniseri_26.R;
import com.example.teniseri_26.db.model.Teniseri;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

public class DetailFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private static int NOTIFICATION_ID = 1;

    private Teniseri teniseri = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated( savedInstanceState );

        if (savedInstanceState != null) {
            teniseri = new Teniseri();
            teniseri.setmId( savedInstanceState.getInt( "id" ) );
            teniseri.setmIme( savedInstanceState.getString( "name" ) );
            teniseri.setmBiografija( savedInstanceState.getString( "description" ) );
            teniseri.setmRating( savedInstanceState.getFloat( "rating" ) );
            teniseri.setmImage( savedInstanceState.getString( "image" ) );
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState( savedInstanceState );

        if (savedInstanceState != null) {
            savedInstanceState.putInt( "id", teniseri.getmId() );
            savedInstanceState.putString( "name", teniseri.getmIme() );
            savedInstanceState.putString( "description", teniseri.getmBiografija() );
            savedInstanceState.putFloat( "rating", teniseri.getmRating() );
            savedInstanceState.putString( "image", teniseri.getmImage() );
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v( "DetailFragment", "onCreateView()" );

        setHasOptionsMenu( true );

        View view = inflater.inflate( R.layout.detail_fragment, container, false );

        TextView name = (TextView) view.findViewById( R.id.name );
        name.setText( teniseri.getmIme() );

        TextView description = (TextView) view.findViewById( R.id.description );
        description.setText( teniseri.getmBiografija() );

//        TextView category = (TextView) view.findViewById(R.id.category);
//        category.setText(teniseri.getTurniri().getmNaziv());

        RatingBar ratingBar = (RatingBar) view.findViewById( R.id.rating );
        ratingBar.setRating( teniseri.getmRating() );

        ImageView imageView = (ImageView) view.findViewById( R.id.image );

        Uri mUri = Uri.parse( teniseri.getmImage() );
        imageView.setImageURI( mUri );

//        FloatingActionButton button = (FloatingActionButton) view.findViewById(R.id.buy);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Creates notification with the notification builder
//                NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity());
//                Bitmap bitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.ic_stat_buy);
//                builder.setSmallIcon(R.drawable.ic_stat_buy);
//                builder.setContentTitle(getActivity().getString(R.string.notification_title));
//                builder.setContentText(getActivity().getString(R.string.notification_text));
//                builder.setLargeIcon(bitmap);
//
//                // Shows notification with the notification manager (notification ID is used to update the notification later on)
//                NotificationManager manager = (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
//                manager.notify(NOTIFICATION_ID, builder.build());
//            }
//        });

        return view;
    }


    public void setTeniseri(Teniseri teniseri) {
        this.teniseri = teniseri;
    }

    public void updateTeniser(Teniseri teniseri) {
        this.teniseri = teniseri;

        TextView name = (TextView) getActivity().findViewById( R.id.name );
        name.setText( teniseri.getmIme() );

        TextView description = (TextView) getActivity().findViewById( R.id.description );
        description.setText( teniseri.getmBiografija() );

        RatingBar ratingBar = (RatingBar) getActivity().findViewById( R.id.rating );
        ratingBar.setRating( teniseri.getmRating() );

        ImageView imageView = (ImageView) getActivity().findViewById( R.id.image );
        InputStream is = null;
        try {
            is = getActivity().getAssets().open( teniseri.getmImage() );
            Drawable drawable = Drawable.createFromStream( is, null );
            imageView.setImageDrawable( drawable );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        teniseri.setTurniri( null );
    }

    /**
     * Kada dodajemo novi element u toolbar potrebno je da obrisemo prethodne elmente
     * zato pozivamo menu.clear() i dodajemo nove toolbar elemente
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate( R.menu.detail_fragment_menu, menu );
        super.onCreateOptionsMenu( menu, inflater );
    }

    /**
     * Na fragment dodajemo element za brisanje elementa
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.remove:
                try {
                    if (teniseri != null) {
                        ((MainActivity) getActivity()).getDatabaseHelper().getTeniserDao().delete( teniseri );
                        getActivity().onBackPressed();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
        }

        return super.onOptionsItemSelected( item );
    }
}



