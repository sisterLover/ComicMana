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
    static private int viewWidth, viewHeight;
    static private Bitmap percentPage = null, errorPage = null;
    static private Context context = null;
    static private boolean paramAvailable = false;

    static public boolean ParamAvailable() { return paramAvailable; }
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

        DrawText(canvas, viewWidth, viewHeight, "" + percent + "%", 1.0f);
        if(percent < 0) percent = 0;
        if(percent > 100) percent = 100;
        DrawArc(canvas, viewWidth, viewHeight, ContextCompat.getColor(context, R.color.colorSSZPViewProgressFill), 0, 360*percent/100);
    }
    static public void DrawError(Canvas canvas) {
        GenErrorPage();
        canvas.drawBitmap(errorPage, 0, 0, null);
    }
    static private void GenPercentPage() {
        if(percentPage != null) return;
        percentPage = GenPage(null);
    }
    static private void GenErrorPage() {
        if(errorPage != null) return;
        errorPage = GenPage("讀取錯誤");
    }
    static private Bitmap GenPage(String string) {
        Bitmap result = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(result);
        canvas.drawColor(ContextCompat.getColor(context, R.color.colorSSZPViewBackground));
        Paint paint = new Paint();

        paint.setColor(ContextCompat.getColor(context, R.color.colorSSZPViewText));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4.0f);
        canvas.drawRect(0, 0, viewWidth, viewHeight, paint);

        if (string != null) {
            DrawText(canvas, viewWidth, viewHeight, string, 1.0f);
        }
        DrawArc(canvas, viewWidth, viewHeight, ContextCompat.getColor(context, R.color.colorSSZPViewProgressEmpty), 0, 360);

        return result;
    }
    static private void DrawArc(Canvas canvas, int width, int height, int color, float begin, float sweep) {
        Paint paint = new Paint();

        paint.setColor(color);
        paint.setStrokeWidth(4.0f);
        paint.setStyle(Paint.Style.STROKE);

        int arcWidth = Math.min(width, height)/3;
        canvas.drawArc((width-arcWidth)/2, (height-arcWidth)/2,
                (width+arcWidth)/2, (height+arcWidth)/2,
                begin, sweep, false, paint);
    }
    static private void DrawText(Canvas canvas, int width, int height, String text, float textSizeFactor) {
        Paint paint = new Paint();

        paint.setAntiAlias(true);
        paint.setColor(ContextCompat.getColor(context, R.color.colorSSZPViewText));
        paint.setTextAlign(Paint.Align.LEFT);

        int fontSize = Math.min(width, height) / 14;
        fontSize = (int)(fontSize*textSizeFactor);
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

        DrawText(canvas, bitmap.getWidth(), bitmap.getHeight(), "Test Comic "+ +chapter + "-" + page, 1.0f);
        return bitmap;
    }

    static void Clear() {
        percentPage = errorPage = null;
    }

    static private Bitmap emptyThumbnail = null;
    static public Bitmap GetEmptyThumbnail(Context context, int width, int height) {
        if(emptyThumbnail != null &&
                emptyThumbnail.getWidth() == width &&
                emptyThumbnail.getHeight() == height) return emptyThumbnail;
        emptyThumbnail = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(emptyThumbnail);
        canvas.drawColor(ContextCompat.getColor(context, R.color.colorSSZPViewBackground));
        if(DefaultPageCache.context == null) {
            DefaultPageCache.context = context;
        }
        DrawText(canvas, width, height, "沒有縮圖唷", 2.0f);
        return emptyThumbnail;
    }
}
