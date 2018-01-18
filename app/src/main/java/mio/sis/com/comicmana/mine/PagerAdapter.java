package mio.sis.com.comicmana.mine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import mio.sis.com.comicmana.R;
import mio.sis.com.comicmana.sdata.ComicInfo;

/**
 * Created by Nako on 2018/1/18.
 */

public class PagerAdapter extends android.support.v4.view.PagerAdapter {
    private Context context;
    private ComicInfo[] comicInfos;
    private LayoutInflater layoutInflater;
    public PagerAdapter(ComicInfo[] comicInfos,Context context) {
        this.context=context;
        this.comicInfos=comicInfos;
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
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        View itemView = layoutInflater.inflate(R.layout.viewpager_item, container, false);
        ImageView imageView = (ImageView)itemView.findViewById(R.id.pageImage);
        //imageView.setImageBitmap(comicInfos[position].thumbnail);

        (container).addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }


}