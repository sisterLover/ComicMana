package mio.sis.com.comicmana.snet.inst;

import android.os.SystemClock;

import mio.sis.com.comicmana.scache.DefaultPageCache;
import mio.sis.com.comicmana.sdata.ComicPosition;
import mio.sis.com.comicmana.sdata.ComicSrc;
import mio.sis.com.comicmana.snet.NetImageHelper;

/**
 * Created by Administrator on 2017/12/29.
 */

public class TestComicImageHelper implements NetImageHelper {

    @Override
    public void GetComicPage(ComicSrc src, ComicPosition position, ComicPageCallback callback) {
        for(int i=0;i<=5;++i) {
            callback.UpdateProgress(20*i);
            SystemClock.sleep(500);
        }
        callback.PageRecieve(DefaultPageCache.GetTestComic(position.chapter, position.page));
    }

    class InnerThread extends Thread {
        ComicPosition position;
        ComicPageCallback callback;
        public InnerThread(ComicPosition position, ComicPageCallback callback) {
            this.position = position;
            this.callback = callback;
        }
        @Override
        public void run() {
            super.run();

        }
    }
}
