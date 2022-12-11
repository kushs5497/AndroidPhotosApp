package com.example.photos62;

import java.io.Serializable;
import java.util.ArrayList;

public class Photo implements Serializable {

    public String imgPath;
    public String name;
    ArrayList<String> tags = new ArrayList<String>();
    boolean location=false;

    public Photo(String filePath, String name){
        imgPath=filePath;
        this.name=name;
    }

    public void addTag(String name, String val) {
        if (name.equals("location")) {
            if(location) {
                //com.example.photos62.MainActivity.makeError("Image already has a location. Please delete location to change.");
                return;
            }
            else location=true;
        }
        if(!hasTag(name+"="+val)) tags.add(name+"="+val);
    }

    public void deleteTag(String name, String val) {
        for(String t:tags) {
            if (t.equals(name+"="+val)) {
                tags.remove(t);
                if (name.equals("location")) location=false;
                return;
            }
        }
    }

    public String toString() {
        return name;
    }

    public boolean hasTag(String t) {
        for(String s: tags) {
            if(s.equals(t)) return true;
        }
        return false;
    }

}

