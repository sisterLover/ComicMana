package mio.sis.com.comicmana.snet;

import android.graphics.Bitmap;

import mio.sis.com.comicmana.sui.SImagePage;

/**
 * Created by Administrator on 2017/12/28.
 */

public class SImagePageCallback implements NetImageHelper.ComicPageCallback {
    SImagePage imagePage;

    public SImagePageCallback(SImagePage imagePage) {
        this.imagePage = imagePage;
    }
    @Override
    public void PageRecieve(Bitmap bitmap) {
        if(bitmap==null) imagePage.PostError();
        imagePage.PostImage(bitmap);
    }

    @Override
    public void UpdateProgress(int percent) {
        imagePage.PostProgress(percent);
    }
}
