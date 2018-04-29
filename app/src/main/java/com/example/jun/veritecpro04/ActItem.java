package com.example.jun.veritecpro04;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JUN on 2017/10/16.
 */




public class ActItem {
    public static final int ADD_PIC_FROM_SHOOTING_TEST = 878;
    public static final int ADD_PIC_FROM_SHOOTING_OLD = 88;
    public static final int ADD_PIC_FROM_ALBUM = 93;
    public static final int VOICE_RECOGNITION = 50;

    public static final int FLG_EDIT = 929;

    BitmapFactory.Options options = new BitmapFactory.Options();

    /**
     * 파일 복사
     * @param file
     * @param save_file
     * @return
     */

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static String getSdCardFilesDirPathListForLollipop(Context context) {
        List<String> sdCardFilesDirPathList = new ArrayList<>();

        // getExternalFilesDirsはAndroid4.4から利用できるAPI。
        // filesディレクトリのリストを取得できる。
        File[] dirArr = context.getExternalFilesDirs(null);

        for (File dir : dirArr) {
            if (dir != null) {
                String path = dir.getAbsolutePath();

                // isExternalStorageRemovableはAndroid5.0から利用できるAPI。
                // 取り外し可能かどうか（SDカードかどうか）を判定している。
                if (Environment.isExternalStorageRemovable(dir)) {

                    // 取り外し可能であればSDカード。
                    if (!sdCardFilesDirPathList.contains(path)) {
                        sdCardFilesDirPathList.add(path);
                    }

                } else {
                    // 取り外し不可能であれば内部ストレージ。
                }
            }
        }
        if(sdCardFilesDirPathList.size() == 0)
            return null;

        String sdPath = sdCardFilesDirPathList.get(0);

        //return sdCardFilesDirPathList;

        return sdPath;
    }


    public void copyFile(File file , String save_file){

        Log.i("TEST:","원본 파일 : " + file);
        Log.i("TEST:","타겟 파일 : " + save_file);

        //boolean result;
        if(file!=null&&file.exists()){
            try {
                FileInputStream fis = new FileInputStream(file);
                FileOutputStream newfos = new FileOutputStream(save_file);
                int readcount=0;
                byte[] buffer = new byte[1024];
                while((readcount = fis.read(buffer,0,1024))!= -1){
                    newfos.write(buffer,0,readcount);
                }
                newfos.close();
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.i("TEST:","파일복사 성공");
        }else{
            Log.i("TEST:","파일복사 실패");
        }
     //   return result;
    }


    /**
     * 外部ストレージマウント確認
     */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


    /**
     * イメージサイズ編集
     */
    public Drawable resizing(String path) {
        Bitmap bitmapResizing = null;
        options.inSampleSize = 5;
        try {
            Bitmap src = BitmapFactory.decodeFile(path, options);
            src = imgRotation(src, path);
            bitmapResizing = Bitmap.createScaledBitmap(src, 600, 600, true);
            Drawable drawable = new BitmapDrawable(bitmapResizing);
            return drawable;

        } catch (Exception e) {

        }

        return null;
    }


    /**
     * イメージ回転
     */
    public Bitmap imgRotation(Bitmap img, String uri) {
        Bitmap image = img;

        try {
            ExifInterface exif = new ExifInterface(uri);
            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int exifDegree = exifOrientationToDegrees(exifOrientation);

            image = rotate(image, exifDegree);
            return image;

        } catch (Exception e) {
        }
        return img;
    }


    /**
     * イメージ回転角度決定
     */
    public int exifOrientationToDegrees(int exifOrientation){

        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90){
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180){
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270){
            return 270;
        }
        return 0;
}


    /**
     * イメージ回転機能
     */
    public Bitmap rotate(Bitmap bitmap, int degrees) {
        if (degrees != 0 && bitmap != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);

            try {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                if (bitmap != converted) {
                    bitmap.recycle();
                    bitmap = converted;
                }
            } catch (OutOfMemoryError ex) {
            }
        }
        return bitmap;
    }





}


