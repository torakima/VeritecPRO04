package com.example.jun.veritecpro04.data;

import android.content.Context;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class RealmManager {
    private Realm mRealm;

    public void RealmInitilize() {
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("dataItem.realm")
                .build();
        mRealm = Realm.getInstance(config);
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

    public void updateImagePath(String oldGroupName, String newGroupName) {
        if (mRealm == null) RealmInitilize();
        String oldFolderPath = "/" + oldGroupName + "/";
        String newFolderPath = "/" + newGroupName + "/";
        ItemObject results = mRealm.where(ItemObject.class).equalTo("GroupName", oldGroupName).findFirst();
        mRealm.beginTransaction();
        RealmList<GroupItemObject> items = results.getGroupItemObjects();
        for (GroupItemObject item : items) {
            item.setImagePath(item.getImagePath().replace(oldFolderPath, newFolderPath));
            item.setTextPath(item.getImagePath().replace(oldFolderPath, newFolderPath));
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
}
