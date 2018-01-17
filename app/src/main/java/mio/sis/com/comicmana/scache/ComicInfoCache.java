package mio.sis.com.comicmana.scache;

import mio.sis.com.comicmana.sdata.ComicInfo;
import mio.sis.com.comicmana.sdata.ComicSrc;
import mio.sis.com.comicmana.snet.NetSiteHelper;

/**
 * Created by Administrator on 2018/1/16.
 */

public class ComicInfoCache {
    /*
        ComicGrid 應該呼叫此函數來取得 comicInfo
     */
    static public void EnumComic(ComicSrc src, int startFrom, int length, NetSiteHelper.EnumCallback callback) {
        /*
            debug use
         */
        ComicInfo[] comicInfos = new ComicInfo[length];
        for(int i=0;i<length;++i) {
            comicInfos[i]=ComicInfo.GetTestComicInfo();
        }
        callback.ComicDiscover(comicInfos);
    }
    /*
        搜尋函數，但是包含了關鍵字
     */
    static public void EnumComic(ComicSrc src, int startFrom, int length, String search, NetSiteHelper.EnumCallback callback) {

    }
}
