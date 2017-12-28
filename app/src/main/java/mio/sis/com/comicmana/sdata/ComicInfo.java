package mio.sis.com.comicmana.sdata;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2017/12/26.
 */

public class ComicInfo {
    /*
        comic info 描述一個漫畫所有訊息
     */
    ComicSrc src;

    String name;        //  漫畫名稱
    int chapterCnt;     //  章節數
    int[] chapterPages; //  每個章節有多少頁
    Bitmap thumbnail;   //  縮圖

    STime updateTime, lastOpenTime;
}
