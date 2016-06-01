package com.group.mydea;

/**
 * Created by andrea on 13/05/16.
 */

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class Nota implements Comparable<Nota>, Parcelable {

    private static final String TAG = "Nota";

    private String id;
    private Boolean isEncrypted;
    private String title;
    private String color;
    private int tag;
    /**
     * This is the Category of the posts:
     * 1=Lavoro
     * 2=Personale
     * 3=Hobby
     * 4=Tempo Libero
     **/
    private String text;
    private String image;           //path immagine
    private String audio;           //path audio
    private Date creationDate;
    private int priority;           //Priorità della nota definita dall'utente, convertita in un numero intero?

    //1 Priorità alta, 2 Priorità media, 3 Priorità bassa
    public Nota() {

        id = UUID.randomUUID().toString();
        isEncrypted = false;
        title = "Nota senza Titolo";
        text = "...";
        color = "#FFFFFF";
        tag = 1;
        image = " ";
        audio = " ";
        creationDate = new Date();
        priority = 3;
    }

    public Nota(String id) {
        this.id = id;
    }

    public String getID() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getColor() {
        return color;
    }

    public Boolean getIsEncrypted() {
        return isEncrypted;
    }

    public void setIsEncrypted(Boolean isEncrypted) {
        this.isEncrypted = isEncrypted;
    }

    public void setColor(String color) {
/*        if (!color.matches("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$")) {
            throw new MalformedHexColorException();
        } else {*/
        this.color = color;
        //}
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public int compareTo(@NonNull Nota nota) {
        return this.title.compareTo(nota.getTitle());
    }


    // Parte per la parcellizzazione

    @Override
    public int describeContents() {
        return getID().hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(isEncrypted.toString());
        dest.writeString(title);
        dest.writeString(text);
        dest.writeString(color);
        dest.writeString(image);
        dest.writeString(audio);
        dest.writeInt(priority);
        dest.writeInt(tag);
        if (creationDate != null)
            dest.writeString(creationDate.toString());
        else
            dest.writeString("01/01/0101");
    }

    public final static Parcelable.Creator<Nota> CREATOR = new ClassLoaderCreator<Nota>() {
        @Override
        public Nota createFromParcel(Parcel source, ClassLoader loader) {
            return new Nota(source);
        }

        @Override
        public Nota createFromParcel(Parcel source) {
            return new Nota(source);
        }

        @Override
        public Nota[] newArray(int size) {
            return new Nota[size];
        }
    };

    private Nota(Parcel in) {

        String expectedPattern = "dd/MM/yyyy";
        SimpleDateFormat formatter = new SimpleDateFormat(expectedPattern);

        id = in.readString();
        isEncrypted = Boolean.parseBoolean(in.readString());
        title = in.readString();
        text = in.readString();
        color = in.readString();
        image = in.readString();
        audio = in.readString();
        priority = in.readInt();
        tag = in.readInt();
        try {
            creationDate = formatter.parse(in.readString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}