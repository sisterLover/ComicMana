package mio.sis.com.comicmana.sui.intf;

import android.content.Context;
import android.view.View;

import mio.sis.com.comicmana.sdata.ComicInfo;

/**
 * Created by Administrator on 2018/1/17.
 */

public interface AbstractSearchBar {
    /*
        產生 SearchBae UI 元件並回傳 root layout
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

    interface ActionCallback {
        /*
            外部 UI 關心的動作只有需要啟動搜尋的時候
            傳入搜尋關鍵字
         */
        void OnSearch(String keyWord);
    }
}
