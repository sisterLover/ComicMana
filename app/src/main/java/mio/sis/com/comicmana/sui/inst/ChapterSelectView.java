package mio.sis.com.comicmana.sui.inst;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import mio.sis.com.comicmana.R;
import mio.sis.com.comicmana.sdata.ComicInfo;
import mio.sis.com.comicmana.sdata.ComicPosition;
import mio.sis.com.comicmana.sdata.ComicSrc;
import mio.sis.com.comicmana.sui.intf.StackableView;

/**
 * Created by Administrator on 2018/1/14.
 */

public class ChapterSelectView implements StackableView {
    /*
            -----------------
            |   thumbnail   |       有縮圖的話顯示縮圖
            -----------------
            |     title     |       漫畫名稱
            -----------------
            |   begin_btn   |       開始按鈕(若已經讀過顯示 "繼續閱讀" 否則顯示 "開始閱讀"
            -----------------
            | chapter table |       顯示各章節以供選擇
            -----------------
            |  comic option |       若是本地端漫畫，提供"從媒體櫃中隱藏、顯示"選項
            -----------------       網路端漫畫，則提供"下載"選項
     */
    LinearLayout root, chapterParent, optionParent;
    ComicInfo comicInfo;
    public ChapterSelectView(ComicInfo comicInfo) {
        this.comicInfo = comicInfo;
    }

    @Override
    public View InflateView(final Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        root = (LinearLayout) inflater.inflate(R.layout.chapter_select_layout, null);

        if(comicInfo.thumbnail != null) {
            ImageView thumbnailView = root.findViewById(R.id.chapter_select_thumbnail_view);
            thumbnailView.setImageBitmap(comicInfo.thumbnail);
        }
        TextView titleView = root.findViewById(R.id.chapter_select_title_view);
        titleView.setText(comicInfo.name);

        Button beginButton = root.findViewById(R.id.chapter_select_begin_button);
        if(comicInfo.lastPosition.chapter == ComicPosition.CHAPTER_NOT_READ_YET) {
            beginButton.setText("開始閱讀");
        }
        else {
            beginButton.setText("繼續閱讀");
        }
        //  chapter
        root.post(new Runnable() {
            @Override
            public void run() {
                GenerateChapter(context);
            }
        });

        //  comic option
        optionParent = root.findViewById(R.id.chapter_select_option_parent);
        if(comicInfo.src.srcType == ComicSrc.SrcType.ST_LOCAL_FILE) {
            Button encryptButton = new Button(context), decryptButton = new Button(context);
            encryptButton.setText("媒體櫃隱藏");
            decryptButton.setText("媒體櫃顯示");
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            encryptButton.setLayoutParams(params);
            decryptButton.setLayoutParams(params);
            optionParent.addView(encryptButton);
            optionParent.addView(decryptButton);
        }
        else {
            Button downloadButton = new Button(context);
            downloadButton.setText("下載");
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            downloadButton.setLayoutParams(params);
            optionParent.addView(downloadButton);
        }
        return root;
    }

    private void GenerateChapter(Context context) {
        chapterParent = root.findViewById(R.id.chapter_select_chapter_grid_parent);
    }
    /*
        產生 chapterIndex 的 chapter button
     */
    private LinearLayout GenerateSingleLine(Context context, int[] chapterIndex) {
        LinearLayout lineParent = new LinearLayout(context);
        LinearLayout.LayoutParams parentParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT),
                buttonParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        lineParent.setOrientation(LinearLayout.HORIZONTAL);
        lineParent.setLayoutParams(parentParams);

        for (int i = 0; i < chapterIndex.length; ++i) {
            Button chapterButton = new Button(context);
            chapterButton.setText(String.valueOf(chapterIndex[i]));
            chapterButton.setLayoutParams(buttonParams);
            lineParent.addView(chapterButton);
        }
        return lineParent;
    }

    private void OnBeginClick() {

    }

    private void OnChapterClick(int chapter) {

    }

    private void OnEncryptClick() {

    }

    private void OnDecryptClick() {

    }

    private void OnDownloadClick() {

    }

    @Override
    public View GetView() {
        return root;
    }

    @Override
    public View FreeView() {
        return null;
    }

    private class ChapterButtonListener implements View.OnClickListener {
        int chapter;

        ChapterButtonListener(int chapter) {
            this.chapter = chapter;
        }

        @Override
        public void onClick(View view) {
            OnChapterClick(chapter);
        }
    }
}
