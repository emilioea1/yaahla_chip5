package com.chip.parkpro1.ui.activities.Entry;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.EditText;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.chip.parkpro1.MyApplication;
import com.chip.parkpro1.R;
import com.chip.parkpro1.ServerCalls;
import com.chip.parkpro1.ui.base.YaAhlaBaseActivity;
import com.chip.parkpro1.utils.Utils;
import com.hbb20.CountryCodePicker;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddNumberActivity extends YaAhlaBaseActivity {
    @BindView(R.id.et_mobile)
    EditText etMobile;
    @BindView(R.id.country_code_picker)
    CountryCodePicker countryCodePicker;

    private AwesomeValidation validator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_number_acitvity);

        ButterKnife.bind(this);

        validator = new AwesomeValidation(ValidationStyle.BASIC);
        setupValidatorRules();

        countryCodePicker.registerCarrierNumberEditText(etMobile);
    }

    @OnClick(R.id.btn_cancel)
    void cancel() {
        onBackPressed();
    }

    @OnClick(R.id.btn_verify)
    void requestPin() {
        if (Utils.isInternetConnected(AddNumberActivity.this)) {

            etMobile.setError(null);
            validator.clear();

            if (validator.validate()) {
                if (countryCodePicker.isValidFullNumber()) {
                    MyApplication.showLoading.setMessage(getString(R.string.please_wait));
                    MyApplication.showLoading.show(getSupportFragmentManager(), "");

                    ServerCalls.requestPin(AddNumberActivity.this, countryCodePicker.getSelectedCountryCode(), etMobile.getText().toString(),
                            () -> {
                                requestPin();
                                return null;
                            }, new Handler(Looper.getMainLooper(), msg -> {
                                if (msg.arg1 == 1) {
                                    try {
                                        JSONObject jsonObject = ((JSONObject) msg.obj);
                                        MyApplication.showLoading.dismiss();
                                        Toast.makeText(AddNumberActivity.this, jsonObject.getString("pin"), Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(AddNumberActivity.this, ValidatePin.class);
                                        intent.putExtra("countryDialCode", countryCodePicker.getSelectedCountryCode());
                                        intent.putExtra("mobile", etMobile.getText().toString());
                                        intent.putExtra("isForgetPassword", false);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
                                    } catch (Exception ignored) {
                                    }
                                } else {
                                    MyApplication.showLoading.dismiss();
                                    Utils.showTopSnackBar(AddNumberActivity.this, "An error occurred, please try again!");
                                }
                                return true;
                            }));
                } else {
                    Utils.showTopSnackBar(AddNumberActivity.this, "Please use a valid number.");
                }
            }
        } else
            Utils.showTopSnackBar(AddNumberActivity.this, getString(R.string.no_internet));
    }

    private void setupValidatorRules() {
        validator.addValidation(this, R.id.et_mobile, RegexTemplate.NOT_EMPTY, R.string.err_mobile_required);
    }
}
