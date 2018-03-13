package com.yyydjk.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by dongjunkun on 2015/6/17.
 */
public class DropDownMenu extends LinearLayout {

  //顶部菜单布局
  private LinearLayout tabMenuView;
  //底部容器，包含popupMenuViews，maskView
  private FrameLayout containerView;
  //弹出菜单父布局
  private FrameLayout popupMenuViews;
  //遮罩半透明View，点击可关闭DropDownMenu
  private View maskView;
  //tabMenuView里面选中的tab位置，-1表示未选中
  private int current_tab_position = -1;

  //分割线颜色
  private int dividerColor = 0xffefefef;
  //tab选中颜色
  private int textSelectedColor = 0xff890c85;
  //tab未选中颜色
  private int textUnselectedColor = 0xff111111;
  //遮罩颜色
  private int maskColor = 0x88888888;
  //tab字体大小
  private int menuTextSize = 14;

  //tab选中图标
  private int menuSelectedIcon;
  //tab未选中图标
  private int menuUnselectedIcon;

  private float menuHeighPercent = 0.5f;

  public DropDownMenu(Context context) {
    super(context, null);
  }

  public DropDownMenu(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public DropDownMenu(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    setOrientation(VERTICAL);

    //为DropDownMenu添加自定义属性
    int menuBackgroundColor = 0xffffffff;
    int underlineColor = 0xffe6e6e6;
    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DropDownMenu);
    underlineColor = a.getColor(R.styleable.DropDownMenu_ddunderlineColor, underlineColor);
    dividerColor = a.getColor(R.styleable.DropDownMenu_dddividerColor, dividerColor);
    textSelectedColor = a.getColor(R.styleable.DropDownMenu_ddtextSelectedColor, textSelectedColor);
    textUnselectedColor =
        a.getColor(R.styleable.DropDownMenu_ddtextUnselectedColor, textUnselectedColor);
    menuBackgroundColor =
        a.getColor(R.styleable.DropDownMenu_ddmenuBackgroundColor, menuBackgroundColor);
    maskColor = a.getColor(R.styleable.DropDownMenu_ddmaskColor, maskColor);
    menuTextSize = a.getDimensionPixelSize(R.styleable.DropDownMenu_ddmenuTextSize, menuTextSize);
    menuSelectedIcon =
        a.getResourceId(R.styleable.DropDownMenu_ddmenuSelectedIcon, menuSelectedIcon);
    menuUnselectedIcon =
        a.getResourceId(R.styleable.DropDownMenu_ddmenuUnselectedIcon, menuUnselectedIcon);
    menuHeighPercent =
        a.getFloat(R.styleable.DropDownMenu_ddmenuMenuHeightPercent, menuHeighPercent);
    a.recycle();

    //初始化tabMenuView并添加到tabMenuView
    tabMenuView = new LinearLayout(context);
    LayoutParams params =
        new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    tabMenuView.setOrientation(HORIZONTAL);
    tabMenuView.setBackgroundColor(menuBackgroundColor);
    tabMenuView.setLayoutParams(params);
    addView(tabMenuView, 0);

    //为tabMenuView添加下划线
    View underLine = new View(getContext());
    underLine.setLayoutParams(
        new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpTpPx(1f)));
    underLine.setBackgroundColor(underlineColor);
    addView(underLine, 1);

