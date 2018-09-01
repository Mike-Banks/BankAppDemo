package com.example.mikebanks.bankscorpfinancial;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mikebanks.bankscorpfinancial.Model.Profile;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class AboutActivity extends Activity {

    //TODO: Can be changed to save all the transactions as well - save statement to device?

    private TextView txtProfileFirstName;
    private TextView txtProfileLastName;
    private TextView txtProfileCountry;
    private TextView txtProfileUsername;
    private TextView txtProfilePassword;

    private Button btnSaveProfile;
    private Button btnGotoPortfolio;

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == btnSaveProfile.getId()) {
                saveProfile();
            } else if (view.getId() == btnGotoPortfolio.getId()) {
                gotoPortfolio();
            }
        }
    };

    private SharedPreferences userPreferences;
    private Profile userProfile;
    private Gson gson;
    private String json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        setValues();
    }

    /**
     * method used to setup the values for the views and fields
     */
    private void setValues() {
        userPreferences = getSharedPreferences("LastProfileUsed", MODE_PRIVATE);

        gson = new Gson();
        json = userPreferences.getString("LastProfileUsed", "");
        userProfile = gson.fromJson(json, Profile.class);

        txtProfileFirstName = findViewById(R.id.txt_profile_first_name);
        txtProfileLastName = findViewById(R.id.txt_profile_last_name);
        txtProfileCountry = findViewById(R.id.txt_profile_country);
        txtProfileUsername = findViewById(R.id.txt_profile_username);
        txtProfilePassword = findViewById(R.id.txt_profile_password);

        btnSaveProfile = findViewById(R.id.btn_save_to_file);
        btnGotoPortfolio = findViewById(R.id.btn_goto_portfolio);

        btnSaveProfile.setOnClickListener(clickListener);
        btnGotoPortfolio.setOnClickListener(clickListener);

        txtProfileFirstName.setText("First Name: " + userProfile.getFirstName());
        txtProfileLastName.setText("Last Name: " + userProfile.getLastName());
        txtProfileCountry.setText("Country: " + userProfile.getCountry());
        txtProfileUsername.setText("Username: " + userProfile.getUsername());
        txtProfilePassword.setText("Password: " + userProfile.getPassword());

    }

    /**
     * Method that is used to save the extracted contents into a text file
     */
    private void saveProfile() {

        //setup the file name
        String fileName = "profile.txt";

        //create a file output stream object
        FileOutputStream fos = null;
        try {
            //try to open the file (if not there, then create it)
            //NOTE: File mode is private, so that it overwrites the file each time, rather than appending (adding) to it
            fos = openFileOutput(fileName, MODE_PRIVATE);

            //catch a file not found exception, and print the stack trace
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //call the write to file method, passing in the file output stream object
        writeToFile(fos);
    }

    /**
     * Method used to write to a file
     * @param fos a file output stream object
     */
    private void writeToFile(FileOutputStream fos) {

        StringBuilder builder = new StringBuilder();
        builder.append("First Name: " + userProfile.getFirstName() + "\n")
                .append("Last Name: " + userProfile.getLastName() + "\n")
                .append("Country: " + userProfile.getCountry() + "\n")
                .append("Username: " + userProfile.getUsername() + "\n")
                .append("Password: " + userProfile.getPassword() + "\n");

        //TODO: Write accounts, payees and transactions to text file
        //convert the string builder to a string
        String toFile = String.format("%s", builder);

        //create a new writer object, passing in the file output stream object
        PrintWriter writer = new PrintWriter(fos);

        //write to the string value from the string builder to the file, then close it
        writer.println(toFile);
        writer.close();

        //display a toast to the user, notifying them that the file was saved to the device
        Toast.makeText(this, "Successfully saved profile to file", Toast.LENGTH_SHORT).show();
    }

    /**
     * method used to go to portfolio - NETWORKING USED HERE
     */
    private void gotoPortfolio() {
        if (isNetworkConnected()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://banksmic.dev.fast.sheridanc.on.ca/"));
            startActivity(intent);
        }
    }

    /**
     * Method used to check internet connection - NETWORKING USED HERE
     * @return
     */
    private boolean isNetworkConnected() {

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected());
    }
}
