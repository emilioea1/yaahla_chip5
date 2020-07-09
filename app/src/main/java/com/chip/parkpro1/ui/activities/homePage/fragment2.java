package com.chip.parkpro1.ui.activities.homePage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.chip.parkpro1.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class fragment2 extends Fragment {

    private TextView formatTxt, contentTxt;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment2, container, false);


        contentTxt=v.findViewById(R.id.scan_content);
        BottomNavigationView bottnav = v.findViewById(R.id.nav_menu2);
        bottnav.setOnNavigationItemSelectedListener(navListener);
        getFragmentManager().beginTransaction().replace(R.id.fragment2,
                new Sell()).commit();


        return v;
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedfragment = null;
                    switch (item.getItemId()) {
                        case R.id.Sell:

                            selectedfragment = new Sell();
                            break;

                        case R.id.Collect:

                            selectedfragment = new Collect();
                            break;
                    }
                    getFragmentManager().beginTransaction().replace(R.id.fragment2, selectedfragment).commit();
                    return true;
                }
            };




}

