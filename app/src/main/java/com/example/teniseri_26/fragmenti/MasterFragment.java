package com.example.teniseri_26.fragmenti;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;


import com.example.teniseri_26.R;
import com.example.teniseri_26.db.DatabaseHelper;
import com.example.teniseri_26.db.model.Teniseri;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.List;

public class MasterFragment extends Fragment {

    private DatabaseHelper databaseHelper;


    public interface OnProductSelectedListener {
        void onProductSelected(int id);
    }

    OnProductSelectedListener listener;
    ListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated( savedInstanceState );

        try {
            List<Teniseri> list = getDatabaseHelper().getTeniserDao().queryForAll();

            adapter = new ArrayAdapter<>( getActivity(), R.layout.single_list_item, list );

            final ListView listView = getActivity().findViewById( R.id.lvList );

            listView.setAdapter( adapter );

            listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Teniseri teniser = (Teniseri) listView.getItemAtPosition( position );

                    listener.onProductSelected( teniser.getmId() );
                }
            } );

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        View view = inflater.inflate( R.layout.master_fragment, container, false );
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach( activity );

        try {
            listener = (OnProductSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException( activity.toString() + " must implement OnProductSelectedListener" );
        }
    }

    public DatabaseHelper getDatabaseHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper( getActivity(), DatabaseHelper.class );
        }
        return databaseHelper;
    }
}
