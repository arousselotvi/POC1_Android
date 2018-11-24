package com.example.antoinerousselot.testvolley;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.antoinerousselot.gesture.DetectSwipeGestureListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.antoinerousselot.network.UrlConstants.POST_URL;

public class RetrofitUploadActivity extends AppCompatActivity implements GestureDetector.OnDoubleTapListener, SensorEventListener, View.OnClickListener {

    private static final String DEBUG_TAG = "Gestures";

    //These are the components used for the image upload
    private int REQ_CODE=100;
    private String image="data";
    private String imageName="Image.jpg";
    private TextView messageText;
    private Button uploadButton, btnselectpic, downloadButton, selectGetButton;
    private EditText etxtUpload;
    private ImageView imageview;
    private ProgressDialog dialog = null;
    private JSONObject jsonObject;
    private Uri selectedImageUri;
    private String urlGetImage;
    private String TAGDL = "DownloadImage";

    // This is the gesture detector compat instance.
    private GestureDetectorCompat gestureDetectorCompat = null;

    private long lastUpdate;
    private SensorManager sensorManager;
    private Sensor mSensor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrofit_upload);

        // Create the buttons and other components for the upload by click
        uploadButton = (Button)findViewById(R.id.button_upload);
        btnselectpic = (Button)findViewById(R.id.button_choose);
        downloadButton = (Button)findViewById(R.id.button_download);
        selectGetButton = (Button)findViewById(R.id.button_select_get);
        messageText  = (TextView)findViewById(R.id.textView);
        imageview = (ImageView)findViewById(R.id.imageView);
        etxtUpload = (EditText)findViewById(R.id.etxtUpload);

        btnselectpic.setOnClickListener(this);
        uploadButton.setOnClickListener(this);
        downloadButton.setOnClickListener(this);
        selectGetButton.setOnClickListener(this);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading Image...");
        dialog.setCancelable(false);

        jsonObject = new JSONObject();

        // Create a common gesture listener object.
        DetectSwipeGestureListener gestureListener = new DetectSwipeGestureListener();

        // Set activity in the listener.
        gestureListener.setActivity(this);

        // Create the gesture detector with the gesture listener.
        gestureDetectorCompat = new GestureDetectorCompat(this, gestureListener);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        lastUpdate = System.currentTimeMillis();

    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
        }

    }

    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];

        float accelationSquareRoot = (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        long actualTime = event.timestamp;
        if (accelationSquareRoot >= 6) //
        {
            lastUpdate = actualTime;
            Toast.makeText(this, "Acceleration detected", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if (this.gestureDetectorCompat.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        Log.d(DEBUG_TAG, "onDoubleTap: " + event.toString());
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent event) {
        Log.d(DEBUG_TAG, "onDoubleTapEvent: " + event.toString());
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        Log.d(DEBUG_TAG, "onSingleTapConfirmed: " + event.toString());
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_choose:
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQ_CODE);
                break;
            case R.id.button_upload:
                //dialog.show();
                uploadFile(selectedImageUri);
                break;
            case R.id.button_download:
                Log.v(TAGDL,"DL button pressed");
                urlGetImage = "http://node.melisse.ovh1.ec-m.fr/uploads/image.jpg";
                getRetrofitImage(urlGetImage);
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void uploadFile(Uri selectedImageUri) {
        // create upload service client
        FileUploadService service =
                ServiceGenerator.createService(FileUploadService.class);

        // use the FileUtils to get the actual file by uri
        File file = FileUtils.getFile(this, selectedImageUri);

        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse(getContentResolver().getType(selectedImageUri)),
                        file
                );

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("Image", file.getName(), requestFile);

        // add another part within the multipart request
        String descriptionString = "hello, this is description speaking";
        RequestBody description =
                RequestBody.create(
                        okhttp3.MultipartBody.FORM, descriptionString);

        // finally, execute the request
        Call<ResponseBody> call = service.upload(description, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                Log.v("Upload", "success");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imageview.setImageURI(selectedImageUri);
        }
    }

    void getRetrofitImage(String urlGetImage) {

        Log.v(TAGDL,"getRetrofitImage accessed");

        // create upload service client
        FileGetService service =
                ServiceGenerator.createService(FileGetService.class);

        Log.v(TAGDL,"FileGetService créé");

        Call<ResponseBody> call = service.getImageDetails();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                Log.v(TAGDL,"onResponse accessed");
                try {

                    Log.d(TAGDL, "Response came from server");

                    boolean FileDownloaded = DownloadImage(response.body());

                    Log.d(TAGDL, "Image is downloaded and saved : " + FileDownloaded);

                } catch (Exception e) {
                    Log.d(TAGDL, "There is an error");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAGDL, t.toString());
            }
        });
    }


    private boolean DownloadImage(ResponseBody body) {
        try {
            Log.d("DownloadImage", "Reading and writing file");
            InputStream in = null;
            FileOutputStream out = null;

            try {
                in = body.byteStream();
                out = new FileOutputStream(getExternalFilesDir(null) + File.separator + "AndroidTutorialPoint.jpg");
                int c;

                while ((c = in.read()) != -1) {
                    out.write(c);
                }
            }
            catch (IOException e) {
                Log.d("DownloadImage",e.toString());
                return false;
            }
            finally {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            }

            int width, height;
            ImageView image = (ImageView) findViewById(R.id.imageView);
            Bitmap bMap = BitmapFactory.decodeFile(getExternalFilesDir(null) + File.separator + "AndroidTutorialPoint.jpg");
            width = bMap.getWidth();
            height = bMap.getHeight();
            Bitmap bMap2 = Bitmap.createScaledBitmap(bMap, width, height, false);
            image.setImageBitmap(bMap2);

            return true;

        } catch (IOException e) {
            Log.d("DownloadImage",e.toString());
            return false;
        }
    }
}