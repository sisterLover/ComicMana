package mio.sis.com.comicmana.sdata;

import android.graphics.Bitmap;

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
    public int[] chapterPages;
    /*
      每個章節有多少頁 chapterPages 存取的時候是以 1 為基底(直觀存取章節)
      因此 chapterCnt = chapterPages.length + 1
       */
    public Bitmap thumbnail;   //  縮圖

    public STime updateTime, lastOpenTime;

    public ComicInfo() {
        src = new ComicSrc();
    }

    static public ComicInfo TEST_COMIC_INFO = null;
    static public ComicInfo GetTestComicInfo() {
        if(TEST_COMIC_INFO != null) return TEST_COMIC_INFO;
        TEST_COMIC_INFO = new ComicInfo();
        TEST_COMIC_INFO.src.srcType = ComicSrc.SrcType.ST_TEST_SRC;
        TEST_COMIC_INFO.chapterCnt = 3;
        TEST_COMIC_INFO.chapterPages = new int[TEST_COMIC_INFO.chapterCnt+1];

        TEST_COMIC_INFO.chapterPages[1] = 7;
        TEST_COMIC_INFO.chapterPages[2] = 8;
        TEST_COMIC_INFO.chapterPages[3] = 9;
        /*TEST_COMIC_INFO.chapterPages[4] = 6;
        TEST_COMIC_INFO.chapterPages[5] = 7;
        TEST_COMIC_INFO.chapterPages[6] = 9;
        TEST_COMIC_INFO.chapterPages[7] = 8;
        TEST_COMIC_INFO.chapterPages[8] = 6;
        TEST_COMIC_INFO.chapterPages[9] = 7;
        TEST_COMIC_INFO.chapterPages[10] = 8;
        */
        return TEST_COMIC_INFO;
    }
}
