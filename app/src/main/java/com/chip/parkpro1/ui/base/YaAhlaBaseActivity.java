package com.chip.parkpro1.ui.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.chip.parkpro1.R;
import com.chip.parkpro1.ui.activities.AnimationHelper.TransitionHelper;
import com.chip.parkpro1.ui.activities.homePage.HomepageActivity;
import com.chip.parkpro1.ui.uierrorhandler.UIErrorHandler;
import com.chip.parkpro1.ui.uierrorhandler.UIType;
import com.chip.parkpro1.utils.Utils;
import com.chip.parkpro1.utils.WindowUtil;

import java.util.concurrent.atomic.AtomicLong;

public class YaAhlaBaseActivity extends AppCompatActivity implements BaseActivityCallback {
    static final String EXTRA_TYPE = "type";
    static final int TYPE_PROGRAMMATICALLY = 0;
    private static final String KEY_ACTIVITY_ID = "KEY_ACTIVITY_ID";
    private static final AtomicLong NEXT_ID = new AtomicLong(0);
    protected Toolbar toolbar;
    private String LOG_TAG = YaAhlaBaseActivity.class.getSimpleName();
    private ProgressDialog progress;
    private long activityId;
    private UIErrorHandler uIErrorHandler;


    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create the ActivityComponent and reuses cached ConfigPersistentComponent if this is
        // being called after a configuration change.
        activityId = savedInstanceState != null ? savedInstanceState.getLong(KEY_ACTIVITY_ID) : NEXT_ID.getAndIncrement();

        getScreenSize();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(KEY_ACTIVITY_ID, activityId);
    }

    public void setActionBarTitle(String title) {
        if (getSupportActionBar() != null && getTitle() != null) {
            setTitle(title);
        }
    }

    public void showBackButton() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public void setActionBarTitle(int title) {
        setActionBarTitle(getResources().getString(title));
    }

    public Toolbar getToolbar() {
        return toolbar;
    }


    public void returnToHomepage() {
        Intent intent = new Intent(this, HomepageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    protected void initializeFeel22UIErrorHandler(Context context, View view) {
        uIErrorHandler = new UIErrorHandler(context, view);
    }

    protected void showFeel22EmptyUI(@NonNull String message, @NonNull Integer featureImage) {
        uIErrorHandler.showEmptyOrErrorOrNoConnectionUI(UIType.EMPTY_UI, message, featureImage);
    }

    protected void showFeel22SearchUI() {
        uIErrorHandler.showEmptyOrErrorOrNoConnectionUI(UIType.SEARCH, null, null);
    }

    protected void showFeel22ErrorUI(@NonNull String featureName) {
        uIErrorHandler.showEmptyOrErrorOrNoConnectionUI(UIType.ERROR_UI, featureName, null);
    }

    protected void showFeel22NoInternetUI() {
        uIErrorHandler.showEmptyOrErrorOrNoConnectionUI(UIType.NO_INTERNET, null, null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isAcceptingText())
                    Utils.hideKeyboard(this, null);
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void showProgressDialog(String message) {
        if (progress == null) {
            progress = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
            progress.setCancelable(false);
        }
        progress.setMessage(message);
        progress.show();
    }

    @Override
    public void setToolbarTitle(String title) {
        setActionBarTitle(title);
    }

    @Override
    public void setToolbarIconVisibility(int Visibility) {
//        if (toolbar != null)
//            toolbar.findViewById(R.id.logo).setVisibility(Visibility);
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputManager = (InputMethodManager) this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager
                .RESULT_UNCHANGED_SHOWN);
    }

    @Override
    public void hideProgressDialog() {
        if (progress != null && progress.isShowing())
            progress.dismiss();
    }


    @Override
    public void logout() {

    }

    /**
     * Replace Fragment in FrameLayout Container.
     *
     * @param fragment       Fragment
     * @param addToBackStack Add to BackStack
     * @param containerId    Container Id
     */
    public void replaceFragment(Fragment fragment, boolean addToBackStack, int containerId) {
        invalidateOptionsMenu();
        String backStateName = fragment.getClass().getName();
        boolean fragmentPopped = getSupportFragmentManager().popBackStackImmediate(backStateName, 0);

        if (!fragmentPopped && getSupportFragmentManager().findFragmentByTag(backStateName) == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(containerId, fragment, backStateName);
            if (addToBackStack) {
                transaction.addToBackStack(backStateName);
            }
            transaction.commit();
        }
    }

    public void clearFragmentBackStack() {
        FragmentManager fm = getSupportFragmentManager();
        int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
        for (int i = 0; i < backStackCount; i++) {
            int backStackId = getSupportFragmentManager().getBackStackEntryAt(i).getId();
            fm.popBackStack(backStackId, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    private void getScreenSize() {
        if (WindowUtil.screenHeight == 0 | WindowUtil.screenWidth == 0) {
            Display display = getWindowManager().getDefaultDisplay();
            WindowUtil.screenWidth = display.getWidth();
            WindowUtil.screenHeight = display.getHeight();
        }
    }

    public void transitionTo(Intent i) {
        final Pair<View, String>[] pairs = TransitionHelper.createSafeTransitionParticipants(this, true);
        ActivityOptionsCompat transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this, pairs);
        startActivity(i, transitionActivityOptions.toBundle());
    }
}
