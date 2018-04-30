package com.example.jun.veritecpro04.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class FileUtil {

    public void WriteTextFile(String foldername, String filename, String contents){
        try{
            File dir = new File (foldername);

            if(!dir.exists()){
                dir.mkdir();
            }

            FileOutputStream fos = new FileOutputStream(foldername+"/"+filename, true);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
            writer.write(contents);
            writer.flush();

            writer.close();
            fos.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }


}
