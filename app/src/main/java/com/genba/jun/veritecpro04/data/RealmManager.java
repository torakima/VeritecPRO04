package com.genba.jun.veritecpro04.data;

import android.content.Context;

import com.genba.jun.veritecpro04.smb.config.IConfig;
import com.genba.jun.veritecpro04.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class RealmManager {
    private Realm mRealm;

//    public void RealmInitilize() {
//        RealmConfiguration config = new RealmConfiguration.Builder()
//                .name("dataItem.realm")
//                .build();
//        mRealm = Realm.getInstance(config);
//    }

    public void RealmInitilize() {
        mRealm = Realm.getDefaultInstance();

    }

    public void CloseReam() {

    }

    public boolean DataCheck() {

        RealmQuery<ItemObject> query = mRealm.where(ItemObject.class);
        RealmResults<ItemObject> result = query.findAll();
        if (result.size() < 1) {
            setData();
            return false;
        }
        return true;
    }

    public ArrayList<String> getGroupList() {
        RealmResults<ItemObject> results = mRealm.where(ItemObject.class).findAll();
        ArrayList<String> arrayList = new ArrayList<>();
        for (ItemObject obj : results) arrayList.add(obj.getGroupName());
        return arrayList;
    }

    public RealmResults<ItemObject> getGroupListResult() {
        return mRealm.where(ItemObject.class).findAll();
    }

    public void setData() {

        try {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    for (int i = 0; i < 10; i++) {
                        final ItemObject group = new ItemObject();
                        group.setGroupNo(i + 1);
                        int index = i + 1;
                        group.setGroupName("GroupNo" + index);
                        // プライマリーキーが同じならアップデート
                        realm.copyToRealmOrUpdate(group);
                    }

                    SettingObject settingObject = new SettingObject();
                    settingObject.setItemNo(0);
                    settingObject.setSaveDirection(new FileUtil().isExternalMemoryAvailable());
                    realm.copyToRealmOrUpdate(settingObject);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateGroupName(ItemObject groupObj, String msg) {
        mRealm.beginTransaction();
        groupObj.setGroupName(msg);
        mRealm.commitTransaction();
    }

    public RealmList<GroupItemObject> getGroup(String groupName) {
        if (mRealm == null) return null;
        else {
            return mRealm.where(ItemObject.class).equalTo("GroupName", groupName).findFirst().getGroupItemObjects();
        }
    }

    public RealmResults<ItemObject> getAllData() {
        if (mRealm == null) return null;
        else {
            return mRealm.where(ItemObject.class).findAll();
        }
    }

    public int getGroupItemSize(String groupName) {
        if (mRealm == null) return 0;
        else {
            return mRealm.where(ItemObject.class).equalTo("GroupName", groupName).findFirst().getGroupItemObjects().size();
        }
    }

    public void setItem(String groupName, GroupItemObject Item) {
        ItemObject results = mRealm.where(ItemObject.class).equalTo("GroupName", groupName).findFirst();
        mRealm.beginTransaction();
        results.getGroupItemObjects().add(Item);
        mRealm.commitTransaction();
    }

    public GroupItemObject getItem(String groupName, String ItemNo) {
        GroupItemObject results = mRealm.where(ItemObject.class).equalTo("GroupName", groupName).findFirst().getGroupItemObjects().where().equalTo("itemNo", ItemNo).findFirst();
        return results;
    }

    public void updateItem(final GroupItemObject item) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                mRealm.copyToRealmOrUpdate(item);
            }
        });
    }

    public Boolean setTrasFolder(String groupName, String folderName) {
        ItemObject itemObject = mRealm.where(ItemObject.class).equalTo("GroupName", groupName).findFirst();
        String saveFolderName = folderName.replace("/", "").replace(">", "").trim();
        mRealm.beginTransaction();
        itemObject.setDataTrasUrl(saveFolderName);
        mRealm.commitTransaction();
        return true;
    }


    public void updateImagePath(String oldGroupName, String newGroupName) {
        if (mRealm == null) RealmInitilize();
        String oldFolderPath = "/" + oldGroupName + "/";
        String newFolderPath = "/" + newGroupName + "/";
        ItemObject results = mRealm.where(ItemObject.class).equalTo("GroupName", oldGroupName).findFirst();
        mRealm.beginTransaction();
        RealmList<GroupItemObject> items = results.getGroupItemObjects();
        for (GroupItemObject item : items) {
            item.setImagePath(item.getImagePath().replace(oldFolderPath, newFolderPath));
            item.setTextPath(item.getTextPath().replace(oldFolderPath, newFolderPath));
            item.setGroupName(newGroupName);

        }
        ItemObject itemObject = mRealm.where(ItemObject.class).equalTo("GroupName", oldGroupName).findFirst();
        itemObject.setGroupName(newGroupName);
        mRealm.commitTransaction();
    }


    public void deleteItem(final String ItemNo) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                mRealm.where(GroupItemObject.class).equalTo("itemNo", ItemNo).findAll().deleteAllFromRealm();
            }
        });
    }

    public WifiObject getWifiUser() {
        RealmResults<WifiObject> list = mRealm.where(WifiObject.class).findAll();
        if (list.size() == 0) return null;
        else return list.last();
    }

    public void setWifiUser(final IConfig userInfo) {
        final WifiObject obj = new WifiObject();
        obj.setUser(userInfo.user);
        obj.setPassword(userInfo.password);
        obj.setIpAddress(userInfo.host);
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                mRealm.copyToRealmOrUpdate(obj);
            }
        });
    }

    public SettingObject getSetting() {
        return mRealm.where(SettingObject.class).findFirst();
    }

    public void setSetting(boolean save) {
        SettingObject obj = mRealm.where(SettingObject.class).findFirst();
        mRealm.beginTransaction();
        obj.setSaveDirection(save);
        mRealm.commitTransaction();
    }
}
