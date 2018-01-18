package mio.sis.com.comicmana.snet.inst;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import mio.sis.com.comicmana.image.ImageLib;
import mio.sis.com.comicmana.sdata.ComicPosition;
import mio.sis.com.comicmana.sdata.ComicSrc;
import mio.sis.com.comicmana.sfile.LocalStorage;
import mio.sis.com.comicmana.snet.NetImageHelper;

/**
 * Created by Administrator on 2018/1/16.
 */

public class LocalComicImageHelper implements NetImageHelper {
    //static private Semaphore semaphore = new Semaphore(1);
    //static private void Lock() throws InterruptedException { semaphore.acquire(); }
    //static private void Unlock() { semaphore.release(); }

    @Override
    public void GetComicPage(ComicSrc src, ComicPosition position, ComicPageCallback callback) {
        if (src.srcType != ComicSrc.SrcType.ST_LOCAL_FILE) {
            callback.PageRecieve(null);
            return;
        }
        try {
            //Lock();
            File comicDirectory = new File(src.path);
            File[] childs = comicDirectory.listFiles();
            int directoryType = LocalStorage.GetComicDirectoryType(childs);
            switch (directoryType) {
                case LocalStorage.COMIC_DIR_SINGLE:
                    //Log.d("LS_TAG", "single directory detected");
                    if (position.chapter != 1) {
                        callback.PageRecieve(null);
                        break;
                    }
                    callback.PageRecieve(GetComicPageUnderDirectory(comicDirectory, position.page));
                    break;
                case LocalStorage.COMIC_DIR_MULTIPLE: {
                    //Log.d("LS_TAG", "multiple directory detected");
                    ArrayList<String> chapterPath = LocalStorage.ListChapterDir(childs);
                    if (position.chapter < 1 || position.chapter > chapterPath.size()) {
                        callback.PageRecieve(null);
                        break;
                    }
                    Bitmap result = GetComicPageUnderDirectory(
                            new File(comicDirectory, chapterPath.get(position.chapter - 1)),
                            position.page);
                    callback.PageRecieve(result);
                    break;
                }
                case LocalStorage.COMIC_DIR_NOT_DIR:
                    callback.PageRecieve(null);
                    break;
            }
            //Unlock();
        }
        finally {

        }
        /*catch (InterruptedException e) {

        }*/
    }
    /*
        ComicPosition 的 page 是 1 base
        ArrayList 是 0 base
     */
    private Bitmap GetComicPageUnderDirectory(File directory, int page) {
        ArrayList<String> pagePath = LocalStorage.ListComicPage(directory.listFiles());
        /*Log.d("LS_TAG", "List Comic Page Under " + directory.toString());
        for(int i=0;i<pagePath.size();++i) {
            Log.d("LS_TAG", "Page " + String.valueOf(i + 1) + " = " + pagePath.get(i));
        }*/
        if(page < 1 || page > pagePath.size()) return null;
        File bitmapFile = new File(directory, pagePath.get(page - 1));
        //Log.d("LS_TAG", "Read Bitmap " + bitmapFile.toString());
        return ImageLib.LoadFile(bitmapFile);
    }
}
