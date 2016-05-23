package com.group.mydea;


import android.Manifest;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

import com.couchbase.lite.CouchbaseLiteException;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentModificaNota extends DialogFragment {

    private MediaRecorder mediaRecorder;
    private Timer recTimer;
    private boolean isRecording;
    private String audioOutputPath = " ";
    private FrameLayout linearLayout;
    private ImageView imgNota;
    private Snackbar timeProgressSnackbar;
    private View fabImg;
    private Nota oldNota;
    private Nota newNota;
    private int pos;

    public static String TAG = "debug tag";
    public static String NOTA = "gfcg";
    private static final int SELECT_PICTURE = 1;
    int TAKE_PHOTO_CODE = 0;

    private CouchDB database;
    TextView mTvTitolo, mTvTestoNota;
    Button save;
    String myID;
    String path;
    String myImgNota;


    public interface addedItem {
        public void itemUpdated(Nota nota, int pos);
    }


    private addedItem listener = new addedItem() {
        @Override
        public void itemUpdated(Nota nota, int pos) {

        }
    };

    public static FragmentModificaNota getInstance(Nota nota, int position) {

        FragmentModificaNota fragmentModificaNota = new FragmentModificaNota();
        Bundle bundle = new Bundle();
        bundle.putParcelable(NOTA, nota);
        bundle.putInt("POS", position);
        fragmentModificaNota.setArguments(bundle);
        return fragmentModificaNota;
    }

    public FragmentModificaNota() {
        // Required empty public constructor
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        newNota = new Nota();
        newNota.setId(myID);
        newNota.setText(mTvTestoNota.getText().toString());
        newNota.setTitle(mTvTitolo.getText().toString());
        if(audioOutputPath!=" ")
            newNota.setAudio(audioOutputPath);
        else
            newNota.setAudio(path);
        Log.i(TAG, "onClick: " + newNota.getID());


        try {
            database.salvaNota(newNota);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        listener.itemUpdated(newNota, pos);
        //getActivity().onBackPressed();
        super.onDismiss(dialog);
    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        Activity hostActivity = getActivity();
        if (hostActivity instanceof addedItem) {
            listener = (addedItem) hostActivity;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vView = inflater.inflate(R.layout.fragment_fragment_modifica_nota, container, false);



        mTvTitolo = (TextView) vView.findViewById(R.id.tvtTitoloNota);
        mTvTestoNota = (TextView) vView.findViewById(R.id.tvtTestoNota);

        if (getArguments() != null) {
            pos = getArguments().getInt("POS");
            oldNota = getArguments().getParcelable(NOTA);
            //TODO settare testi della nota...
            mTvTitolo.setText(oldNota.getTitle());
            mTvTestoNota.setText(oldNota.getText());
            myID = oldNota.getID();
            path = oldNota.getAudio();
            myImgNota=oldNota.getImage();

            Log.d(TAG, "onCreateView: " + myID);
        }


        imgNota=(ImageView) vView.findViewById(R.id.imageViewNota);

        fabImg = vView.findViewById(R.id.fabImg);

        fabImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "Fab img hasbeen pressed.");

                new AlertDialog.Builder(getActivity())
                        .setTitle("Do you want to take a pic?")
                        .setPositiveButton("Yep!", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0, int arg1) {
                                Dexter.checkPermission(new PermissionListener() {
                                    @Override
                                    public void onPermissionGranted(PermissionGrantedResponse response) {
                                        Toast.makeText(getActivity().getApplicationContext(), "Permessoooooo", Toast.LENGTH_LONG).show();

                                        takePicture();
                                    }

                                    @Override
                                    public void onPermissionDenied(PermissionDeniedResponse response) {
                                        Toast.makeText(getActivity().getApplicationContext(), "Se non mi dai i permessi cazzo vuoi registrare?", Toast.LENGTH_LONG).show();

                                    }

                                    @Override
                                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                        Toast.makeText(getActivity().getApplicationContext(), "Cosa stranaaaaa", Toast.LENGTH_LONG).show();
                                        token.continuePermissionRequest();

                                    }
                                }, Manifest.permission.CAMERA);

                            }
                        })
                        .setNegativeButton("Nope.", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0, int arg1) {
                                /**Select file from gallery*/
                                getImgFromGallery();
                            }
                        }).create().show();


            }
        });


        linearLayout = (FrameLayout) vView.findViewById(R.id.layoutfrag);


        database = new CouchDB(getActivity());


        View btnRec = vView.findViewById(R.id.btnRec);
        btnRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.checkPermission(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Toast.makeText(getActivity().getApplicationContext(), "Permessoooooo", Toast.LENGTH_LONG).show();

                        mediaRecorder = new MediaRecorder();
                        mediaRecorder.reset();
                        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

                        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                        //mediaRecorder.setOutputFile();
                        recTimer = new Timer();
                        if (!isRecording) {
                            startRecording();
                        } else {
                            stopRecording();
                        }
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(getActivity().getApplicationContext(), "Se non mi dai i permessi cazzo vuoi registrare?", Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        Toast.makeText(getActivity().getApplicationContext(), "Cosa stranaaaaa", Toast.LENGTH_LONG).show();
                        token.continuePermissionRequest();

                    }
                }, Manifest.permission.RECORD_AUDIO);

            }
        });


        return vView;
    }


    private void getImgFromGallery() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), SELECT_PICTURE);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                Log.d(TAG, "Selected img path: " + selectedImageUri.getPath());
                setImg(selectedImageUri);
                //setImgNotPicasso(selectedImageUri.getPath());
                oldNota.setImage(selectedImageUri.getPath());
            }
        }
    }


    private void takePicture(){

        final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/";
        File newdir = new File(dir);

        if(!newdir.exists())
            newdir.mkdirs();


        Log.d(TAG, "Take a pic, bro!");

        int time=(int)(System.currentTimeMillis());
        String timeStp="imgPost"+"_"+new Timestamp(time).toString().replace("-","").replace(" ","").replace(".","").replace(":","");
        String myPicFile = dir + timeStp + ".jpg";
        File newImgfile = new File(myPicFile) ;
        try {

            newImgfile.createNewFile();

            Uri outputFileUri = Uri.fromFile(newImgfile);
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);
            /*
            TODO: callback (wait for pic)
            TODO: handle permission.
             */

            setImg(outputFileUri);
            oldNota.setImage(outputFileUri.toString());

            } catch (IOException e) {
                Log.d(TAG, "Error on taking a pic: "+ e.toString());
            }

    }


    private void setImg(Uri fileImg) {

        //String myExtFilePath=Environment.getExternalStorageDirectory()+fileImg.getAbsolutePath()+".jpg";

        Log.d(TAG, "myExtFilePath URI: " + fileImg);


        try {
                Picasso.with(getActivity())
                        .load(fileImg)
                        .resize(800,600).onlyScaleDown().centerInside()
                        .error(R.drawable.couldnotloadimg)
                        .into(imgNota, new Callback() {

                            @Override
                            public void onSuccess() {

                                Log.d(TAG, "Image setted correctly.");
                            }

                            @Override
                            public void onError() {

                                Log.d(TAG, "Image not setted correctly.");
                                    }
                                });
            } catch (Exception e) {
                Log.d(TAG, "Image not setted correctly: " + e);
            }
    }

    private Boolean isValidPath(String imgPath){
        /**TODO: TEST*/
        File f = new File(imgPath);
        if(f.exists() && !f.isDirectory())
            return true;
        else {
            Log.d(TAG,"File Path not valid!!\n Path: " + imgPath + "\n Resetting file to null...");
            oldNota.setImage(null);
            return false;
        }
    }


    private void startRecording() {


        mediaRecorder = setupRecorderWithPermission();
        if (mediaRecorder != null) {
            try {
                mediaRecorder.prepare();
            } catch (IOException e) {
                Log.d("AUDIO", "prepare() failed");
                e.printStackTrace();
            }
            mediaRecorder.start();
            isRecording = true;

            timeProgressSnackbar = Snackbar.make(linearLayout, "Regsitriamo" + " - 00:00", Snackbar.LENGTH_INDEFINITE);
            timeProgressSnackbar.show();
            recTimer = new Timer();
            recTimer.schedule(createTimerTask(), 1000, 1000);
        }
    }

    private void stopRecording() {
        isRecording = false;
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
        recTimer.cancel();

        if (timeProgressSnackbar != null) {
            timeProgressSnackbar.dismiss();
        }
        Toast.makeText(getActivity(), "Registrazione salvata", Toast.LENGTH_SHORT).show();
    }

    private MediaRecorder setupRecorder() {
        MediaRecorder recorder = new MediaRecorder();
        isRecording = false;
        audioOutputPath = getActivity().getExternalFilesDir("MydeaAudios") + "/" + (new Date()).getTime() + ".3gp";
        Log.d("Setup recorder", "Path: " + audioOutputPath);

        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setOutputFile(audioOutputPath);
        return recorder;
    }

    private MediaRecorder setupRecorderWithPermission() {
        return setupRecorder();
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
                                     timeProgressSnackbar.setText("Registriamo" + " - " + sdf.format(data));
                                 }
                             }

                );
            }
        };
    }
}

        