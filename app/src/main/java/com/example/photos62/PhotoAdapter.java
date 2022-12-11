package com.example.photos62;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PhotoAdapter extends ArrayAdapter<Photo> {

    public PhotoAdapter(Context context, ArrayList<Photo> album){
        super(context,0,album);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Photo photo = getItem(i);
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.image_layout_view, viewGroup, false);
        }
        ImageView imageView = (ImageView) view.findViewById(R.id.item_image);
        TextView textView = (TextView) view.findViewById(R.id.item_title);
        imageView.setImageURI(Uri.parse(photo.imgPath));
        imageView.setMaxWidth(view.getWidth());
        imageView.setMaxHeight(view.getHeight());
        textView.setText(photo.name);
        return view;
    }
}
