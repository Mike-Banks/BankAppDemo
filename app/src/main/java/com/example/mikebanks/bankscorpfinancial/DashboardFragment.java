package com.example.mikebanks.bankscorpfinancial;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.mikebanks.bankscorpfinancial.Model.Profile;
import com.google.gson.Gson;

import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

public class DashboardFragment extends Fragment {

    private TextView txtWelcome;
    private TextView txtMessage;
    private Button btnAddAccount;

    private SharedPreferences userPreferences;

    private String json;
    private Gson gson;
    private Profile userProfile;

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
        txtMessage = rootView.findViewById(R.id.txt_details_msg);
        btnAddAccount = rootView.findViewById(R.id.btn_add_account);

        setupViews();
        return rootView;

    }

    /**
     * method used to setup the values for the views and fields
     */
    private void setupViews() {

        userPreferences = getActivity().getSharedPreferences("LastProfileUsed", MODE_PRIVATE);
        gson = new Gson();
        json = userPreferences.getString("LastProfileUsed", "");
        userProfile = gson.fromJson(json, Profile.class);

        btnAddAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DrawerActivity) getActivity()).manualNavigation(DrawerActivity.manualNavID.ACCOUNTS_ID);
            }
        });

        if (userProfile.getAccounts().size() == 0) {
            txtMessage.setVisibility(View.VISIBLE);
            btnAddAccount.setVisibility(View.VISIBLE);
            txtMessage.setText("You do not have any accounts, click below to add an account");
        } else {
            txtMessage.setVisibility(View.GONE);//TEMP to clear field
            btnAddAccount.setVisibility(View.GONE);
            //TODO: Have message say some kind of statistic about how many transactions they have made today
        }

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
