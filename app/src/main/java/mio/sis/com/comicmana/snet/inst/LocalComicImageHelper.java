package mio.sis.com.comicmana.snet.inst;

import android.graphics.Bitmap;

import java.io.File;
import java.util.ArrayList;

import mio.sis.com.comicmana.image.ImageLib;
import mio.sis.com.comicmana.sdata.ComicPosition;
import mio.sis.com.comicmana.sdata.ComicSrc;
import mio.sis.com.comicmana.sfile.LocalStorage;
import mio.sis.com.comicmana.snet.NetImageHelper;

/**
 * Created by Administrator on 2018/1/16.
 */

public class LocalComicImageHelper implements NetImageHelper {
    @Override
    public void GetComicPage(ComicSrc src, ComicPosition position, ComicPageCallback callback) {
        if (src.srcType != ComicSrc.SrcType.ST_LOCAL_FILE) {
            callback.PageRecieve(null);
            return;
        }

        File comicDirectory = new File(src.path);
        File[] childs = comicDirectory.listFiles();
        int directoryType = LocalStorage.GetComicDirectoryType(childs);
        switch (directoryType) {
            case LocalStorage.COMIC_DIR_SINGLE:
                if (position.chapter != 1) {
                    callback.PageRecieve(null);
                    break;
                }
                callback.PageRecieve(GetComicPageUnderDirectory(comicDirectory, position.page));
                break;
            case LocalStorage.COMIC_DIR_MULTIPLE: {
                ArrayList<String> chapterPath = LocalStorage.ListChapterDir(childs);
                if (position.chapter >= chapterPath.size()) {
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
    }
    /*
        ComicPosition 的 page 是 1 base
        ArrayList 是 0 base
     */
    private Bitmap GetComicPageUnderDirectory(File directory, int page) {
        ArrayList<String> pagePath = LocalStorage.ListComicPage(directory.listFiles());

        if(page >= pagePath.size()) return null;
        File bitmapFile = new File(directory, pagePath.get(page - 1));
        return ImageLib.LoadFile(bitmapFile);
    }
}
