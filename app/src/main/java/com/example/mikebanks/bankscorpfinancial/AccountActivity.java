package com.example.mikebanks.bankscorpfinancial;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mikebanks.bankscorpfinancial.Adapters.PaymentAdapter;
import com.example.mikebanks.bankscorpfinancial.Adapters.TransferAdapter;
import com.example.mikebanks.bankscorpfinancial.Model.Profile;
import com.example.mikebanks.bankscorpfinancial.Model.Transaction;
import com.example.mikebanks.bankscorpfinancial.Model.Transaction.TRANSACTION_TYPE;
import com.example.mikebanks.bankscorpfinancial.Model.db.ApplicationDB;
import com.google.gson.Gson;

import java.util.ArrayList;

import static android.view.View.*;

public class AccountActivity extends Activity {

    private TextView txtAccountName;
    private TextView txtAccountNo;
    private TextView txtAccountBalance;
    private TextView txtTransactionMsg;

    private Button btnPayments;
    private Button btnTransfers;

    private TextView txtNoTransfersMsg;
    private TextView txtNoPaymentsMsg;

    private ListView lstPayments;
    private ListView lstTransfers;

    private Button btnAddDeposit;
    private EditText edtDepositAmount;
    private Button btnMakeDeposit;

    private OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.btn_payments:
                    displayPayments();
                    break;
                case R.id.btn_transfers:
                    displayTransfers();
                    break;
                case R.id.btn_add_deposit:
                    showDepositViews();
                    break;
                case R.id.btn_make_deposit:
                    makeDeposit();
                    break;
            }
        }
    };

    private Gson gson;
    private String json;
    private SharedPreferences userPreferences;
    private Profile userProfile;
    private Intent intent;

    private int selectedAccountIndex;
    private boolean containsTransfers;
    private boolean containsPayments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

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

        intent = getIntent();
        selectedAccountIndex = intent.getIntExtra("selectedAccountIndex", 0);

        txtAccountName = findViewById(R.id.txt_account_name);
        txtAccountNo = findViewById(R.id.txt_account_no);
        txtAccountBalance = findViewById(R.id.txt_to_acc);
        txtTransactionMsg = findViewById(R.id.txt_transactions_msg);

        btnPayments = findViewById(R.id.btn_payments);
        btnTransfers = findViewById(R.id.btn_transfers);

        txtNoPaymentsMsg = findViewById(R.id.txt_no_payments_msg);
        txtNoTransfersMsg = findViewById(R.id.txt_no_transfers_msg);

        txtNoTransfersMsg.setVisibility(GONE);
        txtNoPaymentsMsg.setVisibility(GONE);

        btnPayments.setOnClickListener(clickListener);
        btnTransfers.setOnClickListener(clickListener);

        lstPayments = findViewById(R.id.lst_payments);
        lstTransfers = findViewById(R.id.lst_transfers);

        btnAddDeposit = findViewById(R.id.btn_add_deposit);
        edtDepositAmount = findViewById(R.id.edt_deposit_amount);
        btnMakeDeposit = findViewById(R.id.btn_make_deposit);

        btnAddDeposit.setOnClickListener(clickListener);
        btnMakeDeposit.setOnClickListener(clickListener);

        edtDepositAmount.setVisibility(GONE);
        btnMakeDeposit.setVisibility(GONE);

        getTransactionTypes();
        checkTransactionHistory();
        setupAdapters();

        txtAccountName.setText("Name:" + " " + userProfile.getAccounts().get(selectedAccountIndex).getAccountName());
        txtAccountNo.setText("No:" + " " + userProfile.getAccounts().get(selectedAccountIndex).getAccountNo());
        txtAccountBalance.setText("Balance: $" + String.format("%.2f",userProfile.getAccounts().get(selectedAccountIndex).getAccountBalance()));
    }

    /**
     * method used to get the transaction types
     */
    private void getTransactionTypes() {
        for (int i = 0; i < userProfile.getAccounts().get(selectedAccountIndex).getTransactions().size(); i++) {
            if (userProfile.getAccounts().get(selectedAccountIndex).getTransactions().get(i).getTransactionType() == TRANSACTION_TYPE.TRANSFER) {
                containsTransfers = true;
            } else {
                containsPayments = true;
            }
        }
    }

    /**
     * method used to check the transaction history of the current account
     */
    private void checkTransactionHistory() {
        if (userProfile.getAccounts().get(selectedAccountIndex).getTransactions().size() != 0) {

            txtTransactionMsg.setVisibility(GONE);

            btnPayments.setVisibility(VISIBLE);
            btnTransfers.setVisibility(VISIBLE);

            if (containsPayments) {
                btnPayments.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                lstTransfers.setVisibility(GONE);
                lstPayments.setVisibility(VISIBLE);
            } else {
                btnTransfers.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                lstTransfers.setVisibility(VISIBLE);
                lstPayments.setVisibility(GONE);
            }

        } else {

            txtTransactionMsg.setVisibility(VISIBLE);

            btnPayments.setVisibility(GONE);
            btnTransfers.setVisibility(GONE);
            txtNoTransfersMsg.setVisibility(GONE);
            txtNoPaymentsMsg.setVisibility(GONE);
            lstPayments.setVisibility(GONE);
            lstTransfers.setVisibility(GONE);
        }
    }

    /**
     * method used to setup the adapters
     */
    private void setupAdapters() {

        ArrayList<Transaction> transactions = userProfile.getAccounts().get(selectedAccountIndex).getTransactions();
        ArrayList<Transaction> transfers = new ArrayList<>();
        ArrayList<Transaction> payments = new ArrayList<>();

        for (int i = 0; i < transactions.size(); i++) {
            if (transactions.get(i).getTransactionType() == TRANSACTION_TYPE.TRANSFER) {
                transfers.add(transactions.get(i));
            } else {
                payments.add(transactions.get(i));
            }
        }

        TransferAdapter transferAdapter = new TransferAdapter(this, R.layout.lst_transfers, transfers);
        lstTransfers.setAdapter(transferAdapter);

        PaymentAdapter paymentAdapter = new PaymentAdapter(this, R.layout.lst_payments, payments);
        lstPayments.setAdapter(paymentAdapter);
    }

    /**
     * method used to display the payments
     */
    private void displayPayments() {
        lstTransfers.setVisibility(GONE);
        txtNoTransfersMsg.setVisibility(GONE);
        btnPayments.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
        btnTransfers.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));

        if (containsPayments) {
            txtNoPaymentsMsg.setVisibility(GONE);
            lstPayments.setVisibility(VISIBLE);
        } else {
            txtNoPaymentsMsg.setVisibility(VISIBLE);
            lstPayments.setVisibility(GONE);
        }
    }

    /**
     * method used to display the transfers
     */
    private void displayTransfers() {
        lstPayments.setVisibility(GONE);
        txtNoPaymentsMsg.setVisibility(GONE);
        btnPayments.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        btnTransfers.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));

        if (containsTransfers) {
            txtNoTransfersMsg.setVisibility(GONE);
            lstTransfers.setVisibility(VISIBLE);
        } else {
            txtNoTransfersMsg.setVisibility(VISIBLE);
            lstTransfers.setVisibility(GONE);
        }

    }

    /**
     * method used to display the deposit views
     */
    private void showDepositViews() {
        btnAddDeposit.setVisibility(GONE);
        btnMakeDeposit.setVisibility(VISIBLE);
        edtDepositAmount.setVisibility(VISIBLE);
    }
    //TODO: Make separate fragment for deposits
    /**
     * method used to make a deposit
     */
    private void makeDeposit() {

        double depositAmount = Double.parseDouble(edtDepositAmount.getText().toString());

        if (edtDepositAmount.getText().toString().equals("")) {
            Toast.makeText(this, "Please enter an amount to deposit", Toast.LENGTH_SHORT).show();
        } else {
            if (depositAmount < 10) {
                Toast.makeText(this, R.string.balance_less_than_ten, Toast.LENGTH_SHORT).show();
            } else {

                userProfile.getAccounts().get(selectedAccountIndex).setAccountBalance(userProfile.getAccounts().get(selectedAccountIndex).getAccountBalance() + depositAmount);

                SharedPreferences.Editor prefsEditor = userPreferences.edit();
                gson = new Gson();
                json = gson.toJson(userProfile);
                prefsEditor.putString("LastProfileUsed", json);
                prefsEditor.commit();

                ApplicationDB applicationDb = new ApplicationDB(getApplicationContext());
                applicationDb.overwriteAccount(userProfile, userProfile.getAccounts().get(selectedAccountIndex));

                txtAccountBalance.setText("Balance: $" + String.format("%.2f",userProfile.getAccounts().get(selectedAccountIndex).getAccountBalance()));

                Toast.makeText(this, "Deposit of $" + String.format("%.2f",depositAmount) + " " + "made successfully", Toast.LENGTH_SHORT).show();

                btnAddDeposit.setVisibility(VISIBLE);
                btnMakeDeposit.setVisibility(GONE);

                edtDepositAmount.getText().clear();
                edtDepositAmount.setVisibility(GONE);
            }
        }


    }
}
