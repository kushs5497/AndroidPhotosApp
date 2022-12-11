package com.example.photos62;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AlbumViewController extends AppCompatActivity {

    FloatingActionButton fabPhoto;
    ListView listView;
    Context context;
    static Album selectedAlbum;
    static PhotoAdapter adapter;
    static Photo selectedPhoto;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_view);
        fabPhoto=findViewById(R.id.fab_photoAdd);
        listView=findViewById(R.id.imgListView);
        context=this;
        selectedAlbum = MainActivity.SELECTED_ALBUM;
        adapter = new PhotoAdapter(this,selectedAlbum.album);
        listView.setAdapter(adapter);
        setTitle(selectedAlbum.name);

        fabPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentChooseNewImage = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentChooseNewImage,3);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                selectedPhoto = selectedAlbum.album.get(position);


                Intent intentToImageView = new Intent(view.getContext(), ImageViewController.class);
                startActivity(intentToImageView);

            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
            }
        };
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data!=null) {
            Uri selectedImageUri = data.getData();

            AlertDialog.Builder photoNameEntry = new AlertDialog.Builder(context);
            EditText input = new EditText(context);
            photoNameEntry.setTitle("Enter New Photo Name");
            photoNameEntry.setView(input);
            photoNameEntry.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Photo newPhoto = new Photo(selectedImageUri.toString(),input.getText().toString());
                    selectedAlbum.add(newPhoto);
                    adapter.notifyDataSetChanged();
                    MainActivity.save();
                }
            });
            photoNameEntry.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
            });
            photoNameEntry.show();


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.photo_list_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Intent intentBack = new Intent(this,MainActivity.class);
                startActivity(intentBack);
                return true;
            case R.id.menuDelete:
                Intent intentToMainActivity = new Intent(this,MainActivity.class);
                startActivity(intentToMainActivity);
                String deletedAlbumName = MainActivity.SELECTED_ALBUM.name;
                MainActivity.albums.remove(MainActivity.SELECTED_ALBUM);
                MainActivity.save();
                Toast.makeText(this,deletedAlbumName+" Deleted",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menuRename:
                AlertDialog.Builder albumRenameDialog = new AlertDialog.Builder(this);
                EditText input = new EditText(context);

                albumRenameDialog.setTitle("Enter New Album Name");
                albumRenameDialog.setView(input);

                albumRenameDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedAlbum.name=input.getText().toString();
                        setTitle(selectedAlbum.name);
                        MainActivity.save();
                        toaster("Renamed to "+selectedAlbum.name,context);
                    }
                });

                albumRenameDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                albumRenameDialog.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void toaster(String s,Context c){
        Toast.makeText(c,s,Toast.LENGTH_SHORT).show();
    }
}
