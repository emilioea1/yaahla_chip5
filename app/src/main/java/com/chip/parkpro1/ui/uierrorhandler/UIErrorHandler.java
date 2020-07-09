package com.chip.parkpro1.ui.uierrorhandler;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.chip.parkpro1.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UIErrorHandler {
    @Nullable
    @BindView(R.id.iv_empty_feature_image)
    ImageView ivEmptyFeatureImage;
    @Nullable
    @BindView(R.id.iv_error_no_connection)
    ImageView ivErrorNoInternet;
    @Nullable
    @BindView(R.id.tv_empty_feature_name)
    TextView tvFeatureName;
    @Nullable
    @BindView(R.id.ll_empty_ui)
    LinearLayout llEmptyUI;
    @Nullable
    @BindView(R.id.ll_error_to_load)
    LinearLayout llErrorToLoad;
    @Nullable
    @BindView(R.id.ll_no_internet)
    LinearLayout llNoInternet;
    @Nullable
    @BindView(R.id.ll_error)
    LinearLayout llError;
    @Nullable
    @BindView(R.id.tv_error_feature_name)
    TextView tvErrorFeatureName;

    private Context context;
    private View view;

    public UIErrorHandler(Context context, View view) {
        this.context = context;
        this.view = view;
        ButterKnife.bind(this, view);
    }

    public void showEmptyOrErrorOrNoConnectionUI(UIType errorType, String message, @Nullable Integer featureImage) {
        switch (errorType) {
            case EMPTY_UI:
                llEmptyUI.setVisibility(View.VISIBLE);
                ivEmptyFeatureImage.setImageResource(featureImage);
                tvFeatureName.setText(message);
                llErrorToLoad.setVisibility(View.GONE);
                break;
            case ERROR_UI:
                llEmptyUI.setVisibility(View.GONE);
                llNoInternet.setVisibility(View.GONE);
                llError.setVisibility(View.VISIBLE);
                llErrorToLoad.setVisibility(View.VISIBLE);
                tvErrorFeatureName.setText(message);
                ivErrorNoInternet.setImageResource(R.drawable.ic_cloud_off_black_24dp);
                break;
            case NO_INTERNET:
                llEmptyUI.setVisibility(View.GONE);
                llError.setVisibility(View.GONE);
                llNoInternet.setVisibility(View.VISIBLE);
                llErrorToLoad.setVisibility(View.VISIBLE);
                ivErrorNoInternet.setImageResource(R.drawable.ic_portable_wifi_off_black_24dp);

                break;
        }
    }
}
