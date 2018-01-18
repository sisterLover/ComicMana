package mio.sis.com.comicmana.sui.inst;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import mio.sis.com.comicmana.MainActivity;
import mio.sis.com.comicmana.R;
import mio.sis.com.comicmana.sdata.ComicInfo;
import mio.sis.com.comicmana.sdata.STime;
import mio.sis.com.comicmana.sui.comp.sszpview.SSZPView;
import mio.sis.com.comicmana.sui.comp.sszpview.SSZPViewClickListener;
import mio.sis.com.comicmana.sui.intf.StackableView;
import mio.sis.com.comicmana.sui.intf.ViewStack;

/**
 * Created by Administrator on 2018/1/14.
 */

public class ComicView implements StackableView {
    private ViewStack viewStack;
    private ConstraintLayout root;
    private LinearLayout sszp_parent, tooltipTop, tooltipBottom;
    private SSZPView sszpView;
    private ComicInfo comicInfo;

    /*
        載入 comicInfo 的漫畫，並從 lastPosition 開始
     */
    public ComicView(ViewStack viewStack, ComicInfo comicInfo) {
        this.viewStack = viewStack;
        this.comicInfo = comicInfo;

        MainActivity.historyRecord.PushRecord(comicInfo.src);
    }

    @Override
    public View InflateView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        root = (ConstraintLayout)inflater.inflate(R.layout.comic_view_layout, null);

        sszp_parent = root.findViewById(R.id.comic_view_sszp_parent);
        sszpView = new SSZPView(context);
        sszpView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        sszpView.setOrientation(LinearLayout.VERTICAL);
        sszpView.SetClickListener(new SSZPClickListener());
        sszpView.PostComicInfo(comicInfo, comicInfo.lastPosition);

        sszp_parent.addView(sszpView);

        tooltipTop = root.findViewById(R.id.comic_view_tooltip_top);
        tooltipBottom = root.findViewById(R.id.comic_view_tooltip_bottom);

        return root;
    }

    @Override
    public View GetView() {
        return root;
    }

    @Override
    public void FreeView() {
        comicInfo.lastOpenTime = new STime();
        comicInfo.lastOpenTime.GetCurrentTime();
        comicInfo.lastPosition = sszpView.GetCurrentPosition();
        comicInfo.TryWriteComicConfig();    //  若是非 LOCAL 漫畫則此函數會直接回傳

        sszp_parent.removeAllViews();
        sszpView = null;
        sszp_parent = null;
        tooltipTop.removeAllViews();
        tooltipTop = null;
        tooltipBottom.removeAllViews();
        tooltipBottom = null;
        root = null;
    }

    @Override
    public boolean OnBackPress() {
        return true;
    }

    private void OnSSZPClick(int x, int y, int width, int height) {

    }

    private class SSZPClickListener implements SSZPViewClickListener {
        @Override
        public void OnClick(int x, int y, int width, int height) {
            OnSSZPClick(x, y, width, height);
        }
    }
}
