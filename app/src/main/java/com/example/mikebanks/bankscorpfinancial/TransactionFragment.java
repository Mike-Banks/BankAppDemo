package com.example.mikebanks.bankscorpfinancial;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mikebanks.bankscorpfinancial.Adapters.DepositAdapter;
import com.example.mikebanks.bankscorpfinancial.Adapters.PaymentAdapter;
import com.example.mikebanks.bankscorpfinancial.Adapters.TransferAdapter;
import com.example.mikebanks.bankscorpfinancial.Model.Profile;
import com.example.mikebanks.bankscorpfinancial.Model.Transaction;
import com.google.gson.Gson;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class TransactionFragment extends Fragment {

    private TextView txtAccountName;
    private TextView txtAccountNo;
    private TextView txtAccountBalance;
    private TextView txtTransactionMsg;

    private Button btnPayments;
    private Button btnTransfers;
    private Button btnDeposits;

    private TextView txtNoTransfersMsg;
    private TextView txtNoPaymentsMsg;
    private TextView txtNoDepositMsg;

    private ListView lstPayments;
    private ListView lstTransfers;
    private ListView lstDeposits;

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.btn_payments:
                    displayPayments();
                    break;
                case R.id.btn_transfers:
                    displayTransfers();
                    break;
                case R.id.btn_deposits:
                    displayDeposits();
            }
        }
    };

    private Gson gson;
    private String json;
    private SharedPreferences userPreferences;
    private Profile userProfile;

    private int selectedAccountIndex;

    private boolean containsTransfers;
    private boolean containsPayments;
    private boolean containsDeposits;

    public TransactionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();

        getActivity().setTitle("Transactions");
        selectedAccountIndex = bundle.getInt("SelectedAccount", 0);

        //TODO: Try and get more screen space for transactions
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_transaction, container, false);

        txtAccountName = rootView.findViewById(R.id.txt_account_name);
        txtAccountNo = rootView.findViewById(R.id.txt_account_no);
        txtAccountBalance = rootView.findViewById(R.id.txt_to_acc);
        txtTransactionMsg = rootView.findViewById(R.id.txt_transactions_msg);

        btnPayments = rootView.findViewById(R.id.btn_payments);
        btnTransfers = rootView.findViewById(R.id.btn_transfers);
        btnDeposits = rootView.findViewById(R.id.btn_deposits);

        txtNoPaymentsMsg = rootView.findViewById(R.id.txt_no_payments_msg);
        txtNoTransfersMsg = rootView.findViewById(R.id.txt_no_transfers_msg);
        txtNoDepositMsg = rootView.findViewById(R.id.txt_no_deposits_msg);

        lstPayments = rootView.findViewById(R.id.lst_payments);
        lstTransfers = rootView.findViewById(R.id.lst_transfers);
        lstDeposits = rootView.findViewById(R.id.lst_deposits);

        ((DrawerActivity) getActivity()).showUpButton();

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

        txtNoTransfersMsg.setVisibility(GONE);
        txtNoPaymentsMsg.setVisibility(GONE);
        txtNoDepositMsg.setVisibility(GONE);

        btnPayments.setOnClickListener(clickListener);
        btnTransfers.setOnClickListener(clickListener);
        btnDeposits.setOnClickListener(clickListener);

        getTransactionTypes();
        checkTransactionHistory();
        setupAdapters();

        txtAccountName.setText("Account Name:" + " " + userProfile.getAccounts().get(selectedAccountIndex).getAccountName());
        txtAccountNo.setText("Account No:" + " " + userProfile.getAccounts().get(selectedAccountIndex).getAccountNo());
        txtAccountBalance.setText("Account Balance: $" + String.format("%.2f",userProfile.getAccounts().get(selectedAccountIndex).getAccountBalance()));
    }

    /**
     * method used to get the transaction types
     */
    private void getTransactionTypes() {
        for (int i = 0; i < userProfile.getAccounts().get(selectedAccountIndex).getTransactions().size(); i++) {
            if (userProfile.getAccounts().get(selectedAccountIndex).getTransactions().get(i).getTransactionType() == Transaction.TRANSACTION_TYPE.TRANSFER) {
                containsTransfers = true;
            } else if (userProfile.getAccounts().get(selectedAccountIndex).getTransactions().get(i).getTransactionType() == Transaction.TRANSACTION_TYPE.PAYMENT) {
                containsPayments = true;
            } else {
                containsDeposits = true;
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
            btnDeposits.setVisibility(VISIBLE);

            if (containsPayments) {
                btnPayments.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                lstTransfers.setVisibility(GONE);
                lstPayments.setVisibility(VISIBLE);
                lstDeposits.setVisibility(GONE);
            } else if (containsTransfers){
                btnTransfers.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                lstTransfers.setVisibility(VISIBLE);
                lstPayments.setVisibility(GONE);
                lstDeposits.setVisibility(GONE);
            } else {
                btnDeposits.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                lstDeposits.setVisibility(VISIBLE);
                lstPayments.setVisibility(GONE);
                lstTransfers.setVisibility(GONE);
            }

        } else {

            txtTransactionMsg.setVisibility(VISIBLE);

            btnPayments.setVisibility(GONE);
            btnTransfers.setVisibility(GONE);
            btnDeposits.setVisibility(GONE);

            txtNoTransfersMsg.setVisibility(GONE);
            txtNoPaymentsMsg.setVisibility(GONE);
            txtNoDepositMsg.setVisibility(GONE);

            lstPayments.setVisibility(GONE);
            lstTransfers.setVisibility(GONE);
            lstDeposits.setVisibility(GONE);
        }
    }

    //TODO: Have all transactions under the same adapter and ListView? Tricky, do some research - dynamically add TextViews? - have filtering options to only see them of one type?
    /**
     * method used to setup the adapters
     */
    private void setupAdapters() {

        ArrayList<Transaction> transactions = userProfile.getAccounts().get(selectedAccountIndex).getTransactions();
        ArrayList<Transaction> transfers = new ArrayList<>();
        ArrayList<Transaction> payments = new ArrayList<>();
        ArrayList<Transaction> deposits = new ArrayList<>();

        for (int i = 0; i < transactions.size(); i++) {
            if (transactions.get(i).getTransactionType() == Transaction.TRANSACTION_TYPE.TRANSFER) {
                transfers.add(transactions.get(i));
            } else if (transactions.get(i).getTransactionType() == Transaction.TRANSACTION_TYPE.PAYMENT){
                payments.add(transactions.get(i));
            } else {
                deposits.add(transactions.get(i));
            }
        }

        TransferAdapter transferAdapter = new TransferAdapter(getActivity(), R.layout.lst_transfers, transfers);
        lstTransfers.setAdapter(transferAdapter);

        PaymentAdapter paymentAdapter = new PaymentAdapter(getActivity(), R.layout.lst_payments, payments);
        lstPayments.setAdapter(paymentAdapter);

        DepositAdapter depositAdapter = new DepositAdapter(getActivity(), R.layout.lst_deposits, deposits);
        lstDeposits.setAdapter(depositAdapter);
    }

    /**
     * method used to display the payments
     */
    private void displayPayments() {

        lstTransfers.setVisibility(GONE);
        lstDeposits.setVisibility(GONE);

        txtNoTransfersMsg.setVisibility(GONE);
        txtNoDepositMsg.setVisibility(GONE);

        btnPayments.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
        btnTransfers.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        btnDeposits.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));

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
        lstDeposits.setVisibility(GONE);

        txtNoPaymentsMsg.setVisibility(GONE);
        txtNoDepositMsg.setVisibility(GONE);

        btnPayments.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        btnTransfers.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
        btnDeposits.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));

        if (containsTransfers) {
            txtNoTransfersMsg.setVisibility(GONE);
            lstTransfers.setVisibility(VISIBLE);
        } else {
            txtNoTransfersMsg.setVisibility(VISIBLE);
            lstTransfers.setVisibility(GONE);
        }

    }

    private void displayDeposits() {
        lstTransfers.setVisibility(GONE);
        lstPayments.setVisibility(GONE);

        txtNoPaymentsMsg.setVisibility(GONE);
        txtNoTransfersMsg.setVisibility(GONE);

        btnPayments.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        btnTransfers.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        btnDeposits.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));

        if (containsDeposits) {
            txtNoDepositMsg.setVisibility(GONE);
            lstDeposits.setVisibility(VISIBLE);
        } else {
            txtNoDepositMsg.setVisibility(VISIBLE);
            lstDeposits.setVisibility(GONE);
        }

    }

}
