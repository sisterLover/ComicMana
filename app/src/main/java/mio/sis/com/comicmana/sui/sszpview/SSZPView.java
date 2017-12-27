package mio.sis.com.comicmana.sui.sszpview;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

import mio.sis.com.comicmana.R;

/**
 * Created by Administrator on 2017/12/27.
 */

public class SSZPView extends LinearLayout {
    static final int STANDARD_WIDTH_FACTOR = 2;

    ScaleGestureDetector scaleGestureDetector;
    InnerScaleListener scaleListener;
    int offsetX, offsetY,
            childWidth, childHeight,
            scaleChildWidth, scaleChildHeight,
            width, height;
    float zoomFactor;
    LinearLayout attachView;
    ArrayList<View> childs;
    ArrayList<Integer> childsHeight;

    public SSZPView(Context context) {
        super(context);
        Initialize(context);
    }

    public SSZPView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Initialize(context);
    }

    public SSZPView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Initialize(context);
    }

    public SSZPView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        Initialize(context);
    }
    void Initialize(Context context) {
        offsetX = offsetY = 0;
        zoomFactor = InnerScaleListener.INI_FACTOR;

        scaleListener = new InnerScaleListener();
        scaleGestureDetector = new ScaleGestureDetector(context, scaleListener);
        setOnTouchListener(new InnerTouchListener(scaleGestureDetector, scaleListener));

        /*attachView = new LinearLayout(context);
        attachView.setOrientation(VERTICAL);
        */
        attachView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.scroll_test, null);
        CalculateAttachSize();

        addView(attachView);

        childs = new ArrayList<>();
        childsHeight = new ArrayList<>();
    }
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    void OnScale(float factor/*, float x, float y*/) {
        float offsetPercentX = ((offsetX + (width / (float) 2)) * 100) / scaleChildWidth,
                offsetPercentY = ((offsetY + (height / (float) 2)) * 100) / scaleChildHeight;
        /*float offsetPercentX = ((offsetX + x) * 100) / scaleChildWidth,
                offsetPercentY = ((offsetY + y) * 100) / scaleChildHeight;*/

        scaleChildWidth = (int) (childWidth * factor);
        scaleChildHeight = (int) (childHeight * factor);
        offsetX = (int) (scaleChildWidth * offsetPercentX / 100 - width / (float) 2);
        offsetY = (int) (scaleChildHeight * offsetPercentY / 100 - height / (float) 2);
        zoomFactor = factor;

        BoundOffset();

        invalidate();
    }

    void OnScroll(int x, int y) {
        offsetX += x;
        offsetY += y;

        BoundOffset();

        Log.d("SSZ_TAG", "Scroll " + offsetX + ", " + offsetY);

        invalidate();
    }

    void BoundOffset() {
        if(offsetX+width>scaleChildWidth) offsetX = scaleChildWidth - width;
        //  有可能 childWidth - width < 0，所以 < 0 放在後面判斷
        if(offsetX<0) offsetX = 0;

        if(offsetY+height>scaleChildHeight) offsetY = scaleChildHeight - height;
        if(offsetY<0) offsetY = 0;
    }

    void CalculateAttachSize() {
        attachView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        childWidth = attachView.getMeasuredWidth();
        childHeight = attachView.getMeasuredHeight();
        scaleChildWidth = (int)(childWidth*zoomFactor);
        scaleChildHeight = (int)(childHeight*zoomFactor);
        ViewGroup.LayoutParams params = new LayoutParams(childWidth, childHeight);
        attachView.setLayoutParams(params);
        attachView.requestLayout();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        //Log.d("SSZ_TAG", "onDraw");
        canvas.translate(-offsetX, -offsetY);
        canvas.scale(zoomFactor, zoomFactor);
        //matrix.postTranslate(offsetX, offsetY);
        //canvas.setMatrix(matrix);
        //Log.d("SSZ_TAG", "setMatrix");
        super.dispatchDraw(canvas);
    }

    public int GetStandardWidth() {
        return width*STANDARD_WIDTH_FACTOR;
    }
    /*
        push 函數必須確保 view 已經 setLayoutParam
     */
    public void PushFrontView(View view) {
        int viewHeight = view.getLayoutParams().height;
        childsHeight.add(0, viewHeight);
        offsetY += viewHeight*zoomFactor;
        childs.add(0, view);
        attachView.addView(view, 0);

        invalidate();
    }
    public void PopFrontView() {
        offsetY -= childsHeight.get(0)*zoomFactor;
        childsHeight.remove(0);
        childs.set(0, null);
        childs.remove(0);
        attachView.removeViewAt(0);

        invalidate();
    }
    public void PushBackView(View view) {
        attachView.addView(view);
        childs.add(view);
        childsHeight.add(view.getLayoutParams().height);
    }
    public void PopBackView() {
        attachView.removeView(childs.get(childs.size()-1));
        childs.set(childs.size()-1, null);
        childs.remove(childs.size()-1);
        childsHeight.remove(childsHeight.size()-1);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    class InnerScaleListener implements ScaleGestureDetector.OnScaleGestureListener {
        //static final float MAX_FACTOR = 1.0f, MIN_FACTOR = 1/((float)STANDARD_WIDTH_FACTOR), INI_FACTOR = MIN_FACTOR;
        static final float MAX_FACTOR = 3.0f, MIN_FACTOR = 0.5f, INI_FACTOR = 1.0f;
        float scaleFactor/*, centerX, centerY*/;
        boolean scaling;

        public InnerScaleListener() {
            scaleFactor = INI_FACTOR;
            scaling = false;
        }
        public boolean IsScaling() {
            return scaling;
        }

        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            scaleFactor *= scaleGestureDetector.getScaleFactor();
            if (scaleFactor < MIN_FACTOR) scaleFactor = MIN_FACTOR;
            if (scaleFactor > MAX_FACTOR) scaleFactor = MAX_FACTOR;

            OnScale(scaleFactor/*, centerX, centerY*/);
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
            scaling = true;
            /*centerX = scaleGestureDetector.getFocusX();
            centerY = scaleGestureDetector.getFocusY();*/
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
            scaling = false;
        }
    }

    class InnerTouchListener implements OnTouchListener {
        boolean lastValid;
        int scaleCount;
        float lastX, lastY;
        ScaleGestureDetector detector;
        InnerScaleListener listener;

        public InnerTouchListener(ScaleGestureDetector detector, InnerScaleListener listener) {
            this.detector = detector;
            this.listener = listener;
            lastValid = false;
            scaleCount = 0;
        }

        @Override
        public boolean onTouch(View view, MotionEvent ev) {
            float curX, curY;
            boolean scaling = listener.IsScaling();

            detector.onTouchEvent(ev);
            if(scaling) {
                scaleCount = 10;
            }
            if(scaleCount>0) {
                --scaleCount;
            }

            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = ev.getX();
                    lastY = ev.getY();
                    //Log.d("SSV_TAG", "DOWN at " + lastX + ", " + lastY);
                    lastValid = true;
                    break;
                case MotionEvent.ACTION_MOVE:
                    curX = ev.getX();
                    curY = ev.getY();
                    if(!scaling && lastValid && scaleCount==0) {
                        OnScroll((int) (lastX - curX), (int) (lastY - curY));
                    }
                    lastX = curX;
                    lastY = curY;
                    //Log.d("SSV_TAG", "Move to " + curX + ", " + curY);
                    break;
                case MotionEvent.ACTION_UP:
                    if (!lastValid) break;
                    curX = ev.getX();
                    curY = ev.getY();

                    if(!scaling && lastValid && scaleCount==0) {
                        OnScroll((int) (lastX - curX), (int) (lastY - curY));
                    }
                    //Log.d("SSV_TAG", "Up at " + curX + ", " + curY);
                    lastValid = false;
                    break;
                default:
            }
            return true;
        }
    }
}
