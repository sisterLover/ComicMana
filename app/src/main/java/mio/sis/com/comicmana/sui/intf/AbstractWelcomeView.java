package mio.sis.com.comicmana.sui.intf;

import android.content.Context;
import android.view.View;

import mio.sis.com.comicmana.sdata.ComicInfo;

/**
 * Created by Administrator on 2018/1/17.
 */

public interface AbstractWelcomeView {
    /*
        WelcomeView 要有此建構子取得顯示的 comicInfo
        comicInfos = null 或 length = 0 表示沒有歷史瀏覽資訊
        應該要顯示一個預設歡迎畫面
     */
    //AbstractWelcomeView(ComicInfo[] comicInfos)
    /*
        產生 Welcome UI 元件並回傳 root layout
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

    interface ActionCallback {
        /*
            外部 UI 關心的動作只有當漫畫被按下
            傳入被按下的漫畫的 comicInfo
         */
        void OnComicClick(ComicInfo comicInfo);
    }
}
