package mio.sis.com.comicmana.snet.inst;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import mio.sis.com.comicmana.MainActivity;
import mio.sis.com.comicmana.sdata.ComicInfo;
import mio.sis.com.comicmana.sdata.ComicSrc;
import mio.sis.com.comicmana.sfile.LocalStorage;
import mio.sis.com.comicmana.snet.NetSiteHelper;

/**
 * Created by Administrator on 2018/1/17.
 */

public class LocalComicSiteHelper implements NetSiteHelper {
    static private ArrayList<File> comicDirs = new ArrayList<>();
    static private Semaphore semaphore = new Semaphore(1);
    static private void Lock() throws InterruptedException { semaphore.acquire(); }
    static private void Unlock() { semaphore.release(); }
    @Override
    public void EnumComic(final ComicSrc src, final int startFrom, final int length, final EnumCallback callback) {
        new Thread() {
            @Override
            public void run() {
                InnerEnumComic(src, startFrom, length, callback);
            }
        }.start();
    }
    private void InnerEnumComic(ComicSrc src, int startFrom, int length, EnumCallback callback) {
        ComicInfo[] result = null;
        File[] resultFiles = null;
        try {
            Lock();
            int realLength = Math.min(length, comicDirs.size() - startFrom);
            if (realLength > 0) {
                resultFiles = new File[realLength];
                for (int i = 0; i < realLength; ++i) {
                    resultFiles[i] = comicDirs.get(startFrom + i);
                }
            }
            Unlock();
        } catch (InterruptedException e) {

        }
        if (resultFiles == null) {
            callback.ComicDiscover(null);
            return;
        }
        result = new ComicInfo[resultFiles.length];
        for (int i = 0; i < resultFiles.length; ++i) {
            result[i] = new ComicInfo();
            result[i].src.srcType = ComicSrc.SrcType.ST_LOCAL_FILE;
            result[i].src.path = resultFiles[i].toString();
            LocalStorage.LoadComicInfo(result[i]);
        }
        callback.ComicDiscover(result);
    }
    static public void LoadComicDir() {
        try {
            //  為了保證 Enum 的時候
            Lock();
            ArrayList<File> newComicDirs = LocalStorage.ScanContainerDirectory(MainActivity.manaConfig.containterDirs);
            comicDirs = newComicDirs;
            Unlock();
        }
        catch (InterruptedException e) {

        }
    }
    static public void ClearComicDir() {
        try {
            Lock();
            comicDirs.clear();
            Unlock();
        }
        catch (InterruptedException e) {

        }
    }
}
