package mio.sis.com.comicmana.sfile;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;

import mio.sis.com.comicmana.other.SChar;
import mio.sis.com.comicmana.sdata.ComicConfig;
import mio.sis.com.comicmana.sdata.ComicInfo;
import mio.sis.com.comicmana.sdata.ComicSrc;

/**
 * Created by Administrator on 2018/1/2.
 */

public class LocalStorage {
    /*
        在此專案中，有六種目錄
        1. 程式目錄：APP_DIR
                    負責存放 config 檔案以及歷程紀錄檔
        2. 下載目錄：於APP_DIR底下，負責存放下載的漫畫
        3. container directory：包含 APP_DIR/APP_DOWNLOAD_DIR 和使用者設定的漫畫存放目錄
                    container 表示這些目錄是漫畫目錄的父目錄
        4. comic directory：存放漫畫檔案本體的目錄
                    基本的 comic directory 如下
                    [Directory Name]
                     |----picture file(s)
                     |----mana.cfg [optional]
                     |----thunbnail.png/thumbnail.cmp [optional]
                     或是
                    [Directory Name]
                     |----chapter directory(s) [以字典序決定 chpater 順序]
                     |    |----picture file(s)
                     |----mana.cfg [optional]
                     |----thunbnail.png/thumbnail.cmp [optional]
        5. chapter directory：存放特定章節的目錄，於 comic directory 底下
        6. cache directory：存放漫畫快取的目錄
     */
    static final String APP_DIR = "ComicMana";
    static final String APP_CONFIG_FILE = "comicmana.cfg";
    static final String APP_DOWNLOAD_DIR = "Download";
    static final String APP_CACHE_DIR = "Cache";
    static final String CACHE_CONFIG_FILE = "cache.rec";
    static final String HISTORY_FILE = "history.rec";

    static final String COMIC_THUMBNAIL_FILE = "thumbnail";
    static final String COMIC_CONFIG_FILE = "comic.cfg";

    static public final String APP_ALTER_EXTENSION = "cmp";    //  comic mana picture
    static public final String[] SUPPORT_EXTENSION = {"png" , "jpg", "jpeg", APP_ALTER_EXTENSION};

    static public final int
            COMIC_DIR_NOT_DIR = 0,      //  表示此路徑下並不是漫畫
            COMIC_DIR_SINGLE = 1,       //  表示此 comic directory 只有一章節，且圖片檔案直接放在目錄下
            COMIC_DIR_MULTIPLE = 2;     //  表示此 comic directory 可以含有多章節，每章節圖片放在 chapter directory 裡

    static private File basePath = null;
    static private boolean pathAvailable = false;

