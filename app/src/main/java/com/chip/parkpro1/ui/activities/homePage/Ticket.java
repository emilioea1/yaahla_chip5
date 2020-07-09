package com.chip.parkpro1.ui.activities.homePage;

//import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DigitalClock;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.chip.parkpro1.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Ticket extends AppCompatActivity {

    TextView date,from,to,amount1,to1,from1;
    Button print;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);
        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
        date=findViewById(R.id.date);
        from=findViewById(R.id.from);
        to=findViewById(R.id.to);
        amount1=findViewById(R.id.amount1);
        Intent in = getIntent();
        String tv1= in.getExtras().getString("Amount");
        amount1.setText(tv1+" "+"L.L.");
        from1=findViewById(R.id.from1);
        from1.setText(currentDateTimeString);
        to1=findViewById(R.id.to1);
        to1.setText(currentDateTimeString);
        print=findViewById(R.id.Print);
        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Ticket.this,"Ticket Printed",Toast.LENGTH_SHORT).show();
                finish();
            }
        });








    }

}
