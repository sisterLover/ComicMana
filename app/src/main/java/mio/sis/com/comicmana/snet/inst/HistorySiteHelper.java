package mio.sis.com.comicmana.snet.inst;

import java.util.ArrayList;

import mio.sis.com.comicmana.MainActivity;
import mio.sis.com.comicmana.scache.ComicInfoCache;
import mio.sis.com.comicmana.sdata.ComicInfo;
import mio.sis.com.comicmana.sdata.ComicSrc;
import mio.sis.com.comicmana.snet.NetSiteHelper;

/**
 * Created by Administrator on 2018/1/18.
 */

public class HistorySiteHelper implements NetSiteHelper {
    @Override
    public void EnumComic(ComicSrc src, int startFrom, int length, EnumCallback callback) {
        if(src.srcType != ComicSrc.SrcType.ST_HISTORY) {
            callback.ComicDiscover(null);
            return;
        }
        ComicSrc[] comicSrcs = MainActivity.historyRecord.GetHistory();
        ArrayList<ComicInfo> comicInfos = new ArrayList<>();
        for(int i=0;i<comicSrcs.length;++i) {
            if(ComicInfoCache.IsComicAvailable(comicSrcs[i])) {
                comicInfos.add(ComicInfoCache.RequestComicInfo(comicSrcs[i]));
                if(comicInfos.size() >= startFrom + length) break;
            }
        }
        int resultLength = comicInfos.size() - startFrom;
        if(resultLength <= 0) {
            callback.ComicDiscover(null);
            return;
        }
        ComicInfo[] result = new ComicInfo[resultLength];
        for(int i=0;i<resultLength;++i) {
            result[i] = comicInfos.get(startFrom + i);
        }
        callback.ComicDiscover(result);
    }

    @Override
    public boolean IsComicAvailable(ComicSrc src) {
        return false;
    }

    @Override
    public ComicInfo RequestComicInfo(ComicSrc src) {
        return null;
    }
}
