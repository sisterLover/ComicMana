package mio.sis.com.comicmana;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.provider.DocumentFile;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import mio.sis.com.comicmana.scache.ComicInfoCache;
import mio.sis.com.comicmana.sdata.HistoryRecord;
import mio.sis.com.comicmana.sdata.ManaConfig;
import mio.sis.com.comicmana.sfile.SFile;
import mio.sis.com.comicmana.sfile.saf.SSAF;
import mio.sis.com.comicmana.snet.inst.LocalComicSiteHelper;
import mio.sis.com.comicmana.sui.comp.PathSelector;
import mio.sis.com.comicmana.sui.comp.PathSelectorListener;
import mio.sis.com.comicmana.sui.inst.ConfigView;
import mio.sis.com.comicmana.sui.inst.MainView;
import mio.sis.com.comicmana.sui.intf.ViewStack;

public class MainActivity extends AppCompatActivity {
    static public MainActivity MAIN_ACTIVITY = null;

    static public ManaConfig manaConfig = new ManaConfig();
    static public HistoryRecord historyRecord = new HistoryRecord();
    static public SSAF ssaf = new SSAF();

    static public int SDCARD_REQUEST = 200;

    static private boolean requestFlag = false;
    static public void SetRequestFlag() {
        requestFlag = true;
    }
    static private void ResetRequestFlag() {
        requestFlag = false;
    }
    static public boolean TestRequestFlag() {
        return requestFlag;
    }

    ViewStack viewStack;

    File sd_card;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MAIN_ACTIVITY = this;

        setContentView(R.layout.activity_layout);

        if(SFile.ChechPermission(this)) {
            LoadEveryThing();
        }
        else {
            SFile.RequestPermission(this);
        }
        /*MainActivity.MAIN_ACTIVITY.startActivityForResult(
                new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE),
                MainActivity.SDCARD_REQUEST);*/

        LinearLayout root = (LinearLayout)findViewById(R.id.root_layout);

        viewStack = new ViewStack(this, root);
        //viewStack.Push(new ChapterSelectView(viewStack, ComicInfo.GetTestComicInfo()));
        viewStack.Push(new MainView(viewStack));
        viewStack.Push(new ConfigView(viewStack));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(viewStack.GetViewCnt()<=1) {
                return super.onKeyDown(keyCode, event);
            }
            if(viewStack.GetLastView().OnBackPress()) {
                viewStack.Pop();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode != SFile.REQUEST_CODE) return;
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            LoadEveryThing();
        }
        else {
            Toast.makeText(this, "拒絕權限將會導致SD功能無效", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == SDCARD_REQUEST) {
            Uri uri = data.getData();
            Toast.makeText(this, uri.toString(), Toast.LENGTH_SHORT).show();
            Log.d("LS_TAG", uri.toString());
            ssaf.PushPermission(uri);

            /*DocumentFile documentFile = DocumentFile.fromTreeUri(this, uri);
            DocumentFile[] list = documentFile.listFiles();
            if(list == null) {
                Log.d("LS_TAG","LIST FAIL");
            }
            else {
                Log.d("LS_TAG", "LIST " + String.valueOf(list.length) + " by " + documentFile.toString());
                for (DocumentFile file : list) {
                    Log.d("LS_TAG", file.toString());
                }
            }*/

            ResetRequestFlag();
        }
    }

    private void LoadEveryThing() {
        Log.d("LS_TAG", "Reading Everything");
        manaConfig.LoadConfig();
        historyRecord.LoadRecord();
        ssaf.LoadSSAF();
        //  load config 自動呼叫 LoadComicDir
        //  否則可能會導致 config thread 還沒讀入 config， LoadComicDir thread 就先存取空白的 containerDir 進行掃描
        //LocalComicSiteHelper.LoadComicDir();
    }
}
