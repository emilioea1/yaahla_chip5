package com.chip.parkpro1.ui.activities.Entry;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.EditText;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.chip.parkpro1.R;
import com.chip.parkpro1.ServerCalls;
import com.chip.parkpro1.ui.base.YaAhlaBaseActivity;
import com.chip.parkpro1.utils.Utils;
import com.hbb20.CountryCodePicker;

import org.json.JSONObject;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewPassword extends YaAhlaBaseActivity {

    @BindView(R.id.et_mobile)
    EditText etMobile;
    @BindView(R.id.et_new_password)
    EditText etNewPassword;
    @BindView(R.id.et_confirm_new_password)
    EditText etConfirmNewPassword;
    @BindView(R.id.country_code_picker)
    CountryCodePicker countryCodePicker;

    private AwesomeValidation validator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);

        ButterKnife.bind(this);

        validator = new AwesomeValidation(ValidationStyle.BASIC);
        setupValidatorRules();

        countryCodePicker.registerCarrierNumberEditText(etMobile);
    }

    @OnClick(R.id.btn_cancel)
    void cancel() {
        onBackPressed();
    }

    @OnClick(R.id.btn_change_password)
    void resetPassword() {

        etMobile.setError(null);
        etNewPassword.setError(null);
        etConfirmNewPassword.setError(null);
        validator.clear();

        if (validator.validate()) {
            if (countryCodePicker.isValidFullNumber()) {
                if (etNewPassword.getText().toString().equals(etConfirmNewPassword.getText().toString())) {
                    requestPin();

                    Intent intent = new Intent(NewPassword.this, ValidatePin.class);
                    intent.putExtra("isPasswordChanged", true);
                    intent.putExtra("mobile_number", etMobile.getText().toString());
                    intent.putExtra("country_code", countryCodePicker.getSelectedCountryCode());
                    intent.putExtra("new_password", etNewPassword.getText().toString());
                    startActivity(intent);
                    finish();
                } else {
                    Utils.showTopSnackBar(this, "Your password and confirmation password do not match.");
                }
            } else {
                Utils.showTopSnackBar(this, "Please use a valid number.");
            }
        }
    }

    private void requestPin() {
        if (Utils.isInternetConnected(NewPassword.this)) {
            ServerCalls.requestPin(
                    NewPassword.this,
                    countryCodePicker.getSelectedCountryCode(),
                    etMobile.getText().toString(),
                    () -> {
                        requestPin();
                        return null;
                    }, new Handler(Looper.getMainLooper(), msg -> {
                        if (msg.arg1 == 1) {
                            try {
                                JSONObject jsonObject = ((JSONObject) msg.obj);
                                Utils.showTopSnackBar(NewPassword.this, jsonObject.getString("pin"));
                                Objects.requireNonNull(NewPassword.this.getCurrentFocus()).clearFocus();
                            } catch (Exception ignored) {
                            }
                        } else {
                            Objects.requireNonNull(NewPassword.this.getCurrentFocus()).clearFocus();
                            Utils.showTopSnackBar(NewPassword.this, msg.obj.toString());
                        }
                        return false;
                    }));
        } else
            Utils.showTopSnackBar(NewPassword.this, getString(R.string.no_internet));
    }

    private void setupValidatorRules() {
        validator.addValidation(this, R.id.et_mobile, RegexTemplate.NOT_EMPTY, R.string.err_mobile_required);
        validator.addValidation(this, R.id.et_new_password, RegexTemplate.NOT_EMPTY, R.string.err_password_required);
        validator.addValidation(this, R.id.et_confirm_new_password, RegexTemplate.NOT_EMPTY, R.string.err_password_required);
    }
}