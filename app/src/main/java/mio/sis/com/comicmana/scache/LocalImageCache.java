package mio.sis.com.comicmana.scache;

import android.graphics.Bitmap;

import java.util.ArrayList;

import mio.sis.com.comicmana.sdata.ComicPosition;
import mio.sis.com.comicmana.sdata.ComicSrc;

/**
 * Created by Administrator on 2018/1/17.
 */

public class LocalImageCache {
    ArrayList<Entry> entries;

    public void PushCache(ComicSrc comicSrc, ComicPosition comicPosition, Bitmap bitmap) {

    }
    public Bitmap FindCache(ComicSrc comicSrc, ComicPosition comicPosition) {
        return null;
    }

    static private class Entry {
        ComicSrc comicSrc;
        ComicPosition comicPosition;
        String fileName;
    }
}
