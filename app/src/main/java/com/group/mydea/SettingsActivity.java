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

import com.couchbase.lite.CouchbaseLiteException;

import java.io.IOException;
import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    EditText inputPsw;
    Button btnConfirm;

    private CryptData myCypher;
    private CouchDB database;
    private String myEncPsw = "";
    private String newPsw;
    private ArrayList<Nota> note;

    public static String TAG = "debug tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        inputPsw = (EditText) findViewById(R.id.editText);
        btnConfirm = (Button) findViewById(R.id.btnConfirm);


        database = new CouchDB(getApplicationContext());

        try {

            myEncPsw = database.getEncryptionPassword();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        myCypher = new CryptData(myEncPsw, SettingsActivity.this);


        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                newPsw = inputPsw.getText().toString();

                /*TODO: Sistemare alertDialog*/

                new AlertDialog.Builder(SettingsActivity.this)
                        .setTitle("Do you want to save new password?")
                        .setPositiveButton("Yep!", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface arg0, int arg1) {

                                        Log.d(TAG, "Setting new encryptionPsw:" + inputPsw);

                                /*TODO: decriptare le note secondo la vecchia password, salvare la nuova password, decriptare le note secondo la nuova password*/

                                        //notes should be decrypted yet.

                                        try {
                                            //getNotes and decrypt em
                                            ArrayList<Nota> tempNotes=myCypher.decryptAllNotes(database.leggiNote());

                                            //Save new psw
                                            database.overwriteEncryptionPassword(myCypher.generateSha256(newPsw));
                                            Log.d(TAG, "Password has been updated!");

                                            //Crypt all notes with new password
                                            note=myCypher.encryptAllNotes(tempNotes,database);
                                            //save notes
                                            database.salvaNote(note);

                                            /**TODO: DELETE ALL NOTES. kek*/

                                            //Do login again.
                                            Intent vIntent = new Intent(getApplicationContext(), LoginActivity.class);
                                            startActivity(vIntent);

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                            Log.d(TAG, "Error on updating Encryption password: " + e);
                                        } catch (CouchbaseLiteException e) {
                                            e.printStackTrace();
                                            Log.d(TAG, "Error on updating Encryption password: " + e);
                                        }

                                    }
                                })
                        .setNegativeButton("Nope.", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface arg0, int arg1) {
                                        Log.d(TAG, "Psw not setted.");
                                    }
                                }

                        ).create().show();
            }
        });

    }
}
