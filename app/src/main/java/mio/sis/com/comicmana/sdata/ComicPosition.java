package mio.sis.com.comicmana.sdata;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mio.sis.com.comicmana.sfile.ReadWritable;

/**
 * Created by Administrator on 2017/12/26.
 */

public class ComicPosition implements ReadWritable {
    static public int CHAPTER_NOT_READ_YET = -1;    //  chapter = -1 表示從沒讀過這本漫畫
    /*
        ComicPosition 描述一個漫畫頁面
        也就是給定 ComicSrc 和 ComicPosition 後，即可唯一定位出一頁漫畫
        chapter 跟 page 皆為 1 base
     */
    public int chapter, page;

    public ComicPosition() {
        chapter = CHAPTER_NOT_READ_YET;
    }
    /*
        if this > position => return 1
           this < position => return -1
           this = position => return 0
     */
    public int Compare(ComicPosition position) {
        if(chapter > position.chapter) return 1;
        if(chapter < position.chapter) return -1;
        if(page > position.page) return 1;
        if(page < position.page) return -1;
        return 0;
    }
    public boolean Equal(ComicPosition position) {
        return chapter == position.chapter && page == position.page;
    }
    public void Copy(ComicPosition position) {
        chapter = position.chapter;
        page = position.page;
    }

    @Override
    public void WriteStream(DataOutputStream stream) throws IOException {
        stream.writeInt(chapter);
        stream.writeInt(page);
    }

    @Override
    public void ReadStream(DataInputStream stream) throws IOException {
        chapter = stream.readInt();
        page = stream.readInt();
    }
}
