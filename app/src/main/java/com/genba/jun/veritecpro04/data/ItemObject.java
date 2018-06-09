package com.genba.jun.veritecpro04.data;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ItemObject  extends RealmObject {

    @PrimaryKey
    private int GroupNo;
    private String GroupName;
    private String sortFIle;
    private String dataTrasUrl;
    private String dataOriginTrasUrl;

    private RealmList<GroupItemObject> groupItemObjects;

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

    public String getSortFIle() {
        return sortFIle;
    }

    public void setSortFIle(String sortFIle) {
        this.sortFIle = sortFIle;
    }

    public RealmList<GroupItemObject> getGroupItemObjects() {
        return groupItemObjects;
    }

    public void setGroupItemObjects(RealmList<GroupItemObject> groupItemObjects) {
        this.groupItemObjects = groupItemObjects;
    }

    public String getDataTrasUrl() {
        return dataTrasUrl;
    }

    public void setDataTrasUrl(String dataTrasUrl) {
        this.dataTrasUrl = dataTrasUrl;
    }

    public String getDataOriginTrasUrl() {
        return dataOriginTrasUrl;
    }

    public void setDataOriginTrasUrl(String dataOriginTrasUrl) {
        this.dataOriginTrasUrl = dataOriginTrasUrl;
    }
}
