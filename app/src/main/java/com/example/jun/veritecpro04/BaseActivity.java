package com.example.jun.veritecpro04;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.jun.veritecpro04.data.GroupItemObject;
import com.example.jun.veritecpro04.data.RealmManager;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import java.util.ArrayList;
import java.util.regex.Pattern;

import io.realm.RealmList;

public class BaseActivity extends AppCompatActivity {
    RealmManager realmManager = new RealmManager();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realmManager.RealmInitilize();
        realmManager.DataCheck();
    }


    public ArrayList<String> getGroupList() {
        return realmManager.getGroupList();
    }

    public ArrayList<String> set() {
        return realmManager.getGroupList();
    }

    public void setItem(String groupName, GroupItemObject Item) {
        realmManager.setItem(groupName, Item);
    }

    public RealmList<GroupItemObject> getItems(String groupName) {
        return realmManager.getGroup(groupName);
    }

    public int  getItemSize(String groupName) {
        return realmManager.getGroupItemSize(groupName);
    }

    public void  deleteItem(String itemNo) {
         realmManager.deleteItem(itemNo);
    }
}