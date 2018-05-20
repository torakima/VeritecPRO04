package com.example.jun.veritecpro04.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jun.veritecpro04.R;
import com.example.jun.veritecpro04.data.ListItem;

import java.util.ArrayList;

/**
 * Created by JUN on 2017/10/23.
 */

public class ListAdapter extends BaseAdapter{

    //Item Array
    private ArrayList<ListItem> mItems = new ArrayList<>();

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public ListItem getItem(int position) {
        return mItems.get(position);
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Context context = parent.getContext();
        //Custom listviewーのレイアウトをinflate
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_custom, parent, false);
        }

        //各部品の参照先作成
        ImageView iv_img = (ImageView) convertView.findViewById(R.id.iv_img) ;
        TextView tv_name = (TextView) convertView.findViewById(R.id.tv_name) ;
        TextView tv_contents = (TextView) convertView.findViewById(R.id.tv_contents) ;

        //内容取得
        ListItem myItem = getItem(position);

        //各部品にデータセット
        iv_img.setImageDrawable(myItem.getIcon());
        tv_name.setText(myItem.getName());
        tv_contents.setText(myItem.getContents());

        return convertView;
    }

    //データ追加のメソッド
    public void addItem(Drawable img, String name, String contents, String imagePath, String textPath, String itemNo ,String imageName) {

        ListItem mItem = new ListItem();

        mItem.setIcon(img);
        mItem.setName(name);
        mItem.setContents(contents);
        mItem.setImagePath(imagePath);
        mItem.setTextPath(textPath);
        mItem.setItemNo(itemNo);
        mItem.setImageName(imageName);
        mItems.add(mItem);
    }
}
