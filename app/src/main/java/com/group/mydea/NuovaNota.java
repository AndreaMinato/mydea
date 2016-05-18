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
    EditText titolo;
    EditText corpo;

    public static String TAG="debug tag";

    @Override
    public void onBackPressed() {
        if(!(((corpo.getText().toString().trim().isEmpty())) && ((titolo.getText().toString().trim().isEmpty())))) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.exitDialog)
                    .setNegativeButton(R.string.exitDialogContinue, null)
                    .setPositiveButton(R.string.exitDialogExit, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            NuovaNota.super.onBackPressed();
                        }
                    }).create().show();
        } else {
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuova_nota);

        database = new CouchDB(getApplicationContext());

        titolo = (EditText)findViewById(R.id.editTitle);
        corpo = (EditText)findViewById(R.id.editBody);
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
                        Log.d(TAG,"bassa");
                    }
                    if ((media.isChecked())) {
                        nuova.setPriority(2);
                        Log.d(TAG, "media");
                    }
                    if (alta.isChecked()) {
                        nuova.setPriority(1);
                        Log.d(TAG, "alta");
                    }


                    if(!(((corpo.getText().toString().trim().isEmpty())) && ((titolo.getText().toString().trim().isEmpty())))) {
                        if (titolo.getText().toString().trim().isEmpty()) {
                            nuova.setTitle(""+R.string.noteUntitled);
                        }
                        try {
                            database.salvaNota(nuova);
                        } catch (Exception e) {

                        }
                        Toast.makeText(NuovaNota.this, R.string.noteSaved, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(NuovaNota.this, R.string.noteEmpty, Toast.LENGTH_LONG).show();
                    }
                    finish();
                }
            });
        }


    }
}