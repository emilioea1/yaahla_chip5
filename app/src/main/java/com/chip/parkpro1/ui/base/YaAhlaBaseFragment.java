package com.chip.parkpro1.ui.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.chip.parkpro1.ui.uierrorhandler.UIErrorHandler;
import com.chip.parkpro1.ui.uierrorhandler.UIType;
import com.chip.parkpro1.utils.ProgressBarHandler;

public class YaAhlaBaseFragment extends Fragment {

    private BaseActivityCallback callback;
    private ProgressBarHandler progressBarHandler;
    private UIErrorHandler uIErrorHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressBarHandler = new ProgressBarHandler(getActivity());
    }

    protected void showProgressDialog(String message) {
        if (callback != null) {
            callback.showProgressDialog(message);
        }
    }

    protected void hideProgressDialog() {
        if (callback != null) {
            callback.hideProgressDialog();
        }
    }

    public void hideKeyboard(View view, Context context) {
        InputMethodManager inputManager = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager
                .RESULT_UNCHANGED_SHOWN);
    }

    protected void setToolbarTitle(String title) {
        callback.setToolbarTitle(title);
    }

    protected void setToolbarIconVisibility(int Visibility) {
        callback.setToolbarIconVisibility(Visibility);
    }

    protected void showProgressBar() {
        progressBarHandler.show();
    }

    protected void hideProgressBar() {
        progressBarHandler.hide();
    }

    protected void initializeUIErrorHandler(Context context, View view) {
        uIErrorHandler = new UIErrorHandler(context, view);
    }

    protected void showEmptyUI(@NonNull String message, @NonNull Integer featureImage) {
        uIErrorHandler.showEmptyOrErrorOrNoConnectionUI(UIType.EMPTY_UI, message, featureImage);
    }

    protected void showSearchUI() {
        uIErrorHandler.showEmptyOrErrorOrNoConnectionUI(UIType.SEARCH, null, null);
    }

    protected void showErrorUI(@NonNull String featureName) {
        uIErrorHandler.showEmptyOrErrorOrNoConnectionUI(UIType.ERROR_UI, featureName, null);
    }

    protected void show2NoInternetUI() {
        uIErrorHandler.showEmptyOrErrorOrNoConnectionUI(UIType.NO_INTERNET, null, null);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = context instanceof Activity ? (Activity) context : null;
        try {
            callback = (BaseActivityCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement BaseActivityCallback methods");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }
}