    /*
        取得基礎檔案路徑 sdcard/ComicMana
     */
    static public void UpdatePath() {
        if(pathAvailable) return;
        ArrayList<File> sdcard = SFile.GetSDCardDirs();
        ArrayList<File> candidate = new ArrayList<>();
        File path = null;
        for(File file : sdcard) {
            path = new File(file, APP_DIR);
            if(path.exists()) {
                if(path.isDirectory()) {
                    Log.d("LS_TAG", "found exist directory at " + path.toString());
                    basePath = path;
                    pathAvailable = true;
                    return;
                }
                //  exist but not directory = exist a file with same name
                //  so such path cannot be candidate if we want to build new directory
            }
            else {
                //  not exist path can be candidate of new directory
                candidate.add(path);
            }
        }
        if(candidate.size() == 0) {
            //  directory not found and cannot build new directory
            pathAvailable = false;
            Log.d("LS_TAG", "directory not found and cannot build");
            return;
        }
        pathAvailable = false;
        for(int i=0;i<candidate.size();++i) {
            path = candidate.get(i);
            Log.d("LS_TAG", "Try to build " + path.toString());
            if(path.mkdirs()) {
                basePath = path;
                pathAvailable = true;
                return;
            }
            Log.d("LS_TAG", "but fail");
        }
    }
    /*
        comicInfo.src 指向了 local storage，以此讀取漫畫資訊
     */
    static public void LoadComicInfo(ComicInfo comicInfo) {
        if(comicInfo.src.srcType != ComicSrc.SrcType.ST_LOCAL_FILE) return;
        File comicPath = new File(comicInfo.src.path);
        File[] childs = comicPath.listFiles();
        /*
            讀取 ComicConfig
         */
        comicInfo.name = comicPath.getName();
        comicInfo.TryReadComicConfig();
        /*
            填入 comicInfo
         */
        switch(GetComicDirectoryType(childs)) {
            case COMIC_DIR_SINGLE:
                comicInfo.AllocateChapter(1);
                //  計算目錄底下有多少張圖片
                comicInfo.chapterInfo[1].pageCnt = CountPicture(comicInfo, childs);
                comicInfo.chapterInfo[1].title = "1";
                if(comicInfo.chapterInfo[1].pageCnt==0) {
                    //  理論上要成為 COMIC_DIR_SINGLE 就一定有圖片，此區段理論上永遠不執行
                    comicInfo.chapterCnt = 0;
                    comicInfo.chapterInfo = null;
                }
                break;
            case COMIC_DIR_MULTIPLE: {
                ArrayList<PageCountPair> pages = new ArrayList<>();
                comicInfo.chapterCnt = 0;
                for (File file : childs) {
                    if (file.isDirectory()) {
                        PageCountPair pair = new PageCountPair();
                        pair.SetPath(file.getName());
                        pair.pageCnt = CountPicture(comicInfo, file.listFiles());
                        if (pair.pageCnt > 0) {
                            pages.add(pair);
                        }
                    }
                }
                Collections.sort(pages);
                if(pages.size() > 0) {
                    comicInfo.AllocateChapter(pages.size());

                    ComicInfo.ChapterInfo chapterInfo;
                    for (int i = 0; i < comicInfo.chapterCnt; ++i) {
                        chapterInfo = comicInfo.chapterInfo[i + 1];
                        chapterInfo.pageCnt = pages.get(i).pageCnt;
                        chapterInfo.title = chapterInfo.path = pages.get(i).path;
                    }
                    //  載入縮圖(如果存在的話)
                    CountPicture(comicInfo, childs);
                }
                break;
            }
            case COMIC_DIR_NOT_DIR:
                comicInfo.chapterCnt = 0;
                break;
        }
        /*
            如果沒有縮圖，就用第一章第一頁
         */
        if(comicInfo.thumbnail==null) {

        }
    }
    /*
        計算 childs 當中有多少張圖片(不含 thumbnail)
        遇到 thumbnail 自動讀入到 comicInfo
     */
    static private int CountPicture(ComicInfo comicInfo, File[] childs) {
        int result = 0;
        for(File file : childs) {
            if(file.isDirectory()) continue;
            String ext = SFile.GetExtension(file);
            if(ext == null) continue;
            if(SChar.StringInListIgnoreCase(ext, SUPPORT_EXTENSION)) {
                if (SFile.GetNameWithoutExtension(file).compareToIgnoreCase(COMIC_THUMBNAIL_FILE) == 0) {
                    //  讀入縮圖
                } else {
                    ++result;
                }
            }
        }
        return result;
    }
    /*
        取得本地端所有 comic directory 的路徑
        需要掃描 containerDirs 指向的目錄 + download 目錄
        containerDirs 為使用者設定的目錄
     */
    static public ArrayList<File> ScanContainerDirectory(ArrayList<File> containerDirs) {
        ArrayList<File> comicDirs = new ArrayList<>();
        for(File file : containerDirs) {
            if(!file.isDirectory()) continue;
            Log.d("LS_TAG", "Scanning " + file.toString());
            InnerScanContainerDirectory(file, comicDirs);
        }
        Collections.sort(comicDirs);
        return comicDirs;
    }
    /*
        ScanComicDirectory 的內部調用函數
        檢查 dir 是否為 comic directory，是的話加入到 comicDirs 中
        不是的話將 dir 視為 container directory 遞迴下去

        由於解除了 chapter directory 必須是數字名字的限制
        因此包含 SINGLE comic directory 的 container directory 看起來與 MULTIPLE comic directory 一樣
        因此 InnerScanContainerDirectory 不再提供遞迴搜尋功能
        將傳入參數直接視為 constainer direcotry 而不檢查其 ComicDirectoryType
     */
    static private void InnerScanContainerDirectory(File dir, ArrayList<File> comicDirs) {
        if(!dir.exists() || !dir.isDirectory()) return;
        File[] childs = dir.listFiles();
        /*int type = GetComicDirectoryType(childs);
        if(type == COMIC_DIR_SINGLE || type == COMIC_DIR_MULTIPLE) {
            comicDirs.add(dir);
            return;
        }*/
        //  如果當前目錄不是 comic directory，就將當前目錄視為 container directory 遞迴下去
        for(File file : childs) {
            if(!file.isDirectory()) continue;
            int type = GetComicDirectoryType(file.listFiles());
            if(type == COMIC_DIR_SINGLE || type == COMIC_DIR_MULTIPLE) {
                comicDirs.add(file);
            }
            //InnerScanContainerDirectory(file, comicDirs);
        }
    }
    /*
        傳入某個 directory 的子目錄/子檔案，判斷此 directory 是否為 comic directory
     */
    static public int GetComicDirectoryType(File[] childs) {
        for(File file : childs) {
            if(file.isDirectory()) {
                //  子資料夾
                if (GetComicDirectoryType(file.listFiles()) == COMIC_DIR_SINGLE)
                    return COMIC_DIR_MULTIPLE;
            }
            else {
                String ext = SFile.GetExtension(file);
                if(ext == null) continue;
                if(SChar.StringInListIgnoreCase(ext, SUPPORT_EXTENSION)) {
                    //  找到圖片副檔名，要確認是否為縮圖
                    if (SFile.GetNameWithoutExtension(file).compareToIgnoreCase(COMIC_THUMBNAIL_FILE) == 0) {
                        break;
                    }
                    //  找到非縮圖的圖片，表示這是圖片直接放在 comic directory 的漫畫
                    return COMIC_DIR_SINGLE;
                }
            }
        }
        return COMIC_DIR_NOT_DIR;
    }

