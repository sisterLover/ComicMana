package mio.sis.com.comicmana.sui;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import mio.sis.com.comicmana.scache.ImgCache;
import mio.sis.com.comicmana.sdata.ComicPosition;
import mio.sis.com.comicmana.sdata.ComicSrc;

/**
 * Created by Administrator on 2017/12/26.
 */

public class SImgPage {
    /*
        SImgPage 是 UI 控件，負責顯示一頁漫畫，SImgPage 是 ImgViewer 的其中一頁
        ImgPage 有以下狀態
        當使用者正在觀看的區域離當前頁面很遠時，ImgViewer 會呼叫 Out 來提醒 SImgPage 此頁面不需要重新渲染
     */
    final int SIP_WAIT = 0,     //  圖片未讀入
            SIP_WAIT_OUT = 1,   //  圖片未讀入，不需要認真顯示
            SIP_READY = 2,      //  圖片已載入
            SIP_READY_OUT = 3,  //  圖片已載入，不需要認真顯示
            SIP_ERROR = 4,      //  圖片載入失敗
            SIP_ERROR_OUT = 5;  //  圖片載入失敗，不需要認真顯示


    ImgCache cache;
    ImageView view;

    //  當前漫畫和頁數
    ComicSrc src;
    ComicPosition position;

    int status;
    int percent;            //  當前圖片讀取進度
    Bitmap bitmap;          //  當前圖片物件
    int img_width;          //  當前圖片寬度(pixel)
    int screen_width,
            screen_height;   //  當前螢幕長寬
    float scale_rate;       //  當前放大倍率
    int view_width, view_height;
    /*
      以 img_width, screen_width, scale_rate 綜合計算出當前 ImageView 長寬應該多少
       */
    public SImgPage(Context context, ImgCache cache) {
        this.cache = cache;
        status = SIP_WAIT_OUT;
        bitmap = null;
        percent = 0;
        view = new ImageView(context);
    }

    /*
        更新圖示為漫畫影像
     */
    public void SetImage(Bitmap bitmap) {

    }
    /*
        更新圖示為錯誤提醒圖示
     */
    public void SetError() {

    }
    /*
        更新圖示為目前讀取進度
        percent = 0~100
     */
    public void SetProgress(int percent) {

    }
    /*
        更新目前螢幕寬度
     */
    public void SetWidthPixel(int pixel) {

    }
    /*
        更新目前放大倍率
     */
    public void SetScale(float scale) {

    }
    /*

     */
    public void Out() {
        switch(status) {
            case SIP_WAIT:
                status = SIP_WAIT_OUT;
                break;
            case SIP_READY:
                cache.CacheBitmap(src, position, bitmap);
                bitmap = null;
                status = SIP_READY_OUT;
                break;
            case SIP_ERROR:
                status = SIP_ERROR_OUT;
                break;
            default:
                break;
        }
    }
    public void In() {
        switch(status) {
            case SIP_WAIT_OUT:
                status = SIP_WAIT;
                Redraw();
                break;
            case SIP_READY_OUT:
                //  重新從 cache 取回圖片
                status = SIP_WAIT;
                percent = 0;
                Redraw();
                //  直接由 SetImage 更新 status
                break;
            case SIP_ERROR_OUT:
                status = SIP_ERROR;
                Redraw();
                break;
        }
    }
    /*
        更新 ImageView
     */
    void Redraw() {

    }
}
