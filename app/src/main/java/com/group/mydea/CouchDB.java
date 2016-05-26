package com.group.mydea;

/**
 * Created by andrea on 13/05/16.
 */

import android.content.Context;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseOptions;
import com.couchbase.lite.Document;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.UnsavedRevision;
import com.couchbase.lite.View;
import com.couchbase.lite.android.AndroidContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CouchDB {
    private static final String TAG = "CouchDB";
    private static final String TYPE_KEY = "type";
    private static final String DB_NAME = "noteme";

    private static final String VIEW_NOTE = "viewNote";

    private Manager man;
    private Database db;
    private Context ctx;

    public CouchDB(Context c) {
        ctx = c;
        createManager();
        createViewForNota();
    }

    /**
     * Crea il database manager
     */
    private void createManager() {
        try {
            man = new Manager(new AndroidContext(ctx), Manager.DEFAULT_OPTIONS);
            Log.d(TAG, "Manager Creato\n");
        } catch (IOException e) {
            Log.d(TAG, "Impossibile creare l'oggetto Manager");
            e.printStackTrace();
        }
        if (!Manager.isValidDatabaseName(DB_NAME)) {
            Log.d(TAG, "Nome del Database errato");

        } else {
            try {
                DatabaseOptions options = new DatabaseOptions();
                options.setCreate(true);
                db = man.getDatabase(DB_NAME);
                //db = man.openDatabase(DB_NAME, options);
                Log.d(TAG, "Database creato");


            } catch (CouchbaseLiteException e) {
                Log.d(TAG, "Impossibile accedere al database");
                e.printStackTrace();
            }
        }
    }

    private void createViewForNota() {
        View view = db.getView(VIEW_NOTE);
        view.setMap(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {
                if (document.containsKey(TYPE_KEY) &&
                        document.get(TYPE_KEY).equals(Nota.class.getName())) {
                    emitter.emit(Nota.class.getName(), document.get(Nota.class.getName()));
                }
            }
        }, "1");
    }

    /**
     * Salva una singola nota nel database
     *
     * @param nota
     * @throws IOException
     * @throws CouchbaseLiteException
     */
    public void salvaNota(Nota nota) throws IOException, CouchbaseLiteException {
        Document document = db.getDocument(nota.getID());
        Log.i(TAG, "salvaNota: " + nota.getID());
        Map<String, Object> properties = new HashMap<>(   );
        if (document.getProperties() != null)
            properties.putAll(document.getProperties());
        ObjectMapper objectMapper = new ObjectMapper();
        String s = objectMapper.writeValueAsString(nota);
        properties.put(Nota.class.getName(), s);            // metto nelle properties una stringa json
        properties.put(TYPE_KEY, Nota.class.getName());
        document.putProperties(properties);
    }


    public void eliminaNota(Nota nota) throws IOException, CouchbaseLiteException {
        Document document = db.getDocument(nota.getID());
        document.delete();
    }


    /**
     * Salva un ArrayList di oggetti di tipo Nota nel database
     *
     * @param note
     * @throws IOException
     * @throws CouchbaseLiteException
     */
    public void salvaNote(ArrayList<Nota> note) throws IOException, CouchbaseLiteException {
        long time = System.currentTimeMillis();
        for (Nota nota : note)
            salvaNota(nota);
        Log.d(TAG, String.format("note salvate in %s ms", System.currentTimeMillis() - time));
    }

    public Nota leggiNota(String id) {
        // TODO
        return null;
    }

    /**
     * Legge le note memorizzate nel database
     *
     * @return
     * @throws CouchbaseLiteException
     * @throws IOException
     */
    public ArrayList<Nota> leggiNote() throws CouchbaseLiteException, IOException {
        long time = System.currentTimeMillis();
        Query query = db.getView(VIEW_NOTE).createQuery();
        query.setMapOnly(true);
        QueryEnumerator rows = query.run();

        ArrayList<Nota> note = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        for (QueryRow row : rows) {
            note.add(objectMapper.readValue(((String) row.getValue()), Nota.class));
        }
        Log.d(TAG, String.format("note lette in %s ms", System.currentTimeMillis() - time));
        return note;
    }

    /*TODO: questi 2 metodi*/
    public void setEncryptionPassword(String newPsw)throws IOException, CouchbaseLiteException {
        long time = System.currentTimeMillis();


        Log.d(TAG, String.format("note salvate in %s ms", System.currentTimeMillis() - time));

    }

    public void getEncryptionPassword(){

    }
}