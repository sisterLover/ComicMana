package mio.sis.com.comicmana.snet;

import android.graphics.Bitmap;

import mio.sis.com.comicmana.sui.SImagePage;

/**
 * Created by Administrator on 2017/12/29.
 */

public class TestComicImageHelperCallback implements NetImageHelper.ComicPageCallback {
    SImagePage imagePage;
    public TestComicImageHelperCallback(SImagePage imagePage) {
        this.imagePage = imagePage;
    }
    @Override
    public void PageRecieve(Bitmap bitmap) {
        if(bitmap == null) imagePage.PostError();
        else imagePage.PostImage(bitmap);
    }

    @Override
    public void UpdateProgress(int percent) {
        imagePage.PostProgress(percent);
    }
}
