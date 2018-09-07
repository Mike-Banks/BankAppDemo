package com.example.mikebanks.bankscorpfinancial;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mikebanks.bankscorpfinancial.Adapters.AccountAdapter;
import com.example.mikebanks.bankscorpfinancial.Model.Account;
import com.example.mikebanks.bankscorpfinancial.Model.Profile;
import com.example.mikebanks.bankscorpfinancial.Model.db.ApplicationDB;
import com.google.gson.Gson;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class AccountOverviewFragment extends Fragment {

    private ListView lstAccounts;

    private Gson gson;
    private Profile userProfile;
    private SharedPreferences userPreferences;

    //TODO B: Clicking on an account will automatically go to the details page
    //TODO C1: Have Floating Action Button (research for Fragment) to add an account (opens dialogue maybe, asks for name and initial deposit (maybe no initial deposit anymore?) - if user cancels or creates - toast is displayed
    //TODO C2: Additionally, if user comes here from the transfer dialog (only one account), automatically open the dialog (or have the dialog appear in the drawerActivity)
    //TODO D1: Add functionality to remove accounts (note: ensure i remove from database as well (restructure db when removed)
    //TODO D2: Add functionality to remove payees and profiles as well
    //TODO D3: Add swiping functionality so that the user can swipe on the account to remove or see details

    //KEEP ME
    private int selectedAccountIndex;

    public AccountOverviewFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_account_overview, container, false);

        lstAccounts = rootView.findViewById(R.id.lst_accounts);

        ((DrawerActivity) getActivity()).showDrawerButton();

        setValues();

        return rootView;
    }

    private void setValues() {
        selectedAccountIndex = 0;

        userPreferences = this.getActivity().getSharedPreferences("LastProfileUsed", MODE_PRIVATE);
        gson = new Gson();
        String json = userPreferences.getString("LastProfileUsed", "");
        userProfile = gson.fromJson(json, Profile.class);

        AccountAdapter adapter = new AccountAdapter(this.getActivity(), R.layout.lst_accounts, userProfile.getAccounts());
        lstAccounts.setAdapter(adapter);

        lstAccounts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedAccountIndex = i;
                viewAccount();
            }
        });
    }

    /**
     * method used to view an account
     */
    private void viewAccount() {
        AccountFragment nextFragment = new AccountFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.flContent, nextFragment,"findThisFragment")
                .addToBackStack(null)
                .commit();

        //TODO: Pass the selectedAccountIndex to the AccountFragment - research passing data between fragments
    }

    //TODO: Floating Action Button to open dialog (probably) can use this logic below

    /**
     * method used to add an account
     */
    /*
    private void addAccount() {

        int accountNum = userProfile.getAccounts().size();

        if (!(edtAccountName.getText().toString().equals("") || edtAccountAmount.getText().toString().equals(""))) {

            if (Double.parseDouble(edtAccountAmount.getText().toString()) < 10) {
                Toast.makeText(this.getActivity(), R.string.balance_less_than_ten, Toast.LENGTH_SHORT).show();
            } else if (edtAccountName.getText().toString().length() > 10) {

                Toast.makeText(this.getActivity(), R.string.account_name_exceeds_char, Toast.LENGTH_SHORT).show();

            } else {

                boolean match = false;

                for (int i = 0; i < userProfile.getAccounts().size(); i++) {
                    if (edtAccountName.getText().toString().equalsIgnoreCase(userProfile.getAccounts().get(i).getAccountName())) {
                        match = true;
                    }
                }

                if (!match) {

                    if (accountNum < 10) {

                        ApplicationDB applicationDb = new ApplicationDB(getActivity().getApplicationContext());

                        userProfile.addAccount(edtAccountName.getText().toString(), Double.parseDouble(edtAccountAmount.getText().toString()));

                        applicationDb.saveNewAccount(userProfile, userProfile.getAccounts().get(accountNum));

                        edtAccountName.setText("");
                        edtAccountAmount.setText("");

                        Toast.makeText(this.getActivity(), R.string.acc_saved_successfully, Toast.LENGTH_SHORT).show();

                        ArrayList<Account> accounts = userProfile.getAccounts();

                        AccountAdapter adapter = new AccountAdapter(this.getActivity(), R.layout.lst_accounts, accounts);
                        lstAccounts.setAdapter(adapter);

                        SharedPreferences.Editor prefsEditor = userPreferences.edit();
                        String json = gson.toJson(userProfile);
                        prefsEditor.putString("LastProfileUsed", json).commit();
                    } else {

                        Toast.makeText(this.getActivity(), "You have reached the maximum amount of accounts (10)", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(this.getActivity(), R.string.account_name_error, Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(this.getActivity(), R.string.acc_fields_incomplete, Toast.LENGTH_SHORT).show();
        }
    }
    */
}
