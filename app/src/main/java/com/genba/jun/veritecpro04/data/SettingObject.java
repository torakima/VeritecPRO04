package com.genba.jun.veritecpro04.data;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SettingObject extends RealmObject {

    @PrimaryKey
    private int itemNo;

    private boolean saveDirection = false; //false : 内部　true:sd card

    public int getItemNo() {
        return itemNo;
    }

    public void setItemNo(int itemNo) {
        this.itemNo = itemNo;
    }

    public boolean isSaveDirection() {
        return saveDirection;
    }

    public void setSaveDirection(boolean saveDirection) {
        this.saveDirection = saveDirection;
    }
}
