package com.group.mydea;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentModificaNota extends Fragment {

    public static String TAG = "debug tag";
    public static String NOTA = "gfcg";


    TextView mTvTitolo, mTvTestoNota;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vView = inflater.inflate(R.layout.fragment_fragment_modifica_nota, container, false);

            mTvTitolo = (TextView) vView.findViewById(R.id.tvTitoloNota);
            mTvTestoNota = (TextView) vView.findViewById(R.id.tvTestoNota);
            mTvTestoNota.setText("Modify text..");
        if (getArguments() != null) {
            Nota nota = getArguments().getParcelable(NOTA);
            //TODO settare testi della nota...
            mTvTitolo.setText(nota.getTitle());
        }
        return vView;
    }

}
