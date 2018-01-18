package mio.sis.com.comicmana.sui.inst;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import mio.sis.com.comicmana.R;
import mio.sis.com.comicmana.mine.Grid;
import mio.sis.com.comicmana.mine.Pager;
import mio.sis.com.comicmana.scache.ComicInfoCache;
import mio.sis.com.comicmana.sdata.ComicInfo;
import mio.sis.com.comicmana.sdata.ComicSrc;
import mio.sis.com.comicmana.snet.NetSiteHelper;
import mio.sis.com.comicmana.sui.intf.AbstractComicGrid;
import mio.sis.com.comicmana.sui.intf.AbstractWelcomeView;
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
    private LinearLayout root, grid_parent, search_bar_parent;
    private int currentState;
    private Button historyButton, localButton, netButton;
    private ImageView searchButton, configButton;
    private EditText searchEdit;
    private Pager pager;
    private Grid grid;

    public MainView(ViewStack viewStack) {
        this.viewStack = viewStack;
        currentState = STATE_INI;
    }

    @Override
    public View InflateView(Context context) {
        this.context = context;

        LayoutInflater inflater = LayoutInflater.from(context);
        root = (LinearLayout) inflater.inflate(R.layout.main_view_layout, null);

        search_bar_parent = root.findViewById(R.id.main_view_search_bar_parent);

        grid_parent = root.findViewById(R.id.main_view_grid_parent);

        historyButton =  root.findViewById(R.id.main_view_history_button);
        historyButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        OnHistoryClick();
                    }
                }
        );
        localButton = root.findViewById(R.id.main_view_local_button);
        localButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        OnLocalClick();
                    }
                }
        );
        netButton = root.findViewById(R.id.main_view_net_button);
        netButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        OnNetClick();
                    }
                }
        );
        searchButton = root.findViewById(R.id.main_view_search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnSearchClick();
            }
        });
        configButton = root.findViewById(R.id.main_view_config_button);
        configButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnConfigClick();
            }
        });

        searchEdit = root.findViewById(R.id.main_view_search_edit);

        InflateCurrentView();

        return root;
    }

    @Override
    public View GetView() {
        return root;
    }

    @Override
    public void FreeView() {
        search_bar_parent = null;

        ClearCurrentView();
        grid_parent = null;

        historyButton = localButton = netButton = null;
        searchButton = configButton = null;
        searchEdit = null;
        root = null;
    }

    @Override
    public boolean OnBackPress() {
        return true;
    }

    private void ClearCurrentView() {
        grid_parent.removeAllViews();
        pager = null;
        grid = null;
    }

    private void InflateCurrentView() {
        ComicSrc comicSrc = new ComicSrc();
        if (currentState == STATE_INI) {
            comicSrc.srcType = ComicSrc.SrcType.ST_HISTORY;
            ComicInfoCache.EnumComic(comicSrc, 0, Pager.MAX_PAGES, new NetSiteHelper.EnumCallback() {
                @Override
                public void ComicDiscover(final ComicInfo[] info) {
                    if(root == null) {
                        //  表示在 ComicDiscover 被呼叫之前，使用者已經離開 MainView
                        return;
                    }
                    root.post(new Runnable() {
                        @Override
                        public void run() {
                            pager = new Pager(info);
                            pager.SetActionCallback(new AbstractWelcomeView.ActionCallback() {
                                @Override
                                public void OnComicClick(ComicInfo comicInfo) {
                                    MV_OnComicClick(comicInfo);
                                }
                            });
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                            );
                            View view = pager.InflateView(context);
                            view.setLayoutParams(params);
                            grid_parent.addView(pager.GetView());
                        }
                    });
                }
            });
            ResetButton(historyButton);
            ResetButton(localButton);
            ResetButton(netButton);
            return;
        }
        TextView textView = new TextView(context);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        switch (currentState) {
            case STATE_HISTORY:
                textView.setText("最近瀏覽");
                comicSrc.srcType = ComicSrc.SrcType.ST_HISTORY;

                search_bar_parent.setVisibility(View.GONE);
                HighLightButton(historyButton);
                ResetButton(localButton);
                ResetButton(netButton);
                break;
            case STATE_LOCAL:
                textView.setText("本地漫畫");
                comicSrc.srcType = ComicSrc.SrcType.ST_LOCAL_FILE;

                searchEdit.setText("");
                search_bar_parent.setVisibility(View.VISIBLE);
                ResetButton(historyButton);
                HighLightButton(localButton);
                ResetButton(netButton);
                break;
            case STATE_NET:
                textView.setText("線上漫畫");
                comicSrc.srcType = ComicSrc.SrcType.ST_NET_WNACG;

                search_bar_parent.setVisibility(View.GONE);
                ResetButton(historyButton);
                ResetButton(localButton);
                HighLightButton(netButton);
                break;
        }
        grid = new Grid(comicSrc);
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
    private void HighLightButton(Button button) {
        button.setBackgroundResource(R.drawable.mana_ui_button_border_background);
        button.setTextColor(ContextCompat.getColor(context, R.color.manaBtnBaseTextColor));
    }
    private void ResetButton(Button button) {
        button.setBackgroundResource(R.drawable.mana_ui_base_border_background);
        button.setTextColor(ContextCompat.getColor(context, R.color.manaUIBaseTextColor));
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
    private void OnSearchClick() {
        //searchEdit.clearFocus();
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(
                searchEdit.getWindowToken(), 0);
        grid.SetSearch(searchEdit.getText().toString());
    }
    private void OnConfigClick() {
        viewStack.Push(new ConfigView(viewStack));
    }
    private void MV_OnComicClick(ComicInfo comicInfo) {
        viewStack.Push(new ChapterSelectView(viewStack, comicInfo));
    }
}
