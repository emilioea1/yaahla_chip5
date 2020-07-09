package com.chip.parkpro1.ui.activities.homePage;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.chip.parkpro1.R;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static androidx.databinding.DataBindingUtil.setContentView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class Collect extends Fragment implements OnClickListener {

    private Button scanBtn;
    private TextView contentTxt, txt1;
    View edit_view_line_top;
    Button issue, Collect,clear;


    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.collect, container, false);

        scanBtn = v.findViewById(R.id.scan_button);
        Collect = v.findViewById(R.id.Collect);
        clear=v.findViewById(R.id.clear);
        clear.setVisibility(View.INVISIBLE);
        contentTxt = v.findViewById(R.id.scan_content);
        edit_view_line_top = v.findViewById(R.id.edit_view_line_top);
        edit_view_line_top.setVisibility(View.INVISIBLE);
        txt1 = v.findViewById(R.id.txt1);
        txt1.setVisibility(View.INVISIBLE);
        issue = v.findViewById(R.id.issue);
        issue.setVisibility(View.INVISIBLE);

        scanBtn.setOnClickListener(this);
        Collect.setOnClickListener((View view) -> {
            String text = contentTxt.getText().toString();
            if (text.equals("1234567890")) {
                txt1.setVisibility(View.VISIBLE);
                edit_view_line_top.setVisibility(View.VISIBLE);
                issue.setVisibility(View.VISIBLE);
                clear.setVisibility(View.VISIBLE);
                txt1.setText("10,000 L.L.");

            } else {
                txt1.setVisibility(View.VISIBLE);
                edit_view_line_top.setVisibility(View.VISIBLE);
               // issue.setVisibility(View.VISIBLE);
                clear.setVisibility(View.VISIBLE);
                txt1.setText("No Issue");
            }
        });

        issue.setOnClickListener((View view) -> {

            if (view.getId() == R.id.issue) {
                txt1.setVisibility(View.INVISIBLE);
                edit_view_line_top.setVisibility(View.INVISIBLE);
                issue.setVisibility(View.INVISIBLE);
                clear.setVisibility(View.INVISIBLE);
                contentTxt.setText(null);
                Toast.makeText(getActivity(), "Money Collected", Toast.LENGTH_LONG).show();

            }
        });

        clear.setOnClickListener((View view) -> {

           clear.setVisibility(View.INVISIBLE);
           issue.setVisibility(View.INVISIBLE);
           txt1.setVisibility(View.INVISIBLE);
           contentTxt.setText(null);
           edit_view_line_top.setVisibility(View.INVISIBLE);


        });


        return v;
    }


    public void onClick(View view) {
        if (view.getId() == R.id.scan_button) {
            IntentIntegrator scanIntegrator = new IntentIntegrator(getActivity());
            scanIntegrator.initiateScan();


        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            contentTxt.setText(scanContent);
        } else {
            Toast toast = Toast.makeText(getActivity(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }


}
