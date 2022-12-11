package com.example.photos62;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ImageViewController extends AppCompatActivity {

    Album album;
    Photo currentPhoto;
    int index;
    ArrayList<String> tags;
    Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.open_image_layout);
         context = this;

        album = AlbumViewController.selectedAlbum;
        currentPhoto = AlbumViewController.selectedPhoto;
        index = album.album.indexOf(currentPhoto);
        tags = new ArrayList<String>();
        tags.addAll(currentPhoto.tags);

        ImageView imageView = findViewById(R.id.imageView_pic_slideshow);
        Button left = findViewById(R.id.back_pic_button);
        Button right = findViewById(R.id.forward_pic_button);
        ListView tagsList = findViewById(R.id.list_view_tags);
        FloatingActionButton addPerson = findViewById(R.id.fab_add_person_tag);
        FloatingActionButton addLocation = findViewById(R.id.fab_add_location_tag);

        imageView.setImageURI(Uri.parse(currentPhoto.imgPath));
        if(currentPhoto.location) addLocation.setVisibility(View.INVISIBLE);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,tags);
        tagsList.setAdapter(adapter);

        left.setOnClickListener(view -> {
            index--;
            if(index<0) index=album.size-1;
            currentPhoto=album.get(index);
            imageView.setImageURI(Uri.parse(currentPhoto.imgPath));
            if(currentPhoto.location) addLocation.setVisibility(View.INVISIBLE);
            else addLocation.setVisibility(View.VISIBLE);
            tags.clear();
            tags.addAll(currentPhoto.tags);
            adapter.notifyDataSetChanged();
        });

        right.setOnClickListener(view -> {
            index++;
            if(index>= album.size) index=0;
            currentPhoto=album.get(index);
            imageView.setImageURI(Uri.parse(currentPhoto.imgPath));
            if(currentPhoto.location) addLocation.setVisibility(View.INVISIBLE);
            else addLocation.setVisibility(View.VISIBLE);
            tags.clear();
            tags.addAll(currentPhoto.tags);
            adapter.notifyDataSetChanged();
        });

        addPerson.setOnClickListener(view -> {
            AlertDialog.Builder personTagDialog = new AlertDialog.Builder(context);
            personTagDialog.setTitle("Enter Person Name");
            EditText personTagValue = new EditText(context);
            personTagDialog.setView(personTagValue);

            personTagDialog.setPositiveButton("Add", (dialogInterface, i) -> {
                if(personTagValue.getText().toString().isEmpty()) {
                    Toast.makeText(context,"Tag Value is Empty",Toast.LENGTH_SHORT).show();
                    dialogInterface.cancel();
                }
                else if(currentPhoto.tags.contains("person="+personTagValue.getText().toString())){
                    Toast.makeText(context,"Tag Already Exists",Toast.LENGTH_SHORT).show();
                    dialogInterface.cancel();
                }
                else{
                    currentPhoto.tags.add("person="+personTagValue.getText().toString());
                    tags.add("person="+personTagValue.getText().toString());
                    MainActivity.allTags.add(personTagValue.getText().toString());
                    adapter.notifyDataSetChanged();
                    MainActivity.save();
                }
            });

            personTagDialog.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());

            personTagDialog.show();
        });

        addLocation.setOnClickListener(view -> {
            AlertDialog.Builder locationTagDialog = new AlertDialog.Builder(context);
            locationTagDialog.setTitle("Enter Location Name");
            EditText locationTagValue = new EditText(context);
            locationTagDialog.setView(locationTagValue);

            locationTagDialog.setPositiveButton("Add", (dialogInterface, i) -> {
                if(locationTagValue.getText().toString().isEmpty()) {
                    Toast.makeText(context,"Tag Value is Empty",Toast.LENGTH_SHORT).show();
                    dialogInterface.cancel();
                }
                else{
                    currentPhoto.tags.add("location="+locationTagValue.getText().toString());
                    tags.add("location="+locationTagValue.getText().toString());
                    MainActivity.allTags.add(locationTagValue.getText().toString());
                    currentPhoto.location=true;
                    addLocation.setVisibility(View.INVISIBLE);
                    adapter.notifyDataSetChanged();
                    MainActivity.save();
                }
            });

            locationTagDialog.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());

            locationTagDialog.show();

        });

        tagsList.setOnItemClickListener((adapterView, view, i, l) -> {
            AlertDialog.Builder deleteTagDialog = new AlertDialog.Builder(context);
            deleteTagDialog.setTitle("Are you sure you want to delete this tag?");

            deleteTagDialog.setPositiveButton("Yes", (dialogInterface, index) -> {
                String removedTag = tags.remove(i);
                if(removedTag.substring(0,removedTag.indexOf('=')).equals("location")) addLocation.setVisibility(View.VISIBLE);
                MainActivity.allTags.remove(currentPhoto.tags.remove(i));
                adapter.notifyDataSetChanged();
                MainActivity.save();
            });

            deleteTagDialog.setNegativeButton("Cancel", (dialogInterface, i1) -> dialogInterface.cancel());

            deleteTagDialog.show();
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.select_photo_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.move_menu_item){
            AlertDialog.Builder albumSelectionDialog = new AlertDialog.Builder(context);
            albumSelectionDialog.setTitle("Where do you want to move this Photo?");
            ListView listView = new ListView(context);
            AlbumAdapter adapter = new AlbumAdapter(this,MainActivity.albums);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            albumSelectionDialog.setView(listView);

            listView.setOnItemClickListener((adapterView, view, i, l) -> {
                album.remove(currentPhoto);
                MainActivity.albums.get(i).add(currentPhoto);
                MainActivity.save();
                Intent intentToAlbumView = new Intent(view.getContext(), AlbumViewController.class);
                startActivity(intentToAlbumView);
                Toast.makeText(context,"Moved Photo",Toast.LENGTH_SHORT).show();
            });

            albumSelectionDialog.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());

            albumSelectionDialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
