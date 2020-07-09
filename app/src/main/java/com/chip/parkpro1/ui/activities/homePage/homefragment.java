package com.chip.parkpro1.ui.activities.homePage;

import android.content.Context;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.textclassifier.TextLinks;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.chip.parkpro1.R;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

public class homefragment extends Fragment {

    Spinner carCode;
    EditText plateNumber;
    public static final String errorRequire = "This field is required";
    View edit_view_line_top2;
    Button btn_cancel, issue_fine, check;
    TextView data1, data2, data3, data4;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);


        carCode = v.findViewById(R.id.carCode_spinner);
        plateNumber = v.findViewById(R.id.plateNumber_editText);
        check = v.findViewById(R.id.Check_plate_number);
        btn_cancel = v.findViewById(R.id.btn_cancel);
        issue_fine = v.findViewById(R.id.issue_fine);
        btn_cancel.setVisibility(View.INVISIBLE);
        issue_fine.setVisibility(View.INVISIBLE);
        data1 = v.findViewById(R.id.data1);
        data1.setVisibility(View.INVISIBLE);
        data2 = v.findViewById(R.id.data2);
        data2.setVisibility(View.INVISIBLE);
        data3 = v.findViewById(R.id.data3);
        data3.setVisibility(View.INVISIBLE);
        data4 = v.findViewById(R.id.data4);
        data4.setVisibility(View.INVISIBLE);
        edit_view_line_top2 = v.findViewById(R.id.edit_view_line_top2);
        edit_view_line_top2.setVisibility(View.INVISIBLE);
        //requestQueue=Volley.newRequestQueue(getActivity());

        check.setOnClickListener((View view) -> {


            if (plateNumber.getText() == null || plateNumber.getText().toString().equals("")) {
                plateNumber.setError(errorRequire);
                //data.setText("Put Plate Number Please");
                //data.setTextColor(getResources().getColor(R.color.sample_red));
                return;
            } else {
                onclick();
            }


        });


        btn_cancel.setOnClickListener((View view) -> {
            clearbtn();
        });

        issue_fine.setOnClickListener((View view) -> {
            Toast.makeText(getActivity(), "Issued Fine", Toast.LENGTH_LONG).show();
        });


        return v;
    }


    private void clearbtn() {
        // edit_view_line_top3.setVisibility(View.INVISIBLE);
        btn_cancel.setVisibility(View.INVISIBLE);
        issue_fine.setVisibility(View.INVISIBLE);
        data1.setVisibility(View.INVISIBLE);
        data2.setVisibility(View.INVISIBLE);
        data3.setVisibility(View.INVISIBLE);
        data4.setVisibility(View.INVISIBLE);
        edit_view_line_top2.setVisibility(View.INVISIBLE);
        plateNumber.setError(null);
        plateNumber.setText(null);
        carCode.setSelection(0);


    }


    private void onclick() {
        String text = plateNumber.getText().toString();
        if (text.equals("241969")) {
            data1.setVisibility(View.VISIBLE);
            data2.setVisibility(View.VISIBLE);
            data3.setVisibility(View.VISIBLE);
            data4.setVisibility(View.VISIBLE);
            data1.setText("Name : User1");
            data2.setText("car : infinity");
            data3.setText("CheckIn : 08:15" + "--" + "CheckOut: 09:30");
            data4.setText("Paid : 02:00");
            btn_cancel.setVisibility(View.VISIBLE);
            //issue_fine.setVisibility(View.VISIBLE);
            edit_view_line_top2.setVisibility(View.VISIBLE);
        }else if (text.equals("111222")) {
            data1.setVisibility(View.VISIBLE);
            data2.setVisibility(View.VISIBLE);
            data3.setVisibility(View.VISIBLE);
            data4.setVisibility(View.VISIBLE);
            data1.setText("Name : User2");
            data2.setText("car : Mercedes");
            data3.setText("CheckIn : 10:15" + "--" + "CheckOut : 13:00");
            data4.setText("Paid : 01:00");
            btn_cancel.setVisibility(View.VISIBLE);
            issue_fine.setVisibility(View.VISIBLE);
            edit_view_line_top2.setVisibility(View.VISIBLE);
        }else if (text.equals("010203")) {
            data1.setVisibility(View.VISIBLE);
            data2.setVisibility(View.VISIBLE);
            data3.setVisibility(View.VISIBLE);
            data4.setVisibility(View.VISIBLE);
            data1.setText("Name : User3");
            data2.setText("car : BMW");
            data3.setText("CheckIn : 12:55" + "--" + "CheckOut : 13:40");
            data4.setText("Paid :00:15");
            // edit_view_line_top3.setVisibility(View.VISIBLE);
            btn_cancel.setVisibility(View.VISIBLE);
            issue_fine.setVisibility(View.VISIBLE);
            edit_view_line_top2.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(getActivity(), "User Not Found", Toast.LENGTH_LONG).show();
        }


    }

}
