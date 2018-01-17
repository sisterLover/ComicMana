package mio.sis.com.comicmana.sfile;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/12/24.
 */

public class SFile {
    /*
        whether string is null or not, something will be write to stream
        null string is treat as a length 0 string(string = "")
     */
    static public void WriteStringToStream(String string, DataOutputStream stream) throws IOException {
        if(string != null) {
            byte[] data = string.getBytes();
            stream.writeInt(data.length);
            stream.write(data);
        }
        else {
            stream.writeInt(0);
        }
    }
    /*
        whether string is null or not, a non-null string will be return
        null string is return as a length 0 string(string = "")
     */
    static public String ReadStringFromStream(DataInputStream stream) throws IOException {
        int string_length = stream.readInt();
        if(string_length == 0) return "";
        byte[] data = new byte[string_length];
        stream.read(data);
        return new String(data);
    }
    static public boolean SDCardValid() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
    static public ArrayList<File> GetSDCardDirs() {
        ArrayList<File> list = new ArrayList<>();
        list.add(Environment.getExternalStorageDirectory());
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            /*
                cannot use
             */
            return list;
        }
        File base = new File("/storage");
        if(!base.exists()) return list;

        File[] files = base.listFiles();
        for(File file : files) {
            if(file.toString().contains("ext_sd")) {
                //  this path cannot use in HTC M9u
                continue;
            }
            if(file.exists()) {
                try {
                    if (Environment.isExternalStorageRemovable(file)) {
                        list.add(file);
                    }
                }
                catch (Exception e) {
                    //  do nothing, just ignore
                }
            }
        }
        return list;
    }
    static public boolean ChechPermission(Context context) {
        int permission_write = context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE),
        permission_read = context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        return permission_write == PackageManager.PERMISSION_GRANTED &&
                permission_read == PackageManager.PERMISSION_GRANTED;
    }
    static public final int REQUEST_CODE = 8897;
    static public void RequestPermission(Activity activity) {
        //  when get write permission, we get read permission also
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_CODE);
        //  need overload onRequestPermissionsResult with request code = REQUEST_CODE
    }
    /*
        取得 File 的副檔名
     */
    static public String GetExtension(File file) {
        if (file.isDirectory()) return null;
        return GetExtension(file.getName());
    }
    static public String GetExtension(String string) {
        int start_pos = string.lastIndexOf('.');
        if (start_pos == -1) return null;
        if (start_pos == string.length() - 1) return null;
        return string.substring(start_pos + 1);
    }
    static public String GetNameWithoutExtension(File file) {
        return GetNameWithoutExtension(file.getName());
    }
    static public String GetNameWithoutExtension(String string) {
        int dot_pos = string.lastIndexOf('.');
        if(dot_pos==-1) return string;
        return string.substring(0, dot_pos);
    }

    static public void EnumFile(File directory, int maxLevel, EnumFileCallback callback) {
        EnumFileInner(directory, 1, maxLevel, callback);
    }
    static private void EnumFileInner(File directory, int level, int maxLevel, EnumFileCallback callback) {
        File[] childs = directory.listFiles();
        for(File file : childs) {
            callback.OnFile(file);
            if (level < maxLevel && file.isDirectory()) {
                EnumFileInner(file, level + 1, maxLevel, callback);
            }
        }
    }

    public interface EnumFileCallback {
        void OnFile(File file);
    }
}
