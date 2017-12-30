package mio.sis.com.comicmana.scache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;

import mio.sis.com.comicmana.R;

/**
 * Created by Administrator on 2017/12/28.
 */

public class DefaultPageCache {
    static int viewWidth, viewHeight;
    static Bitmap percentPage = null, errorPage = null;
    static Context context = null;
    static boolean paramAvailable = false;

    static public boolean ParamAbailable() { return paramAvailable; }
    static public int GetWidth() {
        return viewWidth;
    }
    static public int GetHeight() {
        return viewHeight;
    }
    /*
        SSZPView 會呼叫此函數來設定預設 view 長寬
        其他 static function 以此為參數產生 cache
     */
    static public void SetParams(Context context, int viewWidth, int viewHeight) {
        DefaultPageCache.context = context;
        DefaultPageCache.viewWidth = viewWidth;
        DefaultPageCache.viewHeight = viewHeight;
        paramAvailable = true;
    }
    static public void DrawPercent(Canvas canvas, int page, int percent) {
        GenPercentPage();
        canvas.drawBitmap(percentPage, 0, 0, null);

        DrawText(canvas, viewWidth, viewHeight, "" + percent + "%");
        if(percent < 0) percent = 0;
        if(percent > 100) percent = 100;
        DrawArc(canvas, viewWidth, viewHeight, ContextCompat.getColor(context, R.color.colorSSZPViewProgressFill), 0, 360*percent/100);
    }
    static public void DrawError(Canvas canvas) {
        GenErrorPage();
        canvas.drawBitmap(errorPage, 0, 0, null);
    }
    static void GenPercentPage() {
        if(percentPage != null) return;
        percentPage = GenPage(null);
    }
    static void GenErrorPage() {
        if(errorPage != null) return;
        errorPage = GenPage("讀取錯誤");
    }
    static Bitmap GenPage(String string) {
        Bitmap result = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(result);
        canvas.drawColor(ContextCompat.getColor(context, R.color.colorSSZPViewBackground));
        Paint paint = new Paint();

        paint.setColor(ContextCompat.getColor(context, R.color.colorSSZPViewText));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4.0f);
        canvas.drawRect(0, 0, viewWidth, viewHeight, paint);

        if (string != null) {
            DrawText(canvas, viewWidth, viewHeight, string);
        }
        DrawArc(canvas, viewWidth, viewHeight, ContextCompat.getColor(context, R.color.colorSSZPViewProgressEmpty), 0, 360);

        return result;
    }
    static void DrawArc(Canvas canvas, int width, int height, int color, float begin, float sweep) {
        Paint paint = new Paint();

        paint.setColor(color);
        paint.setStrokeWidth(4.0f);
        paint.setStyle(Paint.Style.STROKE);

        int arcWidth = Math.min(width, height)/3;
        canvas.drawArc((width-arcWidth)/2, (height-arcWidth)/2,
                (width+arcWidth)/2, (height+arcWidth)/2,
                begin, sweep, false, paint);
    }
    static void DrawText(Canvas canvas, int width, int height, String text) {
        Paint paint = new Paint();

        paint.setAntiAlias(true);
        paint.setColor(ContextCompat.getColor(context, R.color.colorSSZPViewText));
        paint.setTextAlign(Paint.Align.LEFT);

        int fontSize = Math.min(width, height) / 14;
        paint.setTextSize(fontSize);
        Rect bound = new Rect();
        paint.getTextBounds(text, 0, text.length(), bound);

        canvas.drawText(text,
                (width - bound.right + bound.left) / 2, (height + bound.bottom - bound.top) / 2,
                paint);
    }
    static public Bitmap GetTestComic(int chapter, int page) {
        Bitmap bitmap = Bitmap.createBitmap(viewWidth, viewHeight/2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(ContextCompat.getColor(context, R.color.colorSSZPViewBackground));

        Paint paint = new Paint();

        paint.setColor(ContextCompat.getColor(context, R.color.colorSSZPViewText));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4.0f);
        canvas.drawRect(0, 0, bitmap.getWidth(), bitmap.getHeight(), paint);

        DrawText(canvas, bitmap.getWidth(), bitmap.getHeight(), "Test Comic "+ +chapter + "-" + page);
        return bitmap;
    }

    static void Clear() {
        percentPage = errorPage = null;
    }
}
