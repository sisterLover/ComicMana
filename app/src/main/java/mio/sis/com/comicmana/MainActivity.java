package mio.sis.com.comicmana;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import mio.sis.com.comicmana.sdata.ComicInfo;
import mio.sis.com.comicmana.sdata.ComicPosition;
import mio.sis.com.comicmana.sfile.SFile;
import mio.sis.com.comicmana.sui.comp.PathSelector;
import mio.sis.com.comicmana.sui.comp.PathSelectorListener;
import mio.sis.com.comicmana.sui.comp.sszpview.SSZPView;
import mio.sis.com.comicmana.sui.inst.ChapterSelectView;
import mio.sis.com.comicmana.sui.intf.ViewStack;

public class MainActivity extends AppCompatActivity {
    PathSelector selector;
    PathSelectorListener selectorListener;
    TextView textView;
    ViewStack viewStack;

    File sd_card;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        textView = ((TextView)findViewById(R.id.TV));
        ArrayList<File> arrayList = SFile.GetSDCardDirs();
        String s = "";
        for(File file : arrayList) {
            if(file.toString().contains("ext_sd")) {
                Log.d("SD_TAG", "kill " + file.toString());
            }
            s+=file.toString();
            Log.d("SD_TAG", s);
            s+="\n";
        }
        sd_card = arrayList.get(0);
        textView.setText(s);

        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.test_layout);

        selectorListener = new PathSelectorListener() {
            @Override
            public void OnPathSelect(File file) {
                textView.setText("Select "+file.getAbsolutePath());
            }

            @Override
            public void OnCancel() {
                textView.setText("Cancel");
            }
        };
        selector = new PathSelector(this, selectorListener);

        if(SFile.ChechPermission(this)) {
            selector.SetCurrentPath(sd_card.getAbsolutePath());
        }
        else {
            //SFile.RequestPermission(this);
        }
        linearLayout.addView(selector.GetView());



        linearLayout = (LinearLayout)findViewById(R.id.scroll_test_layout);

        viewStack = new ViewStack(this, linearLayout);
        viewStack.Push(new ChapterSelectView(viewStack, ComicInfo.GetTestComicInfo()));
        /*SSZPView sszpView = new SSZPView(this);
        sszpView.setLayoutParams(
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT)
        );
        sszpView.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(sszpView);

        ComicPosition position = new ComicPosition();
        position.chapter = 1;
        position.page = 1;
        sszpView.PostComicInfo(ComicInfo.GetTestComicInfo(), position);*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode != SFile.REQUEST_CODE) return;
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selector.SetCurrentPath(sd_card.getAbsolutePath());
        }
        else {
            Toast.makeText(this, "What, deny????", Toast.LENGTH_SHORT).show();
        }
    }
}
