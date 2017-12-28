package mio.sis.com.comicmana.scache;

import android.graphics.Bitmap;

import mio.sis.com.comicmana.sdata.ComicPosition;
import mio.sis.com.comicmana.sdata.ComicSrc;
import mio.sis.com.comicmana.sui.SImagePage;

/**
 * Created by Administrator on 2017/12/26.
 */

public class ImageCache {
    /*
        ImageCache 負責在記憶體-SD卡-網路三者之間存取圖片
     */



    /*
        GetComicPage 取得漫畫的某一頁並讀入至 SImgPage 裡
        此函數會在 UI thread 被呼叫
        圖片並不會在此函數被設定，而是藉由呼叫 page.SetImage 設定
     */
    static public void GetComicPage(SImagePage page, ComicSrc src, ComicPosition position) {

    }
    /*
        當某頁漫畫進入 SIP_READY_OUT 時，會將 bitmap 交由 cache 保管
        固定 bitmap 存入後會將最先存入之 bitmap 從 cache 刪除
     */
    static public void CacheBitmap(ComicSrc src, ComicPosition position, Bitmap bitmap) {

    }
}
