package com.group.mydea;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

    public interface addedItem {
        public void itemAdded(Nota nota);
    }

    public static String TAG = "debug tag";
    public static String NOTA = "gfcg";

    private addedItem listener = new addedItem() {
        @Override
        public void itemAdded(Nota nota) {

        }
    };
    private CouchDB database;


    TextView mTvTitolo, mTvTestoNota;
    Button save;
    String myID;

    public static FragmentModificaNota getInstance(Nota nota) {

        FragmentModificaNota fragmentModificaNota = new FragmentModificaNota();
        Bundle bundle = new Bundle();
        bundle.putParcelable(NOTA, nota);
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
            Nota nota = getArguments().getParcelable(NOTA);
            //TODO settare testi della nota...
            mTvTitolo.setText(nota.getTitle());
            mTvTestoNota.setText(nota.getText());
            myID = nota.getID();
        }


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Nota nota = new Nota();
                nota.setId(myID);
                nota.setText(mTvTestoNota.getText().toString());
                nota.setTitle(mTvTitolo.getText().toString());


                try {
                    database.salvaNota(nota);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (CouchbaseLiteException e) {
                    e.printStackTrace();
                }
                listener.itemAdded(nota);
                getActivity().onBackPressed();
            }
        });
        return vView;
    }

}
