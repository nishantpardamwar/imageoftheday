package contrailtech.com.imageoftheday;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    private Button setAsWallpaper;
    private ImageView image;
    private ImageView btnLeft, btnRight;
    private TextView tvName, tvDate, tvDesc;
    private AsyncHttpClient httpClient;
    private AsyncHttpClient imageClient;
    private List<ItemClass> itemList;
    private int listSize = 0;
    private int currentItemNo = 0;
    private ProgressBar progressBar;
    private Bitmap bitmap;
    private String serverUrl = "http://192.168.1.109:8000/";
    private AsyncTask<Void,Void,Boolean> setWallpaperInBackgroundTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        httpClient = new AsyncHttpClient();
        imageClient = new AsyncHttpClient();
        setContentView(R.layout.activity_main);
        initializeUI();
        initializeOnClickListeners();
        getDataFromServer();
    }

    private void initializeUI() {
        image = (ImageView) findViewById(R.id.iv_image_display);
        btnLeft = (ImageView) findViewById(R.id.btn_left);
        btnRight = (ImageView) findViewById(R.id.btn_right);
        tvName = (TextView) findViewById(R.id.tv_image_name);
        tvDate = (TextView) findViewById(R.id.tv_image_date);
        tvDesc = (TextView) findViewById(R.id.tv_image_desc);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        setAsWallpaper = (Button) findViewById(R.id.btn_set_as_wallpaper);
        btnLeft.setVisibility(View.INVISIBLE);
        btnRight.setVisibility(View.INVISIBLE);
        setAsWallpaper.setVisibility(View.INVISIBLE);
    }

    private void initializeOnClickListeners() {
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRight.setClickable(false);
                currentItemNo++;

                tvName.setText(itemList.get(currentItemNo).getName());
                tvDate.setText(getDate(itemList.get(currentItemNo).getDate()));
                tvDesc.setText(itemList.get(currentItemNo).getDescription());
                getImage(itemList.get(currentItemNo).getImageUrl());

                if ((currentItemNo + 1) == listSize)
                    btnRight.setVisibility(View.INVISIBLE);

                if (currentItemNo > 0)
                    btnLeft.setVisibility(View.VISIBLE);
                btnRight.setClickable(true);
            }
        });
        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLeft.setClickable(false);
                currentItemNo--;

                tvName.setText(itemList.get(currentItemNo).getName());
                tvDate.setText(getDate(itemList.get(currentItemNo).getDate()));
                tvDesc.setText(itemList.get(currentItemNo).getDescription());
                getImage(itemList.get(currentItemNo).getImageUrl());

                if ((currentItemNo - 1) < 0)
                    btnLeft.setVisibility(View.INVISIBLE);

                if ((currentItemNo + 1) < listSize)
                    btnRight.setVisibility(View.VISIBLE);
                btnLeft.setClickable(true);
            }
        });

        setAsWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAsWallpaper.setClickable(false);

                setWallpaperInBackgroundTask = new AsyncTask<Void, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(Void... params) {
                        try {
                            WallpaperManager wm = WallpaperManager.getInstance(MainActivity.this);
                            wm.setBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return true;
                    }

                    @Override
                    protected void onPostExecute(Boolean aBoolean) {
                        super.onPostExecute(aBoolean);
                        setAsWallpaper.setClickable(true);
                        Toast.makeText(MainActivity.this, "Wallpaper changed Successfully", Toast.LENGTH_SHORT).show();
                    }
                };
                setWallpaperInBackgroundTask.execute();
            }
        });
    }

    private void getDataFromServer() {
        httpClient.get(serverUrl + "imageofthedayjson", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i("Main", "Failed\n" + responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.i("Main", "Success\n" + responseString);
                try {
                    JSONObject itemListObject = new JSONObject(responseString);
                    itemList = Parser.parseData(itemListObject);
                    if (itemList != null) {
                        btnLeft.setVisibility(View.VISIBLE);
                        btnRight.setVisibility(View.VISIBLE);
                        listSize = itemList.size();
                        Log.i("MAIN", "" + listSize);
                        currentItemNo = 0;
                        Log.i("MAIN", "" + currentItemNo);
                        tvName.setText(itemList.get(currentItemNo).getName());
                        tvDate.setText(getDate(itemList.get(currentItemNo).getDate()));
                        tvDesc.setText(itemList.get(currentItemNo).getDescription());
                        getImage(itemList.get(currentItemNo).getImageUrl());
                        btnLeft.setVisibility(View.INVISIBLE);
                        if ((currentItemNo + 1) == listSize)
                            btnRight.setVisibility(View.INVISIBLE);
                    } else {
                        btnLeft.setVisibility(View.INVISIBLE);
                        btnRight.setVisibility(View.INVISIBLE);
                        setAsWallpaper.setVisibility(View.INVISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getImage(String imageUrl) {
        setAsWallpaper.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        image.setImageBitmap(null);
        imageClient.cancelAllRequests(true);
        imageClient.get(this, serverUrl + imageUrl, new BinaryHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] binaryData) {
                bitmap = null;
                bitmap = BitmapFactory.decodeByteArray(binaryData, 0, binaryData.length);
                progressBar.setVisibility(View.INVISIBLE);
                setAsWallpaper.setVisibility(View.VISIBLE);
                image.setImageBitmap(bitmap);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] binaryData, Throwable error) {
                Log.i("Main", "Failed\n" + error);
                progressBar.setVisibility(View.INVISIBLE);
                setAsWallpaper.setVisibility(View.INVISIBLE);
            }
        });
    }

    public String getDate(long t) {
        if (t != 0) {
            Date date = new Date(t);

            String day_number = (String) android.text.format.DateFormat.format("dd", date);
            String month = (String) android.text.format.DateFormat.format("MMM", date);
            String year = (String) android.text.format.DateFormat.format("yyyy", date);
            return day_number + " " + month + " " + year;
        }
        return null;
    }
}
