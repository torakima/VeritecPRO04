package com.genba.jun.veritecpro04.setting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.View;
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
    CardView saveFolderView;
    FileUtil fileUtil;
    String oldRoot;
    int groupPosiotion = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_main);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) groupPosiotion = getIntent().getExtras().getInt("position");


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
        if (!fileUtil.isExternalMemoryAvailable()) {
            saveFolderView.setCardBackgroundColor(getResources().getColor(R.color.grey_alpha_80));
        } else {
            saveFolderView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog(getSetting().isSaveDirection());
                }
            });
        }
        isSaveDirection = getSetting().isSaveDirection();
        if (!isSaveDirection) saveText.setText("現在保存先：本体");
        else saveText.setText("現在保存先：SD CARD");
    }

    public void showDialog(Boolean save) {
        if (dialogBuilder == null) {
            dialogBuilder = new AlertDialog.Builder(this);
        }
        StringBuilder st = new StringBuilder();
        st.append("保存先を");
        if (isSaveDirection) st.append("本体");
        else st.append("SD CARD");
        st.append("に 変更しますか");
        dialogBuilder.setTitle("確認");
        dialogBuilder.setMessage(st);
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                setSdcardFolder();
            }
        });
        dialogBuilder.show();

    }


    private void setSdcardFolder() {
        setProgressDialog();
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
                updateItem(newUpdateItem);
            }
            String old = oldRoot + rootDir + "/" + group.getGroupName() + sortTxt;
            fileUtil.copyFile(new File(oldRoot + rootDir + "/" + group.getGroupName() + sortTxt), root + "/" + group.getGroupName() + sortTxt);
        }
        isSaveDirection = getSetting().isSaveDirection();
        if (!isSaveDirection) saveText.setText("現在保存先：本体");
        else saveText.setText("現在保存先：SD CARD");
        fileUtil.setDirEmpty(oldRoot + rootDir);
        dismissProgressDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
