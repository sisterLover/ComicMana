package mio.sis.com.comicmana.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import mio.sis.com.comicmana.MainActivity;

/**
 * Created by Administrator on 2018/1/15.
 */

public class ImageLib {
    static public Bitmap LoadFile(File file) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeFile(file.toString(), options);
    }
    static public boolean SaveFile(File file, Bitmap bitmap) {
        boolean result = true;
        try {
            OutputStream outputStream = MainActivity.ssaf.OpenOutputStream(file);
            if(outputStream == null) return false;
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
            outputStream.close();
        }
        catch (Exception e) {
            result = false;
        }
        return result;
    }
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
    static public String GetRenameExtension(File file) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] data = new byte[4];
            int byteCnt = fileInputStream.read(data);
            fileInputStream.close();
            if(byteCnt < 4) return null;
            String hex = bytesToHex(data);
            if(hex.compareToIgnoreCase("89504E47")==0) {
                return "png";
            }
            else {
                return "jpg";
            }
        }
        catch (Exception e) {

        }
        return null;
    }
    static public Bitmap Scale(Bitmap bitmap, int destWidth, int destHeight) {
        if(bitmap.getWidth() == destWidth && bitmap.getHeight() == destHeight) {
            return bitmap;
        }
        Bitmap result = Bitmap.createBitmap(destWidth, destHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        RectF destRect = new RectF();
        destRect.left = 0; destRect.top = 0;
        destRect.right = destWidth; destRect.bottom = destHeight;
        canvas.drawBitmap(bitmap, null, destRect, null);
        return result;
    }
}
