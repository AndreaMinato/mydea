package com.group.mydea;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;
import com.couchbase.lite.CouchbaseLiteException;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.Timer;
import java.util.TimerTask;

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
    String pathImg, pathGal;



    private MediaRecorder mediaRecorder;
    private Timer recTimer;
    private boolean isRecording;
    private Snackbar timeProgressSnackbar;
    private String audioOutputPath = " ";

    public static String TAG="debug tag";
    private static String BITMAP = "bitmap";


    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int PICK_IMAGE = 2; //il numero deve essere diverso da REQUEST_IMAGE_CAPTURE


    private void startRecording() {


        mediaRecorder = setupRecorder();
        if (mediaRecorder != null) {
            try {
                mediaRecorder.prepare();
            } catch (IOException e) {
                Log.d("AUDIO", "prepare() failed");
                e.printStackTrace();
            }
            mediaRecorder.start();
            isRecording = true;

            //timeProgressSnackbar = Snackbar.make(linearLayout, "Regsitriamo" + " - 00:00", Snackbar.LENGTH_INDEFINITE);
            //timeProgressSnackbar.show();
            recTimer = new Timer();
            recTimer.schedule(createTimerTask(), 1000, 1000);
        }
    }

    private void stopRecording() {
        isRecording = false;
        mediaRecorder.stop();
        mediaRecorder.reset();
        mediaRecorder.release();
        mediaRecorder = null;
        recTimer.cancel();

        if (timeProgressSnackbar != null) {
            timeProgressSnackbar.dismiss();
        }
        Toast.makeText(this, R.string.recSaved, Toast.LENGTH_SHORT).show();
    }

    private MediaRecorder setupRecorder() {
        MediaRecorder recorder = new MediaRecorder();
        isRecording = false;
        audioOutputPath = this.getExternalFilesDir("MydeaAudios") + "/" + (new Date()).getTime() + ".3gp";
        Log.d("Setup recorder", "Path: " + audioOutputPath);

        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setOutputFile(audioOutputPath);
        return recorder;
    }


    private TimerTask createTimerTask() {
        final Handler handler = new Handler();
        return new TimerTask() {
            private Date data = new Date(0);
            private SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");

            @Override
            public void run() {
                data.setTime(data.getTime() + 1000);
                handler.post(new Runnable() {
                                 @Override
                                 public void run() {
                                     //timeProgressSnackbar.setText("Registriamo" + " - " + sdf.format(data));
                                 }
                             }

                );
            }
        };
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            //startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            startActivityForResult(Intent.createChooser(takePictureIntent, "Select Picture"), REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        Log.d(TAG, "qui");


        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Log.d(TAG, "sono dentro");
            Bundle extras = data.getExtras();
            Uri uriImg = data.getData();
            pathImg = uriImg.toString();

            Log.d(TAG, "URL " + pathImg);

            Bitmap imageBitmap = (Bitmap) extras.get("data");
            temp = imageBitmap;
            immagine.setImageBitmap(imageBitmap);
        }

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uriGal = data.getData();
            pathGal = uriGal.toString();
            Log.d(TAG, "URL " + pathGal);

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriGal);
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
        if (!(((corpo.getText().toString().trim().isEmpty())) && ((titolo.getText().toString().trim().isEmpty())))) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.exitDialog)
                    .setNegativeButton(R.string.dialogNo, null)
                    .setPositiveButton(R.string.dialogYes, new DialogInterface.OnClickListener() {

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

        titolo = (EditText) findViewById(R.id.editTitle);
        corpo = (EditText) findViewById(R.id.editBody);
        bassa = (RadioButton) findViewById(R.id.radioSlow);
        media = (RadioButton) findViewById(R.id.radioAverage);
        alta = (RadioButton) findViewById(R.id.radioHigh);
        lavoro = (RadioButton) findViewById(R.id.radioWork);
        personale = (RadioButton) findViewById(R.id.radioPersonal);
        hobby = (RadioButton) findViewById(R.id.radioHobby);
        tempolibero = (RadioButton) findViewById(R.id.radioFreetime);
        View fabImg = findViewById(R.id.fabImg);
        View fabGal = findViewById(R.id.fabGal);
        final View btnRec = findViewById(R.id.btnRec);
        /*final FloatingActionButton fabImg = (FloatingActionButton)findViewById(R.id.fabImg);
        final FloatingActionButton fabGal = (FloatingActionButton)findViewById(R.id.fabGal);*/
        immagine = (ImageView) findViewById(R.id.imgviewFoto);

        if (savedInstanceState != null) {
            temp = savedInstanceState.getParcelable(BITMAP);
            immagine.setImageBitmap(temp);
        }


        if (fabGal != null) {
            fabGal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                }
            });
        }
        if (fabImg != null) {
            fabImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dispatchTakePictureIntent();
                }
            });
        }


        if (btnRec != null) {
            btnRec.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dexter.checkPermission(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {

                            recTimer = new Timer();
                            if (!isRecording) {
                                startRecording();
                                Toast.makeText(NuovaNota.this, R.string.recStarted, Toast.LENGTH_LONG).show();
                                btnRec.setBackgroundColor(Color.RED);

                            } else {
                                stopRecording();
                                btnRec.setBackgroundColor(Color.WHITE);
                            }
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            //Toast.makeText(getActivity().getApplicationContext(), "Se non mi dai i permessi cazzo vuoi registrare?", Toast.LENGTH_LONG).show();

                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                            token.continuePermissionRequest();

                        }
                    }, Manifest.permission.RECORD_AUDIO);

                }
            });
        }

        LinearLayout player = (LinearLayout) findViewById(R.id.player);




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
                if (pathGal != null) {
                    Log.d(TAG, "salvo immagine galleria");
                    nuova.setImage(pathGal);
                }
                if (pathImg != null) {
                    Log.d(TAG, "salvo immagine fotocamera");
                    nuova.setImage(pathImg);
                }
                nuova.setAudio(audioOutputPath);
                nuova.setCreationDate(new Date());

                if ((bassa).isChecked()) {
                    nuova.setPriority(3);
                    Log.d(TAG, "bassa");
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

                if (!(((corpo.getText().toString().trim().isEmpty())) && ((titolo.getText().toString().trim().isEmpty())))) {
                    if (titolo.getText().toString().trim().isEmpty()) {
                        String empty = getResources().getString(R.string.noteUntitled);
                        nuova.setTitle(empty);
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