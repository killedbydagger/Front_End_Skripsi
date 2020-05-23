package com.example.skripsi;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

public class ProfileFragment extends Fragment implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    Button btn_edit;

    TextView tv_nama, tv_dob, tv_pendidikanTerakhir, tv_lokasi, tv_desc;

    SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        btn_edit = (Button) v.findViewById(R.id.btn_edit);
        btn_edit.setOnClickListener(this);

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
        tv_dob.setText(mDob);
        tv_pendidikanTerakhir.setText(mPendidikanTerakhir);
        tv_lokasi.setText(mLokasi);
        tv_desc.setText(mDescription);

        return v;

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menuBusinessCenter:
                Intent businessCenterIntent = new Intent(getView().getContext(), BusinessCenter.class);
                startActivity(businessCenterIntent);
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
