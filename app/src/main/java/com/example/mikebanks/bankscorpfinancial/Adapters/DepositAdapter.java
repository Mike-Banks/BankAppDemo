package com.example.mikebanks.bankscorpfinancial.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.mikebanks.bankscorpfinancial.Model.Transaction;
import com.example.mikebanks.bankscorpfinancial.R;

import java.util.ArrayList;

public class DepositAdapter extends ArrayAdapter<Transaction> {

    private Context context;
    private int resource;

    public DepositAdapter(Context context, int resource, ArrayList<Transaction> transactions) {
        super(context, resource, transactions);

        this.context = context;
        this.resource = resource;
    }

    /**
     * function used to get the view from the adapter
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView (int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(resource, parent, false);
        }

        Transaction deposit = getItem(position);

        TextView txtTransactionID = convertView.findViewById(R.id.txt_transaction_id);
        txtTransactionID.setText(deposit.getTransactionType() + ": " + deposit.getTransactionID());

        TextView txtTimestamp = convertView.findViewById(R.id.txt_timestamp);
        txtTimestamp.setText(deposit.getTimestamp());

        TextView txtAmount = convertView.findViewById(R.id.txt_amount);
        txtAmount.setText("Amount: $" + String.format("%.2f",deposit.getAmount()));

        return convertView;
    }
}
