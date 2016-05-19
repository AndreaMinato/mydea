package com.group.mydea;


import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
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
    ImageView imgNota;
    private Snackbar timeProgressSnackbar;
    FloatingActionButton fabImg;
    private Nota oldNota;
    private Nota newNota;
    private int pos;

    public static String TAG = "debug tag";
    public static String NOTA = "gfcg";
    int TAKE_PHOTO_CODE = 0;
    public static int count = 0;


    public interface addedItem {
        public void itemUpdated(Nota nota, int pos);
    }


    private addedItem listener = new addedItem() {
        @Override
        public void itemUpdated(Nota nota, int pos) {

        }
    };
    private CouchDB database;


    TextView mTvTitolo, mTvTestoNota;
    Button save;
    String myID;
    String path;

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

            imgNota=(ImageView) vView.findViewById(R.id.imageViewNota);
            fabImg = (FloatingActionButton) vView.findViewById(R.id.fabImg);

            // Here, we are making a folder named picFolder to store
            // pics taken by the camera using this application.
            final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/";
            File newdir = new File(dir);
            newdir.mkdirs();

            fabImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                /*
                *TODO: SISTEMARE TUTTO LOL
                */
                    Log.d(TAG, "Fab img hasbeen pressed.");

                    if (oldNota.getImage() == null) {
                        Log.d(TAG, "This Note has an image yet.");
                        /*
                        TODO: show dialog "Vuoi modificare l'img?"
                         */
                       setImg((File) new File(oldNota.getImage()));
                    } else {
                        Log.d(TAG, "Take a pic...");

                        // Here, the counter will be incremented each time, and the
                        // picture taken by camera will be stored as 1.jpg,2.jpg
                        // and likewise.
                        count++;
                        String file = dir + count + ".jpg";
                        File newfile = new File(file);
                        try {
                            newfile.createNewFile();
                            oldNota.setImage(newfile.getPath());
                        } catch (IOException e) {
                            Log.d(TAG, "Error on taking a pic: "+ e.toString());
                        }
                        /*
                        TODO: handle permission.
                         */
                        Uri outputFileUri = Uri.fromFile(newfile);
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                        startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);

                        setImg(newfile);

                    }
                }
            });

        linearLayout = (FrameLayout) vView.findViewById(R.id.layoutfrag);
        mediaRecorder = new MediaRecorder();
        mediaRecorder.reset();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        //mediaRecorder.setOutputFile();


        recTimer = new Timer();


        database = new CouchDB(getActivity());

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
            Log.d(TAG, "onCreateView: " + myID);
        }

        mTvTitolo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "mTvTitolo pressed!");

               //mTvTitolo.setCursorVisible(true);
               /* Animation animation=new ScaleAnimation(
                        1f,1.2f,
                        0.6f,1.2f);
                animation.setDuration(600);
                animation.setFillAfter(true);
                mTvTitolo.startAnimation(animation);*/
            }
        });

        Button btnRec = (Button) vView.findViewById(R.id.btnRec);
        btnRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecording) {
                    startRecording();
                } else {
                    stopRecording();
                }
            }
        });


        return vView;
    }

    private void setImg(File myImg){
        /*
        *TODO: getContext()
        */
        Picasso.with(getActivity())
                .load(myImg)
                .resize(1200, 800)
                .centerCrop()
                .into(imgNota);
    }

    private void takePic(){

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
        Permission.askForPermissions(getActivity());

        if (!Permission.needsToAskForPermissions(getActivity())) {
            return setupRecorder();
        }
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Permission.MY_PERMISSIONS_REQUEST_RECORD_AUDIO:
                break;
            case Permission.MY_PERMISSIONS_REQUEST_STORAGE:
                break;
            default:
                break;
        }
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

        