package com.example.mikebanks.bankscorpfinancial;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mikebanks.bankscorpfinancial.Adapters.AccountAdapter;
import com.example.mikebanks.bankscorpfinancial.Model.Account;
import com.example.mikebanks.bankscorpfinancial.Model.Payee;
import com.example.mikebanks.bankscorpfinancial.Model.Profile;
import com.example.mikebanks.bankscorpfinancial.Model.Transaction;
import com.example.mikebanks.bankscorpfinancial.Model.db.ApplicationDB;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

public class DashboardFragment extends Fragment {

    private TextView txtWelcome;
    private ListView lstAccounts;

    private SharedPreferences userPreferences;

    private String json;
    private Gson gson;
    private Profile userProfile;

    //TODO: Add some kind of back navigation for fragments? - research - fragmentStack does not work here for some reason (unless fragment is launched from another fragment)
    //TODO: Pressing back goes to main android screen, rather than login screen - don't start Drawer Activity for result, start it, and finish login act - when logging out with button, finish the drawer activity (maybe), start Login activity - still somehow display logging out message in login activity

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_dashboard, container, false);

        txtWelcome = rootView.findViewById(R.id.txt_welcome);
        lstAccounts = rootView.findViewById(R.id.lst_accounts);

        setupViews();
        return rootView;

    }

    @Override
    public void onResume() {
        json = userPreferences.getString("LastProfileUsed", "");
        userProfile = gson.fromJson(json, Profile.class);

        AccountAdapter adapter = new AccountAdapter(this.getActivity(), R.layout.lst_accounts, userProfile.getAccounts());
        lstAccounts.setAdapter(adapter);

        super.onResume();
    }

    /**
     * method used to setup the values for the views and fields
     */
    private void setupViews() {

        userPreferences = this.getActivity().getSharedPreferences("LastProfileUsed", MODE_PRIVATE);
        gson = new Gson();
        json = userPreferences.getString("LastProfileUsed", "");
        userProfile = gson.fromJson(json, Profile.class);

        lstAccounts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ((DrawerActivity) getActivity()).manualNavigation(DrawerActivity.manualNavID.ACCOUNTS_ID);
            }
        });

        StringBuilder welcomeString = new StringBuilder();

        Calendar calendar = Calendar.getInstance();

        int timeOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        if (timeOfDay >= 5 && timeOfDay < 12) {
            welcomeString.append(getString(R.string.good_morning));
        } else if (timeOfDay >= 12 && timeOfDay < 17) {
            welcomeString.append(getString(R.string.good_afternoon));

        } else {
            welcomeString.append(getString(R.string.good_evening));
        }

        welcomeString.append(", ")
                .append(userProfile.getFirstName())
                .append(getString(R.string.happy))
                .append(" ");

        int day = calendar.get(Calendar.DAY_OF_WEEK);

        String[] days = getResources().getStringArray(R.array.days);
        String dow = "";

        switch(day) {
            case Calendar.SUNDAY:
                dow = days[0];
                break;
            case Calendar.MONDAY:
                dow = days[1];
                break;
            case Calendar.TUESDAY:
                dow = days[2];
                break;
            case Calendar.WEDNESDAY:
                dow = days[3];
                break;
            case Calendar.THURSDAY:
                dow = days[4];
                break;
            case Calendar.FRIDAY:
                dow = days[5];
                break;
            case Calendar.SATURDAY:
                dow = days[6];
                break;
            default:
                break;
        }

        welcomeString.append(dow)
                .append(".");

        txtWelcome.setText(welcomeString.toString());
    }

}
