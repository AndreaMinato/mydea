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

    public static String TAG="debug tag";

    TextView mTvTitolo,mTvTestoNota;

    public static FragmentModificaNota getInstance(){
        return new FragmentModificaNota();
    }

    public FragmentModificaNota() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vView=inflater.inflate(R.layout.fragment_fragment_modifica_nota, container, false);

        mTvTitolo=(TextView)vView.findViewById(R.id.tvTitoloNota);
        mTvTestoNota=(TextView)vView.findViewById(R.id.tvTestoNota);

        mTvTestoNota.setText("Modify text..");

        return vView;
    }

}
