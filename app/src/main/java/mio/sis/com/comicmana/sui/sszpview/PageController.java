package mio.sis.com.comicmana.sui.sszpview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import mio.sis.com.comicmana.R;
import mio.sis.com.comicmana.sdata.ComicInfo;
import mio.sis.com.comicmana.sdata.ComicPosition;
import mio.sis.com.comicmana.sui.SImagePage;

/**
 * Created by Administrator on 2017/12/28.
 */

public class PageController {
    static final int PAGE_GROUP_SIZE = 5;  //  10 頁為一個單位
    static final String HEAD_LOADING = "載入中...", HEAD_NO_MORE = "已經沒有了QQ";

    Context context;
    SSZPView sszpView;

    ComicInfo comicInfo;

    ComicPosition currentGroup, lastGroup, nextGroup;       //  當前應該產生的 page
    int currentGroupSize, lastGroupSize, nextGroupSize;     //  group size = group 有幾頁
    boolean currentInserted, lastInserted, nextInserted;    //  當前應該產生的 page 是否已經加入到 attachView
    SImagePage[] currentPages, lastPages, nextPages;
    //  group.page 為該 group 第一個 page 的頁數，必為 PAGE_GROUP_SIZE 的倍數 + 1

    LinearLayout head, tail;

    //  current position
    int[] viewHeightInfos;   //  紀錄每個 page 的高度(還沒 scale 過的高度) (藉此知道 scroll 到哪一頁)
    int scrollGroup, scrollPage, scrollOffset;  //  表示當前 scroll 的位置
    /*
        group
            -1 = head
            0  = last
            1  = current
            2  = next
            3  = tail
        page 表示是該 group 的第幾頁 (1 base)
        offset 表示從該頁開始往下位移多少 pixel(還沒 scale 過的 pixel)
     */

