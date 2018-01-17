package mio.sis.com.comicmana.scache;

import android.graphics.Bitmap;

import mio.sis.com.comicmana.snet.NetImageHelper;
import mio.sis.com.comicmana.sui.comp.SImagePage;

/**
 * Created by Administrator on 2018/1/16.
 */

public class DefaultImageHelper implements NetImageHelper.ComicPageCallback {
    private SImagePage imagePage;
    public DefaultImageHelper(SImagePage imagePage) {
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
