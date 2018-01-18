package mio.sis.com.comicmana.mine;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import mio.sis.com.comicmana.R;
import mio.sis.com.comicmana.sdata.ComicInfo;

/**
 * Created by Nako on 2018/1/16.
 */

public class GridAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private ComicInfo[] comicInfo;
    final private int col=3;
    final private int row=4;
    final int viewfactor=7;
    final int imagefactor=6;
    private int width;
    private int height;
    static class ViewHolder
    {
        public ImageView image;
        public TextView title;
    }
    public GridAdapter( ComicInfo[] comicInfo, Context context)
    {
        super();
        this.comicInfo=comicInfo;
        layoutInflater=LayoutInflater.from(context);

        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        height=dm.heightPixels;
    }

    @Override
    public int getCount( )
    {
        if (comicInfo.length>0)
        {
            return comicInfo.length;
        }
        else
        {
            return 0;
        }
    }
    @Override
    public Object getItem( int position )
    {
        return comicInfo[position];
    }

    @Override
    public long getItemId( int position )
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent )
    {

        ViewHolder viewHolder;

        if (convertView == null)
        {
            convertView = layoutInflater.inflate(R.layout.grid_item,null);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.grid_text);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.grid_image);

            int w=width/col;
            int h=height/row;
            //AbsListView.LayoutParams lp=new AbsListView.LayoutParams(w,(h*viewfactor)/10);
            AbsListView.LayoutParams lp=new AbsListView.LayoutParams(parent.getWidth()/3, parent.getHeight()/4);
            convertView.setLayoutParams(lp);

            ViewGroup.LayoutParams ps = viewHolder.image.getLayoutParams();
            /*ps.width = w*imagefactor/10;
            ps.height = h*imagefactor/10;
            */
            /*ps.width = lp.width;
            ps.height = lp.height - 15;
            viewHolder.image.setLayoutParams(ps);

            ViewGroup.LayoutParams titleParams = viewHolder.title.getLayoutParams();
            titleParams.width = ps.width;
            titleParams.height = 15;
            viewHolder.title.setLayoutParams(titleParams);
            */
            convertView.setTag(viewHolder);
        } else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.title.setText(comicInfo[position].name);
        //viewHolder.pages.setText("頁數: "+comicInfo[position].chapterPages);
        // viewHolder.image.setImageBitmap(comicInfo[position].thumbnail);
        return convertView;
    }

}