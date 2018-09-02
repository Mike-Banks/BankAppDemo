package com.example.mikebanks.bankscorpfinancial;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.mikebanks.bankscorpfinancial.Model.Profile;
import com.example.mikebanks.bankscorpfinancial.Model.db.ApplicationDB;
import com.google.gson.Gson;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername;
    private EditText edtPassword;
    private Button btnLogin;
    private CheckBox chkRememberCred;
    private TextView txtAccountMsg;
    private Button btnCreateAccount;

    private static final int CREATE_ACCOUNT_ACTIVITY = 1;

    private Profile lastProfileUsed;
    private Gson gson;
    private String json;
    private SharedPreferences userPreferences;
    private Intent intent;

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

             if (view.getId() == btnLogin.getId()) {
                 login();
             } else if (view.getId() == btnCreateAccount.getId()) {
                 createAccount();
             }
         }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setupViews();
    }

    /**
     * method used to setup the values for the views and fields
     */
    private void setupViews() {

        edtUsername = findViewById(R.id.edt_username);
        edtPassword = findViewById(R.id.edt_password);
        btnLogin = findViewById(R.id.btn_login);
        chkRememberCred = findViewById(R.id.chk_remember);
        txtAccountMsg = findViewById(R.id.txt_account_msg);
        btnCreateAccount = findViewById(R.id.btn_create_account);

        btnLogin.setOnClickListener(clickListener);
        btnCreateAccount.setOnClickListener(clickListener);

        userPreferences = getSharedPreferences("LastProfileUsed", MODE_PRIVATE);

        chkRememberCred.setChecked(userPreferences.getBoolean("rememberMe", false));
        if (chkRememberCred.isChecked()) {

            gson = new Gson();
            json = userPreferences.getString("LastProfileUsed", "");
            lastProfileUsed = gson.fromJson(json, Profile.class);

            edtUsername.setText(lastProfileUsed.getUsername());
            edtPassword.setText(lastProfileUsed.getPassword());

            //login();                                        //----- NOTE: This automatically logs in for the user
            //finish();                                       //----- NOTE: This ensures the user cannot return to the login screen by pressing the back button
        }

    }

    /**
     * method that runs code when the application is stopped
     */
    @Override
    protected void onStop() {
        if (lastProfileUsed != null) {
            if (edtUsername.getText().toString().equals(lastProfileUsed.getUsername()) && edtPassword.getText().toString().equals(lastProfileUsed.getPassword())) {
                userPreferences.edit().putBoolean("rememberMe", chkRememberCred.isChecked()).commit();
            } else {
                userPreferences.edit().putBoolean("rememberMe", false).commit();
            }
        }

        super.onStop();
    }

    /**
     * method that runs when the user logs in
     */
    private void login() {

        validateAccount();
    }

    private void validateAccount() {
        ApplicationDB applicationDB = new ApplicationDB(getApplicationContext());
        ArrayList<Profile> profiles = applicationDB.getAllProfiles();

        boolean match = false;

        if (profiles.size() > 0) {
            for (int i = 0; i < profiles.size(); i++) {
                if (edtUsername.getText().toString().equals(profiles.get(i).getUsername()) && edtPassword.getText().toString().equals(profiles.get(i).getPassword())) {

                    match = true;

                    userPreferences.edit().putBoolean("rememberMe", chkRememberCred.isChecked()).commit();

                    lastProfileUsed = profiles.get(i);

                    Editor prefsEditor = userPreferences.edit();
                    gson = new Gson();
                    json = gson.toJson(lastProfileUsed);
                    prefsEditor.putString("LastProfileUsed", json);
                    prefsEditor.commit();

                    intent = new Intent(getApplicationContext(), DrawerActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
            if (!match) {
                Toast.makeText(this, R.string.incorrect_login, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, R.string.incorrect_login, Toast.LENGTH_SHORT).show();
        }


    }

    /**
     * method that creates an account
     */
    private void createAccount() {
        intent = new Intent(getApplicationContext(), CreateProfileActivity.class);
        startActivityForResult(intent, CREATE_ACCOUNT_ACTIVITY);
    }

    /**
     * method that runs when receiving a result from another activity
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (requestCode == CREATE_ACCOUNT_ACTIVITY) {

            //if the user has saved their changes
            if (resultCode == RESULT_OK && data != null) {

                Toast.makeText(this, R.string.account_success, Toast.LENGTH_SHORT).show();

                edtUsername.setText(data.getStringExtra("Username"));
                edtPassword.setText(data.getStringExtra("Password"));
                chkRememberCred.setChecked(true);

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, R.string.account_cancelled, Toast.LENGTH_SHORT).show();

                edtUsername.setText("");
                edtPassword.setText("");
                chkRememberCred.setChecked(false);

            }
        }
    }
}
