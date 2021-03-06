package com.example.tomal.jupitarplatform;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.media.CamcorderProfile.get;
import static com.example.tomal.jupitarplatform.MainActivity.*;


public class Lead_central_Activity extends AppCompatActivity {
    ListView listView;
    private ArrayList<LeadCentralDataClass> arrayListLeadCentral = new ArrayList<>();
    private LeadCentralAdapter centralLeadAdapter;
    private FloatingActionButton fab;
    private Button dateRangeChange;
    private Button backButton;
    ProgressBar progressBar;
    private TextView subtext;
    DashboardDataProfile dashboardDataProfile;
    private SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lead_central_);

        System.out.println(SITE_ID);

        listView = findViewById(R.id.listView);
        fab = findViewById(R.id.floatingButton);
        dateRangeChange = findViewById(R.id.dateRangeButton);
        backButton = findViewById(R.id.back_button);
        subtext = (findViewById(R.id.toolbarSubText));
        progressBar = (findViewById(R.id.lead_central_progressBar));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbarText);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        dashboardDataProfile = new DashboardDataProfile();
        subtext.setText(COMPANY_NAME);

        if (fromDateRange) {
            getDateRangedData();
        } else {
            getData();
        }


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Create_lead_activity.class));
            }
        });
        dateRangeChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Date_Range.class));
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), central_station_lead_Activity.class));

            }
        });
        String webSIteName = dashboardDataProfile.getWebsite();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LEAD_ID = arrayListLeadCentral.get(position).getId();
                startActivity(new Intent(getApplicationContext(), Details_Activity.class)

                        .putExtra("state", String.valueOf(arrayListLeadCentral.get(position).getState()))
                        .putExtra("city", String.valueOf(arrayListLeadCentral.get(position).getCity()))
                        .putExtra("Company Name", String.valueOf(arrayListLeadCentral.get(position).getCompany()))
                        .putExtra("fname", String.valueOf(arrayListLeadCentral.get(position).getFname()))
                        .putExtra("lname", String.valueOf(arrayListLeadCentral.get(position).getLname())));
            }
        });
    }

    private void getData() {

        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "userid=" + SITE_ID + "&deviceToken=" + DEVICE_TOKEN + "&undefined=");
        Request request = new Request.Builder()
                .url("https://jupiter.centralstationmarketing.com/api/ios/LeadCentral.php?site_id=" + SITE_ID)
                .get()
                .addHeader("Cookie", COOKIE_FOR_API)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("cache-control", "no-cache")
                .addHeader("Postman-Token", "2f87c8bd-7141-43c3-a4e5-5494aead0bac")
                .build();
        client.newCall(request).enqueue(new Callback() {

            Handler mainHandler = new Handler(getApplicationContext().getMainLooper());

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String myResponse = response.body().string();

                mainHandler.post(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void run() {
                        try {
                            JSONObject json = new JSONObject(myResponse);
                            System.out.println(json.toString());
                            JSONArray jsonResult = json.getJSONArray("LeadCentral");
                            if (jsonResult != null) {

                                for (int i = 0; i < jsonResult.length(); i++) {
                                    JSONObject jsonObject = jsonResult.getJSONObject(i);

                                    arrayListLeadCentral.add(new LeadCentralDataClass(
                                            jsonObject.getString("id"),
                                            jsonObject.getString("fname"),
                                            jsonObject.getString("lname"),
                                            jsonObject.getString("company"),
                                            jsonObject.getString("phone"),
                                            jsonObject.getString("email"),
                                            jsonObject.getString("timestamp"),
                                            jsonObject.getString("set_appointment"),
                                            jsonObject.getString("sold"),
                                            jsonObject.getString("valid_other_desc"),
                                            jsonObject.getString("directory_member_id"),
                                            jsonObject.getString("billable"),
                                            jsonObject.getString("form_type"),
                                            jsonObject.getString("recording_url"),
                                            jsonObject.getString("call_duration"),
                                            jsonObject.getString("city"),
                                            jsonObject.getString("state"),
                                            jsonObject.getString("valid"),
                                            jsonObject.getString("customer"),
                                            jsonObject.getString("parent_id"),
                                            jsonObject.getString("url_tag"),
                                            jsonObject.getString("url_tracking_id"),
                                            jsonObject.getString("revenue"),
                                            jsonObject.getString("cc_comment"),
                                            jsonObject.getString("csm_comment"),
                                            jsonObject.getString("company_name")));


                                }
                                centralLeadAdapter = new LeadCentralAdapter(getApplicationContext(), arrayListLeadCentral);
                                listView.setAdapter(centralLeadAdapter);
                                progressBar.setVisibility(View.GONE);
                                listView.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_item, menu);
        MenuItem item = menu.findItem(R.id.search_button);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setBackgroundColor(Color.WHITE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.equals("")) {

                } else {

                    getSearchData(s.toLowerCase());
                }
                return false;

            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void getSearchData(String s) {
        ArrayList<LeadCentralDataClass> leads = new ArrayList<>();
        for (LeadCentralDataClass leadCentralDataClass : arrayListLeadCentral) {
            if (
                    leadCentralDataClass.getCompany().toLowerCase().contains(s)
                            || leadCentralDataClass.getFname().toLowerCase().contains(s)
                            || leadCentralDataClass.getLname().toLowerCase().contains(s)
                            || leadCentralDataClass.getCompany().toLowerCase().contains(s)
                            || leadCentralDataClass.getEmail().toLowerCase().contains(s)
                            || leadCentralDataClass.getState().toLowerCase().contains(s)
                            || leadCentralDataClass.getCity().toLowerCase().contains(s)) {
                leads.add(leadCentralDataClass);
            }
        }
        centralLeadAdapter = new LeadCentralAdapter(Lead_central_Activity.this, leads);
        listView.setAdapter(centralLeadAdapter);
        progressBar.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
    }

    private void getDateRangedData() {

        final int startDate = getIntent().getIntExtra("From", 0);
        final int endDate = getIntent().getIntExtra("To", 0);
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "userid=" + SITE_ID + "&deviceToken=" + DEVICE_TOKEN + "&undefined=");
        Request request = new Request.Builder()
                .url("https://jupiter.centralstationmarketing.com/api/ios/LeadCentral.php?site_id=" + SITE_ID)
                .get()
                .addHeader("Cookie", COOKIE_FOR_API)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("cache-control", "no-cache")
                .addHeader("Postman-Token", "2f87c8bd-7141-43c3-a4e5-5494aead0bac")
                .build();
        client.newCall(request).enqueue(new Callback() {

            Handler mainHandler = new Handler(getApplicationContext().getMainLooper());

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String myResponse = response.body().string();

                mainHandler.post(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void run() {
                        try {
                            JSONObject json = new JSONObject(myResponse);
                            JSONArray jsonResult = json.getJSONArray("LeadCentral");
                            if (jsonResult != null) {

                                for (int i = 0; i < jsonResult.length(); i++) {
                                    JSONObject jsonObject = jsonResult.getJSONObject(i);

                                    LeadCentralDataClass obj = new LeadCentralDataClass(
                                            jsonObject.getString("id"),
                                            jsonObject.getString("fname"),
                                            jsonObject.getString("lname"),
                                            jsonObject.getString("company"),
                                            jsonObject.getString("phone"),
                                            jsonObject.getString("email"),
                                            jsonObject.getString("timestamp"),
                                            jsonObject.getString("set_appointment"),
                                            jsonObject.getString("sold"),
                                            jsonObject.getString("valid_other_desc"),
                                            jsonObject.getString("directory_member_id"),
                                            jsonObject.getString("billable"),
                                            jsonObject.getString("form_type"),
                                            jsonObject.getString("recording_url"),
                                            jsonObject.getString("call_duration"),
                                            jsonObject.getString("city"),
                                            jsonObject.getString("state"),
                                            jsonObject.getString("valid"),
                                            jsonObject.getString("customer"),
                                            jsonObject.getString("parent_id"),
                                            jsonObject.getString("url_tag"),
                                            jsonObject.getString("url_tracking_id"),
                                            jsonObject.getString("revenue"),
                                            jsonObject.getString("cc_comment"),
                                            jsonObject.getString("csm_comment"),
                                            jsonObject.getString("company_name"));
                                    int currentDate = (Integer.parseInt(obj.getTimestamp().substring(0, 4)) * 10000) +
                                            (Integer.parseInt(obj.getTimestamp().substring(5, 7)) * 100) +
                                            (Integer.parseInt(obj.getTimestamp().substring(8, 10)));
                                    if (currentDate >= startDate && currentDate <= endDate) {
                                        arrayListLeadCentral.add(obj);
                                    }
                                }
                                centralLeadAdapter = new LeadCentralAdapter(getApplicationContext(), arrayListLeadCentral);
                                listView.setAdapter(centralLeadAdapter);
                                progressBar.setVisibility(View.GONE);
                                listView.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
}
