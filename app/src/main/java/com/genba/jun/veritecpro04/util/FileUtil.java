package com.genba.jun.veritecpro04.util;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.util.Log;
import android.widget.Toast;

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
import java.io.RandomAccessFile;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;

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
     * dir delete
     */
    public void setDirEmpty(String path) {
        File dir = new File(path);
        File[] childFileList = dir.listFiles();
        if (dir.exists()) {
            for (File childFile : childFileList) {
                if (childFile.isDirectory()) {
                    setDirEmpty(childFile.getAbsolutePath());
                } else {
                    childFile.delete();
                    Log.i(TAG, "delete File " + childFile.getName());

                }
            }
            dir.delete();
        }
    }


    /**
     * dir create
     *
     * @return dir
     */
    public File makeDirectory(String dir_path) {
        try {
            File dir = new File(dir_path);
            dir.mkdirs();
            Log.i(TAG, "dir create " + dir_path);
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

    /**
     * file clear
     */
    public void clearFiles(String path, ArrayList<String> files) {
        File dir = new File(path);
        File[] childFileList = dir.listFiles();
        for (File childFile : childFileList) {
            Boolean isFile = false;
            for (String file : files) {
                if (childFile.getName().equals(file)) isFile = true;
            }
            if (!isFile) childFile.delete();
        }
    }

    public File makeFile(String file_path) {
        File file;
        boolean isSuccess = false;
        file = new File(file_path);
        if (file.exists()) {
            deleteFile(file);
        }
        Log.i(TAG, "!file.exists");
        try {
            isSuccess = file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Log.i(TAG, "result= " + isSuccess);
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
        if (file.exists()) {
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
        try {
            writer = new FileWriter(file, true);
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
                if (!line.trim().isEmpty() || !line.trim().contains(lineToRemove)) {
                    pw.write(line);
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
            if (!tempFile.renameTo(inFile)) {
                System.out.println("Could not rename file");
            }

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void deleteLine(File targetFile) throws IOException {
        RandomAccessFile file = new RandomAccessFile(targetFile, "rw");
        String delete;
        String task = "";
        byte[] tasking;
        while ((delete = file.readLine()) != null) {
            if (delete.startsWith("BAD")) {
                continue;
            }
            task += delete + "\n";
        }
        System.out.println(task);
        BufferedWriter writer = new BufferedWriter(new FileWriter(targetFile));
        writer.write(task);
        file.close();
        writer.close();
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
            } finally {
                result = true;
            }
        } else {
            result = false;
        }
        return result;
    }

    //directory rename
    public void renameFolder(String oldPath, String newPath) {
        try {
            File filePre = new File(oldPath);
            File fileNow = new File(newPath);
            fileNow.mkdir();
            File[] childFileList = filePre.listFiles();
            for (File childFile : childFileList) {
                new File(oldPath + File.separator + childFile.getName()).renameTo(new File(newPath + File.separator + childFile.getName()));
                childFile.delete();
            }
            filePre.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isExternalMemoryAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public Long checkInternalStorageAllMemory() {
        StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();

        return blockSize * totalBlocks;
    }

    public Long checkInternalAvailableMemory() {
        StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();

        return blockSize * availableBlocks;
    }

    public Long checkExternalStorageAllMemory() {
        Long size = 0L;
        if (isExternalMemoryAvailable()) {
            StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
            long blockSize = stat.getBlockSizeLong();
            long totalBlocks = stat.getBlockCountLong();

            size = totalBlocks * blockSize;
        }
        return size;
    }

    public Long checkExternalAvailableMemory() {
        Long size = 0L;
        if (isExternalMemoryAvailable()) {
            File file = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(file.getPath());
            long blockSize = stat.getBlockSizeLong();
            long availableBlocks = stat.getAvailableBlocksLong();
            size = availableBlocks * blockSize;
        }
        return size;
    }

    public String getFileSize(long size) {
        if (size <= 0)
            return "0";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}