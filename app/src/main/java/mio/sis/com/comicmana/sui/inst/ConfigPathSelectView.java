package mio.sis.com.comicmana.sui.inst;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import java.io.File;
import java.util.ArrayList;

import mio.sis.com.comicmana.MainActivity;
import mio.sis.com.comicmana.R;
import mio.sis.com.comicmana.sui.comp.PathSelector;
import mio.sis.com.comicmana.sui.comp.PathSelectorListener;
import mio.sis.com.comicmana.sui.intf.StackableView;
import mio.sis.com.comicmana.sui.intf.ViewStack;

/**
 * Created by Administrator on 2018/1/18.
 */

public class ConfigPathSelectView implements StackableView {
    private ViewStack viewStack;
    private Context context;
    private ConstraintLayout root;
    private LinearLayout itemParent, selectorParent;
    private Button addButton, deleteButton;
    private boolean selectorActive;
    private ArrayList<File> containerDir;
    private CheckBox[] items;
    private PathSelector pathSelector;

    public ConfigPathSelectView(ViewStack viewStack) {
        this.viewStack = viewStack;
        selectorActive = false;
        containerDir = MainActivity.manaConfig.containterDirs;
    }
    @Override
    public View InflateView(Context context) {
        this.context = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        root = (ConstraintLayout) inflater.inflate(R.layout.config_path_edit_layout, null);

        addButton = root.findViewById(R.id.config_path_edit_add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnAdd();
            }
        });

        deleteButton = root.findViewById(R.id.config_path_edit_delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnDelete();
            }
        });

        itemParent = root.findViewById(R.id.config_path_edit_item_parent);
        InflateItem();

        selectorParent = root.findViewById(R.id.config_path_edit_path_selector_parent);
        return root;
    }
    private void InflateItem() {
        itemParent.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(context);
        items = new CheckBox[containerDir.size()];
        int index = 0;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        for(File file : containerDir) {
            items[index] = (CheckBox)inflater.inflate(R.layout.config_path_edit_item, null);
            items[index].setText(file.toString());
            items[index].setLayoutParams(params);
            itemParent.addView(items[index]);
            ++index;
        }
    }

    @Override
    public View GetView() {
        return root;
    }

    @Override
    public void FreeView() {
        MainActivity.manaConfig.containterDirs = containerDir;
        MainActivity.manaConfig.SaveConfig();
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

    private void RemoveSelector() {
        selectorParent.removeAllViews();
        pathSelector = null;
        selectorActive = false;
    }

    private void OnAdd() {
        pathSelector = new PathSelector(context, new Callback());
        View view = pathSelector.GetView();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        view.setLayoutParams(params);
        selectorParent.addView(view);
        selectorActive = true;
    }
    private void OnDelete() {
        ArrayList<CheckBox> leftViews = new ArrayList<>();
        for(CheckBox checkBox : items) {
            if(checkBox.isChecked()) {
                itemParent.removeView(checkBox);
            }
            else {
                leftViews.add(checkBox);
            }
        }
        ArrayList<File> containerDir = new ArrayList<>();
        items = new CheckBox[leftViews.size()];
        int index = 0;
        for(CheckBox checkBox : leftViews) {
            containerDir.add(new File(checkBox.getText().toString()));
            items[index++] = checkBox;
        }
        this.containerDir = containerDir;
    }
    private class Callback implements PathSelectorListener {
        @Override
        public void OnPathSelect(File file) {
            if(!file.isDirectory()) return;
            CheckBox[] newItems = new CheckBox[items.length+1];
            for(int i=0;i<items.length;++i) {
                newItems[i] = items[i];
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            LayoutInflater inflater = LayoutInflater.from(context);
            newItems[items.length] = (CheckBox)inflater.inflate(R.layout.config_path_edit_item, null);
            newItems[items.length].setText(file.toString());
            newItems[items.length].setLayoutParams(params);
            itemParent.addView(newItems[items.length]);
            items = newItems;
            containerDir.add(file);
            //  remove selector view
            RemoveSelector();
        }

        @Override
        public void OnCancel() {
            RemoveSelector();
        }
    }
}
