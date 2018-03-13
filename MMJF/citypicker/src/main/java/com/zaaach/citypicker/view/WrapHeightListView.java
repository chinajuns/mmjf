package com.zaaach.citypicker.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * author zaaach on 2015/1/26.
 */
public class WrapHeightListView extends ListView {
    public WrapHeightListView(Context context) {
        this(context, null);
    }

    public WrapHeightListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WrapHeightListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightSpec);
    }
}
