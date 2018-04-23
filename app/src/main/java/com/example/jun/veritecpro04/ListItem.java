package com.example.jun.veritecpro04;

import android.graphics.drawable.Drawable;

/**
 * Created by JUN on 2017/10/23.
 */

public class ListItem {
    private Drawable icon;
    private String name;
    private String path;
    private String contents;

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
