package mio.sis.com.comicmana;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
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

import mio.sis.com.comicmana.sdata.ManaConfig;
import mio.sis.com.comicmana.sfile.SFile;
import mio.sis.com.comicmana.sui.comp.PathSelector;
import mio.sis.com.comicmana.sui.comp.PathSelectorListener;
import mio.sis.com.comicmana.sui.inst.MainView;
import mio.sis.com.comicmana.sui.intf.ViewStack;

public class MainActivity extends AppCompatActivity {
    static public ManaConfig manaConfig = new ManaConfig();
    ViewStack viewStack;

    File sd_card;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_layout);

        if(SFile.ChechPermission(this)) {
            manaConfig.LoadConfig();
        }
        else {
            SFile.RequestPermission(this);
        }

        LinearLayout root = (LinearLayout)findViewById(R.id.root_layout);

        viewStack = new ViewStack(this, root);
        //viewStack.Push(new ChapterSelectView(viewStack, ComicInfo.GetTestComicInfo()));
        viewStack.Push(new MainView(viewStack));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(viewStack.GetViewCnt()<=1) {
                return super.onKeyDown(keyCode, event);
            }
            viewStack.Pop();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode != SFile.REQUEST_CODE) return;
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            manaConfig.LoadConfig();
        }
        else {
            Toast.makeText(this, "What, deny????", Toast.LENGTH_SHORT).show();
        }
    }
}
