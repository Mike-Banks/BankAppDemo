package com.example.mikebanks.bankscorpfinancial;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mikebanks.bankscorpfinancial.Model.Profile;
import com.example.mikebanks.bankscorpfinancial.Model.db.ApplicationDB;

public class CreateProfileActivity extends Activity {

    private EditText edtFirstName;
    private EditText edtLastName;
    private EditText edtCountry;
    private EditText edtUsername;
    private EditText edtPassword;
    private EditText edtPasswordConfirm;
    private EditText edtAccountName;
    private EditText edtAccountAmount;
    private Button btnCreateAccount;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        setupViews();
    }

    /**
     * method used to setup the values for the views and fields
     */
    private void setupViews() {
        edtFirstName = findViewById(R.id.edt_first_name);
        edtLastName = findViewById(R.id.edt_last_name);
        edtCountry = findViewById(R.id.edt_country);
        edtUsername = findViewById(R.id.edt_username);
        edtPassword = findViewById(R.id.edt_password);
        edtPasswordConfirm = findViewById(R.id.edt_password_confirm);
        edtAccountName = findViewById(R.id.edt_account_name);
        edtAccountAmount = findViewById(R.id.edt_account_amount);
        btnCreateAccount = findViewById(R.id.btn_create_account);

        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });

        intent = getIntent();
    }

    // if the user preses the back btn , set the result of the intent to cancelled
    @Override
    public boolean onOptionsItemSelected(MenuItem item ){

        // if the user pressed the back BTN
        if(item.getTitle().equals(this.getTitle())){ //works depending on API - use breakpoint to check - WORKS ON THIS API
            setResult(RESULT_CANCELED);
            finish();
            return true;
        }else{
            // if they did not press the back BTN but something else
            return super.onOptionsItemSelected(item);
        }

    }

    /**
     * method used to create an account
     */
    private void createAccount() {

        if (edtFirstName.getText().toString().equals("") || edtLastName.getText().toString().equals("") || edtCountry.getText().toString().equals("") ||
                edtUsername.getText().toString().equals("") || edtPassword.getText().toString().equals("") || edtPasswordConfirm.getText().toString().equals("") ||
                edtAccountName.getText().toString().equals("") || edtAccountAmount.getText().toString().equals("")) {
            Toast.makeText(CreateProfileActivity.this, R.string.fields_blank, Toast.LENGTH_SHORT).show();
        }

        else if (!(edtPassword.getText().toString().equals(edtPasswordConfirm.getText().toString()))) {
            Toast.makeText(CreateProfileActivity.this, R.string.password_mismatch, Toast.LENGTH_SHORT).show();
        }

        else if (edtAccountName.getText().toString().length() > 10) {
            Toast.makeText(CreateProfileActivity.this, R.string.account_name_exceeds_char, Toast.LENGTH_SHORT).show();
        }

        else if (Double.parseDouble(edtAccountAmount.getText().toString()) < 10) {
            Toast.makeText(CreateProfileActivity.this, R.string.balance_less_than_ten, Toast.LENGTH_SHORT).show();
        }
        else {
            Profile userProfile = new Profile(edtFirstName.getText().toString(), edtLastName.getText().toString(), edtCountry.getText().toString(),
                   edtUsername.getText().toString(), edtPassword.getText().toString());

            userProfile.addAccount(edtAccountName.getText().toString(), Double.parseDouble(edtAccountAmount.getText().toString()));

            ApplicationDB applicationDb = new ApplicationDB(getApplicationContext());
            applicationDb.saveNewProfile(userProfile);

            applicationDb.saveNewAccount(userProfile, userProfile.getAccounts().get(0));

            intent.putExtra("Username", userProfile.getUsername());
            intent.putExtra("Password", userProfile.getPassword());

            setResult(RESULT_OK, intent);

            finish();
        }
    }
}
