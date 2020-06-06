package com.example.skripsi;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;

    ViewDialog viewDialog;

    Button btn_edit;
    ImageView img_slideMenuProfile, img_history;
    TextView tv_nama, tv_dob, tv_pendidikanTerakhir, tv_lokasi, tv_desc;
    SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        viewDialog = new ViewDialog(getActivity());
        viewDialog.showDialog();

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

        tv_pendidikanTerakhir.setText(mPendidikanTerakhir);
        tv_lokasi.setText(mLokasi);
        tv_desc.setText(mDescription);

        img_history = v.findViewById(R.id.img_history);
        img_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent historyIntent = new Intent(v.getContext(), ApplicationHistory.class);
                startActivity(historyIntent);
            }
        });

        viewDialog.hideDialog();

        return v;

    }

    @Override
    public void onResume() {
        super.onResume();
        HashMap<String, String> user = sessionManager.getUserDetail();
        String mFirstName = user.get(sessionManager.FIRST_NAME);
        String mLastName = user.get(sessionManager.LAST_NAME);
        String mDob = user.get(sessionManager.DOB);
        String mPendidikanTerakhir = user.get(sessionManager.EDUCATION_NAME);
        String mLokasi = user.get(sessionManager.LOCATION_NAME);
        String mDescription = user.get(sessionManager.DESCRIPTION);

//        tv_nama.setText(mFirstName + " " + mLastName);
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

        tv_pendidikanTerakhir.setText(mPendidikanTerakhir);
        tv_lokasi.setText(mLokasi);
        tv_desc.setText(mDescription);

        viewDialog.hideDialog();

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
}
