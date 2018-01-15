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

        TEST_COMIC_INFO.AllocateChapter(3);

        TEST_COMIC_INFO.chapterInfo[1].pageCnt = 7;
        TEST_COMIC_INFO.chapterInfo[2].pageCnt = 8;
        TEST_COMIC_INFO.chapterInfo[3].pageCnt = 9;
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
}
