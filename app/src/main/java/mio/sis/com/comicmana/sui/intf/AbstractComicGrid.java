package mio.sis.com.comicmana.sui.intf;

import android.content.Context;
import android.view.View;

import mio.sis.com.comicmana.sdata.ComicInfo;
import mio.sis.com.comicmana.sdata.ComicSrc;

/**
 * Created by Administrator on 2018/1/14.
 */

public interface AbstractComicGrid {
    /*
        Comic Grid

        -------------------------
        |       Grid View       |
        -------------------------
        | <- | Page Number | -> |
        -------------------------

        LinearLayout(vertical)
        |-Grid View
        |-LinearLayout(horizontal)
          |-Button
          |-TextView
          |-Button
     */

    /*
        Comic Grid 需要有以下建構子
        comicSrc 指明了此 Grid 中的漫畫是源自何種漫畫來源
        有了此資訊 Grid 即可直接使用 ComicInfoCache.EnumComic 裡提供的
     */
    //AbstractComicGrid(ComicSrc comicSrc);
    /*
        產生 Grid UI 元件並回傳 grid 的 root layout
     */
    View InflateView(Context context);
    /*
        取得已經產生的 root layout
     */
    View GetView();
    /*
        釋放 root layout
        被呼叫時保證 root layout 已經從其他 layout 中移除
     */
    void FreeView();
    /*
        設定動作 callback
     */
    void SetActionCallback(ActionCallback actionCallback);
    /*
        設定漫畫搜尋關鍵字
        當 string = null 時，表示取消搜尋
     */
    void SetSearch(String string);

    interface ActionCallback {
        /*
            外部 UI 關心的動作只有當漫畫被按下
            傳入被按下的漫畫的 comicInfo
         */
        void OnComicClick(ComicInfo comicInfo);
    }
}
