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
    static public void WriteStringToStream(String string, DataOutputStream stream) throws IOException {
        byte[] data = string.getBytes();
        stream.write(data.length);
        stream.write(data);
    }
    static public String ReadStringFromStream(DataInputStream stream) throws IOException {
        byte[] data = new byte[stream.readInt()];
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
}
