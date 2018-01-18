package mio.sis.com.comicmana.snet;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/**
 * Created by Administrator on 2018/1/19.
 */

public class SNet {
    static public boolean ChechPermission(Context context) {
        int permission_net = context.checkSelfPermission(Manifest.permission.INTERNET);
        return permission_net == PackageManager.PERMISSION_GRANTED;
    }

    static public final int REQUEST_CODE = 8898;

    static public void RequestPermission(Activity activity) {
        //  when get write permission, we get read permission also
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.INTERNET},
                REQUEST_CODE);
        //  need overload onRequestPermissionsResult with request code = REQUEST_CODE
    }
}
