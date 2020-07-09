package com.chip.parkpro1.ui.activities.Entry;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.chip.parkpro1.MyApplication;
import com.chip.parkpro1.R;
import com.chip.parkpro1.ServerCalls;
import com.chip.parkpro1.data.models.User;
import com.chip.parkpro1.ui.activities.homePage.HomepageActivity;
import com.chip.parkpro1.ui.base.YaAhlaBaseActivity;
import com.chip.parkpro1.utils.Utils;
import com.hbb20.CountryCodePicker;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignInActivity extends YaAhlaBaseActivity {

    @BindView(R.id.country_code_picker)
    CountryCodePicker countryCodePicker;
    @BindView(R.id.et_mobile)
    EditText edtMobile;
    @BindView(R.id.et_password)
    EditText edtPassword;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private AwesomeValidation validator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        ButterKnife.bind(this);

        validator = new AwesomeValidation(ValidationStyle.BASIC);
        setupValidatorRules();

        countryCodePicker.registerCarrierNumberEditText(edtMobile);

        checkLocationPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    checkIfLoggedIn();
                }
            }
        }
    }

    @OnClick(R.id.btn_verify)
    void verify() {
        if (Utils.isInternetConnected(SignInActivity.this)) {
            edtMobile.setError(null);
            edtPassword.setError(null);
            validator.clear();

            if (validator.validate()) {
                if (countryCodePicker.isValidFullNumber()) {
                    MyApplication.showLoading.setMessage(getString(R.string.signing_in));
                    MyApplication.showLoading.show(getSupportFragmentManager(), "");

                    ServerCalls.signIn(SignInActivity.this, countryCodePicker.getSelectedCountryCode(), edtMobile.getText().toString(),
                            edtPassword.getText().toString(), new Handler(Looper.getMainLooper(), msg -> {
                                if (msg.arg1 == 1) {
                                    try {
                                        JSONObject jsonObject = ((JSONObject) msg.obj);
                                        User.setUser(SignInActivity.this, jsonObject.toString());

                                        String topic = jsonObject.getString("topic");
                                        if (topic != null) {
                                            MyApplication.getInstance().subscribeTopic(topic);
                                        }

                                        Intent intent = new Intent(SignInActivity.this, HomepageActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);

                                        MyApplication.showLoading.dismiss();
                                    } catch (Exception ex) {
                                        Utils.showTopSnackBar(SignInActivity.this, "Wrong username or password");
                                        MyApplication.showLoading.dismiss();
                                    }
                                } else {
                                    if (msg.obj instanceof String) {
                                        Utils.showTopSnackBar(SignInActivity.this, msg.obj.toString());
                                        MyApplication.showLoading.dismiss();
                                    } else {
                                        Utils.showSneaker(SignInActivity.this, "Error", "Something went wrong", Utils.SneakerType.ERROR);
                                        MyApplication.showLoading.dismiss();
                                    }
                                }
                                return false;
                            }));
                } else {
                    Utils.showTopSnackBar(this, "Please use a valid number.");
                }
            }
        } else
            Utils.showTopSnackBar(SignInActivity.this, getString(R.string.no_internet));
    }

    @OnClick(R.id.tv_reset_password)
    void resetPassword() {
        Intent intent = new Intent(SignInActivity.this, NewPassword.class);
        startActivity(intent);
    }

    @OnClick(R.id.tv_sign_up)
    void signUp() {
        Intent intent = new Intent(SignInActivity.this, AddNumberActivity.class);
        startActivity(intent);
    }

    void checkIfLoggedIn() {
        if (User.getUser(SignInActivity.this) != null) {
            Intent intent = new Intent(SignInActivity.this, HomepageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }
    }

    void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(SignInActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_LOCATION);
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
        } else {
            checkIfLoggedIn();
        }
    }

    private void setupValidatorRules() {
        validator.addValidation(this, R.id.et_mobile, RegexTemplate.NOT_EMPTY, R.string.err_mobile_required);
        validator.addValidation(this, R.id.et_password, RegexTemplate.NOT_EMPTY, R.string.err_password_required);
    }
}