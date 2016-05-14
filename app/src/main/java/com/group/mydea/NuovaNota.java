package com.group.mydea;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.Date;

public class NuovaNota extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //TODO fare il dialog per avvisare l'utente che si sta uscendo (OK,ANNULLA)
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuova_nota);

        final EditText titolo = (EditText)findViewById(R.id.editTitle);
        final EditText corpo = (EditText)findViewById(R.id.editBody);
        final RadioButton bassa = (RadioButton)findViewById(R.id.radioSlow);
        final RadioButton media = (RadioButton)findViewById(R.id.radioAverage);
        final RadioButton alta = (RadioButton)findViewById(R.id.radioHigh);

        Button salva = (Button)findViewById(R.id.btnSave);
        if (salva != null) {

            salva.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Nota nuova = new Nota();
                    nuova.setId(nuova.getID() + 1);
                    nuova.setTitle(titolo.toString());
                    //nuova.setColor("Blue");
                    nuova.setTag("Tag");
                    nuova.setText(corpo.toString());
                    nuova.setImage("immagine");
                    nuova.setAudio("audio");
                    nuova.setCreationDate(new Date());

                    if ((bassa).isChecked()) {
                        nuova.setPriority(3);
                        Log.d("RADIO","bassa");
                    }
                    if ((media.isChecked())) {
                        nuova.setPriority(2);
                        Log.d("RADIO", "media");
                    }
                    if (alta.isChecked()) {
                        nuova.setPriority(1);
                        Log.d("RADIO", "alta");
                    }

                    Toast.makeText(NuovaNota.this, "Nota salvata correttamente", Toast.LENGTH_LONG).show();
                }
            });
        }


    }
}
