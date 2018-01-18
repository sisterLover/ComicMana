package mio.sis.com.comicmana.sui.inst;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.File;

import mio.sis.com.comicmana.MainActivity;
import mio.sis.com.comicmana.R;
import mio.sis.com.comicmana.image.ImageLib;
import mio.sis.com.comicmana.scache.ImageCache;
import mio.sis.com.comicmana.sdata.ComicInfo;
import mio.sis.com.comicmana.sdata.ComicPosition;
import mio.sis.com.comicmana.sdata.ComicSrc;
import mio.sis.com.comicmana.sdata.STime;
import mio.sis.com.comicmana.sfile.LocalStorage;
import mio.sis.com.comicmana.snet.NetImageHelper;
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
    private Button configButton, saveButton;
    private boolean tooltipActive, isSafe;  //  當前 tooltip 是否顯示、此 ComicView 是否為安全模式視窗
    private SSZPView sszpView;
    private ComicInfo comicInfo;

    /*
        載入 comicInfo 的漫畫，並從 lastPosition 開始
     */
    public ComicView(ViewStack viewStack, ComicInfo comicInfo, boolean isSafe) {
        this.viewStack = viewStack;
        this.comicInfo = comicInfo;
        this.isSafe = isSafe;

        tooltipActive = false;
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

        configButton = root.findViewById(R.id.comic_view_config_button);
        configButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnConfigClick();
            }
        });
        saveButton = root.findViewById(R.id.comic_view_save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnSaveClick();
            }
        });

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
        tooltipTop = null;

        configButton = saveButton = null;
        tooltipActive = false;
        tooltipBottom.setVisibility(View.GONE);
        tooltipBottom = null;
        root = null;
    }

    @Override
    public boolean OnBackPress() {
        return true;
    }

    private void OnConfigClick() {
        viewStack.Push(new ConfigView(viewStack));
    }
    private void OnSaveClick() {
        ComicPosition position = new ComicPosition();
        position.Copy(sszpView.GetCurrentPosition());
        ImageCache.GetRawComicPage(comicInfo.src, position, new NetImageHelper.ComicPageCallback() {
            @Override
            public void PageRecieve(Bitmap bitmap) {
                if(bitmap == null) return;
                File downloadDir = LocalStorage.GetDownloadDir();
                if(downloadDir == null) return;
                STime time = new STime();
                time.GetCurrentTime();
                ImageLib.SaveFile(
                        new File(
                                downloadDir,
                                "PageSave"+time.year+"_"+time.month+"_"+time.day+
                                        "_"+time.hour+"_"+time.minute+"_"+time.second+".jpg"),
                        bitmap);
            }

            @Override
            public void UpdateProgress(int percent) {
                return;
            }
        });
        ToggleToolTip();
    }

    private void OnSSZPClick(int x, int y, int width, int height) {
        if(!isSafe && y<height/3) {
            //  safe mode
            File safeModeDir = MainActivity.manaConfig.safeModeComicDir;
            if(safeModeDir != null) {
                int type = LocalStorage.GetComicDirectoryType(safeModeDir.listFiles());
                if (type == LocalStorage.COMIC_DIR_SINGLE || type == LocalStorage.COMIC_DIR_MULTIPLE) {
                    ComicInfo comicInfo = new ComicInfo();
                    comicInfo.src.srcType = ComicSrc.SrcType.ST_LOCAL_FILE;
                    comicInfo.src.path = safeModeDir.toString();
                    LocalStorage.LoadComicInfo(comicInfo);
                    if(comicInfo.lastPosition.chapter==ComicPosition.CHAPTER_NOT_READ_YET) {
                        comicInfo.lastPosition.chapter = 1;
                        comicInfo.lastPosition.page = 1;
                    }
                    viewStack.Push(new ComicView(viewStack, comicInfo, true));
                    return;
                }
            }
        }
        //  安全模式沒有啟動或是按在其他地方就切換 tooltip
        //  tooltip
        ToggleToolTip();
    }
    private void ToggleToolTip() {
        if(tooltipActive) {
            tooltipBottom.setVisibility(View.GONE);
            tooltipActive = false;
        }
        else {
            tooltipBottom.setVisibility(View.VISIBLE);
            tooltipActive = true;
        }
    }

    private class SSZPClickListener implements SSZPViewClickListener {
        @Override
        public void OnClick(int x, int y, int width, int height) {
            OnSSZPClick(x, y, width, height);
        }
    }
}
