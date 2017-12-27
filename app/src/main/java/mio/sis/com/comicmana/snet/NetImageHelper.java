package mio.sis.com.comicmana.snet;

import mio.sis.com.comicmana.sdata.ComicPosition;
import mio.sis.com.comicmana.sdata.ComicSrc;
import mio.sis.com.comicmana.sui.SImgPage;

/**
 * Created by Administrator on 2017/12/26.
 */

public interface NetImageHelper {
    /*
        GetComicPage 取得漫畫的某一頁並讀入至 SImgPage 裡
        此函數會在 UI thread 被呼叫
     */
    void GetComicPage(SImgPage page, ComicSrc src, ComicPosition position);
}
