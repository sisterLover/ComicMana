package mio.sis.com.comicmana.mine;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import mio.sis.com.comicmana.R;
import mio.sis.com.comicmana.scache.ComicInfoCache;
import mio.sis.com.comicmana.sdata.ComicInfo;
import mio.sis.com.comicmana.sdata.ComicSrc;
import mio.sis.com.comicmana.sui.intf.AbstractComicGrid;

/**
 * Created by Nako on 2018/1/16.
 */

public class Grid implements AbstractComicGrid{
    protected static final float FLIP_DISTANCE=50;
    protected static final int Row=3;
    View mainLayout;
    GridView gridView;
    GestureDetector gestureDetector;
    Context context;
    ComicSrc src;
    int pages;
    ActionCallback actionCallback;

    public Grid(ComicSrc src)
    {
        this.src=src;
        pages=1;
    }


    class Mlistener extends GestureDetector.SimpleOnGestureListener
    {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(e2.getX()-e1.getX()>FLIP_DISTANCE)
            {
                //向右
                pages++;
                SetGrid();
            }
            else if(e1.getX()-e2.getX()>FLIP_DISTANCE)
            {
                //向左
                if(pages>1) {
                    pages--;
                    SetGrid();
                }
            }
            return false;
        }
    }




    public void SetGrid() {
        ComicInfoCache.EnumComic(this.src, this.pages, 12, new GridCallBack(this));
    }

    public void PostUpdateGridView(final ComicInfo[] comicInfos)
    {
        mainLayout.post(new Runnable() {
            @Override
            public void run() {
                UpdateGridView(comicInfos);
            }
        });
    }

    private void UpdateGridView(final ComicInfo[] comicInfos)
    {

        boolean Flag=true;
        if(comicInfos==null||comicInfos.length==0)
        {
            if(pages!=1) {
                Toast.makeText(context, "無法繼續翻頁", Toast.LENGTH_SHORT).show();
            }
            else
            {

            }
            if(pages>1)
                pages--;
            Flag=false;
        }


        if(Flag) {
            TextView textView=(TextView)mainLayout.findViewById(R.id.grid_page_text);
            textView.setText("第"+pages+"頁");
            gridView=(GridView)mainLayout.findViewById(R.id.main_grid);
            gridView.setNumColumns(Row);
            GridAdapter adapter = new GridAdapter(comicInfos, context);
            adapter.SetFlag(Flag);
            gridView.setAdapter(adapter);
            gridView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return gestureDetector.onTouchEvent(motionEvent);
                }
            });
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    actionCallback.OnComicClick(comicInfos[i]);
                }
            });
        }
        else
        {
            TextView textView=(TextView)mainLayout.findViewById(R.id.grid_page_text);
            textView.setText("No Comics");
            gridView=(GridView)mainLayout.findViewById(R.id.main_grid);
            gridView.setNumColumns(1);
            ComicInfo[] temp=new ComicInfo[1];
            temp[0]=new ComicInfo();
            temp[0].name = null;
            temp[0].thumbnail= BitmapFactory.decodeResource(context.getResources(),R.drawable.sorry);
            GridAdapter adapter = new GridAdapter(temp, context);
            adapter.SetFlag(Flag);
            gridView.setAdapter(adapter);
        }
    }
    @Override
    public View InflateView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        mainLayout = inflater.inflate(R.layout.grid_view, null);
        gestureDetector = new GestureDetector(context, new Mlistener());
        this.context = context;
        ComicInfoCache.EnumComic(this.src, (this.pages - 1) * 12, 12, new GridCallBack(this));
        return mainLayout;
    }

    @Override
    public View GetView() {
        return mainLayout;
    }

    @Override
    public void FreeView() {

    }

    @Override
    public void SetActionCallback(ActionCallback actionCallback) {
        this.actionCallback=actionCallback;
    }

    @Override
    public void SetSearch(String string) {
        if(src.srcType != ComicSrc.SrcType.ST_LOCAL_FILE) return;
        Log.d("LS_TAG", "Searching " + string);
        ComicInfoCache.EnumComic(src, 0, 12, string, new GridCallBack(this));
    }
}
