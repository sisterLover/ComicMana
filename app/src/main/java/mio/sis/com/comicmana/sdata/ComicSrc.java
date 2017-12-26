package mio.sis.com.comicmana.sdata;

/**
 * Created by Administrator on 2017/12/24.
 */

public class ComicSrc {
    /*
        comic source 描述一個漫畫的來源訊息
        來源訊息為在SD卡或網路定位漫畫資料位置的最低訊息
        也就是知道某漫畫之來源訊息後，完全可以取得其他漫畫訊息和漫畫圖片
     */
    public class SrcType {
        public static final int ST_NULL = 0,
        ST_LOCAL_FILE = 1,
        /*
            path = SD card path(absolute)
         */
        ST_NET_EX = 2;
        /*
            path = URL
         */
    }
    //  base info, for locating resource
    int srcType;
    String path;

    //  function
}
