package com.group.mydea;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
        return new HolderAdapterNota(v);
    }


    @Override
    public void onBindViewHolder(final HolderAdapterNota cardHolder, final int position) {

        cardHolder.titolo.setText(note.get(position).getTitle());
        cardHolder.testo.setText(note.get(position).getText());

        cardHolder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(ctx, "Ti piacerebbe premre la card numero " + position + " eh?", Toast.LENGTH_SHORT).show();

                //TODO Chiamare la visualizzazione nota
            }
        });

        cardHolder.card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
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
