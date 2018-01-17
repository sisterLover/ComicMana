package mio.sis.com.comicmana.scache;

import android.graphics.Bitmap;

import java.util.concurrent.Semaphore;

import mio.sis.com.comicmana.sdata.ComicPosition;
import mio.sis.com.comicmana.sdata.ComicSrc;
import mio.sis.com.comicmana.snet.inst.LocalComicImageHelper;
import mio.sis.com.comicmana.snet.NetImageHelper;
import mio.sis.com.comicmana.snet.inst.TestComicImageHelper;
import mio.sis.com.comicmana.sui.comp.SImagePage;

/**
 * Created by Administrator on 2017/12/26.
 */

public class ImageCache {
    /*
        ImageCache 負責在記憶體-SD卡-網路三者之間存取圖片
     */
    static TestComicImageHelper testComicImageHelper = new TestComicImageHelper();
    static LocalComicImageHelper localComicImageHelper = new LocalComicImageHelper();

    static Semaphore semaphore = new Semaphore(1);
    static void Lock() throws InterruptedException { semaphore.acquire(); }
    static void Unlock() { semaphore.release(); }

    static MemoryCache memoryCache = new MemoryCache();


    /*
        GetComicPage 取得漫畫的某一頁並讀入至 SImgPage 裡
        此函數會在 UI thread 被呼叫
        圖片並不會在此函數被設定，而是藉由呼叫 page.SetImage 設定
     */
    static public void GetComicPage(SImagePage page, ComicSrc src, ComicPosition position) {
        new InnerThread(page, src, position).start();
    }
    /*
        只應該在非 UI thread 執行
     */
    static private void InnerGetComicPage(SImagePage page, ComicSrc src, ComicPosition position) {
        Bitmap bitmap = memoryCache.FindCache(src, position);
        if (bitmap != null) {
            page.PostImage(bitmap);
            return;
        }
        switch (src.srcType) {
            case ComicSrc.SrcType.ST_TEST_SRC:
                //  測試漫畫不用快取
                testComicImageHelper.GetComicPage(src, position, new DefaultImageHelper(page));
                break;
            case ComicSrc.SrcType.ST_LOCAL_FILE:

                break;
        }
    }
    static private class InnerThread extends Thread {
        SImagePage page;
        ComicSrc src;
        ComicPosition position;
        InnerThread(SImagePage page, ComicSrc src, ComicPosition position) {
            this.page = page;
            this.src = src;
            this.position = position;
        }
        @Override
        public void run() {
            InnerGetComicPage(page, src, position);
        }
    }
    /*
        當某頁漫畫進入 SIP_READY_OUT 時，會將 bitmap 交由 cache 保管
        固定 bitmap 存入後會將最先存入之 bitmap 從 cache 刪除
     */
    static public void CacheBitmap(ComicSrc src, ComicPosition position, Bitmap bitmap) {

    }

    static class ImageCacheImageHelper implements NetImageHelper.ComicPageCallback {
        private DefaultImageHelper defaultImageHelper;
        private ComicSrc comicSrc;
        private ComicPosition comicPosition;

        public ImageCacheImageHelper(SImagePage page, ComicSrc comicSrc, ComicPosition comicPosition) {
            defaultImageHelper = new DefaultImageHelper(page);
            this.comicSrc = comicSrc;
            this.comicPosition = comicPosition;
        }
        @Override
        public void PageRecieve(Bitmap bitmap) {
            Bitmap scaleBitmap = ImageCache.memoryCache.PushCache(comicSrc, comicPosition, bitmap);
            defaultImageHelper.PageRecieve(scaleBitmap);
        }

        @Override
        public void UpdateProgress(int percent) {
            defaultImageHelper.UpdateProgress(percent);
        }
    }
}
