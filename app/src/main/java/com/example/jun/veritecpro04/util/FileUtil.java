package com.example.jun.veritecpro04.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class FileUtil {
    private String TAG = "FileUtil";

    public void WriteTextFile(String foldername, String filename, String contents) {
        try {
            File dir = new File(foldername);

            if (!dir.exists()) {
                dir.mkdir();
            }

            FileOutputStream fos = new FileOutputStream(foldername + "/" + filename, true);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
            writer.write(contents);
            writer.flush();

            writer.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * dir create
     *
     * @return dir
     */
    public File makeDirectory(String dir_path) {
        File dir = new File(dir_path);
        if (!dir.exists()) {
            dir.mkdirs();
            Log.i(TAG, "!dir.create");
        } else {
            Log.i(TAG, "dir.exists");
        }

        return dir;
    }

    /**
     * file create
     *
     * @param dir
     * @return file
     */
    public File makeFile(File dir, String file_path) {
        File file = null;
        boolean isSuccess = false;
        if (dir.isDirectory()) {
            file = new File(file_path);
            if (file != null && !file.exists()) {
                Log.i(TAG, "!file.exists");
                try {
                    isSuccess = file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    Log.i(TAG, "파일생성 여부 = " + isSuccess);
                }
            } else {
                Log.i(TAG, "file.exists");
            }
        }
        return file;
    }

    public File makeFile(String file_path) {
        File file;
        boolean isSuccess = false;
        file = new File(file_path);
        if (!file.exists()) {
            Log.i(TAG, "!file.exists");
            try {
                isSuccess = file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                Log.i(TAG, "파일생성 여부 = " + isSuccess);
            }
        } else {
            Log.i(TAG, "file.exists");
        }
        return file;
    }

    /**
     * (dir/file) delete
     *
     * @param file
     */
    public boolean deleteFile(File file) {
        boolean result;
        if (file != null && file.exists()) {
            file.delete();
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    /**
     * ファイル確認
     *
     * @param file
     * @return
     */
    private boolean isFileExist(File file) {
        boolean result;
        if (file != null && file.exists()) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    /**
     * ファイル名変更
     *
     * @param file
     */
    public boolean reNameFile(File file, File new_name) {
        boolean result;
        if (file != null && file.exists() && file.renameTo(new_name)) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    /**
     * ファイル名変更
     *
     * @param
     */

    public String[] getList(File dir) {
        if (dir != null && dir.exists())
            return dir.list();
        return null;
    }

    /**
     * ファイルに内容書き
     *
     * @param file
     * @param file_content
     * @return
     */
    public boolean writeFile(File file, byte[] file_content) {
        boolean result;
        FileOutputStream fos;
        if (file != null && file.exists() && file_content != null) {
            try {
                fos = new FileOutputStream(file);
                try {
                    fos.write(file_content);
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    // 파일 생성
    private void CreateFile(String FilePath) {
        try {
            System.out.println(FilePath);

            int nLast = FilePath.lastIndexOf("\\");
            String strDir = FilePath.substring(0, nLast);
            String strFile = FilePath.substring(nLast + 1, FilePath.length());

            File dirFolder = new File(strDir);
            dirFolder.mkdirs();
            File f = new File(dirFolder, strFile);
            f.createNewFile();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }


    // 파일 테스트 읽기
    public String ReadFileText(File file) {
        String strText = "";
        int nBuffer;
        try {
            BufferedReader buffRead = new BufferedReader(new FileReader(file));
            while ((nBuffer = buffRead.read()) != -1) {
                strText += (char) nBuffer;
            }
            buffRead.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return strText;
    }

    // 파일 수정
    public void UpdateFile(String FilePath, String Text) {
        try {
            File f = new File(FilePath);
            if (!f.exists()) {
                CreateFile(FilePath);
            }
            BufferedWriter buffWrite = new BufferedWriter(new FileWriter(f));
            // 파일 쓰기
            buffWrite.write(Text, 0, Text.length());
            // 파일 닫기
            buffWrite.flush();
            buffWrite.close();


        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }


//    public boolean writeFile(File file , String file_content){
//        boolean result;
//        BufferedReader fos;
//        if(file!=null&&file.exists()&&file_content!=null){
//            try {
//                fos = new BufferedReader(file);
//                try {
//                    fos.write(file_content);
//                    fos.flush();
//                    fos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//            result = true;
//        }else{
//            result = false;
//        }
//        return result;
//    }


    /**
     * ファイル読み
     *
     * @param file
     */
    public void readFile(File file) {
        int readcount = 0;
        if (file != null && file.exists()) {
            try {
                FileInputStream fis = new FileInputStream(file);
                readcount = (int) file.length();
                byte[] buffer = new byte[readcount];
                fis.read(buffer);
                for (int i = 0; i < file.length(); i++) {
                    Log.d(TAG, "" + buffer[i]);
                }
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ファイルコピー
     *
     * @param file
     * @param save_file
     * @return
     */
    public boolean copyFile(File file, String save_file) {
        boolean result;
        if (file != null && file.exists()) {
            try {
                FileInputStream fis = new FileInputStream(file);
                FileOutputStream newfos = new FileOutputStream(save_file);
                int readcount = 0;
                byte[] buffer = new byte[1024];
                while ((readcount = fis.read(buffer, 0, 1024)) != -1) {
                    newfos.write(buffer, 0, readcount);
                }
                newfos.close();
                fis.close();
                Log.d(TAG, "이미지 파일 카피 성공");
            } catch (Exception e) {
                Log.d(TAG, "이미지 파일 카피 실패");
                e.printStackTrace();
            }
            result = true;
        } else {
            result = false;
        }
        return result;
    }
}
