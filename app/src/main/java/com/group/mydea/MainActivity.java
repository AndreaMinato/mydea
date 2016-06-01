package com.group.mydea;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.couchbase.lite.CouchbaseLiteException;
import com.karumi.dexter.Dexter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        FragmentModificaNota.addedItem {

    private AdapterNota cardAdapter;
    private RecyclerView recyclerView;
    private StaggeredGridLayoutManager layoutManager;

    private CouchDB database;
    private CryptData myCypher;
    ;// = new CryptData();
    private String myEncPsw = "";
    private FloatingActionButton fab;
    private ArrayList<Nota> note;
    private ArrayList<Nota> myFilteredNotes = new ArrayList<Nota>();
    private MenuItem mSearchAction;
    private boolean isSearchOpened = false;
    private EditText edtSeach;
    private InputMethodManager imm;
    //private List<FloatingActionButton> fabList = new ArrayList<>();

    public static String TAG = "debug tag";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Dexter.initialize(getApplicationContext());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent vIntent = new Intent(MainActivity.this, NuovaNota.class);
                    Bundle vBundle = new Bundle();
                    startActivity(vIntent);
                }
            });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.recycler);

        database = new CouchDB(getApplicationContext());

        try {

            myEncPsw = database.getEncryptionPassword();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        /**
         * TODO: gestire se La password per encriptare le note se non è settata
         */
        myCypher = new CryptData(myEncPsw, MainActivity.this);

        getNoteFromDB();

        if (note.size() == 0) {
            createNotes();
            myCypher.encryptAllNotes(note, database);
        }
        showNotes(note);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mSearchAction = menu.findItem(R.id.action_search);
        return super.onPrepareOptionsMenu(menu);
    }

    protected void handleMenuSearch() {
        ActionBar action = getSupportActionBar(); //get the actionbar

        if (isSearchOpened) { //test if the search is open

            action.setDisplayShowCustomEnabled(false); //disable a custom view inside the actionbar
            action.setDisplayShowTitleEnabled(true); //show the title in the action bar

            //hides the keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edtSeach.getWindowToken(), 0);

            mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_search_24dp));
            isSearchOpened = false;

        } else { //open the search entry

            action.setDisplayShowCustomEnabled(true); //enable it to display a
            // custom view in the action bar.
            action.setCustomView(R.layout.search_bar);//add the custom view
            action.setDisplayShowTitleEnabled(false); //hide the title
            fab.hide();
            edtSeach = (EditText) action.getCustomView().findViewById(R.id.edtSearch); //the text editor
            edtSeach.requestFocus();


            //This is a listener to do a search when the user clicks on search button
            edtSeach.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        doSearch(edtSeach.getText().toString());
                        return true;
                    }
                    return false;
                }
            });

            //This is a listener to do a search when clicks on any letter
            edtSeach.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {

                    // you can call or do what you want with your EditText here
                    doSearch(edtSeach.getText().toString());
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });


            //open the keyboard focused in the edtSearch
            imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(edtSeach, InputMethodManager.SHOW_IMPLICIT);


            mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_action_undo));

            mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_clear_24dp));

            mSearchAction.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    /**Show all posts.
                     * TODO: hide keyboard.
                     */

                    showNotes(note);
                    setTitle(R.string.app_name);
                    imm.hideSoftInputFromInputMethod(edtSeach.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    fab.show();
                    return false;
                }
            });

            isSearchOpened = true;
        }
    }

    private void doSearch(String inputText) {
        Log.d(TAG, "Searching: " + inputText);

        ArrayList<Nota> tmpFilteredNotes = new ArrayList<Nota>();

        for (int i = 0; i < note.size(); i++) {
            if (note.get(i).getTitle().toLowerCase().contains(inputText.toLowerCase()) || note.get(i).getText().toLowerCase().contains(inputText.toLowerCase())) {
                tmpFilteredNotes.add(note.get(i));
            }
        }
        Log.d(TAG, "Note trovate: " + tmpFilteredNotes.size());
        showNotes(tmpFilteredNotes);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (isSearchOpened) {
            handleMenuSearch();
            return;
        }

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

        //for search bar
        switch (id) {
            case R.id.action_settings:

                /*TODO start activity*/

                Intent vIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(vIntent);

                return true;
            case R.id.action_search:
                handleMenuSearch();
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
        int category = 0;

        if (id == R.id.navCatAll) {
            category = 0;
            setTitle(R.string.app_name);
        } else if (id == R.id.navCatLavoro) {
            //Filter by category.
            category = 1;
            setTitle(R.string.navCatWork);
        } else if (id == R.id.navCatPersonale) {
            category = 2;
            setTitle(R.string.navCatPersonal);
        } else if (id == R.id.navCatHobby) {
            category = 3;
            setTitle(R.string.navCatHobby);
        } else if (id == R.id.navCatTempoLibero) {
            category = 4;
            setTitle(R.string.navCatFreetime);
        }

        myFilteredNotes = filterPostByCategory(category);
        Log.d(TAG, "Notes has been filtered:\n" + myFilteredNotes.size() + " has been found with selected category.");

        showNotes(myFilteredNotes);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showNotes(ArrayList<Nota> note) {

        note = myCypher.decryptAllNotes(note);

        cardAdapter = new AdapterNota(note, getApplicationContext(), getFragmentManager());
        recyclerView.setAdapter(cardAdapter);
        //layoutManager = new GridLayoutManager(getApplicationContext(), getResources().getInteger(R.integer.resolution), GridLayoutManager.VERTICAL, false);
        layoutManager = new StaggeredGridLayoutManager(getResources().getInteger(R.integer.resolution), StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
    }

    private ArrayList<Nota> filterPostByCategory(int category) {
        ArrayList<Nota> tmpFilteredNotes = new ArrayList<Nota>();

        if (category != 0) {

            for (int i = 0; i < note.size(); i++) {
                if (note.get(i).getTag() == category) {
                    tmpFilteredNotes.add(note.get(i));
                }
            }

            return tmpFilteredNotes;
        } else {
            return note;
        }
    }

    public void refreshNotes(int pos, Nota nota) {
        note.add(nota);
        cardAdapter.notifyDataSetChanged();
    }

    @Override
    public void itemUpdated(Nota nota, int pos) {

        // scambioNota(nota,pos);

        Log.d(TAG, "Updating Lists...");

        //note.set(pos, nota);
        //cardAdapter.notifyDataSetChanged();

        getNoteFromDB();
        showNotes(note);
    }

 /*   private void scambioNota(Nota nota,int pos){

        /**This method replace the oldNote with the updated one.

        Nota tmpNote=new Nota();
        tmpNote=nota;
        //note.remove(pos);
        note.set(pos, tmpNote);

        Log.d(TAG, "Nota[" + pos + "] has been updated.");
    }*/

    public void getNoteFromDB() {
        try {
            note = database.leggiNote();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
            Log.d(TAG, "Impossibile leggere note dal DB: " + e);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "Impossibile leggere note dal DB: " + e);
        }
    }

    private void createNotes() {

        Log.d(TAG, "Creating 15 new notes...");

        Random rnd = new Random();
        note = new ArrayList<>();

        for (int i = 0; i < 15; i++) {
            Nota nota = new Nota();
            nota.setId(UUID.randomUUID().toString());
            nota.setIsEncrypted(false);
            nota.setTitle("Titolo Figo " + i);
            nota.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
            nota.setTag(rnd.nextInt(4) + 1);
            note.add(i, nota);
        }

    }

}
