package com.genba.jun.veritecpro04.setting;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.genba.jun.veritecpro04.BaseActivity;
import com.genba.jun.veritecpro04.R;
import com.genba.jun.veritecpro04.data.GroupItemObject;
import com.genba.jun.veritecpro04.data.ItemObject;
import com.genba.jun.veritecpro04.util.FileUtil;

import java.io.File;
import java.util.ArrayList;

import io.realm.RealmResults;

public class SettingActivity extends BaseActivity {

    AlertDialog.Builder dialogBuilder;
    TextView saveText;
    Boolean isSaveDirection;
    Boolean backEnable = true;
    CardView saveFolderView;
    LinearLayout save_parent;
    ProgressBar progress_bar;
    FileUtil fileUtil;
    String oldRoot;
    int groupPosiotion = 0;
    AlertDialog dialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_main);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) groupPosiotion = getIntent().getExtras().getInt("position");

        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        save_parent = (LinearLayout) findViewById(R.id.save_parent);
        fileUtil = new FileUtil();
        setExtRoot();
        findViewById(R.id.name_change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingActivity.this, GroupRenameActivity.class));
            }
        });
        findViewById(R.id.wifi_data).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, WifiDataActivity.class);
                intent.putExtra("position", groupPosiotion);
                startActivity(intent);
            }
        });
        saveText = findViewById(R.id.save_folder);
        saveFolderView = findViewById(R.id.save_folder_change);
        saveFolderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
        isSaveDirection = getSetting().isSaveDirection();
        if (!isSaveDirection) saveText.setText("現在保存先：本体");
        else saveText.setText("現在保存先：SD CARD");
    }



    private void setSdcardFolder() {
        backEnable = false;
        setProgressDialog();
        progress_bar.setVisibility(View.VISIBLE);
        save_parent.setVisibility(View.GONE);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                oldRoot = extPath;
                setChangeRoot(!isSaveDirection);
                setSetting(!isSaveDirection);
                File root = fileUtil.makeDirectory(extPath + rootDir);
                ArrayList<String> groupItems = getGroupList();
                for (String item : groupItems) {
                    fileUtil.makeDirectory(root + "/" + item);
                }

                RealmResults<ItemObject> alldata = getAlldata();
                for (ItemObject group : alldata) {
                    String newPath;
                    for (GroupItemObject item : group.getGroupItemObjects()) {
                        File imageFile = new File(item.getImagePath());
                        File textFile = new File(item.getTextPath());
                        String newImagePath = root + File.separator + item.getGroupName() + File.separator + imageFile.getName();
                        String newTextPath = root + File.separator + item.getGroupName() + File.separator + textFile.getName();
                        fileUtil.copyFile(imageFile, newImagePath);
                        fileUtil.copyFile(textFile, newTextPath);
                        GroupItemObject newUpdateItem = new GroupItemObject();
                        newUpdateItem.setItemNo(item.getItemNo());
                        newUpdateItem.setGroupNo(item.getGroupNo());
                        newUpdateItem.setGroupName(item.getGroupName());
                        newUpdateItem.setImageName(item.getImageName());
                        newUpdateItem.setTime(item.getTime());
                        newUpdateItem.setImagePath(newImagePath);
                        newUpdateItem.setImageSize(item.getImageSize());
                        newUpdateItem.setTextContent(item.getTextContent());
                        newUpdateItem.setTextPath(newTextPath);
                        newUpdateItem.setOriginImageName(item.getOriginImageName());
                        updateItem(newUpdateItem);
                    }
//                    String old = oldRoot + rootDir + "/" + group.getGroupName() + sortTxt;
//                    fileUtil.copyFile(new File(oldRoot + rootDir + "/" + group.getGroupName() + sortTxt), root + "/" + group.getGroupName() + sortTxt);
                    progress_bar.setVisibility(View.GONE);
                    save_parent.setVisibility(View.VISIBLE);
                }
            }
        });
        dismissProgressDialog();


        isSaveDirection = getSetting().isSaveDirection();
        if (!isSaveDirection) saveText.setText("現在保存先：本体");
        else saveText.setText("現在保存先：SD CARD");
        fileUtil.setDirEmpty(oldRoot + rootDir);
        backEnable = true;

    }

    private void showDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        isSaveDirection = getSetting().isSaveDirection();
        final Boolean isSdcard = fileUtil.isExternalMemoryAvailable();
        int defaultItem = 0;
        if (isSaveDirection) defaultItem = 1;
        else defaultItem = 0;
        final String[] items = {"SD CARD", "本体"};
        // Init ArrayAdapter with OpenPGP Providers
        ListAdapter adapter = new ArrayAdapter<String>(this, R.layout.checked_layout, items) {
            public View getView(int position, View convertView, ViewGroup parent) {
                // User super class to create the View
                View v = super.getView(position, convertView, parent);
                if (!isSdcard) {
                    if (position == 0) v.setEnabled(false);
                    else v.setEnabled(true);
                }
                return v;
            }
        };

        builder.setSingleChoiceItems(adapter, defaultItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (isSaveDirection) {
                    if (i == 0)
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    else
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                } else {
                    if (i == 1)
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    else
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                setSdcardFolder();
            }
        });
        builder.setNegativeButton("CANCEL", null);
        dialog = builder.create();
        dialog.show();
        if (!isSdcard) dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

    }


    @Override
    public void onBackPressed() {
        if (backEnable)
            super.onBackPressed();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
