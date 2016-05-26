package com.group.mydea;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.couchbase.lite.CouchbaseLiteException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {

    EditText decryptPsw;
    Button decryptBtn;

    CryptData myCypher=new CryptData();
    private CouchDB database;
    String inputPsw;
    String encryptionPsw;

    public static String TAG = "debug tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        decryptPsw = (EditText) findViewById(R.id.editTextPassword);
        decryptBtn = (Button) findViewById(R.id.button_decrypt);

        decryptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*
                TODO: -salvare la password definita dall'utente la prima volta (gnerando lo sha256) nel db.
                      -confrontare che lo sha256 della psw inserita sia uguale a quella presente nel db.
                 */
                inputPsw = decryptPsw.getText().toString();

                if (checkIfPswIsSet()) {

                    if (myCypher.generateSha256(inputPsw).equals(myCypher.generateSha256("psw"))) {
                        Log.d(TAG, "Password is Correct.");

                        Intent vIntent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(vIntent);

                    } else {
                        Log.d(TAG, "Password is Incorrect. Input:" + inputPsw);
                    }

                } else {
                    setEncryptPassword(inputPsw);
                }

            }

        });
    }

    private boolean checkIfPswIsSet(){

        return true;
    }

    private void setEncryptPassword(String inputStr) {
        //database.setEncryptionPassword("psw");
        encryptionPsw=inputStr;
    }
}
