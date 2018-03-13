package com.ysjr.mmjf.widget.loading;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.blankj.utilcode.util.SizeUtils;
import com.ysjr.mmjf.R;

/**
 * Created by Administrator on 2017-11-20.
 */

public class KProgressHUD {
    public enum Style {
        SPIN_INDETERMINATE,PIE_DETERMINATE,ANNLUAR_DETERMINATE,BAR_DETERMINATE
    }
    // To avoid redundant APIs, make the HUD as a wrapper class around a Dialog
    private ProgressDialog mProgressDialog;
    private float mDimAccount;
    private int mWindowColor;
    private float mCornerRadius;
    private Context mContext;
    private int mAnimateSpeed;
    private int mMaxProgress;
    private boolean mIsAutoDismiss;
    private int mGraceTimeMs;
    private Handler mGraceTimer;
    private boolean mFinished;

    public KProgressHUD(Context context) {
        mContext = context;
        mProgressDialog = new ProgressDialog(context);
        mDimAccount = 0;
        mWindowColor = context.getResources().getColor(R.color.kprogresshud_default_color);
        mAnimateSpeed = 1;
        mCornerRadius = 10;
        mIsAutoDismiss = true;
        mGraceTimeMs = 0;
        mFinished = false;
        setStyle(Style.SPIN_INDETERMINATE);
    }

    public static KProgressHUD create(Context context) {
        return new KProgressHUD(context);
    }

    public static KProgressHUD create(Context context, Style style) {
        return new KProgressHUD(context).setStyle(style);
    }

    public KProgressHUD setStyle(Style style) {
        View view = null;
        switch (style) {
            case SPIN_INDETERMINATE:
                view = new SpinView(mContext);
                break;
            case BAR_DETERMINATE:
                break;
            case PIE_DETERMINATE:
                break;
            case ANNLUAR_DETERMINATE:
                break;
        }
        mProgressDialog.setView(view);
        return this;
    }

    public KProgressHUD setDimAccount(float dimAccount) {
        if (dimAccount >= 0 && dimAccount <= 1) {
            mDimAccount = dimAccount;
        }
        return this;
    }

    public KProgressHUD setSize(int width, int height) {
        mProgressDialog.setSize(width, height);
        return this;
    }

    @Deprecated
    public KProgressHUD setWindowColor(int color) {
        mWindowColor = color;
        return this;
    }

    public KProgressHUD setBackgroundColor(int color) {
        mWindowColor = color;
        return this;
    }

    public KProgressHUD setCornerRadius(float radius) {
        mCornerRadius = radius;
        return this;
    }

    public KProgressHUD setAnimateSpeed(int scale) {
        mAnimateSpeed = scale;
        return this;
    }

    public KProgressHUD setLabel(String label) {
        mProgressDialog.setLabel(label);
        return this;
    }

    public KProgressHUD setLable(String label, int color) {
        mProgressDialog.setLabel(label,color);
        return this;
    }
    public KProgressHUD setDetailsLabel(String label) {
        mProgressDialog.setDetailsLabel(label);
        return this;
    }

    public KProgressHUD setDetailsLabel(String label, int color) {
        mProgressDialog.setDetailsLabel(label,color);
        return this;
    }

    public KProgressHUD setMaxProgress(int max) {
        mMaxProgress = max;
        return this;
    }

    public void setProgress(int progress) {
        mProgressDialog.setProgress(progress);
    }

    public KProgressHUD setCustomView(View view) {
        if (view != null) {
            mProgressDialog.setView(view);
        } else {
            throw new RuntimeException("Custom view must not be null");
        }
        return this;
    }

    public KProgressHUD setCancelable(boolean cancelable) {
        mProgressDialog.setCancelable(cancelable);
        mProgressDialog.setOnCancelListener(null);
        return this;
    }

    public KProgressHUD setCancelable(DialogInterface.OnCancelListener listener) {
        mProgressDialog.setCancelable(null != listener);
        mProgressDialog.setOnCancelListener(listener);
        return this;
    }

    public KProgressHUD setAutoDismiss(boolean isAutoDismiss) {
        mIsAutoDismiss = isAutoDismiss;
        return this;
    }

    public KProgressHUD setGraceTime(int graceTime) {
        mGraceTimeMs = graceTime;
        return this;
    }

