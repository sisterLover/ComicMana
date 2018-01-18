package mio.sis.com.comicmana.mine;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import mio.sis.com.comicmana.sdata.ComicInfo;
import mio.sis.com.comicmana.sdata.ComicSrc;
import mio.sis.com.comicmana.snet.NetSiteHelper;

/**
 * Created by Nako on 2018/1/12.
 */

public class Web_wnacg_ComicInfo implements NetSiteHelper {

    //基本上因為是一頁一頁的
    //所以我直接把startFrom當作頁數來用了
    //起始頁為1
    //這網頁一頁就是12格(不管是資訊頁還是瀏覽頁)
    //他內含太多javascript，Jsoup無法動態去抓頁數
    //祈禱他不要更新吧

    @Override
    public ComicInfo RequestComicInfo(ComicSrc src) {
        return null;
    }

    @Override
    public boolean IsComicAvailable(ComicSrc src) {
        return false;
    }

    private String url;


    @Override
    public void EnumComic(ComicSrc src, int startFrom, int length, EnumCallback callback) {
        if(src.srcType != ComicSrc.SrcType.ST_NET_WNACG) {
            callback.ComicDiscover(null);
            return;
        }
        new Web_wnacg_ComicInfo.InnerThread(startFrom, length, callback).start();
    }
    class InnerThread extends Thread {
        int startFrom, length; EnumCallback callback;
        InnerThread(int startFrom, int length, EnumCallback callback) {
            this.startFrom = startFrom; this.length = length; this.callback = callback;
        }
        @Override
        public void run() {
            super.run();
            //爬出陣列 info
            ComicInfo[] info = new ComicInfo[wnacg_util.per];
            int index=0;
            url=(startFrom<1)?"https://www.wnacg.org/albums-index-page-1.html":"https://www.wnacg.org/albums-index-page-"+startFrom+".html";

            try {

                Document doc = Jsoup.connect(url).get();
                Element container=doc.getElementById("classify_container");
                Elements elements=container.select("li");
                for(Element data : elements) {
                    ComicInfo comicInfo=new ComicInfo();
                    comicInfo.AllocateChapter(1);
                    //名稱
                    comicInfo.name=data.select("a.txtA").text();
                    comicInfo.chapterInfo[1].title=comicInfo.name;

                    //來源
                    comicInfo.src.srcType=ComicSrc.SrcType.ST_NET_WNACG;
                    comicInfo.src.path=data.select("a").attr("abs:href").toString();

                    //章節
                    comicInfo.chapterCnt=1;

                    //頁數
                    String s=data.select("span.info").text();
                    int n = Integer.parseInt(s.split("張")[0]);
                    //已1為基底

                    comicInfo.chapterInfo[1].pageCnt=n;


                    //封面
                    String tempUrl=data.select("img").attr("src");
                    comicInfo.thumbnail=wnacg_util.getBitmapFromURL(wnacg_util.WebSite+tempUrl);

                    info[index]=comicInfo;
                    index++;
                }
            }catch (IOException e)
            {
                //Handle error
            }


            callback.ComicDiscover(info);
        }
    }


}
