package mio.sis.com.comicmana.mine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import mio.sis.com.comicmana.R;
import mio.sis.com.comicmana.scache.ComicInfoCache;
import mio.sis.com.comicmana.sdata.ComicInfo;
import mio.sis.com.comicmana.sdata.ComicSrc;
import mio.sis.com.comicmana.sui.intf.AbstractWelcomeView;

/**
 * Created by Nako on 2018/1/18.
 */

public class Pager implements AbstractWelcomeView {


    View mainLayout;
    ViewPager viewPager;
    PagerAdapter pagerAdapter;
    ImageView[] btns;
    Context context;
    ComicSrc comicSrc;
    ComicInfo[] comicInfos;
    int pages;
    static public final int MAX_PAGES=5;


    ActionCallback actionCallback;
    //這邊要加一個actioncallback;

    public Pager(ComicInfo[] comicInfos)
    {
        this.comicInfos=comicInfos;
        //this.comicSrc=comicSrc;
        pages=0;
    }


    @Override
    public View InflateView(Context context) {
        this.context=context;
        LayoutInflater inflater=LayoutInflater.from(context);
        mainLayout=inflater.inflate(R.layout.viewpager,null);
        btns=new ImageView[MAX_PAGES];
        for(int i=1;i<=MAX_PAGES;i++)
        {
            int id=context.getResources().getIdentifier("viewpage_btn"+i, "id", context.getPackageName());
            btns[i-1]=(ImageView)mainLayout.findViewById(id);
        }
        viewPager=(ViewPager)mainLayout.findViewById(R.id.main_pager);
        //是call這個cache嗎?
        //ComicInfoCache.EnumComic(this.comicSrc,0,5,new PagerCallBack(this));
        //Test();
        PostUpdatePager(this.comicInfos);
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



    public void PostUpdatePager(final  ComicInfo[] comicInfos)
    {
        mainLayout.post(new Runnable() {
            @Override
            public void run() {
                UpdatePager(comicInfos);
            }
        });
    }
    public void UpdatePager(final ComicInfo[] comicInfos)
    {
        if(comicInfos.length>4||comicInfos.length<0)
            pages=0;
        else
            pages=comicInfos.length;

        for(int i=1;i<=MAX_PAGES;i++)
        {
            final int p=i;
            if(i>pages)
                btns[i-1].setVisibility(View.GONE);
            else
                btns[i-1].setVisibility(View.VISIBLE);

            btns[i-1].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewPager.setCurrentItem(p - 1, true);
                }
            });
        }

        PagerAdapter pagerAdapter=new PagerAdapter(comicInfos,context);
        viewPager.setAdapter(pagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //Toast.makeText(context,""+position,Toast.LENGTH_SHORT).show();
                BtnStateChange(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setCurrentItem(0);
        viewPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionCallback.OnComicClick(comicInfos[viewPager.getCurrentItem()]);
            }
        });
    }

    public void BtnStateChange(final int selected)
    {
        mainLayout.post(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<pages;i++)
                {
                    if(i==selected)
                        btns[i].setImageDrawable(context.getDrawable(R.drawable.selected));
                    else
                        btns[i].setImageDrawable(context.getDrawable(R.drawable.unseleted));

                }
            }
        });
    }
}