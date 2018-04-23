package com.example.jun.veritecpro04;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class EditActivity extends AppCompatActivity {

    private SharedPreferences sp;

    ActItem actItem = new ActItem();

    //基本パーツ
    EditText accTxt = null;
    TextView uriView = null;
    ImageView picView = null;

    String group = null;
    String picPath = null;

    Uri uri = null;
    Bitmap bm = null;

    String text;
    String pk;

    /*API26 対応*/
    private String imagePath;

    int lang = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        final DBHelper dbHelper = new DBHelper(getApplicationContext(), "PhotoLib.db", null, 1);

        //ID紐づけ
        uriView = (TextView)findViewById(R.id.uriView);
        picView = (ImageView)findViewById(R.id.imgPreView);
        accTxt = (EditText)findViewById(R.id.accTxt);

        //intent処理
        Intent data = this.getIntent();

        //final String picUri = data.getExtras().getString("saveFileUri");
        picPath = data.getExtras().getString("savePath");
        final int flg = data.getExtras().getInt("flg");
        group = data.getExtras().getString("group");


        /*
        String timestamp = new SimpleDateFormat("yyyyMMdd").format(new Date());

        File file = new File(picPath);
        if(file.exists()){
        }


        String path =file.getParentFile().toString();
        String filename = file.getName();

        File fileNow = new File(path+"/GENBA/"+timestamp+".jpg");

        file.renameTo(fileNow);
        */


        if (flg == actItem.FLG_EDIT){
            text = data.getExtras().getString("text");
            pk = data.getExtras().getString("pk");
            //uriView.setText(data.getExtras().getString("savePath"));
            accTxt.setText(text);
        }

        //日付は現在日付に固定・現在時間取得
        Long now = System.currentTimeMillis();
        Date date = new Date(now);


        //出力フォーマット設定
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年　MM月　dd日");
        final String time = simpleDateFormat.format(date);

        drawFromPath(picPath);

        //ボタン処理‐再撮影
        findViewById(R.id.reShoot).setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                long dateTaken = System.currentTimeMillis();
                String filename = DateFormat.format("yyyy-MM-dd_kk.mm.ss", dateTaken).toString() + ".jpg";

                ContentResolver contentResolver = getContentResolver();
                ContentValues values = new ContentValues(5);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                values.put(MediaStore.Images.Media.DATE_MODIFIED, System.currentTimeMillis()/1000);
                values.put(MediaStore.Images.Media.TITLE, filename);
                values.put(MediaStore.Images.Media.DISPLAY_NAME, filename);
                values.put(MediaStore.Images.Media.DATE_TAKEN,System.currentTimeMillis());
                Uri pictureUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                if(sp == null) sp = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("pictureUri", pictureUri.toString());
                editor.commit();

                //savePath =  getPathFromUri(pictureUri);

                Intent intent = new Intent();
                intent.setAction("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);
                //intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                //intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                startActivityForResult(intent, actItem.ADD_PIC_FROM_SHOOTING_TEST);
            }
        });


        //ボタン処理‐音声入力
        findViewById(R.id.voice).setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                speech();
            }
        });


        findViewById(R.id.clear).setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                accTxt.setText("");
            }
        });



        //ボタン処理‐DB入力
        findViewById(R.id.dbadd).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flg == actItem.FLG_EDIT) {
                    //dbHelper.update(pk, accTxt.getText().toString(), uriView.getText().toString());
                    dbHelper.update(pk, accTxt.getText().toString(), picPath);
                    finish();
                } else {
                    String updateTime = time;
                    dbHelper.insert(time, group, picPath, accTxt.getText().toString());
                    finish();
                }
            }
        });
    }


    /**
     * Image View へ画像表示
     */
    public void drawFromPath (String imgPath){
        BitmapFactory.Options factory = new BitmapFactory.Options();
        factory.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(imgPath);
        factory.inJustDecodeBounds = false;
        factory.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imgPath, factory);

        bm = actItem.imgRotation(bitmap, imgPath);

        picView.setImageBitmap(bm);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == actItem.VOICE_RECOGNITION && resultCode == RESULT_OK) {
            // 認識結果を ArrayList で取得
            ArrayList<String> candidates = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            if (candidates.size() > 0) {
                // 認識結果候補で一番有力なものを表示
                insertText(accTxt,candidates.get(0));
//                accTxt.setText(candidates.get(0));
            }
        }


        if (requestCode == actItem.ADD_PIC_FROM_SHOOTING_TEST) {

            if (resultCode != RESULT_OK) {
                // 正しい結果が得られなかった場合の処理
                // 撮影キャンセルなどするとこっちに来る

                if(sp == null) sp = getPreferences(MODE_PRIVATE);
                Uri tmpUri = Uri.parse(sp.getString("pictureUri", ""));
                if(tmpUri != null){
                    ContentResolver contentResolver = getContentResolver();
                    try{
                        contentResolver.delete(tmpUri, null, null);
                    }catch (Exception e) {
                        // 対象ファイルがない場合エラー
                    }
                    sp.edit().remove("pictureUri");
                }
                return;
            }

            // 撮影成功時の処理
            Uri resultUri = null;
            if(sp == null){
                sp = getPreferences(MODE_PRIVATE);
            }
            if(data != null && data.getData() != null){
                resultUri = data.getData();
            }else{
                resultUri = Uri.parse(sp.getString("pictureUri", ""));
            }

            drawFromPath(getPathFromUri(resultUri));
            //uriView.setText(getPathFromUri(resultUri));
            picPath = getPathFromUri(resultUri);


        }
    }

    private void speech(){
        // 音声認識が使えるか確認する
        try {            // 音声認識の　Intent インスタンス
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

            if(lang == 0){
                // 日本語
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.JAPAN.toString() );
            }
            else if(lang == 2){
                // Off line mode
            }
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 100);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "音声を入力" + lang);
            // インテント発行
            startActivityForResult(intent, actItem.VOICE_RECOGNITION);
        }
        catch (ActivityNotFoundException e) {
//            tv.setText("No Activity " );
        }

    }


    public static void insertText(EditText view, String text)
    {
        // Math.max 는 에초에 커서가 잡혀있지않을때를 대비해서 넣음.
        int s = Math.max(view.getSelectionStart(), 0);
        int e = Math.max(view.getSelectionEnd(), 0);
        // 역으로 선택된 경우 s가 e보다 클 수 있다 때문에 이렇게 Math.min Math.max를 쓴다.
        view.getText().replace(Math.min(s, e), Math.max(s, e), text, 0, text.length());
    }

    public String getPathFromUri(Uri uri){
        Cursor cursor = getContentResolver().query(uri, null,null,null,null);
        cursor.moveToNext();
        String path = cursor.getString(cursor.getColumnIndex("_data"));
        cursor.close();

        return path;
    }

}