package mio.sis.com.comicmana.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

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
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.close();
        }
        catch (Exception e) {
            result = false;
        }
        return result;
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
        canvas.drawBitmap(result, null, destRect, null);
        return result;
    }
}
