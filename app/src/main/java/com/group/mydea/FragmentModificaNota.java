package com.group.mydea;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.couchbase.lite.CouchbaseLiteException;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentModificaNota extends Fragment {

    private Nota oldNota;
    private Nota newNota;
    private int pos;


    public interface addedItem {
        public void itemUpdated(Nota nota,int pos);
    }

    public static String TAG = "debug tag";
    public static String NOTA = "gfcg";

    private addedItem listener = new addedItem() {
        @Override
        public void itemUpdated(Nota nota, int pos) {

        }
    };
    private CouchDB database;


    TextView mTvTitolo, mTvTestoNota;
    Button save;
    String myID;

    public static FragmentModificaNota getInstance(Nota nota, int position) {

        FragmentModificaNota fragmentModificaNota = new FragmentModificaNota();
        Bundle bundle = new Bundle();
        bundle.putParcelable(NOTA, nota);
        bundle.putInt("POS",position);
        fragmentModificaNota.setArguments(bundle);
        return fragmentModificaNota;
    }

    public FragmentModificaNota() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        Activity hostActivity = getActivity();
        if (hostActivity instanceof addedItem) {
            listener = (addedItem) hostActivity;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vView = inflater.inflate(R.layout.fragment_fragment_modifica_nota, container, false);

        database = new CouchDB(getContext());

        mTvTitolo = (TextView) vView.findViewById(R.id.tvtTitoloNota);
        mTvTestoNota = (TextView) vView.findViewById(R.id.tvtTestoNota);
        save = (Button) vView.findViewById(R.id.save);
        if (getArguments() != null) {
            pos=getArguments().getInt("POS");
            oldNota = getArguments().getParcelable(NOTA);
            //TODO settare testi della nota...
            mTvTitolo.setText(oldNota.getTitle());
            mTvTestoNota.setText(oldNota.getText());
            myID = oldNota.getID();
            Log.i(TAG, "onCreateView: " + myID);
        }


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newNota = new Nota();
                newNota.setId(myID);
                newNota.setText(mTvTestoNota.getText().toString());
                newNota.setTitle(mTvTitolo.getText().toString());
                Log.i(TAG, "onClick: " + newNota.getID());


                try {
                    database.salvaNota(newNota);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (CouchbaseLiteException e) {
                    e.printStackTrace();
                }
                listener.itemUpdated(newNota, pos);
                getActivity().onBackPressed();
            }
        });
        return vView;
    }

}
