package com.prateek.gem.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.prateek.gem.R;
import com.prateek.gem.logger.DebugLogger;
import com.prateek.gem.utils.Utils;

import java.util.ArrayList;

public class TagLayout extends RelativeLayout {

    private static ArrayList<String> mTags = new ArrayList<String>();
    LayoutInflater mInflater;
    private float parentWidth;


    public TagLayout(Context context) {
        super(context);
        init();
    }


    public TagLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    public TagLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        mInflater = LayoutInflater.from(getContext());

    }

    public void setList(ArrayList<String> tags) {
        mTags = tags;
    }


    public void show(int screenpadding, ScreenContainer screenContainer) {
        removeAllViews();
        // parentWidth = HealthRecordsScreen.getScreenWidth();
        parentWidth = screenContainer.getWidth();
        int widthCounter = 0;
        int heightCounter = 0;
        TextView latestView = null;

        int padding = 0;

        int screenPadding = Utils.dpToPixels(screenpadding);

        for (int i = 0; i < mTags.size(); i++) {
            TextView tv = (TextView) mInflater.inflate(R.layout.tag_text_view, this, false);

            int temp = (int) Utils.getTextWidth(tv, mTags.get(i));

            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            int tempvalue = widthCounter + temp;
            DebugLogger.message("tag layout show parent width" + parentWidth);
            if (tempvalue > (parentWidth - screenPadding)) {
                padding = 2;
                widthCounter = 0;
                heightCounter += Utils.getTextHeight(tv, mTags.get(0)) + 35;
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                params.addRule(RelativeLayout.BELOW, latestView.getId());

            } else {
                if (latestView != null) {
                    padding += 2;
                    params.addRule(RelativeLayout.RIGHT_OF, latestView.getId());
                }
            }
            params.leftMargin = widthCounter;
            params.topMargin = heightCounter;
            tv.setLayoutParams(params);
            tv.setText(mTags.get(i));
            addView(tv);
            latestView = tv;

            widthCounter = widthCounter + temp + Utils.dpToPixels(20);
        }
    }

}
