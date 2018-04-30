package com.example.jun.veritecpro04.data;

import io.realm.RealmObject;

public class GroupItemObject extends RealmObject {

    private int GroupNo;

    private String GroupName;

    private String ItemNo;

    private String time;

    private String ImagePath;

    private String ImageName;

    private String ImageTextPath;

    private String ImageText;

    public int getGroupNo() {
        return GroupNo;
    }

    public void setGroupNo(int groupNo) {
        GroupNo = groupNo;
    }

    public String getGroupName() {
        return GroupName;
    }

    public void setGroupName(String groupName) {
        GroupName = groupName;
    }

    public String getItemNo() {
        return ItemNo;
    }

    public void setItemNo(String itemNo) {
        ItemNo = itemNo;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImagePath() {
        return ImagePath;
    }

    public void setImagePath(String imagePath) {
        ImagePath = imagePath;
    }

    public String getImageName() {
        return ImageName;
    }

    public void setImageName(String imageName) {
        ImageName = imageName;
    }

    public String getImageTextPath() {
        return ImageTextPath;
    }

    public void setImageTextPath(String imageTextPath) {
        ImageTextPath = imageTextPath;
    }

    public String getImageText() {
        return ImageText;
    }

    public void setImageText(String imageText) {
        ImageText = imageText;
    }
}
