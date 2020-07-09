package com.chip.parkpro1.ui.activities.Entry;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chip.parkpro1.MyApplication;
import com.chip.parkpro1.R;
import com.chip.parkpro1.ServerCalls;
import com.chip.parkpro1.data.models.User;
import com.chip.parkpro1.ui.activities.homePage.HomepageActivity;
import com.chip.parkpro1.ui.base.YaAhlaBaseActivity;
import com.chip.parkpro1.utils.Utils;
import com.chip.parkpro1.utils.customViews.PINCheckView;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ValidatePin extends YaAhlaBaseActivity {
    @BindView(R.id.btn_validate)
    Button btn_validate;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.pin_check_view)
    PINCheckView pinCheckView;
    @BindView(R.id.tv_invalid_pin)
    TextView tvInvalidPin;
    @BindView(R.id.tv_resend_pin)
    TextView tvResendPin;

    private String mobileNumber, countryDialCode;
    private String password;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate_pin);

        ButterKnife.bind(this);

        countDownTimer = new CountDownTimer(120000, 1000) {

            @SuppressLint("SimpleDateFormat")
            public void onTick(long millisUntilFinished) {
                tvResendPin.setText(String.format("%s %s", getString(R.string.resend_pin), new SimpleDateFormat("mm:ss").format(new Date(millisUntilFinished))));
            }

            public void onFinish() {
                tvResendPin.setText(getString(R.string.resend_pin));
                tvResendPin.setTextColor(getResources().getColor(R.color.appColor));
                countDownTimer.cancel();
            }
        }.start();
    }

    @OnClick(R.id.btn_cancel)
    void cancel() {
        onBackPressed();
    }

    @OnClick(R.id.btn_validate)
    void verifyPin() {
        if (pinCheckView.getPIN() != null) {
            if (getIntent().getBooleanExtra("isMobileChanged", false)) {
                updateMobile();
            } else if (getIntent().getBooleanExtra("isPasswordChanged", false)) {
                Log.e("Majd", "verifyPin");
                mobileNumber = getIntent().getStringExtra("mobile_number");
                countryDialCode = getIntent().getStringExtra("country_code");
                password = getIntent().getStringExtra("new_password");
                changePassword();
            } else
                validatePin();
        } else
            Utils.showTopSnackBar(ValidatePin.this, getString(R.string.fill_code));
    }

    @OnClick(R.id.tv_resend_pin)
    void tvResendPin() {
        if (tvResendPin.getCurrentTextColor() != getResources().getColor(R.color.lightestGray)) {
            requestPin();
            tvResendPin.setText(getString(R.string.resend_pin));
            tvResendPin.setTextColor(getResources().getColor(R.color.lightestGray));
        }
    }

    private void hideViews() {
        tvInvalidPin.setVisibility(View.INVISIBLE);
        btn_validate.setVisibility(View.GONE);
        tvResendPin.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        pinCheckView.canEdit(false);
    }

    private void showViews(Boolean isInvalid) {
        if (isInvalid)
            tvInvalidPin.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        btn_validate.setVisibility(View.VISIBLE);
        tvResendPin.setVisibility(View.VISIBLE);
        countDownTimer.start();
        pinCheckView.canEdit(true);
    }

    private void changePassword() {
        Log.e("Majd", "changePassword");

        ServerCalls.resetPassword(
                ValidatePin.this, String.valueOf(countryDialCode), String.valueOf(mobileNumber), password, pinCheckView.getPIN(),
                new Handler(Looper.getMainLooper(), msg -> {
                    if (msg.arg1 == 1) {
                        try {
                            JSONObject jsonObject = ((JSONObject) msg.obj);
                            if (jsonObject.getBoolean("success")) {
                                Log.e("Majd", "changePassword success");

                                signIn();

                            } else {
                                Log.e("Majd", "changePassword failed");

                                showViews(true);
                                pinCheckView.clearPIN();
                                Objects.requireNonNull(ValidatePin.this.getCurrentFocus()).clearFocus();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    return false;
                })
        );
    }

    private void signIn() {
        Log.e("Majd", "signIn");

        if (Utils.isInternetConnected(ValidatePin.this)) {
            MyApplication.showLoading.setMessage(getString(R.string.signing_in));
            MyApplication.showLoading.show(getSupportFragmentManager(), "");

            ServerCalls.signIn(ValidatePin.this, String.valueOf(countryDialCode), String.valueOf(mobileNumber),
                    String.valueOf(password),
                    new Handler(Looper.getMainLooper(), msg -> {
                        if (msg.arg1 == 1) {
                            try {
                                Log.e("Majd", "signIn");

                                JSONObject jsonObject = ((JSONObject) msg.obj);
                                User.setUser(ValidatePin.this, jsonObject.toString());

                                String topic = jsonObject.getString("topic");
                                if (topic != null) {
                                    MyApplication.getInstance().subscribeTopic(topic);
                                }
                                Intent intent = new Intent(ValidatePin.this, HomepageActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);

                                MyApplication.showLoading.dismiss();
                            } catch (Exception ex) {
                                Utils.showTopSnackBar(ValidatePin.this, "Wrong username or password");
                                MyApplication.showLoading.dismiss();
                            }
                        } else {
                            if (msg.obj instanceof String) {
                                Utils.showTopSnackBar(ValidatePin.this, msg.obj.toString());
                                MyApplication.showLoading.dismiss();
                            } else {
                                Utils.showSneaker(ValidatePin.this, "Error", "Something went wrong", Utils.SneakerType.ERROR);
                                MyApplication.showLoading.dismiss();
                            }
                        }
                        return false;
                    }));
        } else
            Utils.showTopSnackBar(ValidatePin.this, getString(R.string.no_internet));
    }

    private void validatePin() {
        if (Utils.isInternetConnected(ValidatePin.this)) {
            hideViews();
            ServerCalls.validatePin(ValidatePin.this, getIntent().getStringExtra("countryDialCode"),
                    getIntent().getStringExtra("mobile"), pinCheckView.getPIN(), () -> {
                        validatePin();
                        return null;
                    }, new Handler(Looper.getMainLooper(), msg -> {
                        if (msg.arg1 == 1) {
                            try {
                                Intent intent;
                                JSONObject jsonObject = ((JSONObject) msg.obj);
                                if (jsonObject.getBoolean("success")) {
                                    if (getIntent().getBooleanExtra("isMobileChanged", false)) {
                                        Toast.makeText(ValidatePin.this, "", Toast.LENGTH_SHORT).show();
                                    } else {
                                       intent = new Intent(ValidatePin.this, SignInActivity.class);
                                       intent.putExtra("countryDialCode", getIntent().getStringExtra("countryDialCode"));
                                       intent.putExtra("mobile", getIntent().getStringExtra("mobile"));
                                       startActivity(intent);
                                        //overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
                                    }
                                } else {
                                    showViews(true);
                                    pinCheckView.clearPIN();
                                    Objects.requireNonNull(ValidatePin.this.getCurrentFocus()).clearFocus();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            showViews(true);
                            pinCheckView.clearPIN();
                            Objects.requireNonNull(ValidatePin.this.getCurrentFocus()).clearFocus();
                        }
                        return false;
                    }));
        } else
            Utils.showTopSnackBar(ValidatePin.this, getString(R.string.no_internet));
    }

    private void updateMobile() {
        if (Utils.isInternetConnected(ValidatePin.this)) {
            hideViews();

            ServerCalls.updateMobile(ValidatePin.this, getIntent().getStringExtra("countryDialCode"),
                    getIntent().getStringExtra("mobile"), pinCheckView.getPIN(), () -> {
                        updateMobile();
                        return null;
                    }, new Handler(Looper.getMainLooper(), msg -> {
                        if (msg.arg1 == 1) {
                            try {
                                JSONObject jsonObject = ((JSONObject) msg.obj);
                                if (jsonObject.getBoolean("success")) {
                                    Toast.makeText(ValidatePin.this, getString(R.string.mobile_changed), Toast.LENGTH_LONG).show();

                                    User user = User.getUser(ValidatePin.this);
                                    user.token = jsonObject.getString("token");
                                    user.mobile = getIntent().getStringExtra("mobile");
                                    user.countryDialCode = getIntent().getStringExtra("countryDialCode");
                                    User.setUser(ValidatePin.this, new Gson().toJson(user));

                                    Intent intent = new Intent();
                                    setResult(RESULT_OK, intent);
                                    finish();
                                }
                            } catch (Exception ex) {
                            }
                        } else {
                            showViews(true);
                            pinCheckView.clearPIN();
                            ValidatePin.this.getCurrentFocus().clearFocus();
                            Utils.showTopSnackBar(ValidatePin.this, msg.obj.toString());
                        }
                        return false;
                    }));
        } else
            Utils.showTopSnackBar(ValidatePin.this, getString(R.string.no_internet));
    }

    private void requestPin() {
        if (Utils.isInternetConnected(ValidatePin.this)) {
            MyApplication.showLoading.setMessage(getString(R.string.please_wait));
            MyApplication.showLoading.show(getSupportFragmentManager(), "");

            ServerCalls.requestPin(ValidatePin.this, getIntent().getStringExtra("countryDialCode"), getIntent().getStringExtra("mobile"),
                    () -> {
                        requestPin();
                        return null;
                    }, new Handler(Looper.getMainLooper(), msg -> {
                        if (msg.arg1 == 1) {
                            try {
                                JSONObject jsonObject = ((JSONObject) msg.obj);
                                Utils.showTopSnackBar(ValidatePin.this, jsonObject.getString("pin"));
                                pinCheckView.clearPIN();
                                countDownTimer.start();
                                Objects.requireNonNull(ValidatePin.this.getCurrentFocus()).clearFocus();
                                MyApplication.showLoading.dismiss();
                            } catch (Exception ignored) {
                            }
                        } else {
                            pinCheckView.clearPIN();
                            Objects.requireNonNull(ValidatePin.this.getCurrentFocus()).clearFocus();
                            countDownTimer.start();
                            MyApplication.showLoading.dismiss();
                            Utils.showTopSnackBar(ValidatePin.this, msg.obj.toString());
                        }
                        return false;
                    }));
        } else
            Utils.showTopSnackBar(ValidatePin.this, getString(R.string.no_internet));
    }
}