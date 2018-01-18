package mio.sis.com.comicmana.sui.inst;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import mio.sis.com.comicmana.R;
import mio.sis.com.comicmana.sui.intf.StackableView;
import mio.sis.com.comicmana.sui.intf.ViewStack;

/**
 * Created by Administrator on 2018/1/14.
 */

public class ConfigView implements StackableView {
    private ViewStack viewStack;
    private LinearLayout root;

    private Button deleteCacheButton, editPathButton;

    public ConfigView(ViewStack viewStack) {
        this.viewStack = viewStack;
    }
    @Override
    public View InflateView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        root = (LinearLayout) inflater.inflate(R.layout.config_view_layout, null);

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

        return root;
    }

    @Override
    public View GetView() {
        return root;
    }

    @Override
    public void FreeView() {
        deleteCacheButton = editPathButton = null;
        root = null;
    }

    @Override
    public boolean OnBackPress() {
        return true;
    }

    private void OnDeleteCache() {

    }
    private void OnEditPath() {
        viewStack.Push(new ConfigPathSelectView(viewStack));
    }
}
