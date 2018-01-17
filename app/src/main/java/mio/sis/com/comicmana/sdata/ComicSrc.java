package mio.sis.com.comicmana.sdata;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mio.sis.com.comicmana.sfile.ReadWritable;
import mio.sis.com.comicmana.sfile.SFile;

/**
 * Created by Administrator on 2017/12/24.
 */

public class ComicSrc implements ReadWritable {
    /*
        comic source 描述一個漫畫的來源訊息
        來源訊息為在SD卡或網路定位漫畫資料位置的最低訊息
        也就是知道某漫畫之來源訊息後，完全可以取得其他漫畫訊息和漫畫圖片
     */
    static public class SrcType {
        public static final int ST_NULL = 0,
        ST_TEST_SRC = 1,
        /*
            path = unuse
         */
        ST_LOCAL_FILE = 10,
        /*
            path = SD card path(absolute)
         */
        ST_HISTORY = 11,
        /*
            path = unuse
            這個類型只用於 ComicInfoCache.EnumComic 請求回傳歷史紀錄
            歷史紀錄的 ComicInfo 會是該本漫畫原本的 SrcType
         */

        ST_NET_EX = 100,
        /*
            path = URL
         */
        ST_NET_WNACG = 101;
        /*
            path = URL
         */
    }
    //  base info, for locating resource
    public int srcType;
    public String path;

    public ComicSrc() {
        srcType = SrcType.ST_NULL;
        path = null;
    }
    public boolean Equal(ComicSrc src) {
        return srcType == src.srcType && path.compareTo(src.path) == 0;
    }
    public void Copy(ComicSrc src) {
        srcType = src.srcType;
        path = src.path;
    }

    @Override
    public void WriteStream(DataOutputStream stream) throws IOException {
        stream.writeInt(srcType);
        SFile.WriteStringToStream(path, stream);
    }

    @Override
    public void ReadStream(DataInputStream stream) throws IOException {
        srcType = stream.readInt();
        path = SFile.ReadStringFromStream(stream);
    }
}
