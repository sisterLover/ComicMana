package mio.sis.com.comicmana.snet;

import java.util.ArrayList;

import mio.sis.com.comicmana.sdata.ComicInfo;
import mio.sis.com.comicmana.sdata.ComicSrc;

/**
 * Created by Administrator on 2017/12/27.
 */

public interface NetSiteHelper {
    /*
        此函數會在 UI thread 被呼叫
        要求列舉 src 站點位置的漫畫資訊
        忽略前面 startFrom 本，並窮舉 length 本後停止
     */
    void EnumComic(ComicSrc src, int startFrom, int length, EnumCallback callback);

    interface EnumCallback {
        /*
            失敗時 info.length = 0 或 info = null
         */
        void ComicDiscover(ComicInfo[] info);
    }
}
