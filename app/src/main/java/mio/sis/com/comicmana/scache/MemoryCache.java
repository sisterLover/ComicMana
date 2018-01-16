package mio.sis.com.comicmana.scache;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

import java.util.concurrent.Semaphore;

import mio.sis.com.comicmana.sdata.ComicPosition;
import mio.sis.com.comicmana.sdata.ComicSrc;

/**
 * Created by Administrator on 2018/1/2.
 */

/*
    所有圖片要顯示之前都要先 cache 過
    cache 會自動將圖片縮放至合理大小
*/
public class MemoryCache {
    static final int CACHE_SIZE = 10;
    class CacheEntry {
        ComicSrc src;
        ComicPosition pos;
        Bitmap bitmap;
        int next, last;
    }

    int entryConunt, front, back;
    CacheEntry[] entries = new CacheEntry[10];
    Semaphore cacheSemaphore = new Semaphore(1);

    void Lock() throws InterruptedException { cacheSemaphore.acquire(); }
    void Unlock() { cacheSemaphore.release(); }
    public MemoryCache() {
        for(int i=0;i<CACHE_SIZE;++i) {
            entries[i] = new CacheEntry();
            entries[i].src = new ComicSrc();
            entries[i].pos = new ComicPosition();
        }
        Clear();
    }
    public void Clear() {
        try {
            Lock();
            entryConunt = 0;
            front = back = 0;
            for (int i = 0; i < CACHE_SIZE; ++i) {
                entries[i].next = (i + 1) % CACHE_SIZE;
                entries[i].last = (i + CACHE_SIZE - 1) % CACHE_SIZE;
                entries[i].bitmap = null;
            }
            Unlock();
        } catch (Exception e) {
            Log.d("IC_TAG", "Clear interrupted, BAD NEWS");
        }
    }
    /*
        return null as fail
     */
    public Bitmap PushCache(ComicSrc src, ComicPosition pos, Bitmap bitmap) {
        if(bitmap.getWidth() != DefaultPageCache.GetWidth()) {
            bitmap = ScaleBitmap(bitmap);
            if (bitmap == null) {
                Log.d("IC_TAG", "bitmap scale fail");
                return null;
            }
        }
        try {
            Lock();
            if(entryConunt < CACHE_SIZE) {
                //  cache 還沒填滿，則 Clear 時已經將 last next 設定好
                entries[entryConunt].src.Copy(src);
                entries[entryConunt].pos.Copy(pos);
                entries[entryConunt].bitmap = bitmap;
                back = entryConunt;
                ++entryConunt;
            }
            else {
                    /*  cache 填滿，pop_front
                        原本是 mid - new_front - ... - old_back
                        變成 new_front - ... - old_back - mid
                    */
                int mid = front, new_front = entries[front].next, old_back = back;

                entries[mid].next = new_front;
                entries[mid].last = old_back;
                entries[mid].src.Copy(src);
                entries[mid].pos.Copy(pos);
                entries[mid].bitmap = bitmap;

                entries[new_front].last = mid;
                entries[old_back].next = mid;
            }

            Unlock();
        }
        catch (Exception e) {
        }
        return bitmap;
    }
    /*
        找不到或是錯誤時回傳 null
     */
    public Bitmap FindCache(ComicSrc src, ComicPosition pos) {
        Bitmap result = null;
        int index = -1;
        try {
            Lock();
            for (int i = 0; i < entryConunt; ++i) {
                if (entries[i].src.Equal(src) && entries[i].pos.Equal(pos)) {
                    index = i;
                }
            }
            if (index != -1) {
                int item_last = entries[index].last, item_next = entries[index].next;

                entries[item_last].next = item_next;
                entries[item_next].last = item_last;

                entries[back].next = index;
                entries[index].last = back;

                entries[front].last = index;
                entries[index].next = front;

                back = index;
                result = entries[index].bitmap;
            }
            Unlock();
        } catch (Exception e) {
        }
        return result;
    }
    Bitmap ScaleBitmap(Bitmap bitmap) {
        if(!DefaultPageCache.ParamAvailable()) return null;
        int width = DefaultPageCache.GetWidth(), height;
        height = bitmap.getHeight()*width/bitmap.getWidth();
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Rect src = new Rect(), dst = new Rect();
        src.left = src.top = dst.left = dst.top = 0;
        src.right = bitmap.getWidth();
        src.bottom = bitmap.getHeight();
        dst.right = width;
        dst.bottom = height;
        canvas.drawBitmap(bitmap, src, dst, null);
        return result;
    }
}
