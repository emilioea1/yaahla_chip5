package com.chip.parkpro1.ui.activities.homePage;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.chip.parkpro1.MyApplication;
import com.chip.parkpro1.R;
import com.chip.parkpro1.data.models.User;
import com.chip.parkpro1.ui.activities.Entry.SignInActivity;

import io.realm.Realm;

public class settingfragment extends Fragment {
    View logout_layout,profile_layout,help_layout,about_layout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.settings, container, false);

        logout_layout = v.findViewById(R.id.logout_layout);
        profile_layout=v.findViewById(R.id.profile_layout);
        help_layout = v.findViewById(R.id.help_layout);
        about_layout=v.findViewById(R.id.about_layout);




        profile_layout.setOnClickListener((View view) ->{
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent);

        });


        help_layout.setOnClickListener((View view) ->{
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
            startActivity(browserIntent);

        });

        about_layout.setOnClickListener((View view) ->{
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
            startActivity(browserIntent);

        });


        logout_layout.setOnClickListener((View view) -> {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> logout())
                    .create()
                    .show();

        });


        return v;
    }

    private void logout() {
        if (User.getUser(getActivity()).topic != null) {
            MyApplication.getInstance().unsubscribeTopic(User.getUser(getActivity()).topic);
        }

        User.setUser(getActivity(), null);

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();
        realm.close();

        Intent intent = new Intent(getActivity(), SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }
}
