package com.example.tomal.jupitarplatform;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import static com.example.tomal.jupitarplatform.MainActivity.*;


public class Date_Range extends AppCompatActivity {
    EditText editText;
    private Button backButton;
    TextView toolbarSubText;
    DatePicker xDateFrom, xDateTo;

    Button xBack, xSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date__range);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbarText);
        toolbarSubText = (TextView) toolbar.findViewById(R.id.toolbarSubText);
        toolbarSubText.setText(COMPANY_NAME);

        xBack = findViewById(R.id.back_button);
        xSubmit = findViewById(R.id.date_range_submit);
        xDateFrom = findViewById(R.id.date_range_from);
        xDateTo = findViewById(R.id.date_range_to);

        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        xDateTo.updateDate(mYear, mMonth, mDay);
        xDateFrom.updateDate(mYear, mMonth - 1, mDay);

        xBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Lead_central_Activity.class).putExtra("ID", getIntent().getStringExtra("ID")));
            }
        });
        xSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int fromDate = (xDateFrom.getYear() * 10000) + ((xDateFrom.getMonth() + 1) * 100) + xDateFrom.getDayOfMonth();
                int toDate = (xDateTo.getYear() * 10000) + ((xDateTo.getMonth() + 1) * 100) + xDateTo.getDayOfMonth();
                if (fromDate > toDate) {
                    Toast.makeText(Date_Range.this, "Incorrect Date Range", Toast.LENGTH_SHORT).show();
                } else {
                    fromDateRange = true;
                    startActivity(new Intent(getApplicationContext(), Lead_central_Activity.class).putExtra("From", fromDate).putExtra("To", toDate).putExtra("ID", getIntent().getStringExtra("ID")));
                }
            }
        });
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromDateRange = true;
                startActivity(new Intent(getApplicationContext(), Lead_central_Activity.class).putExtra("ID", getIntent().getStringExtra("website")));


            }
        });


    }
}
