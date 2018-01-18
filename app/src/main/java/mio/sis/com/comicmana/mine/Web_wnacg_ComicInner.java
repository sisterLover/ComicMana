package mio.sis.com.comicmana.mine;

import android.graphics.Bitmap;
import android.os.SystemClock;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import mio.sis.com.comicmana.scache.DefaultPageCache;
import mio.sis.com.comicmana.sdata.ComicPosition;
import mio.sis.com.comicmana.sdata.ComicSrc;
import mio.sis.com.comicmana.snet.NetImageHelper;

/**
 * Created by Nako on 2018/1/12.
 */

public class Web_wnacg_ComicInner implements NetImageHelper {

    public String url;


    @Override
    public void GetComicPage(ComicSrc src, ComicPosition position, ComicPageCallback callback) {
        if (src.srcType != ComicSrc.SrcType.ST_NET_WNACG) {
            callback.PageRecieve(null);
            return;
        }
        new Web_wnacg_ComicInner.InnerThread(src, position, callback).start();
    }

    class InnerThread extends Thread {
        ComicSrc src;
        ComicPosition position;
        ComicPageCallback callback;
        public InnerThread(ComicSrc src,ComicPosition position, ComicPageCallback callback) {
            this.src=src;
            this.position = position;
            this.callback = callback;
        }
        @Override
        public void run() {
            super.run();


            try {
                //第幾頁
                int pages=(position.page-1)/wnacg_util.per+1;
                //該頁第幾格
                int index=position.page-((pages-1)*wnacg_util.per);


                url = src.path.replace("index",",");
                String[] temp=url.split(",");
                url=temp[0]+"index-page-"+pages+temp[1];

                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")
                        .referrer("http://www.google.com")
                        .get();

                //網頁為動態網頁，只能強取img
                //網頁的第一張圖為封面，估忽略
                //緊接幾個ui圖片，因此在尚未偵測到有包含data/t的url時，也忽略
                //Log.d(",",doc.toString());

                int ing=1;
                Elements elements = doc.body().select("img");
                Boolean ignoreFirst = true;
                Bitmap bitmap;
                for (Element data : elements) {
                    String str = data.attr("src");
                    if (str.contains("data/t")) {

                        //忽略第一筆
                        if (ignoreFirst) {
                            Log.d("?", "?");
                            ignoreFirst = false;
                            continue;
                        } else {
                            if(ing==index)
                            {
                                callback.PageRecieve(wnacg_util.getBitmapFromURL(wnacg_util.WebSite+str));
                                break;
                            }
                            else
                            {
                                ing++;
                            }
                        }
                    }
                }
                callback.UpdateProgress(100);
            }
            catch(IOException e)
            {
                callback.UpdateProgress(-1);
            }

        }
    }
}
