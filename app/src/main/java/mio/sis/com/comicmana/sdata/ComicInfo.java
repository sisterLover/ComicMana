package mio.sis.com.comicmana.sdata;

/**
 * Created by Administrator on 2017/12/24.
 */

public class ComicInfo {
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

    //  comic info
    int chapters, pages;

    //  function
}
