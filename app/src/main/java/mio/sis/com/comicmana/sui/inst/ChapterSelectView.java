package mio.sis.com.comicmana.sui.inst;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import mio.sis.com.comicmana.MainActivity;
import mio.sis.com.comicmana.R;
import mio.sis.com.comicmana.image.ImageLib;
import mio.sis.com.comicmana.other.SChar;
import mio.sis.com.comicmana.scache.DefaultPageCache;
import mio.sis.com.comicmana.sdata.ComicInfo;
import mio.sis.com.comicmana.sdata.ComicPosition;
import mio.sis.com.comicmana.sdata.ComicSrc;
import mio.sis.com.comicmana.sfile.LocalStorage;
import mio.sis.com.comicmana.sfile.SFile;
import mio.sis.com.comicmana.sui.intf.StackableView;
import mio.sis.com.comicmana.sui.intf.ViewStack;

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
    private ViewStack viewStack;
    private LinearLayout root, chapterParent, optionParent;
    private ComicInfo comicInfo;
    private boolean encrypting, decrypting;     //  目前是否正在從媒體櫃中隱藏/顯示
    public ChapterSelectView(ViewStack viewStack, ComicInfo comicInfo) {
        this.viewStack = viewStack;
        this.comicInfo = comicInfo;
        encrypting = false;
        decrypting = false;
    }

    @Override
    public View InflateView(final Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        root = (LinearLayout) inflater.inflate(R.layout.chapter_select_layout, null);

        final ImageView thumbnailView = root.findViewById(R.id.chapter_select_thumbnail_view);
        if(comicInfo.thumbnail != null) {
            root.post(new Runnable() {
                @Override
                public void run() {
                    LoadThumbnail(thumbnailView, comicInfo.thumbnail);
                }
            });
        }
        else {
            root.post(new Runnable() {
                @Override
                public void run() {
                    LoadDefaultThumbnail(context, thumbnailView);
                }
            });
        }
        TextView titleView = root.findViewById(R.id.chapter_select_title_view);
        titleView.setText(comicInfo.name);
        titleView.setSelected(true);

        Button beginButton = root.findViewById(R.id.chapter_select_begin_button);
        if(comicInfo.lastPosition.chapter == ComicPosition.CHAPTER_NOT_READ_YET) {
            beginButton.setText("開始閱讀");
        }
        else {
            beginButton.setText("繼續閱讀");
        }
        beginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnBeginClick();
            }
        });
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
            Button encryptButton = CreateButton(inflater, "媒體櫃隱藏"),
                    decryptButton = CreateButton(inflater, "媒體櫃顯示");
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            encryptButton.setLayoutParams(params);
            decryptButton.setLayoutParams(params);
            encryptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OnEncryptClick();
                }
            });
            decryptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OnDecryptClick();
                }
            });
            optionParent.addView(encryptButton);
            optionParent.addView(decryptButton);
        }
        else {
            Button downloadButton = CreateButton(inflater, "下載");
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            downloadButton.setLayoutParams(params);
            downloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OnDownloadClick();
                }
            });
            optionParent.addView(downloadButton);
        }
        return root;
    }

    private void GenerateChapter(Context context) {
        chapterParent = root.findViewById(R.id.chapter_select_chapter_grid_parent);

        int fillIndex = 0;
        String[] chapterTitle = new String[5];
        int[] chapterIndex = new int[5];
        for (int i = comicInfo.chapterCnt; i > 0; --i) {
            chapterTitle[fillIndex] = comicInfo.chapterInfo[i].title;
            chapterIndex[fillIndex] = i;
            ++fillIndex;
            if (fillIndex == 5) {
                chapterParent.addView(GenerateSingleLine(context, chapterTitle, chapterIndex, 5));
                fillIndex = 0;
            }
        }
        if (fillIndex > 0) {
            chapterParent.addView(GenerateSingleLine(context, chapterTitle, chapterIndex, fillIndex));
        }
    }
    /*
        產生 chapterIndex 的 chapter button
     */
    private LinearLayout GenerateSingleLine(Context context, String[] chapterTitle, int[] chapterIndex, int length) {
        LayoutInflater inflater = LayoutInflater.from(context);
        LinearLayout lineParent = new LinearLayout(context);
        LinearLayout.LayoutParams parentParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT),
                buttonParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1),
                textParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        lineParent.setOrientation(LinearLayout.HORIZONTAL);
        lineParent.setLayoutParams(parentParams);

        for (int i = 0; i < length; ++i) {
            Button chapterButton = CreateButton(inflater, chapterTitle[i]);
            chapterButton.setLayoutParams(buttonParams);
            chapterButton.setOnClickListener(new ChapterButtonListener(chapterIndex[i]));
            lineParent.addView(chapterButton);
        }
        for (int i = 0; i < 5 - length; ++i) {
            TextView emptyText = new TextView(context);
            emptyText.setText("");
            emptyText.setLayoutParams(textParams);
            lineParent.addView(emptyText);
        }
        return lineParent;
    }
    /*
        must call after root have width and height
     */
    private int GetThumbnailHeight() {
        return Math.min(root.getWidth(), root.getHeight()) / 3;
    }

    private void LoadDefaultThumbnail(Context context, ImageView imageView) {
        int width = root.getWidth(), height = GetThumbnailHeight();
        imageView.setLayoutParams(new LinearLayout.LayoutParams(width, height));
        imageView.setImageBitmap(
                DefaultPageCache.GetEmptyThumbnail(context, width, height));
    }
    private void LoadThumbnail(ImageView imageView, Bitmap bitmap) {
        int width = root.getWidth(), height = GetThumbnailHeight();
        imageView.setLayoutParams(new LinearLayout.LayoutParams(width, height));
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setImageBitmap(bitmap);
    }

    private void OnBeginClick() {
        if(comicInfo.lastPosition.chapter == ComicPosition.CHAPTER_NOT_READ_YET) {
            comicInfo.lastPosition.chapter = 1;
            comicInfo.lastPosition.page = 1;
        }
        PushSSZPView();
    }

    private void OnChapterClick(int chapter) {
        comicInfo.lastPosition.chapter = chapter;
        comicInfo.lastPosition.page = 1;
        PushSSZPView();
    }

    private void PushSSZPView() {
        viewStack.Push(new ComicView(viewStack, comicInfo));
    }

    private void OnEncryptClick() {
        if(comicInfo.src.srcType != ComicSrc.SrcType.ST_LOCAL_FILE) return;
        if(encrypting) return;
        encrypting = true;
        Log.d("LS_TAG", "Starting Encrypt");
        new Thread() {
            @Override
            public void run() {
                try {
                    SFile.EnumFile(new File(comicInfo.src.path), 2, new EncryptCallback());
                } catch (Exception e) {

                }
                encrypting = false;
            }
        }.start();
    }
    private class EncryptCallback implements SFile.EnumFileCallback {
        @Override
        public void OnFile(File file) {
            if(!file.exists()) return;
            String ext = SFile.GetExtension(file);
            if (ext == null) return;
            if (SChar.StringInListIgnoreCase(ext, LocalStorage.SUPPORT_EXTENSION)) {
                if (ext.compareToIgnoreCase(LocalStorage.APP_ALTER_EXTENSION) != 0) {
                    String title = SFile.GetNameWithoutExtension(file);
                    File newFile = new File(file.getParentFile(), title + "." + LocalStorage.APP_ALTER_EXTENSION);
                    if(!file.canWrite()) {
                        Log.d("LS_TAG", "cannot write");
                    }
                    try {
                        if(!newFile.createNewFile()) {
                            Log.d("LS_TAG", "Create Return Fail");
                        }
                    }
                    catch (Exception e) {
                        Log.d("LS_TAG", "CreateFile Exception");
                        Log.d("LS_TAG", e.toString());
                    }
                    //  失敗我也不能怎樣
                    file.renameTo(newFile);
                }
            }
        }
    }

    private void OnDecryptClick() {
        if(comicInfo.src.srcType != ComicSrc.SrcType.ST_LOCAL_FILE) return;
        if(decrypting) return;
        decrypting = true;
        new Thread() {
            @Override
            public void run() {
                try {
                    SFile.EnumFile(new File(comicInfo.src.path), 2, new DecryptCallback());
                } catch (Exception e) {

                }
                decrypting = false;
            }
        }.start();
    }
    private class DecryptCallback implements SFile.EnumFileCallback {
        @Override
        public void OnFile(File file) {
            if(!file.exists()) return;
            String ext = SFile.GetExtension(file);
            if (ext == null) return;
            if (ext.compareToIgnoreCase(LocalStorage.APP_ALTER_EXTENSION) == 0) {
                String title = SFile.GetNameWithoutExtension(file);
                Bitmap bitmap = ImageLib.LoadFile(file);
                if (bitmap == null) return;
                File orgFile = new File(file.getParentFile(), title + ".png");
                if (ImageLib.SaveFile(orgFile, bitmap)) {
                    file.delete();
                }
            }
        }
    }

    private void OnDownloadClick() {
        if(comicInfo.src.srcType == ComicSrc.SrcType.ST_LOCAL_FILE) return;

    }

    @Override
    public View GetView() {
        return root;
    }

    @Override
    public void FreeView() {
        chapterParent.removeAllViews();
        chapterParent = null;
        optionParent.removeAllViews();
        optionParent = null;
        root = null;
    }

    @Override
    public boolean OnBackPress() {
        return true;
    }

    private Button CreateButton(LayoutInflater inflater, String text) {
        Button result = (Button)inflater.inflate(R.layout.chapter_select_button_item, null);
        result.setText(text);
        return result;
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
