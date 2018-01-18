package mio.sis.com.comicmana.sui.inst;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.File;

import mio.sis.com.comicmana.MainActivity;
import mio.sis.com.comicmana.R;
import mio.sis.com.comicmana.sfile.LocalStorage;
import mio.sis.com.comicmana.sui.comp.PathSelector;
import mio.sis.com.comicmana.sui.comp.PathSelectorListener;
import mio.sis.com.comicmana.sui.intf.StackableView;
import mio.sis.com.comicmana.sui.intf.ViewStack;

/**
 * Created by Administrator on 2018/1/14.
 */

public class ConfigView implements StackableView {
    private ViewStack viewStack;
    private Context context;
    private ConstraintLayout root;
    private LinearLayout selectorParent;

    private Button deleteCacheButton, editPathButton, safeModeButton;
    private PathSelector pathSelector;
    private boolean selectorActive;

    public ConfigView(ViewStack viewStack) {
        this.viewStack = viewStack;
        selectorActive = false;
    }
    @Override
    public View InflateView(Context context) {
        this.context = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        root = (ConstraintLayout) inflater.inflate(R.layout.config_view_layout, null);

        selectorParent = root.findViewById(R.id.config_view_selector_parent);

        deleteCacheButton = root.findViewById(R.id.config_view_delete_cache_button);
        deleteCacheButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnDeleteCache();
            }
        });

        editPathButton = root.findViewById(R.id.config_view_path_edit_button);
        editPathButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnEditPath();
            }
        });

        safeModeButton = root.findViewById(R.id.config_view_safe_mode_button);
        SetSafeModeButtonText();
        safeModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnSafeModeSet();
            }
        });

        return root;
    }
    private void SetSafeModeButtonText() {
        File dir = MainActivity.manaConfig.safeModeComicDir;
        if(dir==null) {
            safeModeButton.setText("設定安全模式漫畫目錄");
        }
        else {
            safeModeButton.setText("安全模式漫畫：" + dir.toString());
        }
    }

    @Override
    public View GetView() {
        return root;
    }

    @Override
    public void FreeView() {
        deleteCacheButton = editPathButton = safeModeButton = null;

        RemoveSelector();
        selectorParent = null;

        root = null;
    }

    @Override
    public boolean OnBackPress() {
        if(selectorActive) {
            RemoveSelector();
            return false;
        }
        else {
            return true;
        }
    }

    private void OnDeleteCache() {
        File historyFile = LocalStorage.GetHistoryFile();
        if(historyFile!= null && historyFile.exists())
            historyFile.delete();
    }
    private void OnEditPath() {
        viewStack.Push(new ConfigPathSelectView(viewStack));
    }

    private void OnSafeModeSet() {
        pathSelector = new PathSelector(context, new Callback());
        File dir = MainActivity.manaConfig.safeModeComicDir;
        if(dir!=null) {
            pathSelector.SetCurrentPath(dir.toString());
        }
        View view = pathSelector.GetView();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        view.setLayoutParams(params);
        selectorParent.addView(view);
        selectorActive = true;
    }
    private void RemoveSelector() {
        selectorParent.removeAllViews();
        pathSelector = null;
        selectorActive = false;
    }
    private class Callback implements PathSelectorListener {
        @Override
        public void OnPathSelect(File file) {
            MainActivity.manaConfig.safeModeComicDir = file;
            MainActivity.manaConfig.SaveConfig();
            SetSafeModeButtonText();
            //  remove selector view
            RemoveSelector();
        }

        @Override
        public void OnCancel() {
            RemoveSelector();
        }
    }
}
