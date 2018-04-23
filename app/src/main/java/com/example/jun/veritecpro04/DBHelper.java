package com.example.jun.veritecpro04;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by JUN on 2017/10/06.
 */

public class DBHelper extends SQLiteOpenHelper {

    // DB情報設定
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    // DB?APP?を新しく生成するとき呼び出す★
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE RELATION (_id INTEGER PRIMARY KEY AUTOINCREMENT, folder TEXT, item TEXT, title TEXT, create_at TEXT );");
    }


    // SELECT
    public Cursor getResult(String group) {
        SQLiteDatabase db = getReadableDatabase();
        Log.i("TEST: 선택 쿼리 -","SELECT * FROM RELATION WHERE folder = "+ group);
        Cursor cursor = db.rawQuery("SELECT * FROM RELATION WHERE folder = '" + group + "'" , null);
        return cursor;
    }


    // INSERT
    public void insert(String create_at, String group, String item, String title) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO RELATION VALUES(null, '" + group + "', '" + item + "', '" + title + "', '" + create_at + "');");
        db.close();
    }


    // UPDATE
    public void update(String id, String title, String name) {
        SQLiteDatabase db = getWritableDatabase();
        //if UPDATE分岐
        db.execSQL("UPDATE RELATION SET title = '" + title + "' WHERE _id = '" + id + "';");
        db.execSQL("UPDATE RELATION SET item = '" + name + "' WHERE _id = '" + id + "';");
        db.close();
    }


    // DELETE
    public void delete(long listnum) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM RELATION WHERE _id = " + listnum);
        db.close();
    }


    // DBアップデート:VER変更の時
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
