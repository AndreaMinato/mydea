package com.group.mydea;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.Date;

public class NuovaNota extends AppCompatActivity {

    private CouchDB database;

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Sei sicuro di voler uscire?")
                .setNegativeButton("Continua a scrivere", null)
                .setPositiveButton("Esci", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        NuovaNota.super.onBackPressed();
                    }
                }).create().show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuova_nota);

        database = new CouchDB(getApplicationContext());

        final EditText titolo = (EditText)findViewById(R.id.editTitle);
        final EditText corpo = (EditText)findViewById(R.id.editBody);
        final RadioButton bassa = (RadioButton)findViewById(R.id.radioSlow);
        final RadioButton media = (RadioButton)findViewById(R.id.radioAverage);
        final RadioButton alta = (RadioButton)findViewById(R.id.radioHigh);

        Button salva = (Button)findViewById(R.id.btnSave);
        if (salva != null) {

            salva.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Nota nuova = new Nota();
                    nuova.setId(nuova.getID() + 1);
                    nuova.setTitle(titolo.getText().toString());
                    //nuova.setColor("Blue");
                    nuova.setTag(1);
                    nuova.setText(corpo.getText().toString());
                    nuova.setImage("immagine");
                    nuova.setAudio("audio");
                    nuova.setCreationDate(new Date());

                    if ((bassa).isChecked()) {
                        nuova.setPriority(3);
                        Log.d("RADIO","bassa");
                    }
                    if ((media.isChecked())) {
                        nuova.setPriority(2);
                        Log.d("RADIO", "media");
                    }
                    if (alta.isChecked()) {
                        nuova.setPriority(1);
                        Log.d("RADIO", "alta");
                    }

                    try {
                        database.salvaNota(nuova);
                    } catch (Exception e) {

                    }
                    if(corpo.getText().toString() != null && !(corpo.getText().toString().trim().isEmpty())) {
                        Toast.makeText(NuovaNota.this, "Nota salvata correttamente", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }


    }
}
