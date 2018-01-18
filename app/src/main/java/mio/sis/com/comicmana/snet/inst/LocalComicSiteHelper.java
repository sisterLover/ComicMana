package mio.sis.com.comicmana.snet.inst;

import android.util.Log;

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
    /*public void EnumComic(final ComicSrc src, final int startFrom, final int length, final EnumCallback callback) {
        new Thread() {
            @Override
            public void run() {
                InnerEnumComic(src, startFrom, length, callback);
            }
        }.start();
    }*/
    public void /*Inner*/EnumComic(ComicSrc src, int startFrom, int length, EnumCallback callback) {
        if(src.srcType != ComicSrc.SrcType.ST_LOCAL_FILE) {
            callback.ComicDiscover(null);
            return;
        }
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
        ComicInfo[] result = new ComicInfo[resultFiles.length];
        for (int i = 0; i < resultFiles.length; ++i) {
            result[i] = new ComicInfo();
            result[i].src.srcType = ComicSrc.SrcType.ST_LOCAL_FILE;
            result[i].src.path = resultFiles[i].toString();
            LocalStorage.LoadComicInfo(result[i]);
        }
        callback.ComicDiscover(result);
    }
    public void EnumComic(ComicSrc src, int startFrom, int length, String string, EnumCallback callback) {
        if (src.srcType != ComicSrc.SrcType.ST_LOCAL_FILE) {
            callback.ComicDiscover(null);
            return;
        }
        ArrayList<File> resultFiles = new ArrayList<>();
        try {
            Lock();
            for (int i = 0; i < comicDirs.size(); ++i) {
                File file = comicDirs.get(i);
                String fileName = file.getName();
                if (fileName.contains(string)) {
                    resultFiles.add(file);
                    if (resultFiles.size() >= startFrom + length) break;
                }
            }
            Unlock();
        } catch (InterruptedException e) {

        }
        int resultLength = Math.min(length, resultFiles.size() - startFrom);
        if (resultLength <= 0 || resultFiles.size() == 0) {
            callback.ComicDiscover(null);
            return;
        }
        ComicInfo[] result = new ComicInfo[resultLength];
        for (int i = 0; i < resultLength; ++i) {
            result[i] = new ComicInfo();
            result[i].src.srcType = ComicSrc.SrcType.ST_LOCAL_FILE;
            result[i].src.path = resultFiles.get(startFrom + i).toString();
            LocalStorage.LoadComicInfo(result[i]);
        }
        callback.ComicDiscover(result);
    }
    static public void LoadComicDir() {
        new Thread() {
            @Override
            public void run() {
                InnerLoadComicDir();
            }
        }.start();
    }
    static private void InnerLoadComicDir() {
        try {
            /*  為了保證 Enum 的時候不會因為正在取得 comicDir 而導致回傳 null
                只要正在取得 comicDir 就直接 Lock
             */
            Lock();
            comicDirs = LocalStorage.ScanContainerDirectory(MainActivity.manaConfig.containterDirs);
            Unlock();
            /*Log.d("LS_TAG", "InnerLoadComicDir finished");
            for(File file : comicDirs) {
                Log.d("LS_TAG", "Local Comic Found = " + file.toString());
            }*/
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

    @Override
    public boolean IsComicAvailable(ComicSrc src) {
        if(src.srcType != ComicSrc.SrcType.ST_LOCAL_FILE) return false;
        File dir = new File(src.path);
        if(!dir.exists() || !dir.isDirectory()) return false;
        int type = LocalStorage.GetComicDirectoryType(dir.listFiles());
        return type == LocalStorage.COMIC_DIR_SINGLE || type == LocalStorage.COMIC_DIR_MULTIPLE;
    }

    @Override
    public ComicInfo RequestComicInfo(ComicSrc src) {
        if(src.srcType != ComicSrc.SrcType.ST_LOCAL_FILE) return null;
        ComicInfo comicInfo = new ComicInfo();
        comicInfo.src = src;
        LocalStorage.LoadComicInfo(comicInfo);
        return comicInfo;
    }
}
