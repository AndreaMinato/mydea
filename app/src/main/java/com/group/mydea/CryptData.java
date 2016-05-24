package com.group.mydea;

import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.scottyab.aescrypt.AESCrypt;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;

/**
 * Created by Enrico on 24/05/2016.
 */
public class CryptData {

    public static String TAG = "debug tag";

    String psw="password";


    public String encryptData(String text){
        /*String password = "password";
        String message = "hello world";*/
        String encryptedTxt="";
        try {

            encryptedTxt = AESCrypt.encrypt(psw, text);
            Log.d(TAG, "-------------------encryptData-----------\nText:" + text + "\nencryptedMsg:" + encryptedTxt);

        }catch (GeneralSecurityException e){
            //handle error
            Log.d(TAG,"Error on encrypting data: " + e);
        }

        return encryptedTxt;
    }

    public String decryptData(String text){
        /*String password = "password";
        String encryptedMsg = "2B22cS3UC5s35WBihLBo8w==";*/

        String decryptedTxt="";

        try {
            decryptedTxt = AESCrypt.decrypt(psw, text);
            Log.d(TAG,"-------------------decryptData-----------\nText:" + text +"\nencryptedMsg:" + decryptedTxt);
        }catch (GeneralSecurityException e){
            //handle error - could be due to incorrect password or tampered encryptedMsg
            Log.d(TAG,"Error on decrypting data: " + e);
        }
        return decryptedTxt;
    }

    public Nota encryptNota(CouchDB database,String myID,String color,int tag,String title,String text,String img, String audio,Date creationDate,int priority){

        /*
        TODO: dopo i test cambiare a retun type void.
         */

        Nota myNote=new Nota();

        myNote.setId(myID);
        myNote.setColor(encryptData(color));
        myNote.setTag(tag);
        myNote.setTitle(encryptData(title));
        myNote.setText(encryptData(text));
        myNote.setImage(encryptData(img));
        myNote.setAudio(encryptData(audio));
        myNote.setCreationDate(creationDate);/**TODO: Crypt creation date.*/
        myNote.setPriority(priority);


        try {

            database.salvaNota(myNote);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        Log.i(TAG, "Note has been encrypted and saved into DB!");

        return myNote;

    }

    public Nota decryptNota(Nota requestedNote){

            Nota myNote = new Nota();
        try {
            myNote.setId(requestedNote.getID());
            myNote.setColor(decryptData(requestedNote.getColor()));
            myNote.setTag(requestedNote.getTag());
            myNote.setTitle(decryptData(requestedNote.getTitle()));
            myNote.setText(decryptData(requestedNote.getText()));
            myNote.setImage(decryptData(requestedNote.getImage()));
            myNote.setAudio(decryptData(requestedNote.getAudio()));
            myNote.setCreationDate(requestedNote.getCreationDate());
            myNote.setPriority(requestedNote.getPriority());

            Log.d(TAG, "Note has been decrypted!");
        }

        catch (Exception e){
            Log.d(TAG, "Error on decrypting note: "+e);
        }

        return myNote;
    }


}
