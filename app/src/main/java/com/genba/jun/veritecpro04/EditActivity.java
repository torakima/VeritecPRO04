package com.genba.jun.veritecpro04;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.text.format.DateFormat;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.genba.jun.veritecpro04.data.ActItem;
import com.genba.jun.veritecpro04.data.GroupItemObject;
import com.genba.jun.veritecpro04.util.FileUtil;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class EditActivity extends BaseActivity {

    private SharedPreferences sp;

    ActItem actItem = new ActItem();

    //基本パーツ
    EditText accTxt = null;
    TextView uriView = null;
    ImageView picView = null;

    String group = null;
    String picPath = null;

    String GroupName = null;

    Uri uri = null;
    Bitmap bm = null;

    String text;
    String pk;
    String itemNo;


    private boolean imageChange = false;
    GroupItemObject itemObj;
    int lang = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
//        final DBHelper dbHelper = new DBHelper(getApplicationContext(), "PhotoLib.db", null, 1);

        //ID紐づけ
        uriView = findViewById(R.id.uriView);
        picView = findViewById(R.id.imgPreView);
        accTxt = findViewById(R.id.accTxt);
        TextView topText = (TextView) findViewById(R.id.uriView);

        //intent処理
        Intent data = this.getIntent();

        //final String picUri = data.getExtras().getString("saveFileUri");
        picPath = data.getExtras().getString("savePath");
        final int flg = data.getExtras().getInt("flg");
        group = data.getExtras().getString("group");
        GroupName = data.getExtras().getString("groupName");


        if (flg == actItem.FLG_EDIT) {
            GroupName = data.getExtras().getString("groupName");
            itemNo = data.getExtras().getString("itemNo");
            itemObj = getItem(GroupName, itemNo);
            text = data.getExtras().getString("text");
            pk = data.getExtras().getString("pk");
            //uriView.setText(data.getExtras().getString("savePath"));
            accTxt.setText(text);
        }

        topText.setText(GroupName);
        //日付は現在日付に固定・現在時間取得
        Long now = System.currentTimeMillis();
        Date date = new Date(now);


        //出力フォーマット設定
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年　MM月　dd日");
        final String time = simpleDateFormat.format(date);

        drawFromPath(picPath);

        //ボタン処理‐再撮影
        findViewById(R.id.reShoot).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                long dateTaken = System.currentTimeMillis();
                String filename = DateFormat.format("yyyy-MM-dd_kk.mm.ss", dateTaken).toString() + ".jpg";

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


                Intent intent = new Intent();
                intent.setAction("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);

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
                Date currentDate = new Date();
                SimpleDateFormat df = new SimpleDateFormat("yy.MM.dd", Locale.JAPAN);
                String formattedDate = df.format(currentDate);
                if (flg == actItem.FLG_EDIT) {

                    //更新処理
                    GroupItemObject newUpdateItem = new GroupItemObject();
                    if (imageChange) {
                        //既存ファイル削除
                        File imageFile;

                        Uri deleteImageUri = Uri.parse(itemObj.getImagePath());
                        imageFile = new File(deleteImageUri.getPath());
                        fileUtil.deleteFile(imageFile);
                        String targetRoot = extPath + rootDir + "/" + GroupName + "/";

                        Uri uri = Uri.parse(picPath);
                        imageFile = new File(uri.getPath());
                        String newImagePath = targetRoot + imageFile.getName();
                        fileUtil.copyFile(imageFile, newImagePath);
                        imageFile = new File(targetRoot, imageFile.getName());
                        imageFile.renameTo(new File(targetRoot, formattedDate + imageFile.getName()));
                        imageFile = new File(targetRoot, formattedDate + imageFile.getName());

                        //イメージ変更後、テキストファイル名も変更
                        int idx = imageFile.getName().lastIndexOf(".");
                        String textFileName = imageFile.getName().substring(0, idx);
                        String newTextPath = targetRoot + textFileName + ".txt";
                        if (itemObj.getTextPath() != null) {
                            new File(itemObj.getTextPath()).renameTo(new File(newTextPath));
                            fileUtil.deleteFile(new File(Uri.parse(itemObj.getTextPath()).getPath())); //既存ファイル削除
                        }
                        newUpdateItem.setImageName(imageFile.getName());
                        newUpdateItem.setImagePath(imageFile.getPath());
                        newUpdateItem.setTextPath(newTextPath);
                    } else {
                        newUpdateItem.setImagePath(itemObj.getImagePath());
                        newUpdateItem.setImageName(itemObj.getImageName());
                        newUpdateItem.setTextPath(itemObj.getTextPath());
                    }
                    newUpdateItem.setTime(itemObj.getTime());
                    newUpdateItem.setGroupNo(itemObj.getGroupNo());
                    newUpdateItem.setItemNo(itemObj.getItemNo());
                    newUpdateItem.setTextContent(accTxt.getText().toString());
                    fileUtil.UpdateFile(itemObj.getTextPath(), accTxt.getText().toString());

                    updateItem(newUpdateItem);
                    finish();
                } else {
                    //新規作成
                    //イメージファイルをコピー
                    Uri uri = Uri.parse(picPath);
                    File imageFile = new File(uri.getPath());
                    String path = extPath + rootDir + "/" + GroupName + "/";
                    String targetRoot = path + imageFile.getName();
                    fileUtil.copyFile(imageFile, targetRoot);
                    imageFile = new File(path, imageFile.getName());
                    imageFile.renameTo(new File(path, formattedDate + imageFile.getName()));
                    imageFile = new File(path, formattedDate + imageFile.getName());
                    //テキストファイル作成
                    int idx = imageFile.getName().lastIndexOf(".");
                    String textFileName = imageFile.getName().substring(0, idx);
                    File sortFile = fileUtil.makeFile(extPath + rootDir + "/" + GroupName + "/sort.txt");
                    if (sortFile != null) {
                        fileUtil.writeSortFile(sortFile, imageFile.getName());
                    }
                    File textFile = fileUtil.makeFile(extPath + rootDir + "/" + GroupName + "/" + "" + textFileName + ".txt");
                    if (textFile != null) {
                        fileUtil.writeFile(textFile, accTxt.getText().toString().getBytes());
                    }

                    GroupItemObject obj = new GroupItemObject();
                    obj.setTime(time);
                    obj.setItemNo(GroupName + "_sub_" + getItemSize(GroupName));
                    obj.setGroupName(GroupName);
                    obj.setImagePath(imageFile.getPath());
                    obj.setImageName(imageFile.getName());
                    obj.setTextPath(textFile.getPath());
                    obj.setTextContent(accTxt.getText().toString());
                    setItem(GroupName, obj);
                    finish();
                }
            }
        });
    }


    /**
     * Image View へ画像表示
     */
    public void drawFromPath(String imgPath) {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imgPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        Bitmap bitmap = decodeSampledBitmapFromFile(imgPath, 700, 700);
        bitmap = rotateBitmap(bitmap, orientation);
        picView.setImageBitmap(bitmap);
    }

    public static Bitmap decodeSampledBitmapFromFile(String filePath, int reqWidth, int reqHeight) {

        // inJustDecodeBounds=true で画像のサイズをチェック
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // inSampleSize を計算
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // inSampleSize をセットしてデコード
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        // 画像の元サイズ
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float)height / (float)reqHeight);
            } else {
                inSampleSize = Math.round((float)width / (float)reqWidth);
            }
        }
        return inSampleSize;
    }

    private Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == actItem.VOICE_RECOGNITION && resultCode == RESULT_OK) {
            // 認識結果を ArrayList で取得
            ArrayList<String> candidates = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            if (candidates.size() > 0) {
                // 認識結果候補で一番有力なものを表示
                insertText(accTxt, candidates.get(0));
//                accTxt.setText(candidates.get(0));
            }
        }


        if (requestCode == actItem.ADD_PIC_FROM_SHOOTING_TEST) {

            if (resultCode != RESULT_OK) {
                // 正しい結果が得られなかった場合の処理
                // 撮影キャンセルなどするとこっちに来る

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
            imageChange = true;
            drawFromPath(getPathFromUri(resultUri));
            picPath = getPathFromUri(resultUri);


        }
    }

    private void speech() {
        // 音声認識が使えるか確認する
        try {            // 音声認識の　Intent インスタンス
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

            if (lang == 0) {
                // 日本語
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.JAPAN.toString());
            } else if (lang == 2) {
                // Off line mode
            }
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 100);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "音声を入力" + lang);
            // インテント発行
            startActivityForResult(intent, actItem.VOICE_RECOGNITION);
        } catch (ActivityNotFoundException e) {
//            tv.setText("No Activity " );
        }

    }


    public static void insertText(EditText view, String text) {
        // Math.max 는 에초에 커서가 잡혀있지않을때를 대비해서 넣음.
        int s = Math.max(view.getSelectionStart(), 0);
        int e = Math.max(view.getSelectionEnd(), 0);
        // 역으로 선택된 경우 s가 e보다 클 수 있다 때문에 이렇게 Math.min Math.max를 쓴다.
        view.getText().replace(Math.min(s, e), Math.max(s, e), text, 0, text.length());
    }

    public String getPathFromUri(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToNext();
        String path = cursor.getString(cursor.getColumnIndex("_data"));
        cursor.close();

        return path;
    }

}