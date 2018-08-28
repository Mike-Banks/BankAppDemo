package com.example.mikebanks.bankscorpfinancial;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

public class DashboardActivity extends Activity {

    private TextView txtWelcome;
    private ListView lstAccounts;

    private SharedPreferences userPreferences;

    private String json;
    private Gson gson;
    private Profile userProfile;

    private DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            if (i == DialogInterface.BUTTON_POSITIVE) {
                Intent intent = new Intent(getApplicationContext(), AccountOverviewActivity.class);
                startActivity(intent);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        setupViews();
    }

    @Override
    protected void onResume() {
        json = userPreferences.getString("LastProfileUsed", "");
        userProfile = gson.fromJson(json, Profile.class);

        AccountAdapter adapter = new AccountAdapter(this, R.layout.lst_accounts, userProfile.getAccounts());
        lstAccounts.setAdapter(adapter);

        super.onResume();
    }

    private void loadPayees() {
        ApplicationDB applicationDb = new ApplicationDB(getApplicationContext());

        ArrayList<Payee> payees = applicationDb.getPayeesFromCurrentProfile(userProfile.getDbId());

        for (int i = 0; i < payees.size(); i++) {
            userProfile.addPayee(payees.get(i).getPayeeName());
        }
    }

    private void loadAccounts() {
        ApplicationDB applicationDb = new ApplicationDB(getApplicationContext());

        ArrayList<Account> accounts = applicationDb.getAccountsFromCurrentProfile(userProfile.getDbId());

        if (userProfile.getAccounts().size() == 0) {
            for (int i = 0; i < accounts.size(); i++) {
                userProfile.addAccount(accounts.get(i).getAccountName(), accounts.get(i).getAccountBalance());
            }
        }

        AccountAdapter adapter = new AccountAdapter(this, R.layout.lst_accounts, accounts);
        lstAccounts.setAdapter(adapter);
    }

    private void loadTransactions() {

        ApplicationDB applicationDb = new ApplicationDB(getApplicationContext());
        ArrayList<Transaction> transactions;

        for (int iAccount = 0; iAccount < userProfile.getAccounts().size(); iAccount++) {
            transactions = applicationDb.getTransactionsFromCurrentAccount(userProfile.getDbId(), userProfile.getAccounts().get(iAccount).toTransactionString());
            if (transactions.size() > 0 && userProfile.getAccounts().get(iAccount).getTransactions().size() == 0) {

                for (int iTrans = 0; iTrans < transactions.size(); iTrans++) {
                    userProfile.getAccounts().get(iAccount).addTransactionFromDB(transactions.get(iTrans));
                }
            }
        }
    }

    /**
     * method used to setup the values for the views and fields
     */
    private void setupViews() {
        txtWelcome = findViewById(R.id.txt_welcome);
        lstAccounts = findViewById(R.id.lst_accounts);

        userPreferences = getSharedPreferences("LastProfileUsed", MODE_PRIVATE);
        gson = new Gson();
        json = userPreferences.getString("LastProfileUsed", "");
        userProfile = gson.fromJson(json, Profile.class);

        loadAccounts();
        loadPayees();
        loadTransactions();

        SharedPreferences.Editor prefsEditor = userPreferences.edit();
        json = gson.toJson(userProfile);
        prefsEditor.putString("LastProfileUsed", json);
        prefsEditor.commit();

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

    /**
     * method used for creating the options menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * method used for handling clicks on menu items
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent;

        // Handle item selection
        switch (item.getItemId()) {
            case R.id.accounts:
                intent = new Intent(getApplicationContext(), AccountOverviewActivity.class);
                startActivity(intent);
                return true;
            case R.id.transfers:
                if (userProfile.getAccounts().size() > 1) {
                    intent = new Intent(getApplicationContext(), TransferActivity.class);
                    startActivity(intent);
                    return true;
                } else {
                    displayDialog();
                    return false;
                }
            case R.id.payments:
                    intent = new Intent(getApplicationContext(), PaymentActivity.class);
                    startActivity(intent);
                    return true;
            case R.id.about:
                intent = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(intent);
                return true;
            case R.id.logout:
                intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * method used for displaying a dialog
     */
    private void displayDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Transfer Error")
                .setMessage("You do not have another account to transfer to. If you would like to add another account, press 'OK'.")
                .setNegativeButton("Cancel", dialogClickListener)
                .setPositiveButton("OK", dialogClickListener);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
