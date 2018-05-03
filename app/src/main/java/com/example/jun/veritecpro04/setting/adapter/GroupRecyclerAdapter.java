package com.example.jun.veritecpro04.setting.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.jun.veritecpro04.R;
import com.example.jun.veritecpro04.data.ActItem;
import com.example.jun.veritecpro04.data.ItemObject;
import com.example.jun.veritecpro04.data.RealmManager;
import com.example.jun.veritecpro04.util.FileUtil;

import java.util.ArrayList;

import io.realm.RealmResults;

public class GroupRecyclerAdapter extends RecyclerView.Adapter<GroupRecyclerAdapter.ViewHolder> {
    private LayoutInflater mInflater;
    private RealmResults<ItemObject> mData;
    private Context mContext;
    private OnRecyclerListener mListener;
    RealmManager realmManager = null;
    ArrayList<String> msgArray = new ArrayList<>();


    public GroupRecyclerAdapter(Context context, OnRecyclerListener listener, RealmManager realmManager) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
//        mData = data;
        mListener = listener;
        this.realmManager = realmManager;
        mData = realmManager.getGroupListResult();
        for (int i = 0; i < mData.size(); i++) {
            msgArray.add(mData.get(i).getGroupName());
        }

    }

    @Override
    public GroupRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        // 表示するレイアウトを設定
        return new ViewHolder(mInflater.inflate(R.layout.list_item, viewGroup, false));
    }

    String mag = null;

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int index) {
        // データ表示
        final ItemObject item = mData.get(index);
        if (mData != null && mData.size() > index && mData.get(index) != null) {

            viewHolder.editView.setText(item.getGroupName());
            viewHolder.editView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    viewHolder.textBtn.setVisibility(View.VISIBLE);
                    msgArray.set(index, charSequence.toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            viewHolder.textBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }

        // クリック処理
        viewHolder.textBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String extPath = ActItem.getSdCardFilesDirPathListForLollipop(mContext);
                new FileUtil().renameFolder(extPath + "/" + item.getGroupName(), extPath + "/" + msgArray.get(index));
                realmManager.updateImagePath(item.getGroupName(), msgArray.get(index));
                viewHolder.textBtn.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public int getItemCount() {
        return msgArray.size();
    }


    // ViewHolder(固有ならインナークラスでOK)
    class ViewHolder extends RecyclerView.ViewHolder {

        EditText editView;
        Button textBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            editView = (EditText) itemView.findViewById(R.id.list_item_text);
            textBtn = (Button) itemView.findViewById(R.id.list_item_btn);
        }
    }

    public interface OnRecyclerListener {

        void onRecyclerClicked(View v, int position);

    }
}
