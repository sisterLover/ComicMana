package mio.sis.com.comicmana.sui.inner;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by Administrator on 2017/12/26.
 */

public class SVScrollView extends ScrollView {
    float scale;
    public SVScrollView(Context context) {
        super(context);
        Initialize();
    }

    public SVScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Initialize();
    }

    public SVScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Initialize();
    }

    public SVScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
    protected int computeVerticalScrollRange() {
        return (int)(super.computeVerticalScrollRange()*scale);
    }

    @Override
    protected int computeVerticalScrollOffset() {
        return super.computeVerticalScrollOffset();
        //return (int)(super.computeVerticalScrollOffset()*scale);
    }

    @Override
    protected int computeVerticalScrollExtent() {
        return super.computeVerticalScrollExtent();
        //return (int)(super.computeVerticalScrollExtent()*scale);
    }*/
}
