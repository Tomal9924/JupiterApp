package com.example.tomal.jupitarplatform;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.drm.DrmStore;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.util.Linkify;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.graphics.ImageFormat.JPEG;
import static com.example.tomal.jupitarplatform.MainActivity.*;


public class Details_Activity extends AppCompatActivity {

    private ImageView cameraImageView;
    private ImageView videoCameraImageView;
    private ImageView rattingImageView;
    private ArrayList<LeadCentralDataClass> arrayListLeadCentral = new ArrayList<>();
    ArrayList<LeadDetailsModel> leadDetailsModelArrayList = new ArrayList<>();
    private TextView callTypeText,dateText,stateText,callNumber,cityText,customerViewText,toolbarSubText, xViewNotes;
    private Button cameraButton;
    private static final int REQUEST_CAPTURE_IMAGE = 100;
    private int REQUEST_CODE_ASK_PERMISSIONS=123;
    int flag=0;
    TextView customerView,commentText;
    private Adapter leadDetailsAdapter;
    private int VIDEO_CAPTURED=100;
    private TextView subtext;
    Button submitBtn;
    EditText noteEditText;
    DashboardDataProfile dashboardDataProfile;
    private String val;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_);

        //findings
        cameraImageView = (findViewById(R.id.cameraImageView));
        videoCameraImageView = (findViewById(R.id.videoCameraView));
        rattingImageView = (findViewById(R.id.rattingImageView));
        customerViewText = (findViewById(R.id.customerViewText));
        callTypeText = (findViewById(R.id.callTypeText));
        callNumber = (findViewById(R.id.callNumber));
        toolbarSubText = (findViewById(R.id.toolbarSubText));

        stateText = (findViewById(R.id.stateText));
        dateText = (findViewById(R.id.dateText));

        noteEditText = findViewById(R.id.noteEditText);
        submitBtn = findViewById(R.id.noteSubmitBtn);

        submitButton();
        callButton();
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
        customerView = findViewById(R.id.customerViewText);
        if(getIntent().getStringExtra("Company Name").equals("null") || getIntent().getStringExtra("Company Name").equals("") || getIntent().getStringExtra("Company Name")== null) {
            customerView.setText(getIntent().getStringExtra("fname") + " ".concat(getIntent().getStringExtra("lname")));
        }
        else {
            customerView.setText(getIntent().getStringExtra("Company Name"));
        }
        commentText = findViewById(R.id.commentText);
        //getData();

        Linkify.addLinks(callNumber, Linkify.ALL);
        SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat myCreated = new SimpleDateFormat("MMM, dd EEE,yyyy hh:mm:ss a");
//        try {
//            dateText.setText(myCreated.format(fromUser.parse("")));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

        dashboardDataProfile = new DashboardDataProfile();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Lead_central_Activity.class));

            }
        });

        videoCameraImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Take_Video.class));
            }
        });

        //Camera
        cameraImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Take_Image.class));
            }
        });
        rattingImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Ratting_Activity.class)
                        .putExtra("fname", getIntent().getStringExtra("fname"))
                        .putExtra("lname", getIntent().getStringExtra("lname"))
                        .putExtra("state", getIntent().getStringExtra("state"))
                        .putExtra("city", getIntent().getStringExtra("city"))
                        .putExtra("Company Name", getIntent().getStringExtra("Company Name"))
                );
            }
        });

        val = callNumber.getText().toString().trim();
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



    private void callButton()
    {
        callNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent CallIntent = new Intent(Intent.ACTION_CALL);
                CallIntent.setData(Uri.parse("tel:" +(val)));
                startActivity(CallIntent);
            }
        });
    }

    private void submitButton() {
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {

                if(!noteEditText.getText().toString().isEmpty())
                {
                    OkHttpClient client = new OkHttpClient();

                    MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
                    RequestBody body = RequestBody.create(mediaType, "leadid=" +LEAD_ID+
                            "&api_userid=1562" +
                            "&add_note="+noteEditText.getText().toString()+
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

                            Details_Activity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (response.code() == 200) {
                                            Toast.makeText(Details_Activity.this, "Note Added Successfully", Toast.LENGTH_SHORT).show();
                                           // startActivity(new Intent(getApplicationContext(), CorrespondenceActivity.class).putExtra("Company ID", CompanyID));
                                        } else {
                                            Toast.makeText(Details_Activity.this, "Error Occurred: ", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }


                    });
                }
                else {
                    if(noteEditText.getText().toString().isEmpty())
                    {
                        noteEditText.setError("*Please enter valid note at least 3 word.");
                    }
                }
            }
        });

    }



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void getData(){
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://jupiter.centralstationmarketing.com/api/ios/getContactDetailUsingLead.php?leadid="+LEAD_ID)
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


    }



