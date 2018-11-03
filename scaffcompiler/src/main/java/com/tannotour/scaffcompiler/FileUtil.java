package com.tannotour.scaffcompiler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * Created by mitnick on 2018/10/20.
 * Description
 */
public class FileUtil {

    public static void writeFile(String fileName, String body){
        try{
            File file = new File("d:/" + fileName);
            if(!file.exists()){
                file.createNewFile();
            }
            FileWriter writer = new FileWriter(file, true);
            BufferedWriter out = new BufferedWriter(writer);
            out.write(body);
            out.flush();
            out.close();
            writer.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
