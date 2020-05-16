package com.example.skripsi;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    Button btn_edit;

    SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        btn_edit = (Button) v.findViewById(R.id.btn_edit);
        btn_edit.setOnClickListener(this);
        return v;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_edit :
                Intent editIntent = new Intent(getActivity().getApplicationContext(),EditProfile.class);
                startActivity(editIntent);
                break;
        }
    }
}
