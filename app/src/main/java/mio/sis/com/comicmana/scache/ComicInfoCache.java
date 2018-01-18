package mio.sis.com.comicmana.scache;

import mio.sis.com.comicmana.mine.Web_wnacg_ComicInfo;
import mio.sis.com.comicmana.sdata.ComicInfo;
import mio.sis.com.comicmana.sdata.ComicSrc;
import mio.sis.com.comicmana.snet.NetSiteHelper;
import mio.sis.com.comicmana.snet.inst.HistorySiteHelper;
import mio.sis.com.comicmana.snet.inst.LocalComicSiteHelper;

/**
 * Created by Administrator on 2018/1/16.
 */

public class ComicInfoCache {
    static private LocalComicSiteHelper localComicSiteHelper = new LocalComicSiteHelper();
    static private HistorySiteHelper historySiteHelper = new HistorySiteHelper();
    static private Web_wnacg_ComicInfo wnacgSitHelper = new Web_wnacg_ComicInfo();
    /*
        ComicGrid 應該呼叫此函數來取得 comicInfo
     */
    static public void EnumComic(final ComicSrc src,
                                 final int startFrom,
                                 final int length,
                                 final NetSiteHelper.EnumCallback callback) {
        new Thread() {
            @Override
            public void run() {
                InnerEnumComic(src ,startFrom, length, callback);
            }
        }.start();
    }
    static private void InnerEnumComic(ComicSrc src,
                                       int startFrom,
                                       int length,
                                       NetSiteHelper.EnumCallback callback) {
        switch (src.srcType) {
            case ComicSrc.SrcType.ST_HISTORY:
                historySiteHelper.EnumComic(src, startFrom, length, callback);
                break;
            case ComicSrc.SrcType.ST_LOCAL_FILE:
                localComicSiteHelper.EnumComic(src, startFrom, length, callback);
                break;
            case ComicSrc.SrcType.ST_NET_WNACG:
                wnacgSitHelper.EnumComic(src, startFrom, length, callback);
                break;
            case ComicSrc.SrcType.ST_TEST_SRC:
                /*
                    debug use
                */
                ComicInfo[] comicInfos = new ComicInfo[length];
                for(int i=0;i<length;++i) {
                    comicInfos[i]=ComicInfo.GetTestComicInfo();
                }
                callback.ComicDiscover(comicInfos);
                break;
            default:
                callback.ComicDiscover(null);
                break;
        }
    }
    /*
        搜尋函數，但是包含了關鍵字
     */
    static public void EnumComic(final ComicSrc src,
                                 final int startFrom,
                                 final int length,
                                 final String search,
                                 final NetSiteHelper.EnumCallback callback) {
        //  本功能只支援 local comic
        if(src.srcType != ComicSrc.SrcType.ST_LOCAL_FILE) {
            callback.ComicDiscover(null);
            return;
        }
        new Thread() {
            @Override
            public void run() {
                super.run();
                InnerEnumComic(src, startFrom, length, search, callback);
            }
        }.start();
    }
    static public void InnerEnumComic(ComicSrc src, int startFrom, int length, String search, NetSiteHelper.EnumCallback callback) {
        if(src.srcType != ComicSrc.SrcType.ST_LOCAL_FILE) {
            callback.ComicDiscover(null);
            return;
        }
        localComicSiteHelper.EnumComic(src, startFrom, length, search, callback);
    }
    static public boolean IsComicAvailable(ComicSrc src) {
        switch (src.srcType) {
            case ComicSrc.SrcType.ST_HISTORY:
                return false;
            case ComicSrc.SrcType.ST_LOCAL_FILE:
                return localComicSiteHelper.IsComicAvailable(src);
            case ComicSrc.SrcType.ST_NET_WNACG:
                return wnacgSitHelper.IsComicAvailable(src);
            case ComicSrc.SrcType.ST_TEST_SRC:
                return true;
            default:
                return false;
        }
    }
    static public ComicInfo RequestComicInfo(ComicSrc src) {
        switch (src.srcType) {
            case ComicSrc.SrcType.ST_HISTORY:
                return null;
            case ComicSrc.SrcType.ST_LOCAL_FILE:
                return localComicSiteHelper.RequestComicInfo(src);
            case ComicSrc.SrcType.ST_NET_WNACG:
                return wnacgSitHelper.RequestComicInfo(src);
            case ComicSrc.SrcType.ST_TEST_SRC:
                return ComicInfo.GetTestComicInfo();
            default:
                return null;
        }
    }
}