    public PageController(Context context, SSZPView sszpView) {
        this.context = context;
        this.sszpView = sszpView;
        head = null;
        tail = null;
        currentInserted = lastInserted = nextInserted = false;
    }
    /*
        必須在 UI Thread 被呼叫
     */
    public void SetComicInfo(ComicInfo comicInfo, ComicPosition comicPosition) {
        if (!sszpView.IsSizeAvailable() || head == null) {
            sszpView.PostComicInfo(comicInfo, comicPosition);
            return;
        }
        LinearLayout attachView = sszpView.GetAttachView();
        attachView.removeAllViews();
        head = tail = null;
        currentInserted = lastInserted = nextInserted = false;

        this.comicInfo = comicInfo;
        ComicPosition currentPage = new ComicPosition();
        currentPage.Copy(comicPosition);

        //  產生頭尾頁
        GenHeadPage();

        //  將不合法的位置校正回正常位置
        if (currentPage.chapter > comicInfo.chapterCnt) {
            currentPage.chapter = comicInfo.chapterCnt;
        }
        if (currentPage.page > comicInfo.chapterPages[currentPage.chapter]) {
            currentPage.page = comicInfo.chapterPages[currentPage.chapter];
        }

        //  計算 group
        currentGroup.chapter = currentPage.chapter;
        currentGroup.page = PageToGroupPage(currentPage.page);

        lastGroup = GetLastGroup(currentGroup);
        nextGroup = GetNextGroup(currentGroup);

        UpdateHeadString();

        currentGroupSize = GetGroupSize(currentGroup);
        lastGroupSize = 0;
        if (lastGroup != null) lastGroupSize = GetGroupSize(lastGroup);
        nextGroupSize = 0;
        if (nextGroup != null) nextGroupSize = GetGroupSize(nextGroup);

        if (lastGroup != null && lastGroup.chapter == currentGroup.chapter) GenLastGroup();
        GenCurrentGroup();
        if (nextGroup != null && nextGroup.chapter == currentGroup.chapter) GenNextGroup();

        //  計算 scroll 位置
        scrollGroup = 1;
        scrollPage = currentPage.page - currentGroup.page + 1;
        scrollOffset = 0;

        SetOffsetY();
    }
    /*
        回傳 int[3]，每個代表 viewGroup.addView(view, position) 的 position 參數
        int[0] = lastGroup 插入參數
        int[1] = currentGroup 插入參數
        int[2] = nextGroup 插入參數
     */
    int[] GenInsertPosition() {
        int[] result = new int[3];
        result[0] = 0;
        if(head != null) ++result[0];
        result[1] = result[0];
        if(lastInserted) result[1] += lastGroupSize;
        result[2] = result[1];
        if(currentInserted) result[2] += currentGroupSize;
        return result;
    }
    void GenLastGroup() {
        if(lastInserted) return;

        lastPages = GenGroupPage(lastGroup, lastGroupSize);
        GenGroup(0, lastPages);
        lastInserted = true;
    }
    void GenCurrentGroup() {
        if(currentInserted) return;

        currentPages = GenGroupPage(currentGroup, currentGroupSize);
        GenGroup(1, currentPages);
        currentInserted = true;
    }
    void GenNextGroup() {
        if(nextInserted) return;

        nextPages = GenGroupPage(nextGroup, nextGroupSize);
        GenGroup(2, nextPages);
        nextInserted = true;
    }
    void GenGroup(int insertIndex, SImagePage[] pages) {
        int[] insertPosition = GenInsertPosition();
        LinearLayout attachView = sszpView.GetAttachView();
        for(int i=0;i<pages.length;++i) {
            attachView.addView(pages[i], insertPosition[insertIndex] + 1);
        }
        for(int i=0;i<pages.length;++i) {
            pages[i].RequestImage();
        }
    }
    SImagePage[] GenGroupPage(ComicPosition group, int groupSize) {
        SImagePage[] pages = new SImagePage[groupSize];
        for(int i=0;i<groupSize;++i) {
            ComicPosition position = new ComicPosition();
            position.chapter = group.chapter;
            position.page = group.page + i;
            SImagePage.Params params = new SImagePage.Params(sszpView ,comicInfo.src, position);
            pages[i] = new SImagePage(context, params);
        }
        return pages;
    }
    public void GenHeadPage() {
        if(head == null) return;

        LayoutInflater inflater = LayoutInflater.from(context);
        head = (LinearLayout)inflater.inflate(R.layout.sszp_head_page_layout, null);
        tail = (LinearLayout)inflater.inflate(R.layout.sszp_head_page_layout, null);

        LinearLayout attachView = sszpView.GetAttachView();
        attachView.addView(head);
        attachView.addView(tail);

        head.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        tail.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        head.setLayoutParams(new LinearLayout.LayoutParams(
                sszpView.GetStandardWidth(), head.getMeasuredHeight()
        ));
        tail.setLayoutParams(new LinearLayout.LayoutParams(
                sszpView.GetStandardWidth(), tail.getMeasuredHeight()
        ));
    }
    void UpdateHeadString() {
        boolean headAvailable = true, tailAvailable = true;
        if(lastGroup == null) headAvailable = false;
        else if(GetLastGroup(lastGroup) == null) headAvailable = false;
        if(nextGroup == null) tailAvailable = false;
        else if(GetNextGroup(nextGroup) == null) tailAvailable = false;
        SetHeadPage(headAvailable, tailAvailable);
    }
    void SetHeadPage(boolean headAvailable, boolean tailAvailable) {
        TextView headTextView, tailTextView;

        headTextView = head.findViewById(R.id.sszp_head_page_text);
        tailTextView = tail.findViewById(R.id.sszp_head_page_text);

        if(headAvailable) {
            headTextView.setText(HEAD_LOADING);
        }
        else {
            headTextView.setText(HEAD_NO_MORE);
        }
        if(tailAvailable) {
            tailTextView.setText(HEAD_LOADING);
        }
        else {
            tailTextView.setText(HEAD_NO_MORE);
        }
    }
    /*
        return null 表示已經沒有前一個 group
     */
    ComicPosition GetNextGroup(ComicPosition position) {
        if (position.chapter < 1 || position.chapter > comicInfo.chapterCnt) return null;
        if (position.page < 1 || position.page > comicInfo.chapterPages[position.chapter])
            return null;

        int chapterLastGroup = PageToGroupPage(comicInfo.chapterPages[position.chapter]);

        ComicPosition result = new ComicPosition();
        if (position.page >= chapterLastGroup) {
            result.chapter = position.chapter + 1;
            if (result.chapter > comicInfo.chapterCnt) {
                result = null;
                return null;
            }
            result.page = 1;
            return result;
        }
        result.chapter = position.chapter;
        result.page = PageToGroupPage(position.page + PAGE_GROUP_SIZE);
        return result;
    }
    ComicPosition GetLastGroup(ComicPosition position) {
        if (position.chapter < 1 || position.chapter > comicInfo.chapterCnt) return null;
        if (position.page < 1 || position.page > comicInfo.chapterPages[position.chapter])
            return null;

        ComicPosition result = new ComicPosition();
        if (position.page <= PAGE_GROUP_SIZE) {
            result.chapter = position.chapter - 1;
            if (result.chapter < 1) {
                result = null;
                return null;
            }
            result.page = PageToGroupPage(comicInfo.chapterPages[result.chapter]);
            return result;
        }
        result.chapter = position.chapter;
        result.page = PageToGroupPage(position.page - PAGE_GROUP_SIZE);
        return result;
    }
    /*
        傳入頁數，回傳該頁數所處的 group 的第一頁的頁數
        f(x) = y
        1~N     => 1
        N+1~2N  => N+1
     */
    int PageToGroupPage(int page) {
        return ((page-1)/PAGE_GROUP_SIZE)*PAGE_GROUP_SIZE+1;
    }
    /*
        取得該 group 有幾頁
        <= PAGE_GROUP_SIZE
     */
    int GetGroupSize(ComicPosition position) {
        int group = PageToGroupPage(position.page);
        return Math.min(PAGE_GROUP_SIZE, comicInfo.chapterPages[position.chapter] - group + 1);
    }

