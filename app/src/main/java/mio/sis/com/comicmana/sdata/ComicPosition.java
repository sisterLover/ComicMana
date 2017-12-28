package mio.sis.com.comicmana.sdata;

/**
 * Created by Administrator on 2017/12/26.
 */

public class ComicPosition {
    /*
        ComicPosition 描述一個漫畫頁面
        也就是給定 ComicSrc 和 ComicPosition 後，即可唯一定位出一頁漫畫
        chapter 跟 page 皆為 1 base
     */
    public int chapter, page;

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
}
