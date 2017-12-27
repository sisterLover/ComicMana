package mio.sis.com.comicmana.sui.inner;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by Administrator on 2017/12/26.
 */

public class SScrollView extends LinearLayout {

    SScrollAttachView attachView;
    SVScrollView svScrollView;
    SHScrollView shScrollView;
    ScaleGestureDetector scaleGestureDetector;

    public SScrollView(Context context) {
        super(context);
        Initialize(context);
    }

    public SScrollView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Initialize(context);
    }

    public SScrollView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Initialize(context);
    }

    public SScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        Initialize(context);
    }

    void Initialize(Context context) {
        ViewGroup.LayoutParams params =
                new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        svScrollView = new SVScrollView(context);
        svScrollView.setLayoutParams(params);

        shScrollView = new SHScrollView(context);
        shScrollView.setLayoutParams(params);

        attachView = new SScrollAttachView(context);
        attachView.setLayoutParams(
                new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        );
        attachView.setOrientation(VERTICAL);

        addView(svScrollView);
        svScrollView.addView(shScrollView);
        shScrollView.addView(attachView);

        scaleGestureDetector = new ScaleGestureDetector(context, new InnerScaleListener(this));
        setOnTouchListener(new InnerTouchListener(scaleGestureDetector));
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    public LinearLayout GetAttachView() {
        return attachView;
    }

    void OnScale(float factor) {
        /*
            factor 表示最終係數
         */
        attachView.setScaleX(factor);
        attachView.setScaleY(factor);

        shScrollView.SetScale(factor);
        svScrollView.SetScale(factor);

        ViewGroup.LayoutParams params =
                new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        svScrollView.setLayoutParams(params);
        svScrollView.requestLayout();

        shScrollView.setLayoutParams(params);
        shScrollView.requestLayout();

        attachView.setLayoutParams(new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        attachView.requestLayout();
        //shScrollView.setScaleX(factor);
        //svScrollView.setScaleY(factor);

        /*post(new Runnable() {
            @Override
            public void run() {
                computeScroll();
                attachView.computeScroll();
                svScrollView.computeScroll();
                shScrollView.computeScroll();
                svScrollView.invalidate();
                shScrollView.invalidate();
            }
        });*/
    }

    class InnerScaleListener implements ScaleGestureDetector.OnScaleGestureListener {
        static final float MAX_FACTOR = 2.0f, MIN_FACTOR = 0.5f;
        SScrollView sScrollView;
        float scaleFactor;

        public InnerScaleListener(SScrollView sScrollView) {
            this.sScrollView = sScrollView;
            scaleFactor = 1.0f;
        }

        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            scaleFactor *= scaleGestureDetector.getScaleFactor();
            if (scaleFactor < MIN_FACTOR) scaleFactor = MIN_FACTOR;
            if (scaleFactor > MAX_FACTOR) scaleFactor = MAX_FACTOR;

            sScrollView.OnScale(scaleFactor);
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {

        }
    }

    class InnerTouchListener implements OnTouchListener {
        float lastX, lastY;
        ScaleGestureDetector detector;

        public InnerTouchListener(ScaleGestureDetector detector) {
            this.detector = detector;
        }

        @Override
        public boolean onTouch(View view, MotionEvent ev) {
            float curX, curY;
            switch(ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = ev.getX();
                    lastY = ev.getY();
                    //Log.d("SSV_TAG", "DOWN at " + lastX + ", " + lastY);
                    break;
                case MotionEvent.ACTION_MOVE:
                    curX = ev.getX();
                    curY = ev.getY();
                    svScrollView.scrollBy((int) (lastX - curX), (int) (lastY - curY));
                    shScrollView.scrollBy((int) (lastX - curX), (int) (lastY - curY));
                    lastX = curX;
                    lastY = curY;
                    //Log.d("SSV_TAG", "Move to " + curX + ", " + curY);
                    break;
                case MotionEvent.ACTION_UP:
                    curX = ev.getX();
                    curY = ev.getY();
                    svScrollView.scrollBy((int) (lastX - curX), (int) (lastY - curY));
                    shScrollView.scrollBy((int) (lastX - curX), (int) (lastY - curY));
                    //Log.d("SSV_TAG", "Up at " + curX + ", " + curY);
                    break;
                default:
            }
            detector.onTouchEvent(ev);
            return true;
        }
    }
}
