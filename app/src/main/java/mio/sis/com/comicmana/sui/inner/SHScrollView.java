package mio.sis.com.comicmana.sui.inner;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

/**
 * Created by Administrator on 2017/12/26.
 */

public class SHScrollView extends HorizontalScrollView {
    public SHScrollView(Context context) {
        super(context);
    }

    public SHScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SHScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SHScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }
}