    //初始化containerView并将其添加到DropDownMenu
    containerView = new FrameLayout(context);
    containerView.setLayoutParams(
        new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT));
    addView(containerView, 2);
  }

  /**
   * 初始化DropDownMenu
   */
  public void setDropDownMenu(@NonNull List<String> tabTexts, @NonNull List<View> popupViews,
      @NonNull View contentView) {
    if (tabTexts.size() != popupViews.size()) {
      throw new IllegalArgumentException(
          "params not match, tabTexts.size() should be equal popupViews.size()");
    }

    for (int i = 0; i < tabTexts.size(); i++) {
      addTab(tabTexts, i);
    }
    containerView.addView(contentView, 0);

    maskView = new View(getContext());
    maskView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
        FrameLayout.LayoutParams.MATCH_PARENT));
    maskView.setBackgroundColor(maskColor);
    maskView.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        closeMenu();
      }
    });
    containerView.addView(maskView, 1);
    maskView.setVisibility(GONE);
    if (containerView.getChildAt(2) != null) {
      containerView.removeViewAt(2);
    }

    popupMenuViews = new FrameLayout(getContext());
    popupMenuViews.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        (int) (DeviceUtils.getScreenSize(getContext()).y * menuHeighPercent)));
    popupMenuViews.setVisibility(GONE);
    containerView.addView(popupMenuViews, 2);

    for (int i = 0; i < popupViews.size(); i++) {
      popupViews.get(i)
          .setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
              ViewGroup.LayoutParams.WRAP_CONTENT));
      popupMenuViews.addView(popupViews.get(i), i);
    }
  }

  private void addTab(@NonNull List<String> tabTexts, int i) {
    final View tab = inflate(getContext(), R.layout.tab_item, null);
    tab.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
    TextView textView = tab.findViewById(R.id.tvTab);
    textView.setSingleLine();
    textView.setEllipsize(TextUtils.TruncateAt.END);
    textView.setGravity(Gravity.CENTER);
    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, menuTextSize);
    textView.setTextColor(textUnselectedColor);
    textView.setCompoundDrawablesWithIntrinsicBounds(null, null,
        getResources().getDrawable(menuUnselectedIcon), null);
    textView.setText(tabTexts.get(i));
    textView.setPadding(dpTpPx(5), dpTpPx(12), dpTpPx(5), dpTpPx(12));
    //添加点击事件
    tab.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        switchMenu(tab);
      }
    });
    tabMenuView.addView(tab);
    //添加分割线
    if (i < tabTexts.size() - 1) {
      View view = new View(getContext());
      LayoutParams layoutParams = new LayoutParams(dpTpPx(0.5f), dpTpPx(20.0f));
      layoutParams.gravity = Gravity.CENTER_VERTICAL;
      view.setLayoutParams(layoutParams);
      view.setBackgroundColor(dividerColor);
      tabMenuView.addView(view);
    }
  }

  private TextView getTabTextView(View tab) {
    TextView textView = tab.findViewById(R.id.tvTab);
    return textView;
  }

  /**
   * 改变tab文字
   */
  public void setTabText(String text) {
    if (current_tab_position != -1) {
      getTabTextView(tabMenuView.getChildAt(current_tab_position)).setText(text);
    }
  }

  /**
   * 改变tab文字
   */
  public void setTabText(String text, int position) {
    getTabTextView(tabMenuView.getChildAt(position)).setText(text);
  }

  public void setTabClickable(boolean clickable) {
    for (int i = 0; i < tabMenuView.getChildCount(); i = i + 2) {
      tabMenuView.getChildAt(i).setClickable(clickable);
    }
  }

  /**
   * 关闭菜单
   */
  public void closeMenu() {
    if (current_tab_position != -1) {
      getTabTextView(tabMenuView.getChildAt(current_tab_position)).setTextColor(
          textUnselectedColor);
      getTabTextView(
          tabMenuView.getChildAt(current_tab_position)).setCompoundDrawablesWithIntrinsicBounds(
          null, null, getResources().getDrawable(menuUnselectedIcon), null);
      popupMenuViews.setVisibility(View.GONE);
      popupMenuViews.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.dd_menu_out));
      maskView.setVisibility(GONE);
      maskView.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.dd_mask_out));
      current_tab_position = -1;
    }
  }

  /**
   * DropDownMenu是否处于可见状态
   */
  public boolean isShowing() {
    return current_tab_position != -1;
  }

  /**
   * 切换菜单
   */
  private void switchMenu(View target) {
    System.out.println(current_tab_position);
    for (int i = 0; i < tabMenuView.getChildCount(); i = i + 2) {
      if (target == tabMenuView.getChildAt(i)) {
        if (current_tab_position == i) {
          closeMenu();
        } else {
          if (current_tab_position == -1) {
            popupMenuViews.setVisibility(View.VISIBLE);
            popupMenuViews.setAnimation(
                AnimationUtils.loadAnimation(getContext(), R.anim.dd_menu_in));
            maskView.setVisibility(VISIBLE);
            maskView.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.dd_mask_in));
            popupMenuViews.getChildAt(i / 2).setVisibility(View.VISIBLE);
          } else {
            popupMenuViews.getChildAt(i / 2).setVisibility(View.VISIBLE);
          }
          current_tab_position = i;
          getTabTextView(tabMenuView.getChildAt(i)).setTextColor(textSelectedColor);
          getTabTextView(tabMenuView.getChildAt(i)).setCompoundDrawablesWithIntrinsicBounds(null,
              null, getResources().getDrawable(menuSelectedIcon), null);
        }
      } else {
        getTabTextView(tabMenuView.getChildAt(i)).setTextColor(textUnselectedColor);
        getTabTextView(tabMenuView.getChildAt(i)).setCompoundDrawablesWithIntrinsicBounds(null,
            null, getResources().getDrawable(menuUnselectedIcon), null);
        popupMenuViews.getChildAt(i / 2).setVisibility(View.GONE);
      }
    }
  }

  public int dpTpPx(float value) {
    DisplayMetrics dm = getResources().getDisplayMetrics();
    return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, dm) + 0.5);
  }
}
