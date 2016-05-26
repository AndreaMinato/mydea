package com.group.mydea;

import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.scottyab.aescrypt.AESCrypt;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Enrico on 24/05/2016.
 */
public class CryptData {

    public static String TAG = "debug tag";
    public static String CRYPT_PSW="password";
    String psw=CRYPT_PSW;


    public String generateSha256(String psw) {

        String mySha256Str="";

        try {

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(psw.getBytes("UTF-8")); // Change this to "UTF-16" if needed
        byte[] digest = md.digest();

        //If you want to format to hex with left zero padding:
        mySha256Str=String.format("%064x", new java.math.BigInteger(1, digest));

        //Log.d(TAG,"Sha-256 of "+psw+" is:"+mySha256Str);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            Log.d(TAG, "Failed to generate Sha-256 of the password. Error:" + e);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.d(TAG, "Failed to generate Sha-256 of the password. Error:" + e);
        }

        return mySha256Str;
    }


    public String encryptData(String text){
        /*String password = "password";
        String message = "hello world";*/
        String encryptedTxt="";
        try {

            encryptedTxt = AESCrypt.encrypt(psw, text);
            //Log.d(TAG, "-------------------encryptData-----------\nText:" + text + "\nencryptedMsg:" + encryptedTxt);

        }catch (GeneralSecurityException e){
            //handle error
            Log.d(TAG,"Error on encrypting data: " + e);
        }

        return encryptedTxt;
    }

    public String decryptData(String text){

        String decryptedTxt="decryptData";

        try {
            decryptedTxt = AESCrypt.decrypt(psw, text);
            //Log.d(TAG,"-------------------decryptData-----------\nText:" + text +"\nencryptedMsg:" + decryptedTxt);
        }catch (GeneralSecurityException e){
            //handle error - could be due to incorrect password or tampered encryptedMsg
            Log.d(TAG,"Error on decrypting data: " + e);
        }
        return decryptedTxt;
    }

    public Nota encryptNota(CouchDB database,String myID,Boolean isEncrypted,String color,int tag,String title,String text,String img, String audio,Date creationDate,int priority){

        /*
        TODO: dopo i test cambiare a retun type void. aggiungere if is crypted
         */

        Nota myNote=new Nota();

        myNote.setId(myID);
        myNote.setIsEncrypted(true);
        myNote.setColor(encryptData(color));
        myNote.setTag(tag);
        myNote.setTitle(encryptData(title));
        myNote.setText(encryptData(text));
        myNote.setImage(encryptData(img));
        myNote.setAudio(encryptData(audio));
        myNote.setCreationDate(creationDate);/**TODO: Crypt creation date.*/
        myNote.setPriority(priority);

       /* Log.d(TAG,"+++++++++++++++++++++++++Note Has Been EnCrypted:+++++++++++++++++++++++" +
                "\nID:"+myID+"--->"+ myNote.getID()+
                "\nTITLE:"+title+"--->"+ myNote.getTitle()+
                "\nTEXT:"+text+"--->"+myNote.getText());*/


        try {
            Log.i(TAG, "Saving note....");
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

            if(requestedNote.getIsEncrypted()) {//Decrypt Note only if is actually crypted.

                myNote.setId(requestedNote.getID());
                myNote.setIsEncrypted(requestedNote.getIsEncrypted());
                myNote.setColor(decryptData(requestedNote.getColor()));
                myNote.setTag(requestedNote.getTag());
                myNote.setTitle(decryptData(requestedNote.getTitle()));
                myNote.setText(decryptData(requestedNote.getText()));
                myNote.setImage(decryptData(requestedNote.getImage()));
                myNote.setAudio(decryptData(requestedNote.getAudio()));
                myNote.setCreationDate(requestedNote.getCreationDate());
                myNote.setPriority(requestedNote.getPriority());

              /*  Log.d(TAG, "--------------------------Note Has Been DeCrypted:---------------------" +
                        "\nID:" + myNote.getID() + "<----" + requestedNote.getID() +
                        "\nCOLOR:" + myNote.getColor() + "<----" + requestedNote.getColor() +
                        "\nTITLE:" + myNote.getTitle() + "<----" + requestedNote.getTitle() +
                        "\nTEXT:" + myNote.getText() + "<----" + requestedNote.getText());*/

                Log.d(TAG, "Note has been decrypted!");
            }
            else {
                Log.d(TAG, "This note is not supposed to be encrypted.");

                myNote.setId(requestedNote.getID());
                myNote.setIsEncrypted(requestedNote.getIsEncrypted());
                myNote.setColor(requestedNote.getColor());
                myNote.setTag(requestedNote.getTag());
                myNote.setTitle(requestedNote.getTitle());
                myNote.setText(requestedNote.getText());
                myNote.setImage(requestedNote.getImage());
                myNote.setAudio(requestedNote.getAudio());
                myNote.setCreationDate(requestedNote.getCreationDate());
                myNote.setPriority(requestedNote.getPriority());
            }
        }

        catch (Exception e){
            Log.d(TAG, "Error on decrypting note: "+e);
        }

        return myNote;
    }

    public ArrayList<Nota> encryptAllNotes(ArrayList<Nota> note,CouchDB database){

        ArrayList<Nota> tempNote=new ArrayList<Nota>();

        for(int i=0; i<note.size();i++) {
            /*TODO: crypt note only if they have to.*/
            tempNote.add(encryptNota(database, note.get(i).getID(), note.get(i).getIsEncrypted(), note.get(i).getColor(), note.get(i).getTag(), note.get(i).getTitle(), note.get(i).getText(), note.get(i).getImage(), note.get(i).getAudio(), note.get(i).getCreationDate(), note.get(i).getPriority()));
        }


        Log.d(TAG,"Encrypt All Notes:");

        for(int i=0;i<tempNote.size();i++){
            Log.d(TAG,"Nota [" + i + "] TITOLO:"+tempNote.get(i).getTitle());
        }

        return tempNote;
    }

    public ArrayList<Nota> decryptAllNotes(ArrayList<Nota> note){

        ArrayList<Nota> tempNote=new ArrayList<Nota>();

        for(int i=0; i<note.size();i++) {
            tempNote.add(decryptNota(note.get(i)));
        }

        Log.d(TAG,"Decrypting All Notes:");

        for(int i=0;i<tempNote.size();i++){
            Log.d(TAG, "Nota [" + i + "] TITOLO:" + tempNote.get(i).getTitle());
        }

        return tempNote;


    }
}
