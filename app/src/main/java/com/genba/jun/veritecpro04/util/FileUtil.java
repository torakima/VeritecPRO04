package com.genba.jun.veritecpro04.util;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.storage.StorageManager;
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
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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

    public static String getExternalStoragePath(Context mContext) {
        String sdPathx = Environment.getExternalStorageDirectory().getAbsolutePath();

        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        String setPath = "";
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
//            if (length == 1) {
            Object storageVolumeElement = Array.get(result, 0);
            setPath = (String) getPath.invoke(storageVolumeElement);
//            } else {
//                for (int i = 0; i < length; i++) {
//                    Object storageVolumeElement = Array.get(result, i);
//                    String path = (String) getPath.invoke(storageVolumeElement);
//                    boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
//                    if (removable) {
//                        setPath = path;
//                    }
//                }
//            }
            return setPath;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * dir create
     *
     * @return dir
     */
    public File makeDirectory(String dir_path) {
        try {
            File dir = new File(dir_path);
            if (!dir.exists()) {
                dir.mkdirs();
                Log.i(TAG, "!dir create " + dir_path);
            } else {
                Log.i(TAG, "dir.exists");
            }
            return dir;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

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
                    Log.i(TAG, "result = " + isSuccess);
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
                Log.i(TAG, "result= " + isSuccess);
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

    /**
     * sortファイルに内容書き
     *
     * @param file
     * @param file_content
     * @return
     */
    public boolean writeSortFile(File file, String file_content) {
        boolean result;
        byte[] writeFIle;
        FileWriter writer = null;
        boolean isSeporator = false;
        String sortContent = ReadFileText(file);
        if (!sortContent.isEmpty()) {
            isSeporator = true;
        }
        try {
            writer = new FileWriter(file, true);
            if (isSeporator) writer.write(System.lineSeparator());
            writer.write(file_content);
            writer.flush();
            writer.close();
            result = true;
        } catch (IOException i) {
            result = false;
        }
        return result;
    }

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

    private void CreateUpdateSorttxt(String FilePath) {
        try {
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


    public void removeLineFromFile(String file, String lineToRemove) {
        try {
            File inFile = new File(file);
            if (!inFile.isFile()) {
                System.out.println("Parameter is not an existing file");
                return;
            }

            //Construct the new file that will later be renamed to the original filename.
            File tempFile = new File(inFile.getAbsolutePath() + ".tmp");

            BufferedReader br = new BufferedReader(new FileReader(file));
            PrintWriter pw = new PrintWriter(new FileWriter(tempFile));
            String line = null;
            //Read from the original file and write to the new
            //unless content matches data to be removed.
            while ((line = br.readLine()) != null) {
                if (!line.trim().equals(lineToRemove)) {
                    pw.println(line);
                    pw.flush();
                }
            }
            pw.close();
            br.close();

            //Delete the original file
            if (!inFile.delete()) {
                System.out.println("Could not delete file");
                return;
            }

            //Rename the new file to the filename the original file had.
            if (!tempFile.renameTo(inFile))
                System.out.println("Could not rename file");

        }
        catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }


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
            } catch (Exception e) {
                e.printStackTrace();
            }
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    //directory rename
    public void renameFolder(String oldPath, String newPath) {
        try {
            new File(Uri.parse(oldPath).getPath()).renameTo(new File(Uri.parse(newPath).getPath()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
