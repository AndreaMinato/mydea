package com.group.mydea;

/**
 * Created by andrea on 13/05/16.
 */
import java.util.Date;
import java.util.UUID;

class MalformedHexColorException extends RuntimeException {
    public MalformedHexColorException() {
        super("Colore non valido (HEX exception)");
    }
}

public class Nota {

    private static final String TAG = "Nota";

    private String id;
    private String title;
    private String color;
    private String tag;
    private String text;
    private String image;           //path immagine
    private String audio;           //path audio
    private Date creationDate;

    public Nota() {
        id = UUID.randomUUID().toString();
    }

    public Nota(String uuid) {
        id = uuid;
    }

    public String getID() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
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

    public void setColor(String color) {
        if (!color.matches("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$")) {
            throw new MalformedHexColorException();
        } else {
            this.color = color;
        }
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

    public void setImage(String imgage) {
        this.image = imgage;
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


}