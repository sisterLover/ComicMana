package mio.sis.com.comicmana.sui.intf;

import android.content.Context;
import android.view.View;

/**
 * Created by Administrator on 2018/1/14.
 */

public interface StackableView {
    /*
        渲染 view
     */
    View InflateView(Context context);
    View GetView();
    void FreeView();
}
