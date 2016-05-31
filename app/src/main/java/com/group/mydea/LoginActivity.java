package com.group.mydea;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.couchbase.lite.CouchbaseLiteException;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {

    EditText etInputPsw;
    TextView tvStatus;
    Button btnCommitActions;

    private CryptData myCypher;//=new CryptData();
    private String myEncPsw="";
    private CouchDB database;
    private String inputPsw;
    private Boolean pswIsSet=false;

    public static String TAG = "debug tag";

    /**
     *TODO: non salva la password o non prende la password.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etInputPsw = (EditText) findViewById(R.id.editTextPassword);
        tvStatus=(TextView) findViewById(R.id.tvEnterPsw);
        btnCommitActions = (Button) findViewById(R.id.button_decrypt);

        database = new CouchDB(getApplicationContext());

        try {

            myEncPsw=database.getEncryptionPassword();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        /**
         * TODO: gestire se La password per encriptare le note non è settata
         */
        myCypher=new CryptData(myEncPsw,LoginActivity.this);


        try {

            if(checkIfPswIsSet()){
                pswIsSet=true;
            }

        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        setUpGUI();


        btnCommitActions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                inputPsw = etInputPsw.getText().toString();

                try {

                    if (pswIsSet) {

                        if (myCypher.generateSha256(inputPsw).equals(database.getEncryptionPassword())) {
                            Log.d(TAG, "Password is Correct.");

                            Intent vIntent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(vIntent);

                        } else {
                            Log.d(TAG, "Password is Incorrect.\nInput psw:" + myCypher.generateSha256(inputPsw) + "\nSettedPsw:" + database.getEncryptionPassword());
                        }

                    } else {

                        /*TODO: fare meglio l'alert dialog.*/

                        new AlertDialog.Builder(LoginActivity.this)
                                .setTitle(R.string.savePassword)
                                .setPositiveButton(R.string.dialogYes, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface arg0, int arg1) {

                                        Log.d(TAG, "Setting new encryptionPsw:" + inputPsw);

                                            setEncryptionPassword(inputPsw);
                                    }
                                })
                                .setNegativeButton(R.string.dialogNo, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface arg0, int arg1) {

                                        Log.d(TAG,"Psw not setted.");

                                    }
                                }).create().show();

                    }


                } catch (CouchbaseLiteException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        });
    }

    private void setUpGUI(){

        if(pswIsSet){
            tvStatus.setText(R.string.pswText);
            btnCommitActions.setText(R.string.decryptButton);
        }
        else {
            tvStatus.setText(R.string.setPswText);
            btnCommitActions.setText(R.string.setPswButton);
        }

    }

    private boolean checkIfPswIsSet() throws CouchbaseLiteException, IOException {

        boolean isSet=false;
        String settedPsw=database.getEncryptionPassword();

        if(settedPsw.length()>0) {//SHA256 d7cb62855cc3a04933d835db565be339b4727bab711fb4d7bc277538709b1d32 for "" lol
            Log.d(TAG, "Password is setted yet.");
            isSet = true;
        }

        return isSet;
    }

    private void setEncryptionPassword(String inputPsw){

        Log.d(TAG, "Setting new login password.");


        try {

            database.setEncryptionPassword(myCypher.generateSha256(inputPsw));

        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "Error on setting new Encryption password: " + e);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
            Log.d(TAG, "Error on setting new Encryption password: " + e);
        }

        pswIsSet=true;
        setUpGUI();

    }

}
