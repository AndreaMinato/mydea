package com.group.mydea;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Timer;

public class NuovaNota extends AppCompatActivity {




    private CouchDB database;
    EditText titolo;
    EditText corpo;
    ImageView immagine;




    public static String TAG="debug tag";



    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            immagine.setImageBitmap(imageBitmap);
        }
    }

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
        final RadioButton lavoro = (RadioButton)findViewById(R.id.radioWork);
        final RadioButton personale = (RadioButton)findViewById(R.id.radioPersonal);
        final RadioButton hobby = (RadioButton)findViewById(R.id.radioHobby);
        final RadioButton tempolibero = (RadioButton)findViewById(R.id.radioFreetime);
        final FloatingActionButton fabImg = (FloatingActionButton)findViewById(R.id.fabImg);
        immagine = (ImageView)findViewById(R.id.imgviewFoto);


        assert fabImg != null;
        fabImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        Button salva = (Button)findViewById(R.id.btnSave);
        if (salva != null) {

            salva.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Nota nuova = new Nota();
                    nuova.setId(nuova.getID() + 1);
                    nuova.setTitle(titolo.getText().toString());
                    //nuova.setColor("Blue");

                    if (lavoro.isChecked()) {
                        nuova.setTag(1);
                    }
                    if (personale.isChecked()) {
                        nuova.setTag(2);
                    }
                    if (hobby.isChecked()) {
                        nuova.setTag(3);
                    }
                    if (tempolibero.isChecked()) {
                        nuova.setTag(4);
                    }

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
                            nuova.setTitle("Senza titolo");
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



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}