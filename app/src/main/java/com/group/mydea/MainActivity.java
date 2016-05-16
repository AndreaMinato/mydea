package com.group.mydea;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener ,
        FragmentModificaNota.addedItem{


    public static final String TAG_FRAGMENT_MODIFICA_NOTA = "tagfragmentmodificanota";

    /** TODO Integrazione CouchBase (Andrea) (--Da testare--);
     *  TODO XML → Card, activity e menu (Ingrid + Matteo);
     *  TODO: lateralNavbar (Cap)
    */

    private FragmentModificaNota FragmentModificaNota;
    private AdapterNota cardAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    public ArrayList<Nota> note;
    ArrayList<Nota> myFilteredNotes = new ArrayList<Nota>();

    public static String TAG="debug tag";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO Chiamare il fragment per salvare la nota
                    Intent vIntent = new Intent(MainActivity.this, NuovaNota.class);
                    Bundle vBundle = new Bundle();
                    startActivity(vIntent);
                }
            });
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        recyclerView = (RecyclerView)findViewById(R.id.recycler);
        Random rnd=new Random();
        note = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            Nota nota = new Nota();
            nota.setTitle("Titolo Figo " + i);
            nota.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
            nota.setTag(rnd.nextInt(4)+1);
            note.add(i, nota);

        }

        showNotes(note);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentModificaNota = (FragmentModificaNota) fragmentManager.findFragmentByTag(TAG_FRAGMENT_MODIFICA_NOTA);


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        //Initialize category with 0 to avoid error. 0 = apply no filter (Show all posts.)
        int category=0;

        if(id==R.id.navCatAll){
            category=0;
        }else if (id == R.id.navCatLavoro) {
            //Filter by category.
            category=1;
        } else if (id == R.id.navCatPersonale) {
            category=2;
        } else if (id == R.id.navCatHobby) {
            category=3;
        } else if (id == R.id.navCatTempoLibero) {
            category=4;
        }

        myFilteredNotes=filterPostByCategory(category);
        Log.d(TAG,"Notes has been filtered:\n"+ myFilteredNotes.size()+" has been found with selected category.");

        showNotes(myFilteredNotes);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showNotes(ArrayList<Nota> note){
        /*
        TODO: Animations on showing notes.
         */
        cardAdapter = new AdapterNota(note, getApplicationContext(),getSupportFragmentManager());
        recyclerView.setAdapter(cardAdapter);
        layoutManager = new GridLayoutManager(getApplicationContext(), getResources().getInteger(R.integer.resolution), GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
    }

    private ArrayList<Nota> filterPostByCategory(int category) {
        ArrayList<Nota> tmpFilteredNotes = new ArrayList<Nota>();

        if(category!=0) {

            for (int i = 0; i < note.size(); i++) {
                if (note.get(i).getTag() == category) {
                    tmpFilteredNotes.add(note.get(i));
                }
            }

            return tmpFilteredNotes;
        }
        else
        {
            return note;
        }
    }


    @Override
    public void itemAdded(Nota nota) {
        note.add(nota);
        cardAdapter.notifyDataSetChanged();
    }
}
