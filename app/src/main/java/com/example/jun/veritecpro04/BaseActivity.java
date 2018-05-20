package com.example.jun.veritecpro04;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.jun.veritecpro04.data.ActItem;
import com.example.jun.veritecpro04.data.GroupItemObject;
import com.example.jun.veritecpro04.data.RealmManager;
import com.example.jun.veritecpro04.util.FileUtil;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.util.ArrayList;

import io.realm.RealmList;

public class BaseActivity extends AppCompatActivity {
    public RealmManager realmManager = new RealmManager();
    public String extPath;
    public String rootDir = "/Genba";
    FileUtil fileUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        realmManager.RealmInitilize();
        fileUtil = new FileUtil();
        extPath = FileUtil.getExternalStoragePath(this);
        fileUtil.makeDirectory(extPath + rootDir);

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();


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

    public GroupItemObject getItem(String groupName, String ItemNo) {
        return realmManager.getItem(groupName, ItemNo);
    }

    public void updateItem(GroupItemObject item) {
        realmManager.updateItem(item);
    }

    public RealmList<GroupItemObject> getItems(String groupName) {
        return realmManager.getGroup(groupName);
    }

    public int getItemSize(String groupName) {
        return realmManager.getGroupItemSize(groupName);
    }

    public void deleteItem(String itemNo) {
        realmManager.deleteItem(itemNo);
    }

    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            ActItem.getSdCardFilesDirPathListForLollipop(getBaseContext());
            File root = fileUtil.makeDirectory(extPath + "/Genba");
            for (int a = 0; a < 10; a++) {
                int index = a + 1;
                fileUtil.makeDirectory(root + "/" + "GroupNo" + index);
            }

        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            finish();
        }


    };
}