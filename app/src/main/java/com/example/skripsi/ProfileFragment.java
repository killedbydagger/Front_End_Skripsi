package com.example.skripsi;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.ProgressBar;

public class ProfileFragment extends Fragment implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;

    ViewDialog viewDialog;

    Button btn_edit, btn_addPortfolio, btn_viewfile;
    ImageView img_slideMenuProfile, img_history, img_profile;
    TextView tv_nama, tv_dob, tv_pendidikanTerakhir, tv_lokasi, tv_desc, tv_noPhoto;
    ProgressBar pbLoading;
    SessionManager sessionManager;

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 10001;

    private RecyclerView mList;

    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private List<Portfolio> portfolioList;
    private RecyclerView.Adapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        pbLoading = v.findViewById(R.id.pb_loading);

        mList = v.findViewById(R.id.rv_photo);

        tv_noPhoto = v.findViewById(R.id.tv_noPhoto);

        portfolioList = new ArrayList<>();
        adapter = new PortfolioAdapter(getContext(),portfolioList);

        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        dividerItemDecoration = new DividerItemDecoration(mList.getContext(), linearLayoutManager.getOrientation());

//        mList.setHasFixedSize(true);
//        mList.addItemDecoration(dividerItemDecoration);
//        mList.setAdapter(adapter);
//        mList.setLayoutManager(new GridLayoutManager(getContext(), 3));

        mList.setHasFixedSize(true);
        mList.setAdapter(adapter);
        mList.setLayoutManager(new GridLayoutManager(getContext(), 3));



        img_profile = v.findViewById(R.id.img_profile);

        btn_edit = (Button) v.findViewById(R.id.btn_edit);
        btn_edit.setOnClickListener(this);

        drawer = v.findViewById(R.id.drawer_layout);
        img_slideMenuProfile = v.findViewById(R.id.img_slideMenuProfile);
        img_slideMenuProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.END);
            }
        });

        tv_nama = v.findViewById(R.id.tv_nama);
        tv_dob = v.findViewById(R.id.tv_dob);
        tv_pendidikanTerakhir = v.findViewById(R.id.tv_pendidikanTerakhir);
        tv_lokasi = v.findViewById(R.id.tv_lokasi);
        tv_desc = v.findViewById(R.id.tv_desc);

        NavigationView navigationView = v.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        sessionManager = new SessionManager(getActivity().getApplicationContext());

        HashMap<String, String> user = sessionManager.getUserDetail();
        String mFirstName = user.get(sessionManager.FIRST_NAME);
        String mLastName = user.get(sessionManager.LAST_NAME);
        String mDob = user.get(sessionManager.DOB);
        String mPendidikanTerakhir = user.get(sessionManager.EDUCATION_NAME);
        String mLokasi = user.get(sessionManager.LOCATION_NAME);
        String mDescription = user.get(sessionManager.DESCRIPTION);
        final String status = user.get(sessionManager.STATUS);
        final String mCV = user.get(sessionManager.CV_URL);

        tv_nama.setText(mFirstName + " " + mLastName);

