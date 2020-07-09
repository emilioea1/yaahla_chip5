package com.chip.parkpro1.ui.activities.DrawerMenu.EditProfile;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chip.parkpro1.MyApplication;
import com.chip.parkpro1.R;
import com.chip.parkpro1.ServerCalls;
import com.chip.parkpro1.data.models.User;
import com.chip.parkpro1.ui.activities.Entry.ValidatePin;
import com.chip.parkpro1.ui.base.YaAhlaBaseActivity;
import com.chip.parkpro1.utils.Utils;
import com.google.android.material.textfield.TextInputLayout;
import com.hbb20.CountryCodePicker;

import org.json.JSONObject;

import java.util.Objects;
import java.util.concurrent.Callable;

public class ChangeMobile extends YaAhlaBaseActivity {

    // region "Views"
    Button btn;
    TextInputLayout layout_password;
    EditText edt_mobile, edt_password;
    CountryCodePicker countryCodePicker;
    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_mobile);
        setTitle(getString(R.string.change_mobile));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // region "Initialize Views"
        btn = findViewById(R.id.btn);
        edt_mobile = findViewById(R.id.edt_mobile);
        edt_password = findViewById(R.id.edt_password);
        layout_password = findViewById(R.id.layout_password);
        countryCodePicker = findViewById(R.id.countryCodePicker);
        // endregion

        edt_mobile.setText(User.getUser(ChangeMobile.this).mobile);

        btn.setOnClickListener(view -> {
            Utils.disableClicks(view);
            Utils.hideKeyboard(ChangeMobile.this, null);
            if (btn.getText().equals(getResources().getString(R.string.validate))) {
                if (edt_password.getText().length() > 0)
                    validateAccount();
                else
                    Utils.showTopSnackBar(ChangeMobile.this, getString(R.string.enter_password));
            } else {
                if (edt_mobile.getText().length() > 0)
                    requestPin();
                else
                    Utils.showTopSnackBar(ChangeMobile.this, getString(R.string.enter_mobile_number));
            }
        });
    }

    private void hideViews() {
        layout_password.setVisibility(View.GONE);
        edt_mobile.requestFocus();
        edt_mobile.setFocusableInTouchMode(true);
        edt_mobile.setText(null);
        countryCodePicker.setClickable(true);
        btn.setText(getString(R.string.continuee));
    }

    private void validateAccount() {
        if (Utils.isInternetConnected(ChangeMobile.this)) {
            MyApplication.showLoading.setMessage(getString(R.string.verifying));
            MyApplication.showLoading.show(getSupportFragmentManager(), "");

            ServerCalls.validateAccount(ChangeMobile.this, countryCodePicker.getSelectedCountryCode(), edt_mobile.getText().toString(),
                    edt_password.getText().toString(), () -> {
                        validateAccount();
                        return null;
                    }, new Handler(Looper.getMainLooper(), msg -> {
                        if (msg.arg1 == 1) {
                            try {
                                JSONObject jsonObject = ((JSONObject) msg.obj);
                                if (jsonObject.getBoolean("valid"))
                                    hideViews();
                                else
                                    Utils.showTopSnackBar(ChangeMobile.this, getString(R.string.incorrect_credentials));
                                MyApplication.showLoading.dismiss();
                            } catch (Exception ex) {
                                MyApplication.showLoading.dismiss();
                            }
                        } else {
                            MyApplication.showLoading.dismiss();
                            Utils.showTopSnackBar(ChangeMobile.this, msg.obj.toString());
                        }
                        return false;
                    }));
        } else
            Utils.showTopSnackBar(ChangeMobile.this, getString(R.string.no_internet));
    }

    private void requestPin() {
        if (Utils.isInternetConnected(ChangeMobile.this)) {
            MyApplication.showLoading.setMessage(getString(R.string.please_wait));
            MyApplication.showLoading.show(getSupportFragmentManager(), "");

            ServerCalls.requestPin(ChangeMobile.this, countryCodePicker.getSelectedCountryCode(), edt_mobile.getText().toString(),
                    new Callable<Void>() {
                        public Void call() {
                            requestPin();
                            return null;
                        }
                    }, new Handler(Looper.getMainLooper(), msg -> {
                        if (msg.arg1 == 1) {
                            try {
                                JSONObject jsonObject = ((JSONObject) msg.obj);
                                MyApplication.showLoading.dismiss();
                                Toast.makeText(ChangeMobile.this, jsonObject.getString("pin"), Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(ChangeMobile.this, ValidatePin.class);
                                intent.putExtra("countryDialCode", countryCodePicker.getSelectedCountryCode());
                                intent.putExtra("mobile", edt_mobile.getText().toString());
                                intent.putExtra("isMobileChanged", true);
                                startActivityForResult(intent, 1);
                                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
                            } catch (Exception ex) {
                                MyApplication.showLoading.dismiss();
                            }
                        } else {
                            MyApplication.showLoading.dismiss();
                            if (msg.obj != null)
                                Utils.showTopSnackBar(ChangeMobile.this, msg.obj.toString());
                        }
                        return false;
                    }));
        } else
            Utils.showTopSnackBar(ChangeMobile.this, getString(R.string.no_internet));
    }

    // region "Override Methods"
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
    // endregion
}