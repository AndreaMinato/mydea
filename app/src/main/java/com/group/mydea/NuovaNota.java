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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
    Bitmap temp;
    RadioButton bassa;
    RadioButton media;
    RadioButton alta;
    RadioButton lavoro;
    RadioButton personale;
    RadioButton hobby;
    RadioButton tempolibero;
    MainActivity myHome;


    public static String TAG="debug tag";
    private static String BITMAP = "bitmap";


    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int PICK_IMAGE = 2; //il numero deve essere diverso da REQUEST_IMAGE_CAPTURE

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        Log.d(TAG, "qui");


        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            temp = imageBitmap;
            immagine.setImageBitmap(imageBitmap);
        }

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));
                temp = bitmap;
                immagine.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
        bassa = (RadioButton)findViewById(R.id.radioSlow);
        media = (RadioButton)findViewById(R.id.radioAverage);
        alta = (RadioButton)findViewById(R.id.radioHigh);
        lavoro = (RadioButton)findViewById(R.id.radioWork);
        personale = (RadioButton)findViewById(R.id.radioPersonal);
        hobby = (RadioButton)findViewById(R.id.radioHobby);
        tempolibero = (RadioButton)findViewById(R.id.radioFreetime);
        View fabImg = findViewById(R.id.fabImg);
        View fabGal = findViewById(R.id.fabGal);
        /*final FloatingActionButton fabImg = (FloatingActionButton)findViewById(R.id.fabImg);
        final FloatingActionButton fabGal = (FloatingActionButton)findViewById(R.id.fabGal);*/
        immagine = (ImageView)findViewById(R.id.imgviewFoto);

        if (savedInstanceState != null) {
            temp = savedInstanceState.getParcelable(BITMAP);
            immagine.setImageBitmap(temp);
        }


        assert fabGal != null;
        fabGal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

        assert fabImg != null;
        fabImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.salva_nuova_nota, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_button:
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
               /* nuova.setImage("immagine");
                nuova.setAudio("audio");*/
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

                    nuova.setText(corpo.getText().toString());
                  /*  nuova.setImage("immagine");
                    nuova.setAudio("audio");*/
                    nuova.setCreationDate(new Date());

                if(!(((corpo.getText().toString().trim().isEmpty())) && ((titolo.getText().toString().trim().isEmpty())))) {
                    if (titolo.getText().toString().trim().isEmpty()) {
                        nuova.setTitle("Senza titolo");
                    }
                    try {
                        database.salvaNota(nuova);
                        myHome.refreshNotes(0, nuova);
                    } catch (Exception e) {

                    }
                    Toast.makeText(NuovaNota.this, R.string.noteSaved, Toast.LENGTH_LONG).show();

                    Intent myIntent = new Intent(NuovaNota.this, MainActivity.class);
                    //myIntent.putExtra("key", value); Optional parameters
                    NuovaNota.this.startActivity(myIntent);
                } else {
                    Toast.makeText(NuovaNota.this, R.string.noteEmpty, Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BITMAP, temp);
    }
}