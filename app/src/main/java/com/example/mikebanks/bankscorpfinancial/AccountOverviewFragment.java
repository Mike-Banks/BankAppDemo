package com.example.mikebanks.bankscorpfinancial;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
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

    private View.OnClickListener addAccountClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == btnCancel.getId()) {
                accountDialog.dismiss();
                Toast.makeText(getActivity(), "Account Creation Cancelled", Toast.LENGTH_SHORT).show();
            } else if (view.getId() == btnAddAccount.getId()) {
                addAccount();
                accountDialog.dismiss();
            }
        }
    };

    private Dialog accountDialog;

    //TODO C2: Additionally, if user comes here from the transfer dialog (only one account), automatically open the dialog (or have the dialog appear in the drawerActivity)
    //TODO D1: Add functionality to remove accounts (note: ensure i remove from database as well (restructure db when removed)
    //TODO D2: Add functionality to remove payees and profiles as well
    //TODO D3: Add swiping functionality so that the user can swipe on the account to remove or see details

    //KEEP ME
    private int selectedAccountIndex;

    public AccountOverviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_account_overview, container, false);

        FloatingActionButton fab = rootView.findViewById(R.id.floating_action_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayAccountDialog();
            }
        });

        lstAccounts = rootView.findViewById(R.id.lst_accounts);
        txtTitleMessage = rootView.findViewById(R.id.txt_title_msg);
        txtDetailMessage = rootView.findViewById(R.id.txt_details_msg);

        ((DrawerActivity) getActivity()).showDrawerButton();

        setValues();

        return rootView;
    }

    private void displayAccountDialog() {

        accountDialog = new Dialog(this.getActivity());
        accountDialog.setContentView(R.layout.account_dialog);


        accountDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        accountDialog.setCanceledOnTouchOutside(true);
        accountDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
            Toast.makeText(getActivity(), "Account Creation Cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        edtAccountName = accountDialog.findViewById(R.id.edt_acc_name);
        edtInitAccountBalance = accountDialog.findViewById(R.id.edt_init_bal); //TODO: Add initial balance to deposit transactions

        btnCancel = accountDialog.findViewById(R.id.btn_cancel_dialog);
        btnAddAccount = accountDialog.findViewById(R.id.btn_add_acc);

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

        //TODO: Pass the selectedAccountIndex to the TransactionFragment - research passing data between fragments
    }

    /**
     * method used to add an account
     */
    private void addAccount() {

        int accountNum = userProfile.getAccounts().size();

        if (!(edtAccountName.getText().toString().equals("") || edtInitAccountBalance.getText().toString().equals(""))) {

            if (Double.parseDouble(edtInitAccountBalance.getText().toString()) < 10) {
                Toast.makeText(this.getActivity(), R.string.balance_less_than_ten, Toast.LENGTH_SHORT).show();
            } else if (edtAccountName.getText().toString().length() > 10) {

                Toast.makeText(this.getActivity(), R.string.account_name_exceeds_char, Toast.LENGTH_SHORT).show();

            } else {

                boolean match = false;

                for (int i = 0; i < userProfile.getAccounts().size(); i++) {
                    if (edtAccountName.getText().toString().equalsIgnoreCase(userProfile.getAccounts().get(i).getAccountName())) {
                        match = true;
                    }
                }

                if (!match) {

                    if (accountNum < 10) {

                        ApplicationDB applicationDb = new ApplicationDB(getActivity().getApplicationContext());

                        userProfile.addAccount(edtAccountName.getText().toString(), Double.parseDouble(edtInitAccountBalance.getText().toString()));

                        applicationDb.saveNewAccount(userProfile, userProfile.getAccounts().get(accountNum));

                        //edtAccountName.setText("");
                        //edtAccountAmount.setText("");

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
                    } else {

                        Toast.makeText(this.getActivity(), "You have reached the maximum amount of accounts (10)", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(this.getActivity(), R.string.account_name_error, Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(this.getActivity(), R.string.acc_fields_incomplete, Toast.LENGTH_SHORT).show();
        }
    }

}
