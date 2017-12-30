package mio.sis.com.comicmana;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import mio.sis.com.comicmana.sdata.ComicInfo;
import mio.sis.com.comicmana.sdata.ComicPosition;
import mio.sis.com.comicmana.sfile.SFile;
import mio.sis.com.comicmana.sui.PathSelector;
import mio.sis.com.comicmana.sui.PathSelectorListener;
import mio.sis.com.comicmana.sui.sszpview.SSZPView;

public class ExploreActivity extends AppCompatActivity {
    PathSelector selector;
    PathSelectorListener selectorListener;
    TextView textView;

    File sd_card;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        textView = ((TextView)findViewById(R.id.TV));
        ArrayList<File> arrayList = SFile.GetSDCardDirs();
        String s = "";
        for(File file : arrayList) {
            s+=file.toString();
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

        /*sScrollView = new SScrollView(this);
        sScrollView.setLayoutParams(
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        sScrollView.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(sScrollView);

        LayoutInflater inflater = LayoutInflater.from(this);
        sScrollView.GetAttachView().addView(inflater.inflate(R.layout.scroll_test, null));*/
        SSZPView sszpView = new SSZPView(this);
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
        sszpView.PostComicInfo(ComicInfo.GetTestComicInfo(), position);
        /*LayoutInflater inflater = LayoutInflater.from(this);
        sszpView.SetChild(inflater.inflate(R.layout.scroll_test, null));*/
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
