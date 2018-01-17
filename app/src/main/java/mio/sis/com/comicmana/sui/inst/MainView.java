package mio.sis.com.comicmana.sui.inst;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import mio.sis.com.comicmana.R;
import mio.sis.com.comicmana.mine.Grid;
import mio.sis.com.comicmana.sdata.ComicInfo;
import mio.sis.com.comicmana.sdata.ComicSrc;
import mio.sis.com.comicmana.sui.intf.AbstractComicGrid;
import mio.sis.com.comicmana.sui.intf.StackableView;
import mio.sis.com.comicmana.sui.intf.ViewStack;

/**
 * Created by Administrator on 2018/1/17.
 */

public class MainView implements StackableView {
    static private final int STATE_INI = 0,
    STATE_HISTORY = 1,
    STATE_LOCAL = 2,
    STATE_NET = 3;

    private Context context;
    private ViewStack viewStack;
    private LinearLayout root, grid_parent;
    private int currentState;

    public MainView(ViewStack viewStack) {
        this.viewStack = viewStack;
        currentState = STATE_INI;
    }

    @Override
    public View InflateView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        root = (LinearLayout) inflater.inflate(R.layout.main_view_layout, null);

        grid_parent = root.findViewById(R.id.main_view_grid_parent);

        this.context = context;
        InflateCurrentView();

        root.findViewById(R.id.main_view_history_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        OnHistoryClick();
                    }
                }
        );
        root.findViewById(R.id.main_view_local_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        OnLocalClick();
                    }
                }
        );
        root.findViewById(R.id.main_view_net_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        OnNetClick();
                    }
                }
        );

        return root;
    }

    @Override
    public View GetView() {
        return root;
    }

    @Override
    public void FreeView() {

    }
    private void ClearCurrentView() {
        grid_parent.removeAllViews();
    }

    private void InflateCurrentView() {
        if (currentState == STATE_INI) {

            return;
        }
        TextView textView = new TextView(context);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        ComicSrc comicSrc = new ComicSrc();
        switch (currentState) {
            case STATE_HISTORY:
                textView.setText("最近瀏覽");
                comicSrc.srcType = ComicSrc.SrcType.ST_HISTORY;
                break;
            case STATE_LOCAL:
                textView.setText("本地漫畫");
                comicSrc.srcType = ComicSrc.SrcType.ST_LOCAL_FILE;
                break;
            case STATE_NET:
                textView.setText("線上漫畫");
                comicSrc.srcType = ComicSrc.SrcType.ST_NET_WNACG;
                break;
        }
        Grid grid = new Grid(comicSrc);
        grid.SetActionCallback(new AbstractComicGrid.ActionCallback() {
            @Override
            public void OnComicClick(ComicInfo comicInfo) {
                MV_OnComicClick(comicInfo);
            }
        });
        View gridView = grid.InflateView(context);
        gridView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        grid_parent.addView(textView);
        grid_parent.addView(gridView);
    }

    private void OnHistoryClick() {
        if(currentState == STATE_HISTORY) return;
        ClearCurrentView();
        currentState = STATE_HISTORY;
        InflateCurrentView();
    }
    private void OnLocalClick() {
        if(currentState == STATE_LOCAL) return;
        ClearCurrentView();
        currentState = STATE_LOCAL;
        InflateCurrentView();
    }
    private void OnNetClick() {
        if(currentState == STATE_NET) return;
        ClearCurrentView();
        currentState = STATE_NET;
        InflateCurrentView();
    }
    private void MV_OnComicClick(ComicInfo comicInfo) {
        viewStack.Push(new ChapterSelectView(viewStack, comicInfo));
    }
}
