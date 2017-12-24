package mio.sis.com.comicmana.sui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import mio.sis.com.comicmana.R;

/**
 * Created by Administrator on 2017/12/24.
 */

public class PathSelector {
    //  data
    File currentPath, lastValidPath;
    PathSelectorListener selectorListener;

    //  ui
    Context context;
    View mainLayout;
    LinearLayout listLayout;
    TextView titleText;
    Button accept, cancel;
    ArrayList<View> items;
    ItemClickListener itemClickListener;

    public PathSelector(Context context, PathSelectorListener listener) {
        this.context = context;
        selectorListener = listener;
        lastValidPath = currentPath = new File("/");    //  root

        LayoutInflater inflater = LayoutInflater.from(context);
        mainLayout = inflater.inflate(R.layout.path_selector_layout, null);
        //mainLayout.setBackgroundColor(context.getColor(R.color.colorPathSelectorBackground));

        listLayout = mainLayout.findViewById(R.id.list_layout);
        titleText = mainLayout.findViewById(R.id.current_path_text);
        //titleText.setTextColor(context.getColor(R.color.colorPathSelectorText));

        accept = mainLayout.findViewById(R.id.accept_button);
        //accept.setTextColor(context.getColor(R.color.colorPathSelectorText));

        cancel = mainLayout.findViewById(R.id.cancel_button);
        //cancel.setTextColor(context.getColor(R.color.colorPathSelectorText));

        items = new ArrayList<>();
        itemClickListener = new ItemClickListener();

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectorListener.OnPathSelect(currentPath);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectorListener.OnCancel();
            }
        });
    }
    public View GetView() {
        return mainLayout;
    }
    public File GetCurrentPath() {
        return currentPath;
    }
    public void SetCurrentPath(String path) {
        lastValidPath = currentPath;
        currentPath = new File(path);
        GenerateView();
    }
    public void MoveToParent() {
        lastValidPath = currentPath;
        currentPath = currentPath.getParentFile();
        GenerateView();
    }
    public void GenerateView() {
        File[] list = currentPath.listFiles();
        if(list==null) {
            Toast.makeText(context, "Invalid Path", Toast.LENGTH_SHORT).show();

            currentPath = lastValidPath;
            list = currentPath.listFiles();
        }
        Arrays.sort(list);

        titleText.setText("Path:" + currentPath.getAbsolutePath());

        listLayout.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(context);
        //  return to parent
        View view = inflater.inflate(R.layout.path_selector_item, null);
        TextView item_text = view.findViewById(R.id.item_text);
        item_text.setText("上層");
        ImageView item_img = view.findViewById(R.id.item_img);
        item_img.setImageResource(R.mipmap.path_selector_uppage_img);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MoveToParent();
            }
        });
        listLayout.addView(view);

        //  next level
        for(File file : list) {
            if(!file.isDirectory()) continue;
            view = inflater.inflate(R.layout.path_selector_item, null);
            item_text = view.findViewById(R.id.item_text);
            item_text.setText(file.getName());
            view.setOnClickListener(itemClickListener);
            listLayout.addView(view);
        }
    }

    public class ItemClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            TextView textView = (TextView)view.findViewById(R.id.item_text);
            SetCurrentPath(currentPath.getAbsolutePath() + "/" + textView.getText().toString());
        }
    }
}
