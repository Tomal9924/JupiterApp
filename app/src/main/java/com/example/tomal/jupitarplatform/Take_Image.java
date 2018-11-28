package com.example.tomal.jupitarplatform;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationListener;
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
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.tomal.jupitarplatform.MainActivity.*;

public class Take_Image extends AppCompatActivity {

    private static final int REQUEST_CAPTURE_IMAGE = 1;
    private ImageView takeImage;
    private Uri path;
    private  Button gallerybtn;
    int flag=0;
    EditText photoTitleText,descriptionText;
    private  Button addPhotoBtn;
    private static final int PICK_IMAGE = 100;
    private Uri imageUri;
    private int VIDEO_CAPTURED=100;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    Button submit;

    byte[] bytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take__image);

        takeImage=(findViewById(R.id.tookImageView));
        gallerybtn=(findViewById(R.id.gallerybtn));
        addPhotoBtn=(findViewById(R.id.addPhotoBtn));
        submit=(findViewById(R.id.sendImage));
        photoTitleText=(findViewById(R.id.photoTitleText));
        descriptionText=(findViewById(R.id.descriptionText));

        //Bitmap
        /*Bitmap bitmap = (Bitmap) getIntent().getParcelableExtra("BitmapImage");
        takeImage.setImageBitmap(bitmap);*/

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitle("CENTRAL \nStation Marketing");
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbarText);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);



        //Back Button
        Button backButton=findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            { startActivity(new Intent(getApplicationContext(), Details_Activity.class).putExtra("Date", "Date"));}
        });
        addPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {openCamera();

            }
        });

        gallerybtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {openGallery();

            }
        });

        openCamera();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitData();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void submitData() {
        OkHttpClient client = new OkHttpClient();
        System.out.println(Base64.encodeToString(bytes, Base64.NO_WRAP));

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("leadid", LEAD_ID)
                .addFormDataPart("file_type", "image")
                .addFormDataPart("file_title", photoTitleText.getText().toString())
                .addFormDataPart("file_location", "Versailles,KY")
                .addFormDataPart("file_desc", descriptionText.getText().toString())
                .addFormDataPart("ufile", Base64.encodeToString(bytes, Base64.NO_WRAP))
                .build();

        Request request = new Request.Builder()
                .url("https://jupiter.centralstationmarketing.com/api/ios/apifileupload.php")
                .post(requestBody)
                .addHeader("Cookie", COOKIE_FOR_API)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("cache-control", "no-cache")
                .addHeader("Postman-Token", "bb669a0f-cffa-46dd-951f-2c401e01d05a")
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
//                            JSONObject json = new JSONObject(myResponse);
                            System.out.println(myResponse);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    //Open Camera
    private void openCamera() {
        flag=2;
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, 1);
    }


    //Open Gallery
    private void openGallery() {
        flag=1;
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        //flag = 1;
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {

            if (flag == 1) {
                if (resultCode == RESULT_OK && requestCode == PICK_IMAGE)
                {
                    imageUri = data.getData();
                    takeImage.setImageURI(imageUri);
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArray);
                    bytes = byteArray.toByteArray();
                }
            } else if (flag == 2) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                takeImage.setImageBitmap(bitmap);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 10, baos);
                bytes = baos.toByteArray();



            }
        } catch (Exception e) {
            Toast.makeText(this, "Exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


}
