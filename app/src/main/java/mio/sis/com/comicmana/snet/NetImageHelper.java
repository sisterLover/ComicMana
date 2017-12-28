package mio.sis.com.comicmana.snet;

import android.graphics.Bitmap;

import mio.sis.com.comicmana.sdata.ComicPosition;
import mio.sis.com.comicmana.sdata.ComicSrc;

/**
 * Created by Administrator on 2017/12/26.
 */

public interface NetImageHelper {
    /*
        GetComicPage 取得漫畫的某一頁並讀入至 SImgPage 裡
        此函數會在 UI thread 被呼叫
     */
    void GetComicPage(ComicSrc src, ComicPosition position, ComicPageCallback callback);

    interface ComicPageCallback {
        /*
            當成功接收完圖形資料後呼叫
            失敗時呼叫此函數且 bitmap = null
         */
        void PageRecieve(Bitmap bitmap);
        /*
            接收進度更新
            percent = 0~100
         */
        void UpdateProgress(int percent);
    }
}
