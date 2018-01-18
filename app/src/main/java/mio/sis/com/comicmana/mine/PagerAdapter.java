package mio.sis.com.comicmana.mine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import mio.sis.com.comicmana.R;
import mio.sis.com.comicmana.sdata.ComicInfo;
import mio.sis.com.comicmana.sui.intf.AbstractWelcomeView;

/**
 * Created by Nako on 2018/1/18.
 */

public class PagerAdapter extends android.support.v4.view.PagerAdapter {
    private Context context;
    private ComicInfo[] comicInfos;
    private LayoutInflater layoutInflater;
    private AbstractWelcomeView.ActionCallback actionCallback;
    private boolean flag;

    public PagerAdapter(ComicInfo[] comicInfos,Context context) {
        this.context=context;
        this.comicInfos=comicInfos;
        flag=false;
    }

    @Override
    public int getCount() {
        if (comicInfos.length>0)
        {
            return comicInfos.length;
        }
        else
        {
            return 0;
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        layoutInflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        View itemView = layoutInflater.inflate(R.layout.viewpager_item, container, false);
        ImageView imageView = (ImageView)itemView.findViewById(R.id.pageImage);
        if(comicInfos[position].thumbnail != null) {
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setImageBitmap(comicInfos[position].thumbnail);
        }
        if(actionCallback!=null&&flag) {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    actionCallback.OnComicClick(comicInfos[position]);
                }
            });
        }

        (container).addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public void SetAction(AbstractWelcomeView.ActionCallback actionCallback,boolean flag)
    {
        this.actionCallback=actionCallback;
        this.flag=flag;
    }
    public void SetFlag(boolean flag)
    {
        this.flag=flag;
    }

}