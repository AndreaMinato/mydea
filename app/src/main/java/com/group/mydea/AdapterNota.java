package com.group.mydea;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.github.florent37.viewanimator.ViewAnimator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by andrea on 13/05/16.
 */
public class AdapterNota extends RecyclerView.Adapter<AdapterNota.HolderAdapterNota> {




    public static final String TAG_FRAGMENT_MODIFICA_NOTA = "tagfragmentmodificanota";
    public static String TAG="debug tag";
    public static String TAG_FRAGMENT_IMG_NOTA="tagfragmentmodificanota";


    private CouchDB db;
    private CryptData myCypher=new CryptData();
    private ArrayList<Nota> note;  //lista di note
    private Context ctx;
    private android.app.FragmentManager fragmentManager;
    private FragmentImmagine mFragmentImmagine;
    private String substrTestoNota;
    private String substrTitoloNota;
    private int sfondoNota;


    public AdapterNota(ArrayList<Nota> note, Context ctx, android.app.FragmentManager fragmentManager) {
        this.note = note;
        this.ctx = ctx;
        this.fragmentManager=fragmentManager;
        db = new CouchDB(ctx);
        Collections.sort(this.note);
    }

    @Override
    public HolderAdapterNota onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_nota, viewGroup, false);

        /**TODO: HERE DECRYPT POSTS.*/
        /*for(int i=0; i<note.size();i++) {
            Log.d(TAG,"------------------------------------------------------------------------------------------------>"+ i);
            myCypher.decryptNota(note.get(i));
        }*/

        return new HolderAdapterNota(v);
    }


    @Override
    public void onBindViewHolder(final HolderAdapterNota cardHolder, final int position) {

        /**TODO: HERE DECRYPT POSTS.*/
       // myCypher.decryptNota(note.get(position));


        Log.d(TAG,"Requested post id:"+note.get(position).getID());


        if(note.get(position).getTitle().length()>15)
            substrTitoloNota = note.get(position).getTitle().substring(0, 12)+"...";
        else
            substrTitoloNota=note.get(position).getTitle();

        if(note.get(position).getText().length()>50)
            substrTestoNota=note.get(position).getText().substring(0,47) + "...";
        else
            substrTestoNota=note.get(position).getText();

        switch (note.get(position).getTag()) {
            case 1: //LAVORO
                sfondoNota = Color.parseColor("#c6ecf0");
                break;
            case 2: //PERSONALE
                sfondoNota = Color.parseColor("#fadbf6");
                break;
            case 3: //HOBBY
                sfondoNota = Color.parseColor("#d7f7cf");
                break;
            case 4: //TEMPO LIBERO
                sfondoNota = Color.parseColor("#efdbae");
                break;
            default:
                sfondoNota = Color.WHITE;
                break;
        }


        cardHolder.titolo.setText(substrTitoloNota);
        cardHolder.testo.setText(substrTestoNota);
        cardHolder.card.setCardBackgroundColor(sfondoNota);

        cardHolder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(ctx, "Ti piacerebbe premere la card numero " + position + " eh?", Toast.LENGTH_SHORT).show();

                //TODO Chiamare la visualizzazione nota e il fragment imgNOTA

                FragmentModificaNota fragmentModificaNota= FragmentModificaNota.getInstance(note.get(position), position);
                fragmentModificaNota.show(fragmentManager, TAG_FRAGMENT_MODIFICA_NOTA);

               /* mFragmentImmagine=FragmentImmagine.getIstance();
                mFragmentImmagine.onCreate(fragmentManager,TAG_FRAGMENT_IMG_NOTA);*/

            }
        });

        cardHolder.card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Snackbar snackbar = Snackbar
                        .make(v, " ", Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.delete, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final Nota tmpNote = note.get(position);
                                note.remove(position);
                                notifyDataSetChanged();
                                Snackbar snackbar1 = Snackbar.make(view, R.string.deleted, Snackbar.LENGTH_SHORT);
                                snackbar1.setAction("Annulla", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        note.add(position, tmpNote);
                                        notifyDataSetChanged();
                                    }
                                });
                                snackbar1.setCallback(new Snackbar.Callback() {
                                    @Override
                                    public void onDismissed(Snackbar snackbar, int event) {
                                        if (event != DISMISS_EVENT_ACTION) {
                                            try {
                                                db.eliminaNota(tmpNote);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            } catch (CouchbaseLiteException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        super.onDismissed(snackbar, event);
                                    }
                                });
                                snackbar1.show();


                            }
                        });

                snackbar.show();
                //Toast.makeText(ctx, "Ti piacerebbe tenere premuto la card numero " + position + " eh?", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
      /*ViewAnimator
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
