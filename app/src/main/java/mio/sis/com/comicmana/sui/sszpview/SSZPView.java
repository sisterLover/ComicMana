package mio.sis.com.comicmana.sui.sszpview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import mio.sis.com.comicmana.R;
import mio.sis.com.comicmana.scache.DefaultPageCache;
import mio.sis.com.comicmana.sdata.ComicInfo;
import mio.sis.com.comicmana.sdata.ComicPosition;
import mio.sis.com.comicmana.sui.HintText;

/**
 * Created by Administrator on 2017/12/27.
 */

public class SSZPView extends LinearLayout {
    static final int STANDARD_FACTOR = 1;

    Context context;
    ScaleGestureDetector scaleGestureDetector;
    InnerScaleListener scaleListener;
    int offsetX, offsetY,                       //  目前 scroll 狀態
            childWidth, childHeight,            //  attachView 原始大小
            scaleChildWidth, scaleChildHeight,  //  attachView 經過 zoomFactor 後的長寬
            width, height;                      //  此 LinearLayout 的長寬
    float zoomFactor;                           //  放大倍率
    LinearLayout attachView;                    //  子容器，用來包含所有 child view
    PageController pageController;

    //  page info

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
        this.context = context;
        offsetX = offsetY = 0;
        width = height = 0;

        zoomFactor = InnerScaleListener.INI_FACTOR;

        scaleListener = new InnerScaleListener();
        scaleGestureDetector = new ScaleGestureDetector(context, scaleListener);
        setOnTouchListener(new InnerTouchListener(scaleGestureDetector, scaleListener));

        attachView = new LinearLayout(context);
        attachView.setOrientation(VERTICAL);

        //attachView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.scroll_test, null);
        CalculateAttachSize();

        addView(attachView);

        pageController = new PageController(context, this);
    }
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    public void PostComicInfo(ComicInfo info, ComicPosition position) {
        post(new PostComicInfoRunnable(this, info, position));
    }
    public LinearLayout GetAttachView() {
        return attachView;
    }
    public float GetZoomFactor() {
        return zoomFactor;
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

        if(pageController.ScrollAvailable()) {
            pageController.UpdateScrollInfo(offsetY);
            pageController.CheckScroll();
            pageController.UpdatePageHint(HintText.STANDARD_HINT);
        }

        invalidate();
    }

    void OnScroll(int x, int y) {
        offsetX += x;
        offsetY += y;

        BoundOffset();

        Log.d("SSZ_TAG", "Scroll " + offsetX + ", " + offsetY);
        if(pageController.ScrollAvailable()) {
            pageController.UpdateScrollInfo(offsetY);
            pageController.DebugScrollInfo();
            pageController.CheckScroll();
            pageController.UpdatePageHint(HintText.STANDARD_HINT);
        }

        invalidate();
    }

    public int GetOffsetX() { return offsetX; }
    public int GetOffsetY() { return offsetY; }
    public void SetOffsetX(int offsetX) { this.offsetX = offsetX; }
    public void SetOffsetY(int offsetY) { this.offsetY = offsetY; }

    void BoundOffset() {
        if(offsetX+width>scaleChildWidth) offsetX = scaleChildWidth - width;
        //  有可能 childWidth - width < 0，所以 < 0 放在後面判斷
        if(offsetX<0) offsetX = 0;

        if(offsetY+height>scaleChildHeight) offsetY = scaleChildHeight - height;
        if(offsetY<0) offsetY = 0;
    }

    public void AttachViewUpdate() {
        CalculateAttachSize();
        if(pageController.ScrollAvailable()) {
            pageController.CalculateViewHeightInfo();
            pageController.SetOffsetY();
        }
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
        BoundOffset();
        canvas.translate(-offsetX, -offsetY);
        canvas.scale(zoomFactor, zoomFactor);
        //matrix.postTranslate(offsetX, offsetY);
        //canvas.setMatrix(matrix);
        //Log.d("SSZ_TAG", "setMatrix");
        super.dispatchDraw(canvas);
    }

    public boolean IsSizeAvailable() { return width != 0; }
    public int GetWidth() { return width; }
    public int GetHeight() { return height; }
    public int GetStandardWidth() {
        return width*STANDARD_FACTOR;
    }
    public int GetStandardHeight() { return height*STANDARD_FACTOR; }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;

        if(!DefaultPageCache.ParamAbailable()) {
            DefaultPageCache.SetParams(context, GetStandardWidth(), GetStandardHeight());

            //InsertTestPage();
        }
        Log.d("SSZ_TAG", "size = " + width + ", " + height);
    }

    void InsertTestPage() {
        ImageView imageView = new ImageView(context);
        Bitmap bitmap = DefaultPageCache.GetTestComic(1, 1);
        imageView.setImageBitmap(bitmap);
        imageView.setLayoutParams(new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        attachView.addView(imageView);

        bitmap = Bitmap.createBitmap(DefaultPageCache.GetWidth(), DefaultPageCache.GetHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        DefaultPageCache.DrawError(canvas);
        imageView = new ImageView(context);
        imageView.setImageBitmap(bitmap);
        imageView.setLayoutParams(new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        attachView.addView(imageView);

        bitmap = Bitmap.createBitmap(DefaultPageCache.GetWidth(), DefaultPageCache.GetHeight(),
                Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        DefaultPageCache.DrawPercent(canvas, 3, 67);
        imageView = new ImageView(context);
        imageView.setImageBitmap(bitmap);
        imageView.setLayoutParams(new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        attachView.addView(imageView);
        CalculateAttachSize();
    }

    class PostComicInfoRunnable implements Runnable {
        SSZPView sszpView;
        ComicInfo comicInfo;
        ComicPosition comicPosition;

        public PostComicInfoRunnable(SSZPView sszpView, ComicInfo comicInfo, ComicPosition comicPosition) {
            this.sszpView = sszpView;
            this.comicInfo = comicInfo;
            this.comicPosition = comicPosition;
        }
        @Override
        public void run() {
            sszpView.pageController.SetComicInfo(comicInfo, comicPosition);
        }
    }

    class InnerScaleListener implements ScaleGestureDetector.OnScaleGestureListener {
        static final float MAX_FACTOR = 2.0f, MIN_FACTOR = 1/((float)STANDARD_FACTOR), INI_FACTOR = MIN_FACTOR;
        //static final float MAX_FACTOR = 3.0f, MIN_FACTOR = 0.5f, INI_FACTOR = 1.0f;
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
