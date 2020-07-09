package com.chip.parkpro1.ui.activities.homePage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.chip.parkpro1.R;

import static com.chip.parkpro1.ui.activities.homePage.homefragment.errorRequire;

public class Sell extends Fragment {
    Button SellTicket;
    EditText amount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.sell, container, false);

        amount=v.findViewById(R.id.amount);
        SellTicket=v.findViewById(R.id.SellTicket);
        SellTicket.setOnClickListener((View view) -> {
            if (amount.getText() == null || amount
                    .getText().toString().equals("")) {
                amount.setError(errorRequire);
            }else {


                Intent i = new Intent(getActivity(), Ticket.class);
                i.putExtra("Amount", amount.getText().toString());
                startActivity(i);
                amount.setText(null);
            }
        });

        return v;
    }


}
