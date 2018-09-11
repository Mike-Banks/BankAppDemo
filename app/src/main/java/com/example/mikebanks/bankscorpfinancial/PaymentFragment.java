package com.example.mikebanks.bankscorpfinancial;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mikebanks.bankscorpfinancial.Model.Account;
import com.example.mikebanks.bankscorpfinancial.Model.Payee;
import com.example.mikebanks.bankscorpfinancial.Model.Profile;
import com.example.mikebanks.bankscorpfinancial.Model.db.ApplicationDB;
import com.google.gson.Gson;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class PaymentFragment extends Fragment {

    private Spinner spnSelectAccount;
    private TextView txtNoPayeesMsg;
    private Spinner spnSelectPayee;
    private EditText edtPaymentAmount;
    private Button btnMakePayment;
    private FloatingActionButton btnAddPayee;

    private Dialog payeeDialog;
    private EditText edtPayeeName;
    private Button btnCancel;
    private Button btnConfirmAddPayee;

    private View.OnClickListener addPayeeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == btnCancel.getId()) {
                payeeDialog.dismiss();
                Toast.makeText(getActivity(), "Payee Creation Cancelled", Toast.LENGTH_SHORT).show();
            } else if (view.getId() == btnConfirmAddPayee.getId()) {
                addPayee();
            }
        }
    };

    private ArrayList<Account> accounts;
    private ArrayAdapter<Account> accountAdapter;

    private ArrayList<Payee> payees;
    private ArrayAdapter<Payee> payeeAdapter;

    View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == btnMakePayment.getId()) {
                makePayment();
            }
        }
    };

    private DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            if (i == DialogInterface.BUTTON_POSITIVE) {
                addPayee();
            }
        }
    };

    private Gson gson;
    private String json;
    private Profile userProfile;
    private SharedPreferences userPreferences;

    public PaymentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_payment, container, false);

        spnSelectAccount = rootView.findViewById(R.id.spn_select_acc);
        txtNoPayeesMsg = rootView.findViewById(R.id.txt_no_payees);
        spnSelectPayee = rootView.findViewById(R.id.spn_select_payee);
        edtPaymentAmount = rootView.findViewById(R.id.edt_payment_amount);
        btnMakePayment = rootView.findViewById(R.id.btn_make_payment);
        btnAddPayee = rootView.findViewById(R.id.floating_action_btn);

        setValues();

        return rootView;
    }

    /**
     * method used to setup the values for the views and fields
     */
    private void setValues() {

        userPreferences = getActivity().getSharedPreferences("LastProfileUsed", MODE_PRIVATE);
        gson = new Gson();
        json = userPreferences.getString("LastProfileUsed", "");
        userProfile = gson.fromJson(json, Profile.class);

        btnMakePayment.setOnClickListener(buttonClickListener);

        btnAddPayee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayPayeeDialog();
            }
        });

        accounts = userProfile.getAccounts();
        accountAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, accounts);
        accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnSelectAccount.setAdapter(accountAdapter);

        payees = userProfile.getPayees();

        payeeAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, payees);
        payeeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnSelectPayee.setAdapter(payeeAdapter);

        checkPayeeInformation();
    }

    private void displayPayeeDialog() {

        payeeDialog = new Dialog(getActivity());
        payeeDialog.setContentView(R.layout.payee_dialog);

        payeeDialog.setCanceledOnTouchOutside(true);
        payeeDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Toast.makeText(getActivity(), "Payee Addition Cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        edtPayeeName = payeeDialog.findViewById(R.id.edt_payee_name);

        btnCancel = payeeDialog.findViewById(R.id.btn_cancel_dialog);
        btnConfirmAddPayee = payeeDialog.findViewById(R.id.btn_add_payee);

        btnCancel.setOnClickListener(addPayeeClickListener);
        btnConfirmAddPayee.setOnClickListener(addPayeeClickListener);

        payeeDialog.show();
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

        boolean isNum = false;

        try {
            Double.parseDouble(edtPaymentAmount.getText().toString());
            if (Double.parseDouble(edtPaymentAmount.getText().toString()) >= 0.01) {
                isNum = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isNum) {

            int selectedAccountIndex = spnSelectAccount.getSelectedItemPosition();

            if (Double.parseDouble(edtPaymentAmount.getText().toString()) > (userProfile.getAccounts().get(selectedAccountIndex)
                    .getAccountBalance())) {

                Toast.makeText(getActivity(), "You do not have sufficient funds to make this payment", Toast.LENGTH_SHORT).show();
            } else {

                int selectedPayeeIndex = spnSelectPayee.getSelectedItemPosition();

                String selectedPayee = userProfile.getPayees().get(selectedPayeeIndex).toString();
                Double amount = Double.parseDouble(edtPaymentAmount.getText().toString());

                userProfile.getAccounts().get(selectedAccountIndex).addPaymentTransaction(selectedPayee, amount);

                accounts = userProfile.getAccounts();
                spnSelectAccount.setAdapter(accountAdapter);
                spnSelectAccount.setSelection(selectedAccountIndex);

                ApplicationDB applicationDb = new ApplicationDB(getActivity().getApplicationContext());
                applicationDb.saveNewTransaction(userProfile, userProfile.getAccounts().get(selectedAccountIndex).toTransactionString(), userProfile.getAccounts().get(selectedAccountIndex).getTransactions().get(userProfile.getAccounts().get(selectedAccountIndex).getTransactions().size()-1));
                applicationDb.overwriteAccount(userProfile, userProfile.getAccounts().get(selectedAccountIndex));

                SharedPreferences.Editor prefsEditor = userPreferences.edit();
                gson = new Gson();
                json = gson.toJson(userProfile);
                prefsEditor.putString("LastProfileUsed", json).apply();

                Toast.makeText(getActivity(), "Payment of $" + String.format("%.2f", amount) + " successfully made", Toast.LENGTH_SHORT).show();
                edtPaymentAmount.getText().clear();
            }
        } else {
            Toast.makeText(getActivity(), "Please enter a valid number, greater than $0.01", Toast.LENGTH_SHORT).show();
            edtPaymentAmount.getText().clear();
        }
    }

    /**
     * method that adds a payee
     */
    private void addPayee() {
        if (!(edtPayeeName.getText().toString().equals(""))) {

            boolean match = false;
            for (int i = 0; i < userProfile.getPayees().size(); i++) {
                if (edtPayeeName.getText().toString().equalsIgnoreCase(userProfile.getPayees().get(i).getPayeeName())) {
                    match = true;
                }
            }

            if (!match) {
                userProfile.addPayee(edtPayeeName.getText().toString());

                edtPayeeName.setText("");

                txtNoPayeesMsg.setVisibility(GONE);
                spnSelectPayee.setVisibility(VISIBLE);
                edtPaymentAmount.setVisibility(VISIBLE);
                btnMakePayment.setVisibility(VISIBLE);

                payees = userProfile.getPayees();
                spnSelectPayee.setAdapter(payeeAdapter);
                spnSelectPayee.setSelection(userProfile.getPayees().size()-1);

                ApplicationDB applicationDb = new ApplicationDB(getActivity().getApplicationContext());
                applicationDb.saveNewPayee(userProfile, userProfile.getPayees().get(userProfile.getPayees().size()-1));

                SharedPreferences.Editor prefsEditor = userPreferences.edit();
                gson = new Gson();
                json = gson.toJson(userProfile);
                prefsEditor.putString("LastProfileUsed", json).apply();

                Toast.makeText(getActivity(), "Payee Added Successfully", Toast.LENGTH_SHORT).show();

                payeeDialog.dismiss();

            } else {
                Toast.makeText(getActivity(), "A Payee with that name already exists", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
