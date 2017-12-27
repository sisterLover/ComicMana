package mio.sis.com.comicmana.snet;

import mio.sis.com.comicmana.sdata.ComicInfo;
import mio.sis.com.comicmana.sdata.ComicSrc;

/**
 * Created by Administrator on 2017/12/27.
 */

public class Demo implements NetSiteHelper {
    @Override
    public void EnumComic(ComicSrc src, int startFrom, int length, EnumCallback callback) {
        switch(src.srcType) {
            case ComicSrc.SrcType.ST_NULL:
            case ComicSrc.SrcType.ST_LOCAL_FILE:
                callback.ComicDiscover(null);
                break;
            case ComicSrc.SrcType.ST_NET_EX:
                new InnerThread(startFrom, length, callback).start();
                break;
        }
    }
    class InnerThread extends Thread {
        int startFrom, length; EnumCallback callback;
        InnerThread(int startFrom, int length, EnumCallback callback) {
            this.startFrom = startFrom; this.length = length; this.callback = callback;
        }
        @Override
        public void run() {
            super.run();
            //爬出陣列 info
            ComicInfo[] info = null;
            callback.ComicDiscover(info);
        }
    }
}
