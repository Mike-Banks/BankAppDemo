package com.example.mikebanks.bankscorpfinancial;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mikebanks.bankscorpfinancial.Model.Account;
import com.example.mikebanks.bankscorpfinancial.Model.Profile;
import com.google.gson.Gson;

import java.util.ArrayList;

public class TransferActivity extends Activity {

    private LinearLayout linTransferInfo;
    private Spinner spnSendingAccount;
    private EditText edtTransferAmount;
    private Spinner spnReceivingAccount;
    private Button btnConfirmTransfer;

    ArrayList<Account> accounts;
    ArrayAdapter<Account> accountAdapter;

    SharedPreferences userPreferences;
    Gson gson;
    String json;
    Profile userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        setValues();
    }

    /**
     * method used to setup the values for the views and fields
     */
    private void setValues() {

        userPreferences = getSharedPreferences("userPreferences", MODE_PRIVATE);

        gson = new Gson();
        json = userPreferences.getString("LastProfileUsed", "");
        userProfile = gson.fromJson(json, Profile.class);

        linTransferInfo = findViewById(R.id.lin_transfer_info);
        spnSendingAccount = findViewById(R.id.spn_select_sending_acc);
        edtTransferAmount = findViewById(R.id.edt_transfer_amount);
        spnReceivingAccount = findViewById(R.id.spn_select_receiving_acc);
        btnConfirmTransfer = findViewById(R.id.btn_confirm_transfer);

        btnConfirmTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmTransfer();
            }
        });

        setAdapters();
    }

    /**
     * method that sets up the adapters
     */
    private void setAdapters() {
        accounts = userProfile.getAccounts();
        accountAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, accounts);
        accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnSendingAccount.setAdapter(accountAdapter);
        spnReceivingAccount.setAdapter(accountAdapter);
        spnReceivingAccount.setSelection(1);
    }

    /**
     * method that confirms the transfer
     */
    private void confirmTransfer() {

        int receivingAccIndex = spnReceivingAccount.getSelectedItemPosition();

        if (spnSendingAccount.getSelectedItemPosition() == receivingAccIndex) {
            Toast.makeText(this, "You cannot transfer to the same account", Toast.LENGTH_SHORT).show();
        } else if (edtTransferAmount.getText().toString().equals("")) {
            Toast.makeText(this, "Please enter an amount to transfer", Toast.LENGTH_SHORT).show();

        } else if(Double.parseDouble(edtTransferAmount.getText().toString()) < 1) {
            Toast.makeText(this, "The minimum amount for a transfer is $1", Toast.LENGTH_SHORT).show();

        } else if (Double.parseDouble(edtTransferAmount.getText().toString()) > userProfile.getAccounts().get(spnSendingAccount.getSelectedItemPosition()).getAccountBalance()) {

            Account acc = (Account) spnSendingAccount.getSelectedItem();
            Toast.makeText(this, "The account," + " " + acc.toString() + " " + "does not have sufficient funds to make this transfer", Toast.LENGTH_LONG).show();
        } else {

            int sendingAccIndex = spnSendingAccount.getSelectedItemPosition();

            Account sendingAcc = (Account) spnSendingAccount.getSelectedItem();
            Account receivingAcc = (Account) spnReceivingAccount.getSelectedItem();

            Double transferAmount = Double.parseDouble(edtTransferAmount.getText().toString());

            sendingAcc.setAccountBalance(sendingAcc.getAccountBalance() - transferAmount);
            receivingAcc.setAccountBalance(receivingAcc.getAccountBalance() + transferAmount);

            String sendingAccString = sendingAcc.toTransactionString();
            String receivingAccString = receivingAcc.toTransactionString();

            userProfile.getAccounts().get(sendingAccIndex).addTransferTransaction(sendingAccString, receivingAccString, transferAmount);
            userProfile.getAccounts().get(receivingAccIndex).addTransferTransaction(sendingAccString, receivingAccString, transferAmount);

            accounts = userProfile.getAccounts();
            spnSendingAccount.setAdapter(accountAdapter);
            spnReceivingAccount.setAdapter(accountAdapter);

            spnSendingAccount.setSelection(sendingAccIndex);
            spnReceivingAccount.setSelection(receivingAccIndex);

            SharedPreferences.Editor prefsEditor = userPreferences.edit();
            gson = new Gson();
            json = gson.toJson(userProfile);
            prefsEditor.putString("UserProfile", json);
            prefsEditor.commit();

            Toast.makeText(this, "Transfer of $" + String.format("%.2f",transferAmount) + " successfully made", Toast.LENGTH_SHORT).show();
        }
    }
}
