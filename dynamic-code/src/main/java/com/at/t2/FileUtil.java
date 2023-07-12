package com.at.t2;

import java.io.File;

public class FileUtil {

    public static boolean isExists(String file) {
        try {
            return new File(file).exists();
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean destroyFile(String file) {

        File ff = new File(file);
        return ff.delete();

    }
}
