package mio.sis.com.comicmana.scache;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by Administrator on 2017/12/28.
 */

public class DefaultPageCache {
    static int viewWidth, viewHeight;
    static Bitmap percentPage, errorPage;

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
    static public void SetParams(int viewWidth, int viewHeight) {
        DefaultPageCache.viewWidth = viewWidth;
        DefaultPageCache.viewHeight = viewHeight;
    }
    static public void DrawPercent(Canvas canvas, int percent) {

    }
    static public void DrawError(Canvas canvas) {

    }
    static void GenPercentPage() {
        if(percentPage != null) return;
    }
    static void GenErrorPage() {
        if(errorPage != null) return;
    }

    static void Clear() {
        percentPage = errorPage = null;
    }
}
