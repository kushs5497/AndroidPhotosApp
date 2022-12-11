package com.example.photos62;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AlbumAdapter extends ArrayAdapter<Album> {

    public AlbumAdapter(Context context, ArrayList<Album> albums){
        super(context,0,albums);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Album album = getItem(i);
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.album_list__item_view, viewGroup, false);
        }
        TextView tvAlbumName = (TextView) view.findViewById(R.id.tvAlbumName);
        tvAlbumName.setText(album.name);
        return view;
    }
}
