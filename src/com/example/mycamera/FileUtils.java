package com.example.mycamera;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.*;

/**
 * Created by marui on 13-9-5.
 */
public class FileUtils {

    public static final String DEFAULT_SAVE_PATH = Environment.DIRECTORY_PICTURES;

    public static boolean folderCheck(File folder) {
        if (folder == null) {
            return false;
        } else if (folder.isDirectory()) {
            return true;
        } else {
            return folder.mkdirs();
        }
    }

    public static boolean saveFile(byte[] data) {
        String fileName = "my_camera_photo_" + System.currentTimeMillis() + ".jpg";
        File filePath = Environment.getExternalStoragePublicDirectory(DEFAULT_SAVE_PATH);
        if (folderCheck(filePath)) {
            File file = new File(filePath.getAbsolutePath() + File.separator + fileName);
            BufferedOutputStream out = null;
            try {
                out = new BufferedOutputStream(new FileOutputStream(file));
                out.write(data);
                Log.d("test", "save success:" + file.getAbsoluteFile());
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
