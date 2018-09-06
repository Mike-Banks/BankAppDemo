package com.example.mikebanks.bankscorpfinancial;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mikebanks.bankscorpfinancial.Model.Account;
import com.example.mikebanks.bankscorpfinancial.Model.Payee;
import com.example.mikebanks.bankscorpfinancial.Model.Profile;
import com.example.mikebanks.bankscorpfinancial.Model.Transaction;
import com.example.mikebanks.bankscorpfinancial.Model.db.ApplicationDB;
import com.google.gson.Gson;

import java.util.ArrayList;

public class DrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    public enum manualNavID {
        DASHBOARD_ID,
        ACCOUNTS_ID
    }

    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;

    private Fragment fragment;
    FragmentManager fragmentManager;
    FragmentTransaction ft;

    SharedPreferences userPreferences;
    Gson gson;
    String json;

    ApplicationDB applicationDb;
    Profile userProfile;

    private Dialog depositDialog;
    Spinner spnAccounts;
    ArrayAdapter<Account> accountAdapter;
    private EditText edtDepositAmount;
    private Button btnCancel;
    private Button btnDeposit;

    private View.OnClickListener depositClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == btnCancel.getId()) {
                depositDialog.dismiss();
                Toast.makeText(DrawerActivity.this, "Deposit Cancelled", Toast.LENGTH_SHORT).show();
            } else if (view.getId() == btnDeposit.getId()) {
                makeDeposit();
            }
        }
    };

    private DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            if (i == DialogInterface.BUTTON_POSITIVE) {
                manualNavigation(manualNavID.ACCOUNTS_ID);
            }
        }
    };

    public void manualNavigation(manualNavID id) {
        ft = getSupportFragmentManager().beginTransaction();

        if (id == manualNavID.DASHBOARD_ID) {
            ft.replace(R.id.flContent, new DashboardFragment()).commit();
            navView.setCheckedItem(R.id.nav_dashboard);
            setTitle("Dashboard");
        } else if (id == manualNavID.ACCOUNTS_ID) {
            ft.replace(R.id.flContent, new AccountOverviewFragment()).commit();
            navView.setCheckedItem(R.id.nav_accounts);
            setTitle("Accounts");
        }

        drawerLayout.closeDrawers();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);

        drawerLayout = findViewById(R.id.drawer_layout);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navView = findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);

        userPreferences = this.getSharedPreferences("LastProfileUsed", MODE_PRIVATE);
        gson = new Gson();
        json = userPreferences.getString("LastProfileUsed", "");
        userProfile = gson.fromJson(json, Profile.class);

        loadAccounts();
        loadPayees();
        loadTransactions();

        SharedPreferences.Editor prefsEditor = userPreferences.edit();
        json = gson.toJson(userProfile);
        prefsEditor.putString("LastProfileUsed", json).commit(); //TODO: this code only runs once - this activity is created once -

        setupHeader();
        manualNavigation(manualNavID.DASHBOARD_ID);
    }

    private void setupHeader() {

        View headerView = navView.getHeaderView(0);

        ImageView imgProfilePic = findViewById(R.id.img_profile);
        TextView txtName = headerView.findViewById(R.id.txt_name);
        TextView txtUsername = headerView.findViewById(R.id.txt_username);

        //TODO: set the profile image

        String name = userProfile.getFirstName() + " " + userProfile.getLastName();
        txtName.setText(name);

        txtUsername.setText(userProfile.getUsername());
    }

    private void loadPayees() {
        applicationDb = new ApplicationDB(getApplicationContext());

        ArrayList<Payee> payees = applicationDb.getPayeesFromCurrentProfile(userProfile.getDbId());

        for (int i = 0; i < payees.size(); i++) {
            userProfile.addPayee(payees.get(i).getPayeeName());
        }
    }

    private void loadAccounts() {
        applicationDb = new ApplicationDB(getApplicationContext());

        ArrayList<Account> accounts = applicationDb.getAccountsFromCurrentProfile(userProfile.getDbId());

        if (userProfile.getAccounts().size() == 0) {
            for (int i = 0; i < accounts.size(); i++) {
                userProfile.addAccount(accounts.get(i).getAccountName(), accounts.get(i).getAccountBalance());
            }
        }
    }

    private void loadTransactions() {

        applicationDb = new ApplicationDB(getApplicationContext());
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

    @Override
    public void onBackPressed() {
        fragmentManager = getSupportFragmentManager();

        if (fragmentManager.getBackStackEntryCount() > 1) {
            fragmentManager.popBackStack();
        } else if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    public void showDrawerButton() {

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        toggle.syncState();
    }

    public void showUpButton() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void displayTransferDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Transfer Error")
                .setMessage("You do not have another account to transfer to. If you would like to add another account, press 'OK'.")
                .setNegativeButton("Cancel", dialogClickListener)
                .setPositiveButton("OK", dialogClickListener);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void displayDepositDialog() {

        depositDialog = new Dialog(this);
        depositDialog.setContentView(R.layout.deposit_layout);
        depositDialog.setTitle("Make a Deposit");

        depositDialog.setCanceledOnTouchOutside(true);
        depositDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Toast.makeText(DrawerActivity.this, "Deposit Cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        spnAccounts = depositDialog.findViewById(R.id.dep_spn_accounts);
        accountAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, userProfile.getAccounts());
        accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnAccounts.setAdapter(accountAdapter);
        spnAccounts.setSelection(0);

        edtDepositAmount = depositDialog.findViewById(R.id.edt_deposit_amount);

        btnCancel = depositDialog.findViewById(R.id.btn_cancel_deposit);
        btnDeposit = depositDialog.findViewById(R.id.btn_deposit);

        btnCancel.setOnClickListener(depositClickListener);
        btnDeposit.setOnClickListener(depositClickListener);

        depositDialog.show();
    }

    /**
     * method used to make a deposit
     */
    private void makeDeposit() {

        int selectedAccountIndex = spnAccounts.getSelectedItemPosition();

        if (edtDepositAmount.getText().toString().equals("")) {
            Toast.makeText(this, "Please enter an amount to deposit", Toast.LENGTH_SHORT).show();
        } else {
            double depositAmount = Double.parseDouble(edtDepositAmount.getText().toString());
            if (depositAmount < 0.01) {
                Toast.makeText(this, "please enter a valid amount", Toast.LENGTH_SHORT).show();
            } else {

                userProfile.getAccounts().get(selectedAccountIndex).setAccountBalance(userProfile.getAccounts().get(selectedAccountIndex).getAccountBalance() + depositAmount);

                SharedPreferences.Editor prefsEditor = userPreferences.edit();
                gson = new Gson();
                json = gson.toJson(userProfile);
                prefsEditor.putString("LastProfileUsed", json).apply();

                ApplicationDB applicationDb = new ApplicationDB(getApplicationContext());
                applicationDb.overwriteAccount(userProfile, userProfile.getAccounts().get(selectedAccountIndex));

                Toast.makeText(this, "Deposit of $" + String.format("%.2f",depositAmount) + " " + "made successfully", Toast.LENGTH_SHORT).show();

                accountAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, userProfile.getAccounts());
                accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnAccounts.setAdapter(accountAdapter);

                //TODO: Add checkbox if they want to make more than one deposit
                depositDialog.dismiss();
                drawerLayout.closeDrawers();
                manualNavigation(manualNavID.ACCOUNTS_ID);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        fragmentManager = getSupportFragmentManager();

        // Handle navigation view item clicks here.
        Class fragmentClass = null;
        String title = item.getTitle().toString();

        switch(item.getItemId()) {
            case R.id.nav_dashboard:
                fragmentClass = DashboardFragment.class;
                break;
            case R.id.nav_accounts:
                fragmentClass = AccountOverviewFragment.class;
                break;
            case R.id.nav_deposit:
                if (userProfile.getAccounts().size() > 0) {
                    displayDepositDialog();
                } else {
                    //TODO: Display a NoAccounts message - or modify transferDialog to be general (just change the title from Transfer error to account error)
                }
                break;
            case R.id.nav_transfer:
                if (userProfile.getAccounts().size() < 2) {
                    displayTransferDialog();
                } else {
                    title = "Transfer";
                    //TODO: Make Transfer fragment
                }
                break;
            case R.id.nav_payment:
                title = "Payment";
                break;
            case R.id.nav_settings:
                //TODO: Make Settings fragment
                break;
            case R.id.nav_logout:
                Toast.makeText(this, "Logging out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), LaunchActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                fragmentClass = DashboardFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

            item.setChecked(true);
            setTitle(title);
            drawerLayout.closeDrawers();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }
}
