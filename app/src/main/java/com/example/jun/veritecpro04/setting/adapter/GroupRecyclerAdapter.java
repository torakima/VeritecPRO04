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
import com.example.jun.veritecpro04.data.ItemObject;
import com.example.jun.veritecpro04.data.RealmManager;

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

            viewHolder.textView.setText(item.getGroupName());
            viewHolder.textView.addTextChangedListener(new TextWatcher() {
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
                realmManager.updateGroupName(item, msgArray.get(index));
//                mListener.onRecyclerClicked(v, i);

            }
        });

    }

    @Override
    public int getItemCount() {
        return 10;
    }


    // ViewHolder(固有ならインナークラスでOK)
    class ViewHolder extends RecyclerView.ViewHolder {

        EditText textView;
        Button textBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (EditText) itemView.findViewById(R.id.list_item_text);
            textBtn = (Button) itemView.findViewById(R.id.list_item_btn);
        }
    }

//    private class MyCustomEditTextListener implements TextWatcher {
//        private int position;
//
//        public void updatePosition(int position) {
//            this.position = position;
//        }
//
//        @Override
//        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
//            // no op
//        }
//
//        @Override
//        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
////            mDataset[position] = charSequence.toString();
//        }
//
//        @Override
//        public void afterTextChanged(Editable editable) {
//            // no op
//        }
//    }

    public interface OnRecyclerListener {

        void onRecyclerClicked(View v, int position);

    }
}
