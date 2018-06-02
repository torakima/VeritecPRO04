package com.genba.jun.veritecpro04;

import android.Manifest;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.genba.jun.veritecpro04.data.ActItem;
import com.genba.jun.veritecpro04.data.GroupItemObject;
import com.genba.jun.veritecpro04.data.ItemObject;
import com.genba.jun.veritecpro04.data.RealmManager;
import com.genba.jun.veritecpro04.data.SettingObject;
import com.genba.jun.veritecpro04.util.FileUtil;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmResults;

public class BaseActivity extends AppCompatActivity {
    public RealmManager realmManager = new RealmManager();
    public String extPath;
    public String rootDir = "/Genba";
    public String sortTxt = "/sort.txt";
    public Boolean isFirst = false;
    FileUtil fileUtil;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        realmManager.RealmInitilize();
        fileUtil = new FileUtil();

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    public void setExtRoot() {
        if (getSetting().isSaveDirection()) {
            extPath = ActItem.getSdCardFilesDirPathListForLollipop(this);
        } else {
            extPath = FileUtil.getExternalStoragePath(this);
        }
    }

    public void setChangeRoot(Boolean sdcard) {
        if (sdcard) extPath = ActItem.getSdCardFilesDirPathListForLollipop(this);
        else extPath = FileUtil.getExternalStoragePath(this);

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

    public RealmResults<ItemObject> getAlldata() {
        return realmManager.getAllData();
    }

    public SettingObject getSetting() {
        return realmManager.getSetting();
    }

    public void setSetting(boolean save) {
        realmManager.setSetting(save);
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
            if (!isFirst) return;
            fileUtil.setDirEmpty(extPath + "/Genba"); // dir delete
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


    public void setProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("処理を実行中しています");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}