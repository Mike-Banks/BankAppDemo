package com.example.mikebanks.bankscorpfinancial;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mikebanks.bankscorpfinancial.Adapters.AccountAdapter;
import com.example.mikebanks.bankscorpfinancial.Model.Account;
import com.example.mikebanks.bankscorpfinancial.Model.Profile;
import com.example.mikebanks.bankscorpfinancial.Model.db.ApplicationDB;
import com.google.gson.Gson;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class AccountOverviewFragment extends Fragment {

    private FloatingActionButton fab;
    private ListView lstAccounts;
    private TextView txtTitleMessage;
    private TextView txtDetailMessage;
    private EditText edtAccountName;
    private EditText edtInitAccountBalance;
    private Button btnCancel;
    private Button btnAddAccount;

    private Gson gson;
    private Profile userProfile;
    private SharedPreferences userPreferences;

    private boolean displayAccountDialogOnLaunch;

    private View.OnClickListener addAccountClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == btnCancel.getId()) {
                accountDialog.dismiss();
                Toast.makeText(getActivity(), "Account Creation Cancelled", Toast.LENGTH_SHORT).show();
            } else if (view.getId() == btnAddAccount.getId()) {
                addAccount();
            }
        }
    };

    private Dialog accountDialog;

    //TODO D1: Add functionality to remove accounts (note: ensure i remove from database as well (restructure db when removed)
    //TODO D2: Add functionality to remove payees and profiles as well
    //TODO D3: Add swiping functionality so that the user can swipe on the account to remove or see details

    private int selectedAccountIndex;

    public AccountOverviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();

        displayAccountDialogOnLaunch = false;

        if (bundle != null) {
            displayAccountDialogOnLaunch = bundle.getBoolean("DisplayAccountDialog", false);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_account_overview, container, false);

        fab = rootView.findViewById(R.id.floating_action_btn);

        lstAccounts = rootView.findViewById(R.id.lst_accounts);
        txtTitleMessage = rootView.findViewById(R.id.txt_title_msg);
        txtDetailMessage = rootView.findViewById(R.id.txt_details_msg);

        getActivity().setTitle("Accounts");
        ((DrawerActivity) getActivity()).showDrawerButton();

        setValues();

        if (displayAccountDialogOnLaunch) {
            displayAccountDialog();
            displayAccountDialogOnLaunch = false;
        }
        return rootView;
    }

    private void displayAccountDialog() {

        accountDialog = new Dialog(getActivity());
        accountDialog.setContentView(R.layout.account_dialog);

        accountDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        accountDialog.setCanceledOnTouchOutside(true);
        accountDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
            Toast.makeText(getActivity(), "Account Creation Cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        edtAccountName = accountDialog.findViewById(R.id.edt_payee_name);
        edtInitAccountBalance = accountDialog.findViewById(R.id.edt_init_bal);

        btnCancel = accountDialog.findViewById(R.id.btn_cancel_dialog);
        btnAddAccount = accountDialog.findViewById(R.id.btn_add_payee);

        btnCancel.setOnClickListener(addAccountClickListener);
        btnAddAccount.setOnClickListener(addAccountClickListener);

        accountDialog.show();

    }

    private void setValues() {
        selectedAccountIndex = 0;

        userPreferences = this.getActivity().getSharedPreferences("LastProfileUsed", MODE_PRIVATE);
        gson = new Gson();
        String json = userPreferences.getString("LastProfileUsed", "");
        userProfile = gson.fromJson(json, Profile.class);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userProfile.getAccounts().size() >= 10) {
                    Toast.makeText(getActivity(), "You have reached the maximum amount of accounts (10)", Toast.LENGTH_SHORT).show();
                } else {
                    displayAccountDialog();
                }
            }
        });

        //TODO: Add this code elsewhere and check when they remove accounts, if it is their last account. If it is, run this code
        if (userProfile.getAccounts().size() == 0) {
            txtTitleMessage.setText("Add an Account with the button below");
            txtDetailMessage.setVisibility(View.GONE);
            lstAccounts.setVisibility(View.GONE);
        } else {
            txtTitleMessage.setText("Select an Account to view Transactions");
            txtDetailMessage.setVisibility(View.VISIBLE);
            lstAccounts.setVisibility(View.VISIBLE);
        }

        AccountAdapter adapter = new AccountAdapter(this.getActivity(), R.layout.lst_accounts, userProfile.getAccounts());
        lstAccounts.setAdapter(adapter);

        lstAccounts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedAccountIndex = i;
                viewAccount();
            }
        });
    }

    /**
     * method used to view an account
     */
    private void viewAccount() {
        TransactionFragment transactionsFragment = new TransactionFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("SelectedAccount", selectedAccountIndex);

        transactionsFragment.setArguments(bundle);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.flContent, transactionsFragment,"findThisFragment")
                .addToBackStack(null)
                .commit();
    }

    /**
     * method used to add an account
     */
    private void addAccount() {

        String balance = edtInitAccountBalance.getText().toString();
        boolean isNum = false;
        double initDepositAmount = 0;

        if (!(edtAccountName.getText().toString().equals(""))) {

            try {
                initDepositAmount = Double.parseDouble(edtInitAccountBalance.getText().toString());
                isNum = true;
            } catch (Exception e) {
                if (!edtInitAccountBalance.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                    edtInitAccountBalance.getText().clear();
                }
            }

            if (edtAccountName.getText().toString().length() > 10) {

                Toast.makeText(this.getActivity(), R.string.account_name_exceeds_char, Toast.LENGTH_SHORT).show();
                edtAccountName.getText().clear();

            } else if ((isNum) || balance.equals("")) {

                boolean match = false;

                for (int i = 0; i < userProfile.getAccounts().size(); i++) {
                    if (edtAccountName.getText().toString().equalsIgnoreCase(userProfile.getAccounts().get(i).getAccountName())) {
                        match = true;
                    }
                }

                if (!match) {

                    ApplicationDB applicationDb = new ApplicationDB(getActivity().getApplicationContext());

                    userProfile.addAccount(edtAccountName.getText().toString(), 0);

                    if (!balance.equals("")) {
                        if (isNum) {
                           if (initDepositAmount >= 0.01) {
                               userProfile.getAccounts().get(userProfile.getAccounts().size()-1).addDepositTransaction(initDepositAmount);
                               applicationDb.saveNewTransaction(userProfile, userProfile.getAccounts().get(userProfile.getAccounts().size()-1).getAccountNo(), userProfile.getAccounts().get(userProfile.getAccounts().size()-1).getTransactions().get(userProfile.getAccounts().get(userProfile.getAccounts().size()-1).getTransactions().size()-1));
                           }
                        }
                    }

                    applicationDb.saveNewAccount(userProfile, userProfile.getAccounts().get(userProfile.getAccounts().size()-1));

                    Toast.makeText(this.getActivity(), R.string.acc_saved_successfully, Toast.LENGTH_SHORT).show();

                    if (userProfile.getAccounts().size() == 1) {
                        txtTitleMessage.setText("Select an Account to view Transactions");
                        txtDetailMessage.setVisibility(View.VISIBLE);
                        lstAccounts.setVisibility(View.VISIBLE);
                    }
                    ArrayList<Account> accounts = userProfile.getAccounts();

                    AccountAdapter adapter = new AccountAdapter(getActivity(), R.layout.lst_accounts, accounts);
                    lstAccounts.setAdapter(adapter);

                    SharedPreferences.Editor prefsEditor = userPreferences.edit();
                    String json = gson.toJson(userProfile);
                    prefsEditor.putString("LastProfileUsed", json).apply();

                    accountDialog.dismiss();

                } else {
                    Toast.makeText(this.getActivity(), R.string.account_name_error, Toast.LENGTH_SHORT).show();
                    edtAccountName.getText().clear();
                }
            }
        } else {
            Toast.makeText(getActivity(), "Please enter an account name", Toast.LENGTH_SHORT).show();
        }
    }

}
