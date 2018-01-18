package mio.sis.com.comicmana.mine;

import mio.sis.com.comicmana.sdata.ComicInfo;
import mio.sis.com.comicmana.snet.NetImageHelper;
import mio.sis.com.comicmana.snet.NetSiteHelper;

/**
 * Created by Nako on 2018/1/16.
 */

public class GridCallBack implements NetSiteHelper.EnumCallback{
    Grid comicGrid;
    public GridCallBack(Grid comicGrid)
    {
        this.comicGrid=comicGrid;
    }

    @Override
    public void ComicDiscover(ComicInfo[] info) {

        comicGrid.PostUpdateGridView(info);
    }
}
