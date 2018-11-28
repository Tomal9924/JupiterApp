package com.example.tomal.jupitarplatform;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.util.Linkify;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.tomal.jupitarplatform.MainActivity.*;


public class DetailsActivity extends AppCompatActivity {

    private ImageView cameraImageView;
    private ImageView videoCameraImageView;
    private ImageView rattingImageView;
    private TextView callTypeText, dateText, stateText, callNumber, customerViewText, toolbarSubText, xViewNotes;
    int flag = 0;
    TextView commentText;
    Button submitBtn;
    EditText noteEditText;
    DashboardProfileModel dashboardProfileModel;
    private String val;

    LeadCentralModel model;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_);

        Gson gson = new Gson();
        model = gson.fromJson(getIntent().getStringExtra("myjson"), LeadCentralModel.class);
        //findings
        cameraImageView = (findViewById(R.id.cameraImageView));
        videoCameraImageView = (findViewById(R.id.videoCameraView));
        rattingImageView = (findViewById(R.id.rattingImageView));
        customerViewText = (findViewById(R.id.customerViewText));
        callTypeText = (findViewById(R.id.callTypeText));
        callNumber = (findViewById(R.id.callNumber));
        toolbarSubText = (findViewById(R.id.toolbarSubText));
        commentText = findViewById(R.id.commentText);
        stateText = (findViewById(R.id.stateText));
        dateText = (findViewById(R.id.dateText));
        noteEditText = findViewById(R.id.noteEditText);
        submitBtn = findViewById(R.id.noteSubmitBtn);

        submitButton();

        toolbarSubText.setText(COMPANY_NAME);
        noteEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                submitBtn.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                submitBtn.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean isReady = noteEditText.getText().toString().length() > 3;
                submitBtn.setEnabled(isReady);
            }
        });
        noteEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        if (model.getCompanyName().equals("null") || model.getCompanyName().equals("") || model.getCompanyName() == null) {
            customerViewText.setText(model.getFname() + " ".concat(model.getLname()));
        } else {
            customerViewText.setText(model.getCompanyName());
        }
        callTypeText.setText(model.getFormType());
        callNumber.setText(model.getPhone());
        stateText.setText(model.getCity() + ", " +model.getState());
        SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat myCreated = new SimpleDateFormat("dd MMM, yyyy hh:mm:ss a");
        try {
            dateText.setText(myCreated.format(fromUser.parse(model.getTimestamp())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        commentText.setText(model.getCcComment());

        dashboardProfileModel = new DashboardProfileModel();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
                dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
            }
        });

        videoCameraImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), TakeVideoActivity.class));
            }
        });

        //Camera
        cameraImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), TakeImageActivity.class));
            }
        });
        rattingImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RatingActivity.class)
                        .putExtra("fname", getIntent().getStringExtra("fname"))
                        .putExtra("lname", getIntent().getStringExtra("lname"))
                        .putExtra("state", getIntent().getStringExtra("state"))
                        .putExtra("city", getIntent().getStringExtra("city"))
                        .putExtra("Company Name", getIntent().getStringExtra("Company Name"))
                );
            }
        });
        callNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent CallIntent = new Intent(Intent.ACTION_DIAL);
                CallIntent.setData(Uri.parse("tel:" + (model.getPhone())));
                startActivity(CallIntent);
            }
        });
        getData();

        xViewNotes = findViewById(R.id.details_view_notes);
        xViewNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ViewNotesActivity.class)
                        .putExtra("fname", getIntent().getStringExtra("fname"))
                        .putExtra("lname", getIntent().getStringExtra("lname"))
                        .putExtra("state", getIntent().getStringExtra("state"))
                        .putExtra("city", getIntent().getStringExtra("city"))
                        .putExtra("Company Name", getIntent().getStringExtra("Company Name"))
                );
            }
        });
    }

    private void submitButton() {
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {

                if (!noteEditText.getText().toString().isEmpty()) {
                    OkHttpClient client = new OkHttpClient();

                    MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
                    RequestBody body = RequestBody.create(mediaType, "leadid=" + LEAD_ID +
                            "&api_userid=1562" +
                            "&add_note=" + noteEditText.getText().toString() +
                            "&undefined=");

                    Request request = new Request.Builder()
                            .url("https://jupiter.centralstationmarketing.com/api/ios/AddNote.php?Content-Type=application/x-www-form-urlencoded")
                            .post(body)
                            .addHeader("Cookie", COOKIE_FOR_API)
                            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                            .addHeader("add_note", noteEditText.getText().toString())
                            .addHeader("cache-control", "no-cache")
                            .addHeader("Postman-Token", "d359f005-d597-462b-a971-9bef66562c1d")
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Toast.makeText(getApplicationContext(), "Exception occured : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onResponse(Call call, final Response response) throws IOException {

                            DetailsActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (response.code() == 200) {
                                            Toast.makeText(DetailsActivity.this, "Note Added Successfully", Toast.LENGTH_SHORT).show();
                                            // startActivity(new Intent(getApplicationContext(), CorrespondenceActivity.class).putExtra("Company ID", CompanyID));
                                        } else {
                                            Toast.makeText(DetailsActivity.this, "Error Occurred: ", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }


                    });
                } else {
                    if (noteEditText.getText().toString().isEmpty()) {
                        noteEditText.setError("*Please enter valid note at least 3 word.");
                    }
                }
            }
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void getData() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://jupiter.centralstationmarketing.com/api/ios/getContactDetailUsingLead.php?leadid=" + LEAD_ID)
                .get()
                .addHeader("Cookie", COOKIE_FOR_API)
                .addHeader("cache-control", "no-cache")
                .addHeader("Postman-Token", "078962c6-b293-4c04-819e-57810f1d6093")
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
                            json.getInt("d_zip");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}



