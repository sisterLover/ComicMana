package mio.sis.com.comicmana.sdata;

import android.graphics.Bitmap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import mio.sis.com.comicmana.MainActivity;
import mio.sis.com.comicmana.sfile.LocalStorage;

/**
 * Created by Administrator on 2017/12/26.
 */

public class ComicInfo {
    /*
        comic info 描述一個漫畫所有訊息
     */
    public ComicSrc src;

    public String name;        //  漫畫名稱
    public int chapterCnt;     //  章節數
    //public int[] chapterPages;
    public ChapterInfo[] chapterInfo;
    /*
      每個章節有多少頁 chapterPages 存取的時候是以 1 為基底(直觀存取章節)
      因此 chapterCnt = chapterPages.length + 1
       */
    public Bitmap thumbnail;   //  縮圖

    public ComicPosition lastPosition;
    public STime lastOpenTime;  //  上次開啟時間

    public ComicInfo() {
        src = new ComicSrc();
        thumbnail = null;
        lastPosition = new ComicPosition();
    }

    public void AllocateChapter(int num) {
        chapterCnt = num;
        chapterInfo = new ChapterInfo[chapterCnt + 1];
        for (int i = 1; i <= chapterCnt; ++i) {
            chapterInfo[i] = new ChapterInfo(i);
        }
    }
    public int GetChapterPageCnt(int index) {
        return chapterInfo[index].pageCnt;
    }

    static public ComicInfo TEST_COMIC_INFO = null;
    static public ComicInfo GetTestComicInfo() {
        if(TEST_COMIC_INFO != null) return TEST_COMIC_INFO;
        TEST_COMIC_INFO = new ComicInfo();
        TEST_COMIC_INFO.src.srcType = ComicSrc.SrcType.ST_TEST_SRC;

        TEST_COMIC_INFO.AllocateChapter(11);

        TEST_COMIC_INFO.name = "Test Comic for Sister";
        TEST_COMIC_INFO.chapterInfo[1].pageCnt = 3;
        TEST_COMIC_INFO.chapterInfo[1].title = "EX1";
        TEST_COMIC_INFO.chapterInfo[2].pageCnt = 2;
        TEST_COMIC_INFO.chapterInfo[2].title = "SIS2";
        TEST_COMIC_INFO.chapterInfo[3].pageCnt = 1;
        TEST_COMIC_INFO.chapterInfo[3].title = "EX3";
        TEST_COMIC_INFO.chapterInfo[4].pageCnt = 6;
        TEST_COMIC_INFO.chapterInfo[5].pageCnt = 7;
        TEST_COMIC_INFO.chapterInfo[6].pageCnt = 9;
        TEST_COMIC_INFO.chapterInfo[7].pageCnt = 8;
        TEST_COMIC_INFO.chapterInfo[8].pageCnt = 6;
        TEST_COMIC_INFO.chapterInfo[9].pageCnt = 7;
        TEST_COMIC_INFO.chapterInfo[10].pageCnt = 8;
        TEST_COMIC_INFO.chapterInfo[11].pageCnt = 3;

        return TEST_COMIC_INFO;
    }

    static public class ChapterInfo {
        public int pageCnt;    //  此 chapter 有幾頁
        public String path;
        /*
          此 chapter 的路徑 (SD卡或網路端)，指向SD卡路徑時，若是直接放在 comic directroy 底下的漫畫
          則 path 無效，放在 chapter 底下的表示此 chapter 路徑為 new File(src.path, this.path)
          */
        public String title;   //  顯示在 chatper selector 的 chapter button 上的文字

        ChapterInfo(int chapterIndex) {
            pageCnt = 0;
            path = null;
            title = String.valueOf(chapterIndex);
        }
    }

    public void TryReadComicConfig() {
        if(src.srcType != ComicSrc.SrcType.ST_LOCAL_FILE) return;

        try {
            FileInputStream fileInputStream = new FileInputStream(LocalStorage.GetComicConfigFile(new File(src.path)));
            DataInputStream dataInputStream = new DataInputStream(fileInputStream);
            ComicConfig comicConfig = new ComicConfig();
            comicConfig.ReadStream(dataInputStream);

            name = comicConfig.name;
            lastOpenTime = comicConfig.lastOpenTime;
            lastPosition = comicConfig.lastPosition;

            dataInputStream.close();
            fileInputStream.close();
        }
        catch (Exception e) {
            //  可能 file not found
        }
    }
    public void TryWriteComicConfig() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                InnerTryWriteComicConfig();
            }
        }.start();
    }
    private void InnerTryWriteComicConfig() {
        if(src.srcType != ComicSrc.SrcType.ST_LOCAL_FILE) return;

        try {
            File comicConfigFile = LocalStorage.GetComicConfigFile(new File(src.path));
            if(!MainActivity.ssaf.HavePermission(comicConfigFile)) {
                MainActivity.ssaf.RequestPermission();
            }
            OutputStream outputStream = MainActivity.ssaf.OpenOutputStream(comicConfigFile);
            if(outputStream!=null) {
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                ComicConfig comicConfig = new ComicConfig();
                comicConfig.name = name;
                comicConfig.lastOpenTime = lastOpenTime;
                comicConfig.lastPosition = lastPosition;

                comicConfig.WriteStream(dataOutputStream);

                dataOutputStream.close();
                outputStream.close();
            }
        }
        catch (Exception e) {

        }
    }
}
