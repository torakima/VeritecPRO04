package com.example.jun.veritecpro04.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.example.jun.veritecpro04.BaseActivity;
import com.example.jun.veritecpro04.R;
import com.example.jun.veritecpro04.data.RealmManager;
import com.example.jun.veritecpro04.setting.adapter.GroupRecyclerAdapter;

public class GroupRenameActivity extends BaseActivity implements GroupRecyclerAdapter.OnRecyclerListener {


    // RecyclerViewとAdapter
    private RecyclerView mRecyclerView = null;
    private GroupRecyclerAdapter mAdapter = null;
    RealmManager realmManager = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_rename_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        realmManager = new RealmManager();
        realmManager.RealmInitilize();
        mAdapter = new GroupRecyclerAdapter(this, this, realmManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onRecyclerClicked(View v, int position) {

    }
}
