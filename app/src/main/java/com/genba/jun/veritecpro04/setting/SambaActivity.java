package com.genba.jun.veritecpro04.setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.genba.jun.veritecpro04.data.RealmManager;
import com.genba.jun.veritecpro04.smb.config.IConfig;
import com.genba.jun.veritecpro04.smb.config.SambaHelper;
import com.genba.jun.veritecpro04.smb.config.SambaUtil;
import com.genba.jun.veritecpro04.smb.jcifs.smb.SmbAuthException;
import com.genba.jun.veritecpro04.smb.jcifs.smb.SmbFile;
import com.genba.jun.veritecpro04.util.FileUtil;

/**
 * Created by ram on 15/1/20.
 */
public class SambaActivity extends AppCompatActivity {

    protected final static String TAG = SambaHelper.TAG;
    protected final static String LOCAL_FOLDER_PATH = "/test/samba";
    protected final static String REMOTE_PARENT = "   ... ";
    protected final static String REMOTE_FOLDER_PREFIX = " > ";
    protected final static String REMOTE_FILE_PREFIX = "    ";
    protected final static int REQUEST_CODE_CHOOSE_IMAGE = 1234;
    protected final static int REQUEST_CODE_CHOOSE_VIDEO = 1235;
    protected SmbFile EMPTY_REMOTE_FILE;

    protected IConfig mConfig;
    protected Map<String, SmbFile> REMOTE_PATHS = new LinkedHashMap<>();
    protected String curRemoteFolder;
    protected String curRemoteFile;
    public ArrayList<String> customRoot = new ArrayList<>();
    public RealmManager realmManager = new RealmManager();
    protected int totalUploadCount = 0;
    protected int uploadedCount = 0;
    private ProgressDialog progressDialog;
    private AlertDialog.Builder dialogBuilder;
    FileUtil fileUtil;
    protected boolean isMemory = false;
    protected String tempCurRemoteFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realmManager.RealmInitilize();
        mConfig = new DefaultConfig();
        try {
            EMPTY_REMOTE_FILE = new SmbFile("");
        } catch (Exception e) {
        }
    }

    protected boolean listAndPrepare(String path) {
        try {
            List<SmbFile> FILES = SambaHelper.listFiles(mConfig, path);
            Map<String, SmbFile> MAP = SmbFileToMap(FILES);

            if (MAP.size() == 0) {
                return false;
            } else {
                prepareCurrentMap(MAP);
                return true;
            }
        } catch (Exception e) {
            handleException(e);
        }
        return false;
    }

    protected void prepareCurrentMap(Map<String, SmbFile> map) {
        if (map == null || map.isEmpty()) {
            return;
        }
        synchronized (REMOTE_PATHS) {
            REMOTE_PATHS.clear();
            REMOTE_PATHS.put(REMOTE_PARENT, EMPTY_REMOTE_FILE);
            REMOTE_PATHS.putAll(map);
        }
    }


    protected final Map<String, SmbFile> SmbFileToMap(List<SmbFile> files) {
        if (files == null || files.isEmpty()) {
            return null;
        }
        Map<String, SmbFile> FILE = new LinkedHashMap<>();
        Map<String, SmbFile> FOLDER = new LinkedHashMap<>();
        for (SmbFile file : files) {
            try {
                final String path = removeHost(file.getPath());
                if (file.isDirectory()) {
                    FOLDER.put(new StringBuilder(REMOTE_FOLDER_PREFIX).append(path).toString(), file);
                } else {
                    FILE.put(new StringBuilder(REMOTE_FILE_PREFIX).append(path).toString(), file);
                }
            } catch (Exception e) {
                e.printStackTrace();
                FILE.put("  | " + file.getPath(), file);
            }
        }
        FOLDER.putAll(FILE);
        return FOLDER;
    }

    protected final String removeHost(String path) {
        if (TextUtils.isEmpty(path)) {
            return path;
        }
        if (path.startsWith("smb://")) {
            path = path.replace("smb://", "");
        }
        if (!path.contains("/")) {
            return path;
        }
        int index = path.indexOf("/");
        path = path.substring(index);
        return path;
    }


    protected final void upload(final String path, final String targetFolder, final Boolean isSortText) {
        new Thread() {
            @Override
            public void run() {

                boolean result = false;
                try {
                    result = SambaHelper.upload(mConfig, path, curRemoteFolder + targetFolder);
                } catch (Exception e) {
                    handleException(e);
                }
                updateResult("upload", path + " " + String.valueOf(result).toUpperCase());
                if (isSortText) fileUtil.deleteFile(new File(path));

                onUploadResult(curRemoteFolder, result);
            }
        }.start();///folder
    }

    protected void onRemoteFolderChange(String path, boolean result) {
        //TODO by child
    }

    protected void onUploadResult(String path, boolean result) {
        //TODO by child
        uploadedCount = uploadedCount + 1;
        Log.d("sdFasfasdfads", uploadedCount + "");
        if (totalUploadCount == uploadedCount) {
            hideProgress();
            if (!isMemory) {
                try {
                    listAndPrepare(customRoot.get(customRoot.size() - 1));
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            } else {
                isMemory = false;
                curRemoteFolder = tempCurRemoteFolder;
            }

            updateResult("upLoadComplete", "upLoadComplete");
        }
    }

    protected void listRoot() {
        curRemoteFolder = SambaUtil.getSmbRootURL(mConfig);
    }

    protected void createFolder(final String name) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean result = false;
                try {
                    result = SambaHelper.createFolder(mConfig, curRemoteFolder, name);
                } catch (Exception e) {
                    handleException(e);
                }
                onRemoteFolderChange(curRemoteFolder, result);
                updateResult("createFolder", SambaUtil.wrapSmbFileUrl(curRemoteFolder, name) + "       " + String.valueOf(result).toUpperCase());
            }
        }).start();
    }

    protected void updateResult(final String action, final String msg) {

    }


    public void startDialog(int count) {
        totalUploadCount = count + 1;
        showProgress();
    }


    /**
     * MalformedURLException
     * SmbException
     */
    protected void handleException(Exception e) {
        e.printStackTrace();
        updateResult("ERROR", e.getClass().getSimpleName() + ": \"" + e.getMessage() + "\"");
        if (e instanceof SmbAuthException) {
            updateResult("handleException", "AUTH ERROR!!! " + e.getMessage());
        }
    }

    public void showProgress() {
        if (progressDialog != null) {
            progressDialog = null;
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("データ送信中");
        progressDialog.show();
    }


    public void hideProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = null;
        uploadedCount = 0;
    }

    public void showDialog(String msg, Boolean error) {
        if (dialogBuilder == null) {
            dialogBuilder = new AlertDialog.Builder(this);
        }
        if (error) dialogBuilder.setTitle("エラー");
        else dialogBuilder.setTitle("確認");
        dialogBuilder.setMessage(msg);
        dialogBuilder.setPositiveButton("OK", null);
        dialogBuilder.show();

    }
}
