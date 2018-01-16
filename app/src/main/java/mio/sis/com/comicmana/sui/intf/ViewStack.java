package mio.sis.com.comicmana.sui.intf;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/1/14.
 */

public class ViewStack {
    private ArrayList<StackableView> stack;
    private Context context;
    private LinearLayout parentLayout;

    public ViewStack(Context context, LinearLayout parentLayout) {
        this.context = context;
        this.parentLayout = parentLayout;
        stack = new ArrayList<>();
    }
    public void Push(StackableView view) {
        if (stack.size() > 0) {
            FreeLastView();
        }
        stack.add(view);
        InflateLastView();
    }
    public void Pop(StackableView view) {
        if (stack.size() == 0) return;
        FreeLastView();
        stack.remove(stack.size() - 1);
        if (stack.size() > 0) InflateLastView();
    }
    private void FreeLastView() {
        StackableView lastView = stack.get(stack.size() - 1);
        parentLayout.removeView(lastView.GetView());
        lastView.FreeView();
    }
    private void InflateLastView() {
        StackableView lastView = stack.get(stack.size() - 1);
        View view = lastView.InflateView(context);
        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        parentLayout.addView(view);
    }
}
