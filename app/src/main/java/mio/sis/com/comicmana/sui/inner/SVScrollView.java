package mio.sis.com.comicmana.sui.inner;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by Administrator on 2017/12/26.
 */

public class SVScrollView extends ScrollView {
    public SVScrollView(Context context) {
        super(context);
    }

    public SVScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SVScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SVScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }
}