    public KProgressHUD show() {
        if (!isShowing()) {
            mFinished = false;
            if (mGraceTimeMs == 0) {
                mProgressDialog.show();
            } else {
                mGraceTimer = new Handler();
                mGraceTimer.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mProgressDialog != null && !mFinished) {
                            mProgressDialog.show();
                        }
                    }
                }, mGraceTimeMs);
            }
        }
        return this;
    }

    public void dismiss() {
        mFinished = true;
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        if (mGraceTimer != null) {
            mGraceTimer.removeCallbacksAndMessages(null);
            mGraceTimer = null;
        }
    }

    private boolean isShowing() {
        return mProgressDialog != null && mProgressDialog.isShowing();
    }

    private class ProgressDialog extends Dialog {
        private Determinate mDeterminateView;
        private Indeterminate mIndeterminateView;
        private View mView;
        private TextView mLabelText;
        private TextView mDetailsText;
        private String mLabel;
        private String mDetailsLabel;
        private FrameLayout mCustomViewContainer;
        private BackgroundLayout mBackgroundLayout;
        private int mWidth, mHeight;
        private int mLabelColor = Color.WHITE;
        private int mDetailColor = Color.WHITE;
        public ProgressDialog(@NonNull Context context) {
            super(context);

        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.kprogresshud_hud);
            Window window = getWindow();
            if (window != null) {
                window.setBackgroundDrawable(new ColorDrawable(0));
                window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                WindowManager.LayoutParams layoutParams = window.getAttributes();
                layoutParams.dimAmount = mDimAccount;
                layoutParams.gravity = Gravity.CENTER;
                window.setAttributes(layoutParams);
            }
            setCanceledOnTouchOutside(false);
            initViews();
        }

        private void initViews() {
            mBackgroundLayout = findViewById(R.id.background);
            mBackgroundLayout.setBaseColor(mWindowColor);
            mBackgroundLayout.setCornerRadius(mCornerRadius);
            if (mWidth != 0) {
                updateBackgroundSize();
            }
            mCustomViewContainer = findViewById(R.id.container);
            addViewToFrame(mView);
            if (mDeterminateView != null) {
                mDeterminateView.setMax(mMaxProgress);
            }
            if (mIndeterminateView != null) {
                mIndeterminateView.setAnimationSpeed(mAnimateSpeed);
            }
            mLabelText = findViewById(R.id.label);
            setLabel(mLabel,mLabelColor);
            mDetailsText = findViewById(R.id.details_label);
            setDetailsLabel(mDetailsLabel,mDetailColor);
        }

        private void updateBackgroundSize() {
            ViewGroup.LayoutParams params = mBackgroundLayout.getLayoutParams();
            params.width = SizeUtils.dp2px(mWidth);
            params.height = SizeUtils.dp2px(mHeight);
            mBackgroundLayout.setLayoutParams(params);
        }

        private void setView(View view) {
            if (view != null) {
                if (view instanceof Determinate) {
                    mDeterminateView = (Determinate) view;
                }
                if (view instanceof Indeterminate) {
                    mIndeterminateView = (Indeterminate) view;
                }
                mView = view;
                if (isShowing()) {
                    mCustomViewContainer.removeAllViews();
                    addViewToFrame(view);
                }
            }
        }

        private void addViewToFrame(View view) {
            if (view == null) return;
            int wrapParam = ViewGroup.LayoutParams.WRAP_CONTENT;
            ViewGroup.LayoutParams params = new FrameLayout.LayoutParams(wrapParam, wrapParam);
            mCustomViewContainer.addView(view,params);
        }

        public void setSize(int width, int height) {
            mWidth = width;
            mHeight = height;
            if (mBackgroundLayout != null) {
                updateBackgroundSize();
            }
        }

        public void setLabel(String label, int color) {
            mLabel = label;
            mLabelColor = color;
            if (mLabelText != null) {
                if (label != null) {
                    mLabelText.setText(label);
                    mLabelText.setTextColor(color);
                    mLabelText.setVisibility(View.VISIBLE);
                } else {
                    mLabelText.setVisibility(View.GONE);
                }
            }
        }
        public void setLabel(String label) {
            mLabel = label;
            if (mLabelText != null) {
                if (label != null) {
                    mLabelText.setText(label);
                    mLabelText.setVisibility(View.VISIBLE);
                } else {
                    mLabelText.setVisibility(View.GONE);
                }
            }
        }
        public void setDetailsLabel(String label) {
            mDetailsLabel = label;
            if (mDetailsText != null) {
                if (label != null) {
                    mDetailsText.setText(label);
                    mDetailsText.setVisibility(View.VISIBLE);
                } else {
                    mDetailsText.setVisibility(View.GONE);
                }
            }
        }



        public void setDetailsLabel(String label, int color) {
            mDetailsLabel = label;
            mDetailColor = color;
            if (mDetailsText != null) {
                if (label != null) {
                    mDetailsText.setTextColor(color);
                    mDetailsText.setText(label);
                    mDetailsText.setVisibility(View.VISIBLE);
                } else {
                    mDetailsText.setVisibility(View.GONE);
                }
            }
        }

        public void setProgress(int progress) {
            if (mDeterminateView != null) {
                mDeterminateView.setProgress(progress);
                if (mIsAutoDismiss && progress >= mMaxProgress) {
                    dismiss();
                }
            }
        }
    }
}
