package com.example.mikebanks.bankscorpfinancial;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mikebanks.bankscorpfinancial.Model.Account;
import com.example.mikebanks.bankscorpfinancial.Model.Payee;
import com.example.mikebanks.bankscorpfinancial.Model.Profile;
import com.example.mikebanks.bankscorpfinancial.Model.db.ApplicationDB;
import com.google.gson.Gson;

import java.util.ArrayList;

import static android.view.View.*;

public class PaymentActivity extends Activity {

    private Spinner spnSelectAccount;
    private TextView txtNoPayeesMsg;
    private Spinner spnSelectPayee;
    private EditText edtPaymentAmount;
    private Button btnMakePayment;
    private Button btnAddPayee;
    private EditText edtPaymentName;

    private ArrayList<Account> accounts;
    ArrayAdapter<Account> accountAdapter;

    private ArrayList<Payee> payees;
    private ArrayAdapter<Payee> payeeAdapter;

    View.OnClickListener buttonClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == btnMakePayment.getId()) {
                makePayment();
            } else if (view.getId() == btnAddPayee.getId()) {
                addPayee();
            }
        }
    };

    private Gson gson;
    private String json;
    private Profile userProfile;
    private SharedPreferences userPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

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

        spnSelectAccount = findViewById(R.id.spn_select_acc);
        txtNoPayeesMsg = findViewById(R.id.txt_no_payees);
        spnSelectPayee = findViewById(R.id.spn_select_payee);
        edtPaymentAmount = findViewById(R.id.edt_payment_amount);
        btnMakePayment = findViewById(R.id.btn_make_payment);
        edtPaymentName = findViewById(R.id.edt_add_payee_name);
        btnAddPayee = findViewById(R.id.btn_add_payee);

        btnMakePayment.setOnClickListener(buttonClickListener);
        btnAddPayee.setOnClickListener(buttonClickListener);

        accounts = userProfile.getAccounts();
        accountAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, accounts);
        accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnSelectAccount.setAdapter(accountAdapter);

        payees = userProfile.getPayees();

        payeeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, payees);
        payeeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnSelectPayee.setAdapter(payeeAdapter);

        checkPayeeInformation();
    }

    /**
     * method that checks the information of the payee
     */
    private void checkPayeeInformation() {
        if (userProfile.getPayees().size() == 0) {
            txtNoPayeesMsg.setVisibility(VISIBLE);

            spnSelectPayee.setVisibility(GONE);
            edtPaymentAmount.setVisibility(GONE);
            btnMakePayment.setVisibility(GONE);
        } else {
            txtNoPayeesMsg.setVisibility(GONE);

            spnSelectPayee.setVisibility(VISIBLE);
            edtPaymentAmount.setVisibility(VISIBLE);
            btnMakePayment.setVisibility(VISIBLE);
        }
    }

    /**
     * method that makes a payment
     */
    private void makePayment() {
        if (!(edtPaymentAmount.getText().toString().equals("") || Double.parseDouble(edtPaymentAmount.getText().toString()) < 1)) {

            int selectedAccountIndex = spnSelectAccount.getSelectedItemPosition();

            if (Double.parseDouble(edtPaymentAmount.getText().toString()) > (userProfile.getAccounts().get(selectedAccountIndex)
                    .getAccountBalance())) {

                Toast.makeText(this, "You do not have sufficient funds to make this payment", Toast.LENGTH_SHORT).show();
            } else {

                int selectedPayeeIndex = spnSelectPayee.getSelectedItemPosition();

                String selectedPayee = userProfile.getPayees().get(selectedPayeeIndex).toString();
                Double amount = Double.parseDouble(edtPaymentAmount.getText().toString());

                userProfile.getAccounts().get(selectedAccountIndex).addPaymentTransaction(selectedPayee, amount);

                accounts = userProfile.getAccounts();
                spnSelectAccount.setAdapter(accountAdapter);
                spnSelectAccount.setSelection(selectedAccountIndex);

                ApplicationDB applicationDb = new ApplicationDB(getApplicationContext());
                applicationDb.saveNewTransaction(userProfile, userProfile.getAccounts().get(selectedAccountIndex).toTransactionString(), userProfile.getAccounts().get(selectedAccountIndex).getTransactions().get(userProfile.getAccounts().get(selectedAccountIndex).getTransactions().size()-1));
                applicationDb.overwriteAccount(userProfile, userProfile.getAccounts().get(selectedAccountIndex));

                SharedPreferences.Editor prefsEditor = userPreferences.edit();
                gson = new Gson();
                json = gson.toJson(userProfile);
                prefsEditor.putString("LastProfileUsed", json);
                prefsEditor.commit();

                Toast.makeText(this, "Payment of $" + String.format("%.2f", amount) + " successfully made", Toast.LENGTH_SHORT).show();
                edtPaymentAmount.getText().clear();
            }
        } else {

            Toast.makeText(this, "Please enter an amount greater than $1 for the payment", Toast.LENGTH_SHORT).show();

        }
    }

    /**
     * method that adds a payee
     */
    private void addPayee() {
        if (!(edtPaymentName.getText().toString().equals(""))) {

            boolean match = false;
            for (int i = 0; i < userProfile.getPayees().size(); i++) {
                if (edtPaymentName.getText().toString().equalsIgnoreCase(userProfile.getPayees().get(i).getPayeeName())) {
                    match = true;
                }
            }

            if (match == false) {
                userProfile.addPayee(edtPaymentName.getText().toString());

                edtPaymentName.setText("");

                txtNoPayeesMsg.setVisibility(GONE);
                spnSelectPayee.setVisibility(VISIBLE);
                edtPaymentAmount.setVisibility(VISIBLE);
                btnMakePayment.setVisibility(VISIBLE);

                payees = userProfile.getPayees();
                spnSelectPayee.setAdapter(payeeAdapter);
                spnSelectPayee.setSelection(userProfile.getPayees().size()-1);

                ApplicationDB applicationDb = new ApplicationDB(getApplicationContext());
                applicationDb.saveNewPayee(userProfile, userProfile.getPayees().get(userProfile.getPayees().size()-1));

                SharedPreferences.Editor prefsEditor = userPreferences.edit();
                gson = new Gson();
                json = gson.toJson(userProfile);
                prefsEditor.putString("UserProfile", json);
                prefsEditor.commit();

                Toast.makeText(this, "Payee Added Successfully", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "A Payee with that name already exists", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
