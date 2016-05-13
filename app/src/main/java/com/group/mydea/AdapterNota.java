package com.group.mydea;



import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.viewanimator.ViewAnimator;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by andrea on 13/05/16.
 */
public class AdapterNota extends RecyclerView.Adapter<AdapterNota.HolderAdapterNota> {

    FragmentModificaNota mFragmentModificaNota;

    public static String TAG_FRAGMENT_MODIFICA_NOTA="tagfragmentmodificanota";

    private ArrayList<Nota> note;  //lista di note
    private Context ctx;

    private static final String TAG = "AdapterNote";

    public AdapterNota(ArrayList<Nota> note, Context ctx) {
        this.note = note;
        this.ctx = ctx;
        Collections.sort(this.note);
    }

    @Override
    public HolderAdapterNota onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_nota, viewGroup, false);

        FragmentManager fragmentManager=((MainActivity)ctx).getSupportFragmentManager();
        mFragmentModificaNota=(FragmentModificaNota) fragmentManager.findFragmentByTag(TAG_FRAGMENT_MODIFICA_NOTA);

        return new HolderAdapterNota(v);
    }



    @Override
    public void onBindViewHolder(final HolderAdapterNota cardHolder, final int position) {

        cardHolder.titolo.setText(note.get(position).getTitle());
        cardHolder.testo.setText(note.get(position).getText());

        cardHolder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(ctx, "Ti piacerebbe premere la card numero " + position + " eh?", Toast.LENGTH_SHORT).show();

                //TODO Chiamare la visualizzazione nota

                if(mFragmentModificaNota==null){
                    FragmentTransaction vTrans=((MainActivity)ctx).getSupportFragmentManager().beginTransaction();
                    mFragmentModificaNota=FragmentModificaNota.getInstance();
                    vTrans.add(R.id.container, mFragmentModificaNota, TAG_FRAGMENT_MODIFICA_NOTA);
                    vTrans.commit();
                }

            }
        });

        cardHolder.card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Snackbar snackbar = Snackbar
                        .make(v, "Vediamo", Snackbar.LENGTH_LONG)
                        .setAction("Cestina", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Snackbar snackbar1 = Snackbar.make(view, "Eliminatoo?", Snackbar.LENGTH_SHORT);
                                snackbar1.show();
                            }
                        })/*("Sharra", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Snackbar snackbar2 = Snackbar.make(view, "Sharrato?", Snackbar.LENGTH_SHORT);
                                snackbar2.show();
                            }
                        })*/;

                snackbar.show();
                //Toast.makeText(ctx, "Ti piacerebbe tenere premuto la card numero " + position + " eh?", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
      /*  ViewAnimator
                .animate(cardHolder.itemView)
                .bounceIn().interpolator(new BounceInterpolator())
                .wave().duration(700)
                .start();*/
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return note.size();
    }


    /**
     * "Contenitore" di ogni card
     */
    public static class HolderAdapterNota extends RecyclerView.ViewHolder {
        CardView card;
        TextView titolo;
        TextView testo;

        HolderAdapterNota(View itemView) {
            super(itemView);
            card = (CardView) itemView.findViewById(R.id.cardView);
            titolo = (TextView) itemView.findViewById(R.id.txtTitoloNota);
            testo = (TextView) itemView.findViewById(R.id.txtTestoNota);
        }
    }

}
