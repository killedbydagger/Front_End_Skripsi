package com.example.skripsi;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ViewPortfolio extends Activity {


    ImageView img_portfolio;

    String imgUrl, imgUserId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_portfolio);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        img_portfolio = findViewById(R.id.img_portfolio);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        imgUrl = getIntent().getExtras().getString("IMG_URL");
        imgUserId = getIntent().getExtras().getString("IMG_USER_ID");

        try {
            URL url = new URL(imgUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
            img_portfolio.setImageBitmap(myBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }



        getWindow().setLayout((int)(width*.8), (int)(height*.4));
    }
}
