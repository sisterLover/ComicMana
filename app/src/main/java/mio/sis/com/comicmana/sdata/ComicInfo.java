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
}
