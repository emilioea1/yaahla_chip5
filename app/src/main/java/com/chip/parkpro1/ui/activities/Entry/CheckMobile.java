package com.chip.parkpro1.ui.activities.Entry;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Fade;
import android.transition.Slide;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.chip.parkpro1.MyApplication;
import com.chip.parkpro1.R;
import com.chip.parkpro1.ServerCalls;
import com.chip.parkpro1.data.models.User;
import com.chip.parkpro1.ui.activities.homePage.HomepageActivity;
//import com.chip.parkpro1.ui.fragments.map.HomeFragment;
import com.chip.parkpro1.utils.Utils;
import com.google.android.material.textfield.TextInputLayout;
import com.hbb20.CountryCodePicker;

import org.json.JSONObject;

public class CheckMobile extends AppCompatActivity {

    // region "Views"
    Button mVerifyButton;
    String mobile;
    TextView txt_forgetPassword;
    TextInputLayout layout_password;
    EditText edt_mobile, edt_password;
    CountryCodePicker countryCodePicker;
    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_mobile);

        // region "Initialize Views"
        mVerifyButton = findViewById(R.id.btn_verify);
        edt_mobile = findViewById(R.id.edt_mobile);
        edt_password = findViewById(R.id.edt_password);
        layout_password = findViewById(R.id.layout_password);
        countryCodePicker = findViewById(R.id.countryCodePicker);
        txt_forgetPassword = findViewById(R.id.txt_sign_up);
        // endregion

//        mVerifyButton.setBackgroundColor(Color.rgb(	39, 	45, 	54));
        mVerifyButton.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.round_button_main,null));

        edt_mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (mobile != null) {
                    if (!mobile.equals(edt_mobile.getText().toString())) {
                        if (layout_password.getVisibility() == View.VISIBLE) {
                            layout_password.setVisibility(View.GONE);
                            txt_forgetPassword.setVisibility(View.INVISIBLE);
                            mVerifyButton.setTextColor(ContextCompat.getColor(CheckMobile.this,R.color.appColor));
                            mVerifyButton.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.white_button,null));
                            mVerifyButton.setText(getString(R.string.verify));
                        }
                    }
                }
            }
        });

        mVerifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.hideKeyboard(CheckMobile.this, null);
                Utils.disableClicks(view);
//                if (mVerifyButton.getText().equals("Sign in")) {
//                    if (!edt_mobile.getText().toString().isEmpty()) {
//                        Data.showDialog(CheckMobile.this, getString(R.string.confirmation), getString(R.string.are_you_sure) +
//                                        " +" + countryCodePicker.getSelectedCountryCode() + " " + edt_mobile.getText().toString() + " " + getString(R.string.is_your_phone_number),
//                                getString(R.string.confirm), getString(R.string.cancel), new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialogInterface, int i) {
//                                        checkMobile();
//                                    }
//                                }, null);
//                    } else
//                        Data.showTopSnackBar(CheckMobile.this, getString(R.string.enter_mobile_number));
//                } else {
                signIn();
//                }
            }
        });

        txt_forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                requestPin(false);
                Intent intent = new Intent(CheckMobile.this, AddNumberActivity.class);
                startActivity(intent);

            }
        });
        setupWindowAnimations();
    }

    /**
     * Showing animation on create
     * using this:
     * https://github.com/lgvalle/Material-Animations
     */
    private void setupWindowAnimations() {
        Fade fade = new Fade();
        fade.setDuration(1000);
        getWindow().setEnterTransition(fade);

        Slide slide = new Slide();
        slide.setDuration(1000);
        getWindow().setReturnTransition(slide);
    }

    private void checkMobile() {
        if (Utils.isInternetConnected(CheckMobile.this)) {
            MyApplication.showLoading.setMessage(getString(R.string.please_wait));
            MyApplication.showLoading.show(getSupportFragmentManager(), "");

            ServerCalls.checkMobile(CheckMobile.this, countryCodePicker.getSelectedCountryCode(), edt_mobile.getText().toString(),
                    new Handler(Looper.getMainLooper(), new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            if (msg.arg1 == 1) {
                                try {
                                    JSONObject jsonObject = ((JSONObject) msg.obj);
                                    MyApplication.showLoading.dismiss();
//                                    if (jsonObject.getInt("available") == 0)
//                                        requestPin(false);
//                                    else {
                                    layout_password.setVisibility(View.VISIBLE);
                                    edt_password.setText(null);
                                    txt_forgetPassword.setVisibility(View.VISIBLE);
                                    mVerifyButton.setTextColor(Color.WHITE);
//                                        mVerifyButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.blue_button,null));
                                    mVerifyButton.setText(getString(R.string.sign_in));
                                    mobile = edt_mobile.getText().toString();
//                                    }
                                } catch (Exception ignored) {
                                }
                            } else {
                                Utils.showTopSnackBar(CheckMobile.this, msg.obj.toString());
                                MyApplication.showLoading.dismiss();
                            }
                            return false;
                        }
                    }));
        } else
            Utils.showTopSnackBar(CheckMobile.this, getString(R.string.no_internet));
    }

    private void signIn() {
        if (Utils.isInternetConnected(CheckMobile.this)) {
            MyApplication.showLoading.setMessage(getString(R.string.signing_in));
            MyApplication.showLoading.show(getSupportFragmentManager(), "");

            ServerCalls.signIn(CheckMobile.this, countryCodePicker.getSelectedCountryCode(), edt_mobile.getText().toString(),
                    edt_password.getText().toString(), new Handler(Looper.getMainLooper(), new Handler.Callback() {

                        @Override
                        public boolean handleMessage(Message msg) {
                            if (msg.arg1 == 1) {
                                try {
                                    JSONObject jsonObject = ((JSONObject) msg.obj);
                                    User.setUser(CheckMobile.this, jsonObject.toString());

                                    String topic = jsonObject.getString("topic");
                                    if (topic != null) {
                                        MyApplication.getInstance().subscribeTopic(topic);
                                    }

                                    Intent intent = new Intent(CheckMobile.this, HomepageActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);

                                    MyApplication.showLoading.dismiss();
                                } catch (Exception ex) {
                                    Utils.showTopSnackBar(CheckMobile.this, "Wrong username or password");
                                    MyApplication.showLoading.dismiss();
                                }
                            } else {
                                if (msg.obj instanceof String) {
                                    Utils.showTopSnackBar(CheckMobile.this, msg.obj.toString());
                                    MyApplication.showLoading.dismiss();
                                } else {
                                    Utils.showSneaker(CheckMobile.this,"Error","Something went wrong", Utils.SneakerType.ERROR);
                                    MyApplication.showLoading.dismiss();
                                }
                            }
                            return false;
                        }
                    }));
        } else
            Utils.showTopSnackBar(CheckMobile.this, getString(R.string.no_internet));
    }
}