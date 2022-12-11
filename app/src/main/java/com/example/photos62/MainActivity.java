package com.example.photos62;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    static ArrayList<Album> albums = new ArrayList<Album>();
    AlbumAdapter adapter;
    FloatingActionButton fab;
    static Album SELECTED_ALBUM;
    Context context;

    boolean continueSearch;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final String ALBUM_DATA = "ALBUM_DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(ALBUM_DATA,MODE_PRIVATE);
        editor=sharedPreferences.edit();
        try { openSave();}
        catch (Exception e) {e.printStackTrace();}

        setContentView(R.layout.activity_main);
        context = this;
        fab = findViewById(R.id.fab_btn);
        adapter = new AlbumAdapter(this,albums);
        listView = findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this,R.drawable.ic_baseline_search_24));

        fab.setOnClickListener(view -> {
            AlertDialog.Builder albumNameEntry = new AlertDialog.Builder(context);
            EditText input = new EditText(context);
            albumNameEntry.setTitle("Enter New Album Name");
            albumNameEntry.setView(input);
            albumNameEntry.setPositiveButton("Add", (dialog, which) -> {
                String albumNameInputted = input.getText().toString();
                if(albumNameInputted.isEmpty() && !albumExists(albumNameInputted)) return;
                albums.add(new Album(albumNameInputted));
                adapter.notifyDataSetChanged();
                try { save(); }
                catch (IOException e) { e.printStackTrace(); }
            });
            albumNameEntry.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            albumNameEntry.show();
        });

        listView.setOnItemClickListener((adapter, view, position, arg) -> {
            SELECTED_ALBUM = albums.get(position);
            Intent intentToAlbumView = new Intent(view.getContext(), AlbumViewController.class);
            startActivity(intentToAlbumView);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.album_list_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.searchByPersonMenuItem:
                search("person");
                return true;
            case R.id.searchByLocationMenuItem:
                search("location");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        //return super.onOptionsItemSelected(item);
    }

    private void search(String tagName) {
        AlertDialog.Builder tagValueDialog = new AlertDialog.Builder(context);
        tagValueDialog.setTitle("Enter "+tagName+" name");
        EditText tagValueInput = new EditText(context);
        tagValueDialog.setView(tagValueInput);
        continueSearch=false;

        tagValueDialog.setPositiveButton("Search", (dialogInterface, i) -> {
            if(tagValueInput.getText().toString().isEmpty()) dialogInterface.cancel();
            else searchFor(tagName+"="+tagValueInput.getText().toString());
        });
        tagValueDialog.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());
        tagValueDialog.show();


    }

    public void searchFor(String tag){
        Album searchResults = new Album("Search Results");
        Log.d("Input text","->"+tag);

        for(Album a:albums) for(Photo p:a.album) for(String t:p.tags) if(t.equalsIgnoreCase(tag))
            searchResults.add(p);

        if(searchResults.size<=0){
            Toast.makeText(context,"No Photos have the tag "+tag, Toast.LENGTH_SHORT).show();
            return;
        }
        for(Photo p: searchResults.album) Log.d("Search Results", p.name);
        SELECTED_ALBUM = searchResults;
        Intent intentToAlbumView = new Intent(context, AlbumViewController.class);
        startActivity(intentToAlbumView);
    }

    private boolean albumExists(String albumName) {
        for(Album a : albums){
            if(a.name.equalsIgnoreCase(albumName)){
                return true;
            }
        }
        return false;
    }

    public void save() throws IOException {
        Gson gson = new Gson();
        String s = gson.toJson(stringList);
        editor.putString(KEY_ARRAY_LIST3, s);
        editor.commit();
    }

    public void openSave() throws IOException, ClassNotFoundException {

    }
}