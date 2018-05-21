package com.genba.jun.veritecpro04;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.genba.jun.veritecpro04.adapter.ListAdapter;
import com.genba.jun.veritecpro04.data.ActItem;
import com.genba.jun.veritecpro04.data.GroupItemObject;
import com.genba.jun.veritecpro04.data.ListItem;
import com.genba.jun.veritecpro04.data.RealmManager;
import com.genba.jun.veritecpro04.setting.SettingActivity;
import com.genba.jun.veritecpro04.util.PermissionRequester;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.RealmList;

public class ListActivity extends BaseActivity implements View.OnClickListener {

    private static final int CAMERA = 0;
    private static final int ALBUM = 1;

    private static final int EDIT_PIC = 0;
    private static final int EDIT_MEMO = 1;
    private static final int DELETE = 2;

    private SharedPreferences sp;
    ActItem actItem = new ActItem();
    ExternalStorage strMng = new ExternalStorage();

    String spinnerPs = "GROUP0";

    ListAdapter listAdapter2;
    ListItem lItem = null;

    ListView lv = null;
    ImageButton insertBtn = null;
    ImageButton insertFromAlbumBtn = null;
    ImageButton settingBtn = null;

    Uri saveUri = null;
    Spinner groupSpinner;
    final Context context = this;
    private int SettingResultCode = 00001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        lv = (ListView) findViewById(R.id.picList);
        insertBtn = (ImageButton) findViewById(R.id.pins);
        insertFromAlbumBtn = (ImageButton) findViewById(R.id.ains);
        insertBtn.setOnClickListener(this);
        insertFromAlbumBtn.setOnClickListener(this);
        settingBtn = (ImageButton) findViewById(R.id.imageButton3);
        settingBtn.setOnClickListener(this);
        groupSpinner = (Spinner) findViewById(R.id.spinner);
        realmManager.DataCheck();
        setView();
    }


    private void setView() {
        //データ取得してスピナーにセット
        ArrayList<String> groupArray = getGroupList();
        String[] arr = groupArray.toArray(new String[groupArray.size()]);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arr);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupSpinner.setAdapter(adapter);

        groupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);

                spinnerPs = parent.getItemAtPosition(position).toString();
                Log.i("TEST:", "Spinner Selected : " + spinnerPs);
                drawList(spinnerPs);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //リスト項目タッチリスナー
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lItem = (ListItem) parent.getItemAtPosition(position);
                final String name = lItem.getName();
                final String path = lItem.getImagePath();
                final String text = lItem.getContents();
                final String itemNo = lItem.getItemNo();

                Intent intent = new Intent(ListActivity.this, EditActivity.class);

                intent.putExtra("flg", actItem.FLG_EDIT);
                intent.putExtra("text", text);
                intent.putExtra("savePath", path);
                intent.putExtra("pk", name);
                intent.putExtra("groupName", spinnerPs);
                intent.putExtra("itemNo", itemNo);
                startActivity(intent);
            }
        });


        //リスト項目長押しリスナー
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                lItem = (ListItem) parent.getItemAtPosition(position);
                final String name = lItem.getName();
                final String path = lItem.getImagePath();
                final String text = lItem.getContents();

                //ポップアップ項目
                final CharSequence[] items = {"写真撮影", "テキスト編集", "削除"};
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                alertDialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        //再撮影
                        if (id == EDIT_PIC) {
                            Intent intentSht = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intentSht, actItem.ADD_PIC_FROM_SHOOTING_OLD);
                        }

                        //コメント修正
                        if (id == EDIT_MEMO) {
                            Intent intent = new Intent(ListActivity.this, EditActivity.class);
                            intent.putExtra("flg", actItem.FLG_EDIT);
                            intent.putExtra("text", text);
                            intent.putExtra("savePath", path);
                            intent.putExtra("pk", name);
                            intent.putExtra("groupName", spinnerPs);
                            startActivity(intent);
                        }

                        //削除
                        if (id == DELETE) {
//                            dbHelper.delete(Integer.parseInt(name));
                            drawList(spinnerPs);
                        }
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return true;
            }
        });

        //リスト更新
        drawList(spinnerPs);

        //リスト項目スワイプリスナー
        SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(
                lv,
                new SwipeDismissListViewTouchListener.DismissCallbacks() {
                    @Override
                    public boolean canDismiss(int position) {
                        return true;
                    }

                    @Override
                    public void onDismiss(ListView listView, int[] reverseSortedPositions) {

                        for (int position : reverseSortedPositions) {


                            ListItem item = listAdapter2.getItem(position);
                            deleteItem(item.getItemNo());
                            fileUtil.deleteFile(new File(Uri.parse(item.getImagePath()).getPath())); //イメージファイル削除
                            fileUtil.deleteFile(new File(Uri.parse(item.getTextPath()).getPath())); //テキストファイル削除
                            fileUtil.removeLineFromFile(extPath + rootDir + "/" + spinnerPs + "/sort.txt", item.getImageName()); //テキストファイル
                        }
                        drawList(spinnerPs);
                    }
                });
        lv.setOnTouchListener(touchListener);
        lv.setOnScrollListener(touchListener.makeScrollListener());
    }

    @Override
    protected void onResume() {
        drawList(spinnerPs);
        super.onResume();
    }

    /**
     * リスト項目更新メソッド
     */
    private void drawList(String group) {

        ListAdapter listAdapter = new ListAdapter();
        Object test = groupSpinner.getSelectedItem();
        RealmList<GroupItemObject> list = getItems(groupSpinner.getSelectedItem().toString());
        for (GroupItemObject obj : list) {
            Drawable reDraPic = actItem.resizing(obj.getImagePath());
            listAdapter.addItem(reDraPic, obj.getTime(), obj.getTextContent(), obj.getImagePath(), obj.getTextPath(), obj.getItemNo(), obj.getImageName());
        }
        lv.setAdapter(listAdapter);
        listAdapter2 = listAdapter;
    }


    /**
     * 追加ボタン処理メソッド
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.ains:
                Intent intentAlb = new Intent(Intent.ACTION_PICK);
                intentAlb.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intentAlb, actItem.ADD_PIC_FROM_ALBUM);
                break;


            case R.id.pins:


                if (isExistsCameraApplication()) {
                    long dateTaken = System.currentTimeMillis();
                    //String filename = DateFormat.format("yyyy", dateTaken).toString() + ".jpg";
                    String filename = DateFormat.format("yyyy", dateTaken).toString() + ".jpg";

                    //▼テスト中
                    ContentResolver contentResolver = getContentResolver();
                    ContentValues values = new ContentValues(5);
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                    values.put(MediaStore.Images.Media.DATE_MODIFIED, System.currentTimeMillis() / 1000);
                    values.put(MediaStore.Images.Media.TITLE, filename);
                    values.put(MediaStore.Images.Media.DISPLAY_NAME, filename);
                    values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
                    Uri pictureUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                    if (sp == null) sp = getPreferences(MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("pictureUri", pictureUri.toString());
                    editor.commit();
                    //▲テスト中

                    Intent intent = new Intent();
                    intent.setAction("android.media.action.IMAGE_CAPTURE");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);

                    //再確認
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                    startActivityForResult(intent, actItem.ADD_PIC_FROM_SHOOTING_TEST);
                }


                break;
            case R.id.imageButton3:
                startActivityForResult(new Intent(this, SettingActivity.class), SettingResultCode);
                break;
            default:
                break;
        }
    }


    public String getPathFromUri(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToNext();
        String path = cursor.getString(cursor.getColumnIndex("_data"));
        cursor.close();

        return path;
    }


    /**
     * 戻り値処理メソッド
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == actItem.ADD_PIC_FROM_SHOOTING_TEST) {

            // 正しい結果が得られなかった場合の処理
            if (resultCode != RESULT_OK) {
                if (sp == null) sp = getPreferences(MODE_PRIVATE);
                Uri tmpUri = Uri.parse(sp.getString("pictureUri", ""));
                if (tmpUri != null) {
                    ContentResolver contentResolver = getContentResolver();
                    try {
                        contentResolver.delete(tmpUri, null, null);
                    } catch (Exception e) {
                        // 対象ファイルがない場合エラー
                    }
                    sp.edit().remove("pictureUri");
                }
                return;
            }

            // 撮影成功時の処理
            Uri resultUri = null;
            if (sp == null) {
                sp = getPreferences(MODE_PRIVATE);
            }
            if (data != null && data.getData() != null) {
                resultUri = data.getData();
            } else {
                resultUri = Uri.parse(sp.getString("pictureUri", ""));
            }

            Intent intentEdit = new Intent(ListActivity.this, EditActivity.class);
            intentEdit.putExtra("saveFileUri", resultUri.toString());

            File rinziF = new File(getPathFromUri(resultUri));

            File targetRoot = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), spinnerPs + "/");
            //String targetPath = targetRoot.toString() + "/" + rinziF.getName();

            String targetPath = extPath + "/" + rinziF.getName();

            Log.i("COPY PATH :", targetPath);


            actItem.copyFile(rinziF, targetPath);
            intentEdit.putExtra("orinImagePath", rinziF);
            intentEdit.putExtra("savePath", targetPath);
            intentEdit.putExtra("flg", requestCode);
            intentEdit.putExtra("group", spinnerPs);
            intentEdit.putExtra("groupName", spinnerPs);
            startActivity(intentEdit);

        } else if (requestCode == actItem.ADD_PIC_FROM_ALBUM && resultCode == RESULT_OK) {
            Intent intentEdit = new Intent(ListActivity.this, EditActivity.class);
            intentEdit.putExtra("saveFileUri", data.getDataString());
            Uri uri = data.getData();


            File rinziF = new File(getPathFromUri(uri));

            File targetRoot = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), spinnerPs + "/");

            intentEdit.putExtra("orinImagePath", rinziF);
            intentEdit.putExtra("savePath", getPathFromUri(uri));
            intentEdit.putExtra("flg", requestCode);
            intentEdit.putExtra("group", spinnerPs);
            intentEdit.putExtra("groupName", spinnerPs);
            startActivity(intentEdit);
        } else if (requestCode == SettingResultCode) {
            //データ取得してスピナーにセット
            if (groupSpinner.getAdapter() != null) {
                groupSpinner.setAdapter(null);
            }
            ArrayList<String> groupArray = getGroupList();
            String[] arr = groupArray.toArray(new String[groupArray.size()]);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arr);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            groupSpinner.setAdapter(adapter);

            drawList(spinnerPs);

        }
    }


    //---------------------------------------------------------------------------------


    /**
     * カメラアプリが設置されているか確認
     */
    private boolean isExistsCameraApplication() {
        PackageManager packageManager = getPackageManager();

        // Camera Application
        Intent cameraApp = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        List<ResolveInfo> cameraApps = packageManager.queryIntentActivities(cameraApp, PackageManager.MATCH_DEFAULT_ONLY);

        return cameraApps.size() > 0;
    }


}
