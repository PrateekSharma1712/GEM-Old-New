package com.prateek.gem.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by prateek on 2/5/15.
 */
public class BottomItemDecorator extends RecyclerView.ItemDecoration {


    private int hSpace, vSpace;

    public BottomItemDecorator(int hSpace, int vSpace) {
        this.hSpace = hSpace;
        this.vSpace = vSpace;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = outRect.right = hSpace;
        outRect.bottom = vSpace;

            /* don't need double spacing everywhere except at the top */
        if (parent.getChildPosition(view) == 0)
            outRect.top = vSpace * 2;
    }

}
