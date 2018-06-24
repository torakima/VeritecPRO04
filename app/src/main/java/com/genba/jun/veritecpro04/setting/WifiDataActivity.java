package com.genba.jun.veritecpro04.setting;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.genba.jun.veritecpro04.BaseActivity;
import com.genba.jun.veritecpro04.R;
import com.genba.jun.veritecpro04.adapter.OnRecyclerListener;
import com.genba.jun.veritecpro04.data.ActItem;
import com.genba.jun.veritecpro04.data.GroupItemObject;
import com.genba.jun.veritecpro04.data.ItemObject;
import com.genba.jun.veritecpro04.data.RealmManager;
import com.genba.jun.veritecpro04.data.WifiObject;
import com.genba.jun.veritecpro04.setting.adapter.GroupRecyclerAdapter;
import com.genba.jun.veritecpro04.setting.adapter.WifiGroupRecyclerAdapter;
import com.genba.jun.veritecpro04.smb.config.IConfig;
import com.genba.jun.veritecpro04.smb.config.SambaUtil;
import com.genba.jun.veritecpro04.smb.jcifs.smb.SmbFile;
import com.genba.jun.veritecpro04.util.FileUtil;

import java.io.File;
import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmResults;

public class WifiDataActivity extends SambaActivity implements IConfig.OnConfigListener, View.OnClickListener, OnRecyclerListener {


