package mio.sis.com.comicmana.sui.inner;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by Administrator on 2017/12/27.
 */

public class SScrollAttachView extends LinearLayout {
    public SScrollAttachView(Context context) {
        super(context);
    }

    public SScrollAttachView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SScrollAttachView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SScrollAttachView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected int computeHorizontalScrollRange() {
        //return (int)(super.computeHorizontalScrollRange()*getScaleX());
        return super.computeHorizontalScrollRange();
    }

    @Override
    protected int computeVerticalScrollRange() {
        //return (int)(super.computeVerticalScrollRange()*getScaleY());
        return super.computeVerticalScrollRange();
    }
}
