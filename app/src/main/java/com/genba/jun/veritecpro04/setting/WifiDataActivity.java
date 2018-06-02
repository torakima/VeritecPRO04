package com.genba.jun.veritecpro04.setting;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.Spinner;
import android.widget.TextView;

import com.genba.jun.veritecpro04.BaseActivity;
import com.genba.jun.veritecpro04.R;
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

import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmResults;

public class WifiDataActivity extends SambaActivity implements IConfig.OnConfigListener, View.OnClickListener {


    // RecyclerViewとAdapter
    private RadioGroup mGroup = null;
    private RealmResults<ItemObject> mData;
    private LayoutInflater inflater;
    private ListView folerListView;
    protected FolderAdapter folderAdapter;
    private String ROOT_TEXT = "   ... ";
    private Boolean isRoot = true;
    private String backFolder = "BACK_FOLDER";
    private String folderName;
    public RealmManager realmManager = new RealmManager();
    private Button connectBtn, clearBtn, sendBtn;
    private EditText ipView, userView, passwordView;
    public String extPath;
    public String rootDir = "/Genba";
    int groupPosiotion = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_trans_main);
        mGroup = (RadioGroup) findViewById(R.id.group_list);
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) groupPosiotion = getIntent().getExtras().getInt("position");
        folerListView = (ListView) findViewById(R.id.folder_list);
        realmManager.RealmInitilize();
        setGroupList();
        init();
    }

    private void init() {
        extPath = FileUtil.getExternalStoragePath(this);

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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send_btn:
                if (folderName == null) {
                    showDialog("グループを選択してください。", false);
                    return;
                }
                createFolder(folderName);
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
                    upload(item, folderName);
                }
                upload(extPath + rootDir + "/" + folderName + "/sort.txt", folderName);
                break;
            case R.id.connect_btn:
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

    private void setGroupList() {
        mData = realmManager.getGroupListResult();
        for (int i = 0; i < mData.size(); i++) {
            RadioButton rad = (RadioButton) inflater.inflate(R.layout.group_radio, null);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER_VERTICAL;
            rad.setText(mData.get(i).getGroupName());

            mGroup.addView(rad);
            mGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
                    boolean isChecked = checkedRadioButton.isChecked();
                    if (isChecked) {
                        folderName = checkedRadioButton.getText().toString();
                    }
                }
            });
            if (groupPosiotion == i) {
                rad.setChecked(true);
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initFolderListView(final String path) {
        if (!sendBtn.isEnabled()) {
            sendBtn.setEnabled(true);
        }
        folderAdapter = new FolderAdapter(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (listAndPrepare(path)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            if (folderAdapter != null) {
//                                folderAdapter.clear();
//                            }
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
        connectBtn.setEnabled(false);
        clearBtn.setEnabled(false);
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
                if (action.equals("ERROR")) {
                    if (!msg.contains("NullPointerException") && !msg.contains(" path specified")) {
                        showDialog(msg, true);
                    }
                    if (msg.contains("SmbException")) {
                        connectBtn.setEnabled(true);
                        clearBtn.setEnabled(true);
                        sendBtn.setEnabled(false);
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
}