    private RealmResults<ItemObject> mData;
    private LayoutInflater inflater;
    private ListView folerListView;
    protected FolderAdapter folderAdapter;
    private String ROOT_TEXT = "   ... ";
    private Boolean isRoot = true;
    private String backFolder = "BACK_FOLDER";
    private String folderName;
    private Button connectBtn, clearBtn, sendBtn;
    private EditText ipView, userView, passwordView;
    public String extPath;
    public String rootDir = "/Genba";
    int groupPosiotion = 0;
    private Boolean isConnect = false;
    // RecyclerViewとAdapter
    private RecyclerView mGroup = null;
    private RecyclerAdapter mAdapter = null;
    public String sortTxt = "/sort.txt";
    private long mLastClickTime;
    private static final long MIN_CLICK_INTERVAL = 600;
    File sortFile = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_trans_main);
        fileUtil = new FileUtil();
        mGroup = (RecyclerView) findViewById(R.id.group_list);
        // レイアウトマネージャを設定(ここで縦方向の標準リストであることを指定)
        mGroup.setLayoutManager(new LinearLayoutManager(this));
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) groupPosiotion = getIntent().getExtras().getInt("position");
        folerListView = (ListView) findViewById(R.id.folder_list);
        setGroupList();
        init();
    }

    private void init() {
        setExtRoot();
        WifiObject userInfo = realmManager.getWifiUser();
        sendBtn = (Button) findViewById(R.id.send_btn);
        connectBtn = (Button) findViewById(R.id.connect_btn);
        clearBtn = (Button) findViewById(R.id.clear_btn);
        ipView = (EditText) findViewById(R.id.ip_address_edit);
        userView = (EditText) findViewById(R.id.user_edit);
        passwordView = (EditText) findViewById(R.id.password_edit);
        sendBtn.setOnClickListener(this);
        connectBtn.setOnClickListener(this);
        clearBtn.setOnClickListener(this);
        if (userInfo != null) {
            ipView.setText(userInfo.getIpAddress());
            userView.setText(userInfo.getUser());
            passwordView.setText(userInfo.getPassword());
        }
    }

    public void setExtRoot() {
        if (realmManager.getSetting().isSaveDirection()) {
            extPath = ActItem.getSdCardFilesDirPathListForLollipop(this);
        } else {
            extPath = FileUtil.getExternalStoragePath(this);
        }
    }

    @Override
    public void onClick(View view) {
        long currentClickTime = SystemClock.uptimeMillis();
        long elapsedTime = currentClickTime - mLastClickTime;
        mLastClickTime = currentClickTime;

        if (elapsedTime <= MIN_CLICK_INTERVAL) {
            return;
        }
        switch (view.getId()) {
            case R.id.send_btn:
                isConnect = false;
                if (folderName == null) {
                    showDialog("グループを選択してください。", false);
                    return;
                }
                if (customRoot.size() <= 1) {
                    showDialog("rootフォルダーです。", false);
                    return;
                }

                sendData(false, null);
//                RealmList<GroupItemObject> uploadList = realmManager.getGroup(folderName);
//                StringBuilder sort = new StringBuilder();
//                for (GroupItemObject obj : uploadList) {
//                    sort.append(obj.getImageName());
//                    sort.append(System.lineSeparator());
//                }
//                sortFile = fileUtil.makeFile(extPath + rootDir + File.separator + folderName + sortTxt);
//
////                    if (sortFile != null) {
//                fileUtil.writeSortFile(sortFile, sort.toString());
//                createFolder(folderName);

                break;
            case R.id.connect_btn:
                connectBtn.setEnabled(false);
                String ipAdd = ipView.getText().toString();
                String userName = userView.getText().toString();
                String password = passwordView.getText().toString();
                if (ipAdd.isEmpty()) {
                    showDialog("ip Addressを入力してください", true);
                    return;
                }
                if (userName.isEmpty()) {
                    showDialog("user名を入力してください", true);
                    return;
                }
                if (password.isEmpty()) {
                    showDialog("passwordを入力してください", true);
                    return;
                }
                customRoot = new ArrayList<>();
                mConfig = new IConfig(ipAdd, userName, password, "");
                listRoot();
                break;
            case R.id.clear_btn:
                ipView.setText("");
                userView.setText("");
                passwordView.setText("");
                break;
        }
    }

    private void sendData(boolean isMemoryClick, String isremoteFolder) {
        if (curRemoteFolder == null) {
            showDialog("check login", true);
            return;
        }
        RealmList<GroupItemObject> uploadList = realmManager.getGroup(folderName);
        StringBuilder sort = new StringBuilder();
        for (GroupItemObject obj : uploadList) {
            sort.append(obj.getImageName());
            sort.append(System.lineSeparator());
        }
        sortFile = fileUtil.makeFile(extPath + rootDir + File.separator + folderName + sortTxt);

//                    if (sortFile != null) {
        fileUtil.writeSortFile(sortFile, sort.toString());
        if (isMemoryClick) {
            isMemory = true;
            createFolder(folderName);
            tempCurRemoteFolder = curRemoteFolder;
            curRemoteFolder = isremoteFolder;
        } else createFolder(folderName);
    }

    private void setGroupList() {
        mData = realmManager.getGroupListResult();
        folderName = mData.get(groupPosiotion).getGroupName();
        mAdapter = new RecyclerAdapter(this, mData, this, groupPosiotion);
        mGroup.setAdapter(mAdapter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mConfig = null;
    }

    private void initFolderListView(final String path) {
        if (!sendBtn.isEnabled()) {
            sendBtn.setEnabled(true);
        }
        if (folderAdapter != null) {
            folderAdapter.clear();
        }
        folderAdapter = new FolderAdapter(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (listAndPrepare(path)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            connectBtn.setEnabled(true);
                            folderAdapter.setFolderList(new ArrayList(REMOTE_PATHS.keySet()));
                            folerListView.setAdapter(folderAdapter);
                            folerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                                    if (!isRoot && position == 0) {
                                        onFileSelected(backFolder);
                                    } else {
                                        String name = folderAdapter.getItem(position);
                                        onFileSelected(name);
                                    }

                                }
                            });
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ArrayList<String> arrayList = new ArrayList<>();
                            arrayList.add(REMOTE_PARENT);
                            folderAdapter.setFolderList(arrayList);
                            folerListView.setAdapter(folderAdapter);
                            folerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                                    if (!isRoot && position == 0) {
                                        onFileSelected(backFolder);
                                    }
                                }
                            });
                        }
                    });
                }
            }
        }).start();
    }

    private void onFileSelected(String name) {
        if (name.equals(REMOTE_PARENT)) {
            if (curRemoteFolder != null) {
            }
            return;
        } else isRoot = false;
        if (name.equals(backFolder)) {
            if (customRoot.size() < 1) {
                listRoot();
            } else {
                StringBuilder tempCurrentFolder = new StringBuilder();
                customRoot.remove(customRoot.size() - 1);
                for (int i = 0; i < customRoot.size(); i++) {
                    tempCurrentFolder.append(customRoot.get(i));
                }
                String deleteString = "/" + REMOTE_FOLDER_PREFIX;
                curRemoteFolder = tempCurrentFolder.toString().replace(deleteString, "");
                initFolderListView(curRemoteFolder);
            }

            return;
        }
        if (customRoot.size() == 0) {
            customRoot.add(SambaUtil.getSmbRootURL(mConfig));
        }
        customRoot.add(name);
        SmbFile file = REMOTE_PATHS.get(name);
        if (name.startsWith(REMOTE_FOLDER_PREFIX)) {
            curRemoteFolder = file.getPath();
            initFolderListView(curRemoteFolder);
        } else if (name.startsWith(REMOTE_FILE_PREFIX)) {
            curRemoteFile = file.getPath();
            curRemoteFolder = file.getParent();
        }
    }


    @Override
    protected void listRoot() {
        super.listRoot();
        isRoot = true;
        realmManager.setWifiUser(mConfig);
//        connectBtn.setEnabled(false);
//        clearBtn.setEnabled(false);
        initFolderListView(curRemoteFolder);
    }


    @Override
    public void onConfig(IConfig config, Object obj) {

    }

    @Override
    protected void updateResult(final String action, final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String ACTION_STR = String.valueOf(action).toUpperCase();
                int MAX_LENGTH = 35;
                int length = ACTION_STR.length();
                final String DIVIDER = "=";
                while (length <= MAX_LENGTH) {
                    ACTION_STR = length % 2 == 0 ? ACTION_STR + DIVIDER : DIVIDER + ACTION_STR;
                    length++;
                }
                StringBuilder builder = new StringBuilder("\n");
                builder.append(ACTION_STR);
                builder.append("\n");
                builder.append(msg);
                connectBtn.setEnabled(true);
                if (action.equals("createFolder")) {
                    RealmList<GroupItemObject> uploadList = realmManager.getGroup(folderName);
                    if (uploadList.size() == 0) {
                        showDialog("データがありません", false);
                        return;
                    }
                    ArrayList<String> itemsPath = new ArrayList<>();
                    for (GroupItemObject item : uploadList) {
                        itemsPath.add(item.getImagePath());
                        itemsPath.add(item.getTextPath());
                    }
                    startDialog(itemsPath.size());
                    for (String item : itemsPath) {
                        upload(item, folderName, false);
                    }
                    upload(extPath + rootDir + File.separator + folderName + sortTxt, folderName, true);

                } else if (action.equals("ERROR")) {
                    if (!msg.contains("NullPointerException") && !msg.contains(" path specified")) {
                        showDialog(msg, true);
                    }
                    if (msg.contains("SmbAuthException")) {
//                        connectBtn.setEnabled(true);
//                        clearBtn.setEnabled(true);
//                        sendBtn.setEnabled(false);
                    }
                }
                if (action.equals("upLoadComplete")) {
                    try {
                        if (realmManager.setTrasFolder(folderName, customRoot.get(customRoot.size() - 1), curRemoteFolder)) {
                            setGroupList();
                        }
                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }

                }
                Log.d(TAG, "updateResult    " + builder);
            }
        });
    }

    public class FolderAdapter extends BaseAdapter {
        Context context;
        LayoutInflater layoutInflater = null;
        ArrayList<String> folderList;

        public FolderAdapter(Context context) {
            this.context = context;
            this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void setFolderList(ArrayList<String> folderList) {
            this.folderList = folderList;
        }

        @Override
        public int getCount() {
            return folderList.size();
        }

        @Override
        public String getItem(int position) {
            return folderList.get(position);
        }

        public void clear() {
            if (folderList != null) {
                folderList.clear();
                notifyDataSetChanged();
            }
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = layoutInflater.inflate(R.layout.folder_item, parent, false);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.folder_image);
            if (folderList.get(position).equals(ROOT_TEXT)) {
                imageView.setVisibility(View.GONE);
                ((TextView) convertView.findViewById(R.id.folder_name)).setText(folderList.get(position));
            } else {
                Boolean isFolder = folderList.get(position).startsWith(REMOTE_FOLDER_PREFIX);
                if (isFolder) {
                    imageView.setBackgroundResource(R.drawable.icons_folder_100);
                    if (customRoot.size() < 1) {
                        ((TextView) convertView.findViewById(R.id.folder_name)).setText(folderList.get(position).replace("/", "").replace(REMOTE_FOLDER_PREFIX, ""));
                    } else {
                        String setText = folderList.get(position).replace(customRoot.get(customRoot.size() - 1), "");
                        ((TextView) convertView.findViewById(R.id.folder_name)).setText(setText.replace("/", "").replace(REMOTE_FOLDER_PREFIX, ""));
                    }
                } else {
                    if (customRoot.size() < 1) {
                        ((TextView) convertView.findViewById(R.id.folder_name)).setText(folderList.get(position).replace("/", "").replace(REMOTE_FOLDER_PREFIX, ""));
                    } else {
                        String setText = folderList.get(position).replaceAll(customRoot.get(customRoot.size() - 1).replace(REMOTE_FOLDER_PREFIX, REMOTE_FILE_PREFIX), "");
                        ((TextView) convertView.findViewById(R.id.folder_name)).setText(setText.replaceAll("/", "").replaceAll(REMOTE_FOLDER_PREFIX, ""));
                    }
                    imageView.setBackgroundResource(R.drawable.icons_file);
                }
            }
            return convertView;
        }

    }

    @Override
    public void onRecyclerClicked(View v, String name, int lastSelectedPosition) {
        folderName = name;
    }

    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

        private LayoutInflater mInflater;
        private RealmResults<ItemObject> items;
        private Context mContext;
        private OnRecyclerListener mListener;
        private int lastSelectedPosition = -1;
        private int groupPosiotion;
        private Boolean firstCheck = true;

        public RecyclerAdapter(Context context, RealmResults<ItemObject> item, OnRecyclerListener listener, int groupPosiotion) {
            mInflater = LayoutInflater.from(context);
            mContext = context;
            items = item;
            mListener = listener;
            this.groupPosiotion = groupPosiotion;
        }

        @Override
        public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            // 表示するレイアウトを設定
            return new ViewHolder(mInflater.inflate(R.layout.group_item, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int i) {
            // データ表示
            if (mData != null && mData.size() > i && mData.get(i) != null) {
                viewHolder.radioButton.setText(mData.get(i).getGroupName());
                if (mData.get(i).getDataTrasUrl() != null) {
                    String saveFolder = getString(R.string.save_folder) + mData.get(i).getDataTrasUrl();
                    viewHolder.saved_folder.setText(saveFolder);
                } else {
                    viewHolder.saved_folder.setText("");
                }

                if (firstCheck) {
                    if (groupPosiotion == i) {
                        viewHolder.radioButton.setChecked(true);
                        viewHolder.button.setEnabled(true);
                        firstCheck = false;
                    }
                } else {
                    viewHolder.radioButton.setChecked(lastSelectedPosition == i);
                }
                if (viewHolder.radioButton.isChecked()) {
                    viewHolder.button.setEnabled(true);
                } else {
                    viewHolder.button.setEnabled(false);
                }
                if (mData.get(i).getDataOriginTrasUrl() != null)
                    viewHolder.button.setVisibility(View.VISIBLE);
                else viewHolder.button.setVisibility(View.GONE);

            }

            // クリック処理

        }

        @Override
        public int getItemCount() {
            if (mData != null) {
                return mData.size();
            } else {
                return 0;
            }
        }

        // ViewHolder(固有ならインナークラスでOK)
        class ViewHolder extends RecyclerView.ViewHolder {

            RadioButton radioButton;
            LinearLayout layout;
            TextView saved_folder;
            Button button;

            public ViewHolder(View itemView) {
                super(itemView);
                radioButton = (RadioButton) itemView.findViewById(R.id.groupName);
                layout = (LinearLayout) itemView.findViewById(R.id.layout);
                saved_folder = (TextView) itemView.findViewById(R.id.saved_folder);
                button = (Button) itemView.findViewById(R.id.send_btn);
                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lastSelectedPosition = getAdapterPosition();
                        notifyDataSetChanged();
                        mListener.onRecyclerClicked(v, items.get(lastSelectedPosition).getGroupName(), lastSelectedPosition);
                    }
                });
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lastSelectedPosition = getAdapterPosition();
                        curRemoteFolder = mData.get(lastSelectedPosition).getDataOriginTrasUrl();
                        Boolean isDiretory = false;
                        for (SmbFile value : REMOTE_PATHS.values()) {
                            if (curRemoteFolder.equals(value.getCanonicalPath())) isDiretory = true;
                        }
                        if (!isDiretory) {
                            showDialog("保存先のフォルダーがありません。", true);
                            curRemoteFolder = tempCurRemoteFolder;
                            return;
                        }
                        sendData(true, mData.get(lastSelectedPosition).getDataOriginTrasUrl());
                    }
                });
            }
        }

    }


}
