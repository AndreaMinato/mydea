package com.group.mydea;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentImmagine extends Fragment {

    ImageView imgNota;

    public static FragmentImmagine getIstance(){
        return new FragmentImmagine();
    }

    public FragmentImmagine() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vView=inflater.inflate(R.layout.fragment_immagine, container, false);

        imgNota=(ImageView) vView.findViewById(R.id.immagineNota);
        setImg();

        return vView;
    }

    public void setImg(){

        Picasso.with(getContext())
                .load(R.drawable.ic_menu_gallery)
                .resize(50, 50)
                .centerCrop()
                .into(imgNota);
    }

}
