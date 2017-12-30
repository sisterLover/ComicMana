package mio.sis.com.comicmana.sui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import mio.sis.com.comicmana.scache.DefaultPageCache;
import mio.sis.com.comicmana.scache.ImageCache;
import mio.sis.com.comicmana.sdata.ComicPosition;
import mio.sis.com.comicmana.sdata.ComicSrc;
import mio.sis.com.comicmana.sui.sszpview.SSZPView;

/**
 * Created by Administrator on 2017/12/26.
 */

public class SImagePage extends View {
    static public class Params {
        public SSZPView sszpView;
        public ComicSrc src;
        public ComicPosition position;
        public Params(SSZPView sszpView, ComicSrc src, ComicPosition position) {
            this.sszpView = sszpView;
            this.src = src;
            this.position = position;
        }
    }
    /*
        SImgPage 是 UI 控件，負責顯示一頁漫畫，SImgPage 是 ImgViewer 的其中一頁
        ImgPage 有以下狀態
        當使用者正在觀看的區域離當前頁面很遠時，ImgViewer 會呼叫 Out 來提醒 SImgPage 此頁面不需要重新渲染
     */
    static final int SIP_WAIT = 0,     //  圖片未讀入
            SIP_READY = 2,      //  圖片已載入
            SIP_ERROR = 4;      //  圖片載入失敗

    //  當前漫畫和頁數
    ComicSrc src;
    ComicPosition position;

    int status;
    int percent;                    //  當前圖片讀取進度
    Bitmap bitmap;                  //  當前圖片物件

    SSZPView sszpView;
    /*
      以 img_width, screen_width, scale_rate 綜合計算出當前 ImageView 長寬應該多少
    */

    public SImagePage(Context context, Params params) {
        super(context);
        Initialize(context, params);
    }

    public SImagePage(Context context, @Nullable AttributeSet attrs,
                      Params params) {
        super(context, attrs);
        Initialize(context, params);
    }

    public SImagePage(Context context, @Nullable AttributeSet attrs,
                      int defStyleAttr, Params params) {
        super(context, attrs, defStyleAttr);
        Initialize(context, params);
    }

    public SImagePage(Context context, @Nullable AttributeSet attrs,
                      int defStyleAttr, int defStyleRes, Params params) {
        super(context, attrs, defStyleAttr, defStyleRes);
        Initialize(context, params);
    }
    void Initialize(Context context, Params params) {
        src = params.src;
        position = params.position;
        bitmap = null;
        sszpView = params.sszpView;

        setLayoutParams(new LinearLayout.LayoutParams(GetWidth(), GetHeight()));
        //  不用 requestLayout 因為根本還沒 attach
        SetProgress(0);
    }

    public int GetWidth() {
        return DefaultPageCache.GetWidth();
    }
    public int GetHeight() {
        if(bitmap == null) return DefaultPageCache.GetHeight();
        return bitmap.getHeight();
    }
    /*
        更新圖示為漫畫影像
     */
    public void PostImage(Bitmap bitmap) {
        post(new PostRunnable(this, PostRunnable.PRT_BITMAP, 0, bitmap));
    }
    void SetImage(Bitmap bitmap) {
        status = SIP_READY;
        this.bitmap = bitmap;

        //  提醒 SSZPView 更新，而不是 view 直接更新

        setLayoutParams(new LinearLayout.LayoutParams(GetWidth(), GetHeight()));
        //requestLayout();
        sszpView.AttachViewUpdate();
        sszpView.invalidate();
    }
    /*
        更新圖示為目前讀取進度
        percent = 0~100
     */
    public void PostProgress(int percent) {
        post(new PostRunnable(this, PostRunnable.PRT_WAIT, percent, null));
    }
    void SetProgress(int percent) {
        status = SIP_WAIT;
        this.percent = percent;

        invalidate();
        sszpView.invalidate();
    }
    /*
        更新圖示為錯誤提醒圖示
     */
    public void PostError() {
        post(new PostRunnable(this, PostRunnable.PRT_ERROR, 0, null));
    }
    void SetError() {
        status = SIP_ERROR;

        //new RequestThread(this).start();

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(status == SIP_READY) {
            canvas.drawBitmap(bitmap, 0, 0, null);
        }
        else if(status == SIP_WAIT) {
            DefaultPageCache.DrawPercent(canvas, position.page, percent);
        }
        else if(status == SIP_ERROR) {
            DefaultPageCache.DrawError(canvas);
        }
    }

    /*
        由 UI thread 呼叫，除了 Error 重試外，此函數只可以被呼叫一次
     */
    public void RequestImage() {
        ImageCache.GetComicPage(this, src, position);
    }

    class RequestThread extends Thread {
        SImagePage imagePage;
        public RequestThread(SImagePage imagePage) {
            super();
            this.imagePage = imagePage;
        }

        @Override
        public void run() {
            super.run();

            SystemClock.sleep(5000);
            imagePage.RequestImage();
        }
    }

    class PostRunnable implements Runnable {
        static final int PRT_BITMAP = 0,
        PRT_WAIT=1,
        PRT_ERROR=2;

        SImagePage imagePage;
        int type, percent;
        Bitmap bitmap;

        PostRunnable(SImagePage imagePage, int type, int percent, Bitmap bitmap) {
            this.imagePage = imagePage;
            this.type = type;
            this.percent = percent;
            this.bitmap = bitmap;
        }
        @Override
        public void run() {
            if(type == PRT_BITMAP) {
                imagePage.SetImage(bitmap);
            }
            else if(type == PRT_WAIT) {
                imagePage.SetProgress(percent);
            }
            else if(type == PRT_ERROR) {
                imagePage.SetError();
            }
        }
    }
}