    /*
        childs 是某個目錄底下的所有節點列表
        回傳已經排序好的漫畫頁"名稱"(非絕對路徑的意思)
        例如傳入 1.png 3.png 2.png
        則回傳 1.png 2.png 3.png
     */
    static public ArrayList<String> ListComicPage(File[] childs) {
        ArrayList<SortableString> sortableStrings = new ArrayList<>();
        for(File file : childs) {
            if(file.isDirectory()) continue;
            String fileName = file.getName();
            String ext = SFile.GetExtension(fileName);
            if(ext == null) continue;
            if(!SChar.StringInListIgnoreCase(ext, SUPPORT_EXTENSION)) continue;
            if(SFile.GetNameWithoutExtension(fileName).compareToIgnoreCase(COMIC_THUMBNAIL_FILE) == 0) continue;
            SortableString sortableString = new SortableString();
            sortableString.SetString(fileName);
            sortableStrings.add(sortableString);
        }
        Collections.sort(sortableStrings);
        ArrayList<String> result = new ArrayList<>();
        for(int i=0;i<sortableStrings.size();++i) {
            result.add(sortableStrings.get(i).GetString());
        }
        return result;
    }

    /*
        childs 是某個目錄底下的所有節點列表
        回傳已經排序好的漫畫章節目錄"名稱"(非絕對路徑的意思)
     */
    static public ArrayList<String> ListChapterDir(File[] childs) {
        ArrayList<SortableString> sortableStrings = new ArrayList<>();
        for(File file : childs) {
            if(!file.isDirectory()) continue;
            int type = GetComicDirectoryType(file.listFiles());
            if(type != COMIC_DIR_SINGLE) continue;;
            SortableString sortableString = new SortableString();
            sortableString.SetString(file.getName());
            sortableStrings.add(sortableString);
        }
        Collections.sort(sortableStrings);
        ArrayList<String> result = new ArrayList<>();
        for(int i=0;i<sortableStrings.size();++i) {
            result.add(sortableStrings.get(i).GetString());
        }
        return result;
    }


    static public File GetConfigFile() {
        UpdatePath();
        if(pathAvailable) return new File(basePath, APP_CONFIG_FILE);
        return null;
    }
    static public File GetHistoryFile() {
        UpdatePath();
        if(!pathAvailable) return null;
        return new File(basePath, HISTORY_FILE);
    }
    static public File GetComicConfigFile(File comicPath) {
        return new File(comicPath, COMIC_CONFIG_FILE);
    }
    static public File GetDownloadDir() {
        UpdatePath();
        if(!pathAvailable) return null;
        File dir = new File(basePath, APP_DOWNLOAD_DIR);
        if(!dir.exists()) dir.mkdirs();
        return dir;
    }
    static public File GetCacheDir() {
        UpdatePath();
        if(!pathAvailable) return null;
        File dir = new File(basePath, APP_CACHE_DIR);
        if(!dir.exists()) dir.mkdirs();
        return dir;
    }
    static public File GetCacheConfigFile() {
        File dir = GetCacheDir();
        if(!dir.exists()) return null;
        return new File(dir, CACHE_CONFIG_FILE);
    }
    static class PageCountPair implements Comparable<PageCountPair> {
        public int pageCnt;
        private String path;
        private int num;

        public void SetPath(String path) {
            this.path = path;
            num = SChar.GetNumber(SFile.GetNameWithoutExtension(path).toCharArray());
        }
        public String GetPath() {
            return path;
        }
        /*
            如果兩者皆為純數字，則進行數字比較
            用來使
            1 2 3 ... 99 可以和
            01 02 03 ... 99 一樣正確排列
         */
        @Override
        public int compareTo(@NonNull PageCountPair pageCountPair) {
            if(num != -1 && pageCountPair.num != -1) {
                if(num < pageCountPair.num) return -1;
                if(num > pageCountPair.num) return 1;
                return 0;
            }
            return path.compareToIgnoreCase(pageCountPair.path);
        }
    }
    static class SortableString implements Comparable<SortableString> {
        private String string;
        private int num;

        public void SetString(String string) {
            this.string = string;
            num = SChar.GetNumber(SFile.GetNameWithoutExtension(string).toCharArray());
        }
        public String GetString() {
            return string;
        }
        @Override
        public int compareTo(@NonNull SortableString sortableString) {
            if(num != -1 && sortableString.num != -1) {
                if(num < sortableString.num) return -1;
                if(num > sortableString.num) return 1;
                return 0;
            }
            return string.compareToIgnoreCase(sortableString.string);
        }
    }
}
