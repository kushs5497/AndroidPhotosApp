package com.example.photos62;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Kush and Dhiren
 *
 */

// Initialize Album class
public class Album implements Serializable {

    ArrayList<Photo> album = new ArrayList<Photo>();
    String name;
    int size;

    // Set name and size
    public Album(String name){
        this.name=name;
        size=0;
    }

    // Initialize string toString
    public String toString() {
        return name;
    }

    // Add method to add photo and increment size
    public void add(Photo p ) {
        album.add(p);
        size++;
    }

    // Remove method to remove int and decrement size
    public void remove(int i) {
        album.remove(i);
        size--;
    }
    // Second remove method to remove photo and decrement size
    public void remove(Photo p) {
        album.remove(p);
        size--;
    }

    // Photo class to get int and return album
    public Photo get(int i){
        return album.get(i);
    }


}

