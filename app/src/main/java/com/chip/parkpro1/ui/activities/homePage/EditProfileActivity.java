package com.chip.parkpro1.ui.activities.homePage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chip.parkpro1.MyApplication;
import com.chip.parkpro1.R;
import com.chip.parkpro1.ServerCalls;
import com.chip.parkpro1.data.models.User;
//import com.chip.parkpro1.ui.activities.DrawerMenu.EditProfile.ChangePassword;
import com.chip.parkpro1.ui.base.YaAhlaBaseActivity;
import com.chip.parkpro1.utils.Utils;
import com.chip.parkpro1.MyApplication;
import com.chip.parkpro1.R;
import com.chip.parkpro1.ServerCalls;
import com.chip.parkpro1.data.models.User;
import com.chip.parkpro1.ui.base.YaAhlaBaseActivity;
import com.chip.parkpro1.utils.Utils;
import com.google.gson.Gson;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditProfileActivity extends YaAhlaBaseActivity {

    @BindView(R.id.profile_text_content)
    EditText nameField;
    @BindView(R.id.surename_text_content)
    EditText sureNameField;
    @BindView(R.id.dob_content)
    EditText dobField;
    @BindView(R.id.email_content)
    EditText emailField;
    @BindView(R.id.edit_profile_done_button)
    TextView done;
    @BindView(R.id.edit_profile_cancel_button)
    TextView cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        //  getUser();
        User user = User.getUser(EditProfileActivity.this);
        nameField.setText(user.firstName);
        sureNameField.setText(user.lastName);
        emailField.setText(user.email);

    }

    @OnClick(R.id.edit_profile_cancel_button)
    void cancel() {
        finish();
    }

    @OnClick(R.id.edit_profile_done_button)
    void done() {
        updateProfile();
        finish();
    }

    private void updateProfile() {
        if (Utils.isInternetConnected(EditProfileActivity.this)) {
            MyApplication.showLoading.setMessage(getString(R.string.please_wait));
            MyApplication.showLoading.show(getSupportFragmentManager(), "");

            ServerCalls.updateProfile(
                    EditProfileActivity.this,
                    nameField.getText().toString(),
                    sureNameField.getText().toString(),
                    emailField.getText().toString(),
                    "",
                    () -> {
                        updateProfile();
                        return null;
                    },
                    new Handler(Looper.getMainLooper(), msg -> {
                        if (msg.arg1 == 1) {
                            try {

                                JSONObject jsonObject = ((JSONObject) msg.obj);
                                MyApplication.showLoading.dismiss();
                                if (jsonObject.getBoolean("success")) {

                                    User user = User.getUser(EditProfileActivity.this);
                                    user.firstName = nameField.getText().toString();
                                    user.lastName = sureNameField.getText().toString();
                                    user.email = emailField.getText().toString();
                                    User.setUser(EditProfileActivity.this, new Gson().toJson(user));

                                    Toast.makeText(EditProfileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();

                                    finish();
                                } else {
                                    Toast.makeText(EditProfileActivity.this, "An error has occurred", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                MyApplication.showLoading.dismiss();
                                Toast.makeText(EditProfileActivity.this, "An error has occurred", Toast.LENGTH_SHORT).show();
                            }
                        }
                        return false;
                    })
            );
        }
    }

    private void getUser() {
        if (Utils.isInternetConnected(EditProfileActivity.this)) {
            MyApplication.showLoading.setMessage(getString(R.string.please_wait));
            MyApplication.showLoading.show(getSupportFragmentManager(), "");

            ServerCalls.getUser(
                    EditProfileActivity.this,
                    () -> {
                        getUser();
                        return null;
                    },
                    new Handler(Looper.getMainLooper(), msg -> {
                        Log.e("Emilio", "msg = " + msg.toString());
                        if (msg.arg1 == 1) {
                            try {

                                JSONObject jsonObject = ((JSONObject) msg.obj);
                                MyApplication.showLoading.dismiss();

                                User user = User.getUser(EditProfileActivity.this);
                                user.card = jsonObject.getString("card");
                                user.expiryMonth = jsonObject.getString("expiryMonth");
                                user.expiryYear = jsonObject.getString("expiryYear");
                                user.plateNumber = jsonObject.getString("plateNumber");
                                user.carModel = jsonObject.getString("carModel");
                                user.defaultPaymentMethod = jsonObject.getInt("defaultPaymentMethod");
                                User.setUser(EditProfileActivity.this, new Gson().toJson(user));

                            } catch (Exception e) {
                                MyApplication.showLoading.dismiss();
                                Toast.makeText(EditProfileActivity.this, "Error occurred while getting profile", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            MyApplication.showLoading.dismiss();
                            Toast.makeText(EditProfileActivity.this, "An error has occurred", Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    })
            );
        }
    }
}
