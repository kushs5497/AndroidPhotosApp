package com.example.photos62;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import com.google.gson.Gson;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG_DATA = "tag_data";
    static ArrayList<String> allTags;
    ListView listView;
    static ArrayList<Album> albums;
    AlbumAdapter adapter;
    FloatingActionButton fab;
    static Album SELECTED_ALBUM;
    Context context;

    boolean continueSearch;

    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;
    private static final String ALBUM_DATA = "ALBUM_DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(ALBUM_DATA,MODE_PRIVATE);
        editor=sharedPreferences.edit();
        openSave();

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
                save();
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
        AutoCompleteTextView tagValueInput = new AutoCompleteTextView(context);
        ArrayAdapter<String> tagAutoCompAdapter = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item ,allTags);
        tagValueInput.setAdapter(tagAutoCompAdapter);
        tagAutoCompAdapter.notifyDataSetChanged();
        tagValueInput.setThreshold(1);

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

    public static void save(){
        Gson gson = new Gson();
        String json = gson.toJson(albums);
        editor.putString(ALBUM_DATA,json);
        editor.putString(TAG_DATA, gson.toJson(allTags));
        editor.commit();
    }

    public static void openSave(){
        Gson gson = new Gson();

        String json = sharedPreferences.getString(ALBUM_DATA,null);
        Type type = new TypeToken<ArrayList<Album>>(){}.getType();
        albums=gson.fromJson(json,type);

        String json2 = sharedPreferences.getString(TAG_DATA,null);
        Type type2 = new TypeToken<ArrayList<String>>(){}.getType();
        allTags=gson.fromJson(json2,type2);

        if (albums==null) albums = new ArrayList<Album>();
        if(allTags==null) allTags=new ArrayList<String>();
    }
}