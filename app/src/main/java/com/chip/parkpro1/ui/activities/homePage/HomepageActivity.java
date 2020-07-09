package com.chip.parkpro1.ui.activities.homePage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.chip.parkpro1.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import io.realm.internal.IOException;

//import static com.chip.parkpro1.ui.activities.homePage.homefragment.getAssetJsonData;


public class HomepageActivity extends AppCompatActivity {
    Button scanBtn;
    private TextView formatTxt, contentTxt;
    private Object fragment2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
      //  String Data = getAssetJsonData(HomepageActivity.this, "coronaquiz.json");


        scanBtn = findViewById(R.id.scan_button);
        contentTxt = findViewById(R.id.scan_content);


        BottomNavigationView bottnav = findViewById(R.id.nav_menu);
        bottnav.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment,
                new homefragment()).commit();


    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedfragment = null;

                    switch (item.getItemId()) {
                        case R.id.navigation_home:

                            selectedfragment = new homefragment();
                            break;

                        case R.id.navigation_car:

                            selectedfragment = new fragment2();
                            break;

                        // case R.id.navigation_history:

                        //     selectedfragment = new homefragment();
                        //     break;

                        case R.id.navigation_settings:

                            selectedfragment = new settingfragment();
                            break;

                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment, selectedfragment).commit();
                    return true;
                }
            };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }


    public String LoadFromAsset(){
        String json=null;

        try {
            InputStream in = this.getAssets().open("Yaahla.json");
            int size=in.available();
            byte[]buffer=new byte[size];
            in.read(buffer);
            in.close();
            json=new String(buffer,"UTF-8");

        }catch (IOException e){
            e.printStackTrace();
            return null;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return json;
    }

}