    void CalculateViewHeightInfo() {
        viewHeightInfos = new int[lastGroupSize + currentGroupSize + nextGroupSize + 2];
        //  +2 = head && tail
        viewHeightInfos[0] = 0;
        viewHeightInfos[1] = head.getMeasuredHeight();
        for(int i=0;i<lastGroupSize;++i) {
            viewHeightInfos[2 + i] = viewHeightInfos[1 + i] + lastPages[i].GetHeight();
        }
        for(int i=0;i<currentGroupSize;++i) {
            viewHeightInfos[2 + lastGroupSize + i] =
                    viewHeightInfos[1 + lastGroupSize + i] + currentPages[i].GetHeight();
        }
        for(int i=0;i<nextGroupSize;++i) {
            viewHeightInfos[2 + lastGroupSize + currentGroupSize + i] =
                    viewHeightInfos[1 + lastGroupSize + currentGroupSize + i] + nextPages[i].GetHeight();
        }
    }
    void UpdateScrollInfo(int offsetY) {
        float scaleOffsetY = offsetY / sszpView.GetZoomFactor();
        int index = 1;
        while (index < viewHeightInfos.length && viewHeightInfos[index] < scaleOffsetY) ++index;
        --index;
        if (index == 0) {
            scrollGroup = -1;
            scrollPage = 0;
            scrollOffset = offsetY;
            return;
        }
        //  理論上不會滑到 tail
        if (index == viewHeightInfos.length - 1) {
            scrollGroup = 3;
            scrollPage = 0;
            scrollOffset = 0;
            return;
        }
        scrollOffset = (int) (scaleOffsetY - viewHeightInfos[index]);
        if (index <= lastGroupSize && lastInserted) {
            scrollGroup = 0;
            scrollPage = index;
        } else if (index <= lastGroupSize + currentGroupSize) {
            scrollGroup = 1;
            scrollPage = index - lastGroupSize;
        } else {
            scrollGroup = 2;
            scrollPage = index - lastGroupSize - currentGroupSize;
        }
    }
    /*
        使用當前的 scroll Group + Page + Offset 設定 SSZPView 的 offsetY
     */
    void SetOffsetY() {
        if (scrollGroup == -1) {
            sszpView.SetOffsetY(scrollOffset);
            return;
        }
        if (scrollGroup == 3) {
            sszpView.SetOffsetY(viewHeightInfos[viewHeightInfos.length - 1] + scrollOffset);
            sszpView.BoundOffset();
            return;
        }
        int baseIndex = 1;
        if (scrollGroup > 0) baseIndex += lastGroupSize;
        if (scrollGroup > 1) baseIndex += currentGroupSize;
        baseIndex += (scrollPage - 1);
        sszpView.SetOffsetY((int) ((viewHeightInfos[baseIndex] + scrollOffset) * sszpView.GetZoomFactor()));
    }
}