//        String[] splitDob = mDob.split("\\s+");
//        System.out.println(splitDob[0]);
//        tv_dob.setText(splitDob[0]);

        String date = user.get(sessionManager.DOB);
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat output = new SimpleDateFormat("dd MMMM yyyy");
        Date oneWayTripDate = null;
        try {
            oneWayTripDate = input.parse(date);
            tv_dob.setText(output.format(oneWayTripDate));
            System.out.println("ini tanggalnya: " + oneWayTripDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        btn_viewfile = v.findViewById(R.id.btn_viewfile);
        btn_viewfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCV.equals(null)||mCV.equals("null")){
                    Toast.makeText(getContext(), "No file can be viewed", Toast.LENGTH_LONG).show();
                }
                else{
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mCV));
                    startActivity(browserIntent);
                }
            }
        });

        tv_pendidikanTerakhir.setText(mPendidikanTerakhir);
        tv_lokasi.setText(mLokasi);
        tv_desc.setText(mDescription);

        btn_addPortfolio = v.findViewById(R.id.btn_addPortfolio);
        btn_addPortfolio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int item = adapter.getItemCount();
                if(status.equals("Premium")){
                    if(item == 15){
                        Toast.makeText(view.getContext(), "Already reach limit", Toast.LENGTH_LONG).show();
                    }
                    else{
                        Intent portfolioIntent = new Intent(view.getContext(), AddPortfolio.class);
                        startActivity(portfolioIntent);
                    }
                }
                else{
                    if(item == 3){
                        Toast.makeText(view.getContext(), "Already reach limit, need more ? upgrade to PREMIUM member", Toast.LENGTH_LONG).show();
                    }
                    else{
                        Intent portfolioIntent = new Intent(view.getContext(), AddPortfolio.class);
                        startActivity(portfolioIntent);
                    }
                }
            }
        });

        img_history = v.findViewById(R.id.img_history);
        img_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent historyIntent = new Intent(v.getContext(), ApplicationHistory.class);
                startActivity(historyIntent);
            }
        });

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here

        }

        String foto = user.get(sessionManager.IMG_URL);

        if (foto.equals(null) || foto.equals("null")) {
            img_profile.setImageResource(R.drawable.logo1);
        }
        else{
            try {
                URL url = new URL(user.get(sessionManager.IMG_URL));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
                img_profile.setImageBitmap(myBitmap);
                img_profile.setScaleType(ImageView.ScaleType.FIT_XY);

//                System.out.println("img url :" + user.get(sessionManager.IMG_URL));
//                URL url = new URL(user.get(sessionManager.IMG_URL));
//                Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//                img_profile.setImageBitmap(bmp);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

//        try {
//            loadImagePortfolio(user.get(sessionManager.ID));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        return v;

    }



    @Override
    public void onResume() {
        super.onResume();

        viewDialog = new ViewDialog(getActivity());
        viewDialog.showDialog();

        HashMap<String, String> user = sessionManager.getUserDetail();
        String mFirstName = user.get(sessionManager.FIRST_NAME);
        String mLastName = user.get(sessionManager.LAST_NAME);
        String mDob = user.get(sessionManager.DOB);
        String mPendidikanTerakhir = user.get(sessionManager.EDUCATION_NAME);
        String mLokasi = user.get(sessionManager.LOCATION_NAME);
        String mDescription = user.get(sessionManager.DESCRIPTION);
        String mImage = user.get(sessionManager.IMG_URL);

        tv_nama.setText(mFirstName + " " + mLastName);
//        String[] splitDob = mDob.split("\\s+");
//        System.out.println(splitDob[0]);
//        tv_dob.setText(splitDob[0]);

        String date = user.get(sessionManager.DOB);
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat output = new SimpleDateFormat("dd MMMM yyyy");
        Date oneWayTripDate = null;                 // parse input
        try {
            oneWayTripDate = input.parse(date);
            tv_dob.setText(output.format(oneWayTripDate));    // format output
            System.out.println("ini tanggalnya: " + oneWayTripDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here

        }

        if (mImage == null || mImage.equals("null")) {
            img_profile.setImageResource(R.drawable.logo1);
        }
        else{
            try {
                URL url = new URL(user.get(sessionManager.IMG_URL));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
                img_profile.setImageBitmap(myBitmap);
                img_profile.setScaleType(ImageView.ScaleType.FIT_XY);

//                System.out.println("img url :" + user.get(sessionManager.IMG_URL));
//                URL url = new URL(user.get(sessionManager.IMG_URL));
//                Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//                img_profile.setImageBitmap(bmp);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        tv_pendidikanTerakhir.setText(mPendidikanTerakhir);
        tv_lokasi.setText(mLokasi);
        tv_desc.setText(mDescription);

        try {
            showLoading(true);
            loadImagePortfolio(user.get(sessionManager.ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menuBusinessCenter:
                Intent businessCenterIntent = new Intent(getView().getContext(), BusinessCenter.class);
                startActivity(businessCenterIntent);
                break;

            case R.id.menuPremium:
                Intent premiumIntent = new Intent(getView().getContext(), Premium.class);
                startActivity(premiumIntent);
                break;

            case R.id.menuChangePassword:
                Intent changePasswordIntent = new Intent(getView().getContext(), ChangePassword.class);
                startActivity(changePasswordIntent);
                break;

            case R.id.menuLogout:
                sessionManager.logout();
                Intent login = new Intent(getView().getContext(), LoginActivity.class);
                startActivity(login);
                getActivity().finish();
                break;
        }

        return true;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_edit:
                Intent editIntent = new Intent(getActivity().getApplicationContext(), EditProfile.class);
                startActivity(editIntent);
                break;
        }
    }

    private void loadImagePortfolio(String id) throws JSONException {
        String URL = "https://springjava-1591708327203.azurewebsites.net/UserPortfolio/getAllUserPortfolio";
        //String URL = "http://25.54.110.177:8095/UserPortfolio/getAllUserPortfolio";
        final JSONObject jsonBody = new JSONObject();
        jsonBody.put("user_id", id);

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    portfolioList.clear();
                    String status = response.getString("status");
                    if (status.equals("Success")) {
                        JSONArray jsonArray = response.getJSONArray("data");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            Portfolio portfolio = new Portfolio();

                            portfolio.setImgId(object.getString("portfolio_id"));
                            portfolio.setImgURL(object.getString("image_url"));
                            portfolio.setUserId(object.getString("user_id"));

                            portfolioList.add(portfolio);
                        }
                        adapter.notifyDataSetChanged();
                        viewDialog.hideDialog();
                        showLoading(false);
                    } else {
                        tv_noPhoto.setVisibility(View.VISIBLE);
                        //Toast.makeText(getContext(), "Load image failed", Toast.LENGTH_LONG).show();
                         viewDialog.hideDialog();
                        showLoading(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> params = new HashMap<String, String>();
                params.put("Context-Type", "application/json");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonObjectRequest);

    }

    public void showLoading(boolean visible) {
        if (visible) {
            pbLoading.setVisibility(View.VISIBLE);
        } else {
            pbLoading.setVisibility(View.GONE);
        }
    }

}
