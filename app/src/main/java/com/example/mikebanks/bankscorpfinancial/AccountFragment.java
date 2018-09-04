package com.example.mikebanks.bankscorpfinancial;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AccountFragment extends Fragment {
    //TODO: Have the up button return to the previous activity, rather than open the drawer menu

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBackButton();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    public void showBackButton() {
        if (getActivity() instanceof DrawerActivity) {
            ((DrawerActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

}
