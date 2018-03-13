package com.ysjr.mmjf.widget.loading;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.blankj.utilcode.util.SizeUtils;
import com.ysjr.mmjf.R;

/**
 * Created by Administrator on 2017-11-20.
 */

public class BackgroundLayout extends LinearLayout {
    private int mBackgroundColor;//加载框背景颜色
    private float mCornerRadius;//背景框圆角
    public BackgroundLayout(Context context) {
        this(context,null);
    }

    public BackgroundLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public BackgroundLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    @SuppressWarnings("deprecation")
    private void init() {
        int color = getContext().getResources().getColor(R.color.kprogresshud_default_color);
        initBackground(color, mCornerRadius);
    }

    private void initBackground(int color, float cornerRadius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(color);
        drawable.setCornerRadius(cornerRadius);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(drawable);
        } else {
            setBackgroundDrawable(drawable);
        }
    }

    public void setCornerRadius(float cornerRadius) {
        mCornerRadius = SizeUtils.dp2px(cornerRadius);
        initBackground(mBackgroundColor,cornerRadius);
    }

    public void setBaseColor(int baseColor) {
        mBackgroundColor = baseColor;
        initBackground(baseColor,mCornerRadius);
    }
}
