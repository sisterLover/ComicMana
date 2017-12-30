package mio.sis.com.comicmana.sui.sszpview;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Set;

import mio.sis.com.comicmana.R;
import mio.sis.com.comicmana.sdata.ComicInfo;
import mio.sis.com.comicmana.sdata.ComicPosition;
import mio.sis.com.comicmana.sui.HintText;
import mio.sis.com.comicmana.sui.SImagePage;

/**
 * Created by Administrator on 2017/12/28.
 */

public class PageController {
    static final String PC_TAG = "PC_TAG";
    static final int PAGE_GROUP_SIZE = 5;  //  5 頁為一個單位
    static final String HEAD_LOADING = "載入中...", HEAD_NO_MORE = "已經沒有了QQ";
    static final int
            GROUP_HEAD = -1,
            GROUP_CURRENT = 1,
            GROUP_LAST = 0,
            GROUP_NEXT = 2,
            GROUP_TAIL = 3;

    Context context;
    SSZPView sszpView;

    ComicInfo comicInfo;

    GroupInfo lastGroup, currentGroup, nextGroup;

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
        lastGroup = new GroupInfo();
        currentGroup = new GroupInfo();
        nextGroup = new GroupInfo();
    }
    public boolean ScrollAvailable() {
        return head != null;
    }
    /*
        必須在 UI Thread 被呼叫
     */
    public void SetComicInfo(ComicInfo comicInfo, ComicPosition comicPosition) {
        if (!sszpView.IsSizeAvailable()) {
            Log.d(PC_TAG, "Repost ComicInfo");
            sszpView.PostComicInfo(comicInfo, comicPosition);
            return;
        }
        Log.d(PC_TAG,"Setting ComicInfo");
        LinearLayout attachView = sszpView.GetAttachView();
        attachView.removeAllViews();
        head = tail = null;
        lastGroup.Clear();
        currentGroup.Clear();
        nextGroup.Clear();

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
        Log.d(PC_TAG, "Current Postion = " + currentPage.chapter + "-" + currentPage.page);
        //  計算 group
        currentGroup.position = new ComicPosition();
        currentGroup.position.chapter = currentPage.chapter;
        currentGroup.position.page = PageToGroupPage(currentPage.page);

        currentGroup.UpdateSize();
        lastGroup.SetLastGroupOf(currentGroup);
        nextGroup.SetNextGroupOf(currentGroup);

        currentGroup.Debug("Current");
        lastGroup.Debug("Last");
        nextGroup.Debug("Next");

        UpdateHeadString();

        if (lastGroup.Valid() && lastGroup.position.chapter == currentGroup.position.chapter) GenLastGroup();
        GenCurrentGroup();
        if (nextGroup.Valid() && nextGroup.position.chapter == currentGroup.position.chapter) GenNextGroup();

        CalculateViewHeightInfo();
        sszpView.CalculateAttachSize();
        //  計算 scroll 位置
        scrollGroup = GROUP_CURRENT;
        scrollPage = currentPage.page - currentGroup.position.page + 1;
        scrollOffset = 0;

        SetOffsetY();
        UpdatePageHint(HintText.STANDARD_HINT);
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
        if(lastGroup.Valid() && lastGroup.inserted) result[1] += lastGroup.size;
        result[2] = result[1];
        if(currentGroup.Valid() && currentGroup.inserted) result[2] += currentGroup.size;
        return result;
    }
    void GenLastGroup() {
        if(lastGroup.inserted) return;

        lastGroup.pages = GenGroupPage(lastGroup.position, lastGroup.size);
        InsertGroupPage(GROUP_LAST, lastGroup.pages);
        lastGroup.inserted = true;
    }
    void GenCurrentGroup() {
        if(currentGroup.inserted) return;

        currentGroup.pages = GenGroupPage(currentGroup.position, currentGroup.size);
        InsertGroupPage(GROUP_CURRENT, currentGroup.pages);
        currentGroup.inserted = true;
    }
    void GenNextGroup() {
        if(nextGroup.inserted) return;

        nextGroup.pages = GenGroupPage(nextGroup.position, nextGroup.size);
        InsertGroupPage(GROUP_NEXT, nextGroup.pages);
        nextGroup.inserted = true;
    }
    void InsertGroupPage(int insertIndex, SImagePage[] pages) {
        int[] insertPosition = GenInsertPosition();
        LinearLayout attachView = sszpView.GetAttachView();
        for(int i=0;i<pages.length;++i) {
            attachView.addView(pages[i], insertPosition[insertIndex] + i);
        }
        for(int i=0;i<pages.length;++i) {
            pages[i].RequestImage();
        }
    }
    void RemoveGroup(int insertIndex, int groupSize) {
        int[] insertPosition = GenInsertPosition();
        LinearLayout attachView = sszpView.GetAttachView();
        /*
            reset class member and check scroll
         */
        if(insertIndex == GROUP_LAST) {
            if(!lastGroup.inserted) return;
            lastGroup.Clear();
            //  scroll
            if(scrollGroup == 0) {
                scrollGroup = GROUP_CURRENT;
                scrollPage = 0;
                scrollOffset = 0;
            }
        }
        else if(insertIndex == GROUP_CURRENT) {
            if(!currentGroup.inserted) return;
            currentGroup.Clear();
        }
        else {
            if(!nextGroup.inserted) return;
            nextGroup.Clear();
            if(scrollGroup == 2) {
                scrollGroup = GROUP_CURRENT;
                scrollPage = 0;
                scrollOffset = 0;
            }
        }
        attachView.removeViews(insertPosition[insertIndex], groupSize);
    }
    SImagePage[] GenGroupPage(ComicPosition group, int groupSize) {
        SImagePage[] pages = new SImagePage[groupSize];
        for(int i=0;i<groupSize;++i) {
            ComicPosition position = new ComicPosition();
            position.chapter = group.chapter;
            position.page = group.page + i;
            Log.d(PC_TAG, "GenPage " + position.chapter + "-" + position.page);
            SImagePage.Params params = new SImagePage.Params(sszpView ,comicInfo.src, position);
            pages[i] = new SImagePage(context, params);
        }
        return pages;
    }
    public void GenHeadPage() {
        if(head != null) return;

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
        if(!lastGroup.Valid()) headAvailable = false;
        else if(GetLastGroup(lastGroup.position) == null) headAvailable = false;
        if(!nextGroup.Valid()) tailAvailable = false;
        else if(GetNextGroup(nextGroup.position) == null) tailAvailable = false;
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
        viewHeightInfos = new int[lastGroup.size + currentGroup.size + nextGroup.size + 2];
        //  +2 = head && tail
        viewHeightInfos[0] = 0;
        viewHeightInfos[1] = head.getMeasuredHeight();
        for (int i = 0; i < lastGroup.size && lastGroup.inserted; ++i) {
            viewHeightInfos[2 + i] = viewHeightInfos[1 + i] + lastGroup.pages[i].GetHeight();
        }
        for (int i = 0; i < currentGroup.size && currentGroup.inserted; ++i) {
            viewHeightInfos[2 + lastGroup.size + i] =
                    viewHeightInfos[1 + lastGroup.size + i] + currentGroup.pages[i].GetHeight();
        }
        for (int i = 0; i < nextGroup.size && nextGroup.inserted; ++i) {
            viewHeightInfos[2 + lastGroup.size + currentGroup.size + i] =
                    viewHeightInfos[1 + lastGroup.size + currentGroup.size + i] + nextGroup.pages[i].GetHeight();
        }
    }
    public void UpdateScrollInfo(int offsetY) {
        float scaleOffsetY = offsetY / sszpView.GetZoomFactor();
        int index = 1;
        while (index < viewHeightInfos.length && viewHeightInfos[index] < scaleOffsetY) ++index;
        --index;
        if (index == 0) {
            scrollGroup = GROUP_HEAD;
            scrollPage = 0;
            scrollOffset = offsetY;
            return;
        }
        //  理論上不會滑到 tail
        if (index == viewHeightInfos.length - 1) {
            scrollGroup = GROUP_TAIL;
            scrollPage = 0;
            scrollOffset = 0;
            return;
        }
        scrollOffset = (int) (scaleOffsetY - viewHeightInfos[index]);
        if (index <= lastGroup.size && lastGroup.inserted) {
            scrollGroup = GROUP_LAST;
            scrollPage = index;
        } else if (index <= lastGroup.size + currentGroup.size) {
            scrollGroup = GROUP_CURRENT;
            scrollPage = index - lastGroup.size;
        } else {
            scrollGroup = GROUP_NEXT;
            scrollPage = index - lastGroup.size - currentGroup.size;
        }
    }
    /*
        更新 hint, 顯示目前章節跟頁數
     */
    public void UpdatePageHint(HintText hintText) {
        //DebugScrollInfo();
        int chapter = 0, page = 0, totalPage = 0;
        if (scrollGroup == GROUP_HEAD) {
            //  first page
            if (lastGroup.inserted) {
                chapter = lastGroup.position.chapter;
                page = lastGroup.position.page;
            } else {
                chapter = currentGroup.position.chapter;
                page = currentGroup.position.page;
            }
        } else if (scrollGroup == GROUP_LAST) {
            chapter = lastGroup.position.chapter;
            page = lastGroup.position.page + scrollPage - 1;
        } else if (scrollGroup == GROUP_CURRENT) {
            chapter = currentGroup.position.chapter;
            page = currentGroup.position.page + scrollPage - 1;
        } else if (scrollGroup == GROUP_NEXT) {
            chapter = nextGroup.position.chapter;
            page = nextGroup.position.page + scrollPage - 1;
        } else {
            //  last page
            if (nextGroup.inserted) {
                chapter = nextGroup.position.chapter;
                page = nextGroup.position.page + nextGroup.size - 1;
            } else {
                chapter = currentGroup.position.chapter;
                page = currentGroup.position.page + currentGroup.size - 1;
            }
        }
        totalPage = comicInfo.chapterPages[chapter];
        hintText.UpdateText("CH" + chapter + "  " + page + "/" + totalPage);
    }
    /*
        檢查目前 scroll 狀態是否需要更改 pageGroup
     */
    public void CheckScroll() {
        int scaleOffsetY = (int) (sszpView.GetOffsetY() / sszpView.GetZoomFactor());
        /*
            scaleSSZPViewHeight 表示現在頁面顯示的 pixel (unscale 狀態)
            因為 scroll info 都是記錄 unscale，要比較現在是否在最後一頁的時候
            使用 viewHeightInfo.Back() - sszpView.GetHeight() 會錯，因為 viewHeight 是 unscale
            而 sszpView.GetHeight 的單位是跟 zoomFactor 同步的
            因為放大縮小的時候 sszpView 會保持同樣 layout 高度
            而顯示內容的 pixel height 卻會跟著 zoom in 變動
         */
        int scaleSSZPViewHeight = (int) (sszpView.GetHeight() / sszpView.GetZoomFactor());
        boolean scrollUpdate = false;   //  是否需要更新卷軸
        if (NeedUpdateLastGroup(scaleOffsetY)) {
            Log.d(PC_TAG, "CheckScroll NeedUpdateLastGroup");
            /*  need 在捲動到 head
             或是 lastGroup 第一頁
             或是 lastGroup 不存在/尚未插入捲動到 currentGroup 第一頁時
             觸發
              */
            if (lastGroup.inserted) {
                /*
                  last group pages 已經插入 attachView
                  表示當前 scroll 在 head 或是 lastGroup 第一頁
                  若現在 scroll 在 lastGroup 則因為
                  需要
                    next group = current group
                    current group = last group
                  所以 scrollGroup = GROUP_CURRENT
                  若 scroll 在 head 則分成兩種情況
                  若 lastGroup 不存在則保持原樣
                  若 lastGroup 存在則 scrollGroup = GROUP_CURRENT (跳回 current group 第一頁)
                  */
                RemoveGroup(GROUP_NEXT, nextGroup.size);
                nextGroup = currentGroup;
                currentGroup = lastGroup;
                lastGroup = new GroupInfo();
                lastGroup.SetLastGroupOf(currentGroup);
                if (scrollGroup == GROUP_LAST)
                    scrollGroup = GROUP_CURRENT;
                /*else {
                    if(lastGroup.Valid()) {
                        //  這個 if 是這段唯一做事的 code
                        //  但是進入此區條件為 scrollGroup == HEAD && lastGroup.Valid()
                        //  以兩個條件在下面 Valid 的邏輯中必定執行到 GenLastGroup 且更新 scrollGroup
                        scrollGroup = GROUP_CURRENT;
                        scrollPage = 1;
                        scrollOffset = 0;
                    }
                    else {
                        //  stay in GROUP_HEAD
                    }
                }*/
                scrollUpdate = true;
            }
            /*
              執行到這邊 lastGroup 存在的話要印出來且 scroll 設定成 GROUP_CURRENT 第一頁 top
              否則保持原樣
               */
            /*
                last group pages 尚未插入到 attachView
                或是載入了新的 lastGroup
                則直接插入
             */
            if (lastGroup.Valid()) {
                if (scrollGroup == GROUP_HEAD ||
                        (lastGroup.position.chapter == currentGroup.position.chapter)) {
                    GenLastGroup();

                    if (scrollGroup != GROUP_CURRENT) {
                        scrollGroup = GROUP_CURRENT;
                        scrollPage = 1;
                        scrollOffset = 0;
                    }
                    scrollUpdate = true;
                }
            }
        }
        if (NeedUpdateNextGroup(scaleOffsetY, scaleSSZPViewHeight)) {
            Log.d(PC_TAG, "CheckScroll NeedUpdateNextGroup");
            if (nextGroup.inserted) {
                /*
                    last = current
                    current = next
                 */
                RemoveGroup(GROUP_LAST, lastGroup.size);
                lastGroup = currentGroup;
                currentGroup = nextGroup;
                nextGroup = new GroupInfo();
                nextGroup.SetNextGroupOf(currentGroup);
                if (scrollGroup == GROUP_NEXT)
                    scrollGroup = GROUP_CURRENT;
                scrollUpdate = true;
            }
            if (nextGroup.Valid()) {
                if ((scaleOffsetY >= (viewHeightInfos[viewHeightInfos.length - 1] - scaleSSZPViewHeight)) ||
                        (nextGroup.position.chapter == currentGroup.position.chapter)) {
                    GenNextGroup();

                    if (scrollGroup != GROUP_CURRENT) {
                        scrollGroup = GROUP_CURRENT;
                        scrollPage = currentGroup.size;
                        scrollOffset = currentGroup.pages[currentGroup.size - 1].GetHeight() - scaleSSZPViewHeight;
                        if (scrollOffset < 0) scrollOffset = 0;
                    }
                    scrollUpdate = true;
                }
            }
        }
        if (scrollUpdate) {
            UpdateHeadString();
            CalculateViewHeightInfo();
            sszpView.CalculateAttachSize();
            SetOffsetY();
        }
    }
    boolean NeedUpdateLastGroup(int scaleOffsetY) {
        //  head
        if (scrollGroup == GROUP_HEAD) {
            //  只要滑到 head 就一定要更新上一章節
            return true;
        }
        //  滑到第一頁就更新
        return scaleOffsetY < viewHeightInfos[2];
        /*
        if(lastGroup.Valid() && lastGroup.inserted) {
            //  last group 存在且有插入的話，滑到 last group 第一頁就需要更新 last group
            if(scrollGroup == 0 && scrollPage == 1) return true;
            return false;
        }
        else {
            //  last group 不存在或是尚未插入，滑到 current group 第一頁就需要更新 last group
            if(scrollGroup == 1 && scrollPage == 1) return true;
            return false;
        }*/
    }
    boolean NeedUpdateNextGroup(int scaleOffsetY, int scaleSSPZViewHeight) {
        //  tail
        if (scrollGroup == GROUP_TAIL) {
            //  只要滑到 tail 就一定要更新
            return true;
        }
        //  最後一頁的判斷必須使用全高 - tail.Height - 視窗高
        return (scaleOffsetY >= viewHeightInfos[viewHeightInfos.length - 2]) ||
                (scaleOffsetY >= (viewHeightInfos[viewHeightInfos.length - 1] - scaleSSPZViewHeight));
    }
    /*
        使用當前的 scroll Group + Page + Offset 設定 SSZPView 的 offsetY
     */
    void SetOffsetY() {
        if (scrollGroup == GROUP_HEAD) {
            sszpView.SetOffsetY(scrollOffset);
            return;
        }
        if (scrollGroup == GROUP_TAIL) {
            sszpView.SetOffsetY(viewHeightInfos[viewHeightInfos.length - 1] + scrollOffset);
            sszpView.BoundOffset();
            return;
        }
        int baseIndex = 1;
        if (scrollGroup > GROUP_LAST) baseIndex += lastGroup.size;
        if (scrollGroup > GROUP_CURRENT) baseIndex += currentGroup.size;
        baseIndex += (scrollPage - 1);
        sszpView.SetOffsetY((int) ((viewHeightInfos[baseIndex] + scrollOffset) * sszpView.GetZoomFactor()));
        sszpView.invalidate();
    }

    class GroupInfo {
        ComicPosition position; //  此 group 的第一頁，必為 PAGE_GROUP_SIZE 的倍數 + 1
        int size;               //  group 有幾頁
        boolean inserted;       //  group 是否已經插入 attachView
        SImagePage[] pages;     //  group 每一頁的 view

        GroupInfo() {
            position = null;
            size = 0;
            inserted = false;
            pages = null;
        }
        boolean Valid() {
            return position != null;
        }
        void SetLastGroupOf(GroupInfo info) {
            position = GetLastGroup(info.position);
            UpdateSize();
            inserted = false;
            pages = null;
        }
        void SetNextGroupOf(GroupInfo info) {
            position = GetNextGroup(info.position);
            UpdateSize();
            inserted = false;
            pages = null;
        }
        void UpdateSize() {
            size = 0;
            if(position!= null) size = GetGroupSize(position);
        }
        void Clear() {
            /*
                僅僅清空 member，必須在此之前將 pages 從 attachView 移除
             */
            position = null;
            size = 0;
            inserted = false;
            pages = null;
        }
        void Debug(String name) {
            if(!Valid()) {
                Log.d(PC_TAG, name + "Group is invalid");
                return;
            }
            Log.d(PC_TAG,
                    name + "Group, pos = " + position.chapter + "-" + position.page +
                            ", size = " + size);
        }
    }
    void DebugScrollInfo() {
        Log.d(PC_TAG,"Scroll = " + scrollGroup + "/" + scrollPage + "/" + scrollOffset);
    }
}
