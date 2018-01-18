package mio.sis.com.comicmana.mine;

import mio.sis.com.comicmana.sdata.ComicInfo;
import mio.sis.com.comicmana.sdata.ComicSrc;
import mio.sis.com.comicmana.snet.NetSiteHelper;

/**
 * Created by Nako on 2018/1/18.
 */

public class PagerCallBack implements NetSiteHelper.EnumCallback{
    Pager pager;
    public PagerCallBack(Pager pager) {this.pager=pager;}


    @Override
    public void ComicDiscover(ComicInfo[] info) {
        pager.PostUpdatePager(info);
    }
}
