package com.example.photos62;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Kush and Dhiren
 *
 */
public class Album implements Serializable {

    ArrayList<Photo> album = new ArrayList<Photo>();
    String name;
    int size;

    public Album(String name){
        this.name=name;
        size=0;
    }

    public String toString() {
        return name;
    }

    public void add(Photo p ) {
        album.add(p);
        size++;
    }

    public void remove(int i) {
        album.remove(i);
        size--;
    }

    public void remove(Photo p) {
        album.remove(p);
        size--;
    }

    public Photo get(int i){
        return album.get(i);
    }


}

