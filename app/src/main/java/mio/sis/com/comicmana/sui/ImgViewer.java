package mio.sis.com.comicmana.sui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import mio.sis.com.comicmana.R;
import mio.sis.com.comicmana.scache.ImageCache;

/**
 * Created by Administrator on 2017/12/25.
 */

public class ImgViewer {
    ImageCache cache;

    //  ui
    Context context;
    View mainLayout;
    boolean tooltipVisible;
    LinearLayout tooltipTop, tooltipBottom, content;

    public ImgViewer(Context context, ImageCache cache) {
        this.context = context;
        this.cache = cache;
        tooltipVisible = false;

        LayoutInflater inflater = LayoutInflater.from(context);
        mainLayout = inflater.inflate(R.layout.img_viewer_layout, null);

        tooltipTop = mainLayout.findViewById(R.id.img_viewer_tooltip_top);
        tooltipBottom = mainLayout.findViewById(R.id.img_viewer_tooltip_bottom);
        content = mainLayout.findViewById(R.id.img_viewer_content);
    }
}
