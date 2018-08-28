package com.example.mikebanks.bankscorpfinancial;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
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

import static android.widget.AdapterView.*;

public class AccountOverviewActivity extends Activity {

    private ListView lstAccounts;
    private Button btnViewAccount;
    private EditText edtAccountName;
    private EditText edtAccountAmount;
    private Button btnAddAccount;

    private Gson gson;
    private Profile userProfile;
    private SharedPreferences userPreferences;

    private int selectedAccountIndex;

    private View.OnClickListener buttonClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == btnViewAccount.getId()) {
                viewAccount();
            } else if (view.getId() == btnAddAccount.getId()) {
                addAccount();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_overview);

        setValues();
    }

    @Override
    protected void onResume() {
        resetFields();
        super.onResume();
    }

    /**
     * method used to setup the values for the views and fields
     */

    private void resetFields() {
        selectedAccountIndex = 0;
        btnViewAccount.setEnabled(false);

        userPreferences = getSharedPreferences("LastProfileUsed", MODE_PRIVATE);

        gson = new Gson();
        String json = userPreferences.getString("LastProfileUsed", "");
        userProfile = gson.fromJson(json, Profile.class);

        AccountAdapter adapter = new AccountAdapter(this, R.layout.lst_accounts, userProfile.getAccounts());
        lstAccounts.setAdapter(adapter);

        edtAccountAmount.getText().clear();
        edtAccountName.getText().clear();
    }

    private void setValues() {

        lstAccounts = findViewById(R.id.lst_accounts);
        lstAccounts.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                btnViewAccount.setEnabled(true);
                selectedAccountIndex = i;
            }
        });

        btnViewAccount = findViewById(R.id.btn_view_account);
        edtAccountName = findViewById(R.id.edt_account_name);
        edtAccountAmount = findViewById(R.id.edt_initial_deposit);
        btnAddAccount = findViewById(R.id.btn_open_account);

        btnViewAccount.setOnClickListener(buttonClickListener);
        btnAddAccount.setOnClickListener(buttonClickListener);
    }

    /**
     * method used to view an account
     */
    private void viewAccount() {
        Intent intent = new Intent (getApplicationContext(), AccountActivity.class);
        intent.putExtra("selectedAccountIndex", selectedAccountIndex);
        startActivity(intent);
    }

    /**
     * method used to add an account
     */
    private void addAccount() {

        int accountNum = userProfile.getAccounts().size();

        if (!(edtAccountName.getText().toString().equals("") || edtAccountAmount.getText().toString().equals(""))) {

            if (Double.parseDouble(edtAccountAmount.getText().toString()) < 10) {
                Toast.makeText(this, R.string.balance_less_than_ten, Toast.LENGTH_SHORT).show();
            } else if (edtAccountName.getText().toString().length() > 10) {

                Toast.makeText(this, R.string.account_name_exceeds_char, Toast.LENGTH_SHORT).show();

            } else {

                boolean match = false;

                for (int i = 0; i < userProfile.getAccounts().size(); i++) {
                    if (edtAccountName.getText().toString().equalsIgnoreCase(userProfile.getAccounts().get(i).getAccountName())) {
                        match = true;
                    }
                }

                if (match == false) {

                    if (accountNum < 10) {

                        ApplicationDB applicationDb = new ApplicationDB(getApplicationContext());

                        userProfile.addAccount(edtAccountName.getText().toString(), Double.parseDouble(edtAccountAmount.getText().toString()));

                        applicationDb.saveNewAccount(userProfile, userProfile.getAccounts().get(accountNum));

                        edtAccountName.setText("");
                        edtAccountAmount.setText("");

                        Toast.makeText(this, R.string.acc_saved_successfully, Toast.LENGTH_SHORT).show();

                        ArrayList<Account> accounts = userProfile.getAccounts();

                        AccountAdapter adapter = new AccountAdapter(this, R.layout.lst_accounts, accounts);
                        lstAccounts.setAdapter(adapter);

                        SharedPreferences.Editor prefsEditor = userPreferences.edit();
                        String json = gson.toJson(userProfile);
                        prefsEditor.putString("LastProfileUsed", json);
                        prefsEditor.commit();
                    } else {

                        Toast.makeText(this, "You have reached the maximum amount of accounts (10)", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(this, R.string.account_name_error, Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(this, R.string.acc_fields_incomplete, Toast.LENGTH_SHORT).show();
        }
    }
}
