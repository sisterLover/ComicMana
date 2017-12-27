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
    float scale;

    public SHScrollView(Context context) {
        super(context);
        Initialize();
    }

    public SHScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Initialize();
    }

    public SHScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Initialize();
    }

    public SHScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        Initialize();
    }
    void Initialize() {
        scale = 1.0f;
    }
    public void SetScale(float scale) {
        this.scale = scale;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

    /*@Override
    protected int computeHorizontalScrollRange() {
        return (int)(super.computeHorizontalScrollRange()*scale);
    }

    @Override
    protected int computeHorizontalScrollOffset() {
        return super.computeHorizontalScrollOffset();
        //return (int)(super.computeHorizontalScrollOffset()*scale);
    }

    @Override
    protected int computeHorizontalScrollExtent() {
        return super.computeHorizontalScrollExtent();
        //return (int)(super.computeHorizontalScrollExtent()*scale);
    }*/
}
