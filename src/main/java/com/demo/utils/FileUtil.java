package com.demo.utils;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author 冯宇明
 * @version 1.0
 * @date 2020/1/3
 * @desc
 */
public class FileUtil {

    public static void write(String filename, byte[] bytes) throws IOException {
        FileOutputStream os = new FileOutputStream(filename);
        os.write(bytes);
        os.close();
    }


}
