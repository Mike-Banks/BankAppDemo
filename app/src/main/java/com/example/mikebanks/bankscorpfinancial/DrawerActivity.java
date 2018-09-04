package com.example.mikebanks.bankscorpfinancial;

import android.content.DialogInterface;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mikebanks.bankscorpfinancial.Model.Profile;
import com.google.gson.Gson;

public class DrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;

    private Fragment fragment;
    FragmentManager fragmentManager;
    FragmentTransaction ft;

    Profile userProfile;

    private DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            if (i == DialogInterface.BUTTON_POSITIVE) {
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.flContent, new AccountOverviewFragment()).commit();
                navView.setCheckedItem(R.id.nav_accounts);
                setTitle("Accounts");
                drawerLayout.closeDrawers();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        drawerLayout = findViewById(R.id.drawer_layout);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navView = findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);

        setupHeader();

        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flContent, new DashboardFragment()).commit();
        navView.setCheckedItem(R.id.nav_dashboard);
        setTitle("Dashboard");
    }

    private void setupHeader() {

        View headerView = navView.getHeaderView(0);

        ImageView imgProfilePic = findViewById(R.id.img_profile);
        TextView txtName = headerView.findViewById(R.id.txt_name);
        TextView txtUsername = headerView.findViewById(R.id.txt_username);

        SharedPreferences userPreferences = this.getSharedPreferences("LastProfileUsed", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = userPreferences.getString("LastProfileUsed", "");
        userProfile = gson.fromJson(json, Profile.class);

        //TODO: set the profile image

        String name = userProfile.getFirstName() + " " + userProfile.getLastName();
        txtName.setText(name);

        txtUsername.setText(userProfile.getUsername());
    }

    @Override
    public void onBackPressed() {
        drawerLayout = findViewById(R.id.drawer_layout);
        fragmentManager = getSupportFragmentManager();

        if (fragmentManager.getBackStackEntryCount() > 1) {
            fragmentManager.popBackStack();
        } else if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void showDrawerButton() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toggle.syncState();
    }

    private void displayDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Transfer Error")
                .setMessage("You do not have another account to transfer to. If you would like to add another account, press 'OK'.")
                .setNegativeButton("Cancel", dialogClickListener)
                .setPositiveButton("OK", dialogClickListener);

        AlertDialog dialog = builder.create();
        dialog.show();
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
            case R.id.nav_transfer:
                if (userProfile.getAccounts().size() < 2) {
                    displayDialog();
                } else {
                    title = "Transfer";
                    //TODO; Make Transfer fragment
                }
                break;
            case R.id.nav_payment:
                title = "Payment";
                break;
            case R.id.nav_settings:
                //TODO: Make Settings fragment
                break;
            case R.id.nav_logout:
                finish();
                break;
            default:
                fragmentClass = DashboardFragment.class;
        }
        try  {

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
