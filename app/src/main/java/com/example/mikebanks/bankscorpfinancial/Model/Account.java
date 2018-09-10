package com.example.mikebanks.bankscorpfinancial.Model;

import java.util.ArrayList;

/**
 * Class used to create an account for the user
 */

public class Account {

    private String accountName;
    private String accountNo;
    private double accountBalance;
    private ArrayList<Transaction> transactions;
    private long dbID;

    /**
     * Constructor used to initialize all of the values
     * @param accountName
     * @param accountNo
     * @param accountBalance
     */
    public Account (String accountName, String accountNo, double accountBalance) {
        this.accountName = accountName;
        this.accountNo = accountNo;
        this.accountBalance = accountBalance;
        transactions = new ArrayList<>();
    }

    public Account (String accountName, String accountNo, double accountBalance, long dbID) {
        this(accountName, accountNo, accountBalance);
        this.dbID = dbID;
    }

    /**
     * Getters for the account name, number and balance
     */
    public String getAccountName() {
        return accountName;
    }
    public String getAccountNo() {
        return accountNo;
    }
    public double getAccountBalance() {
        return accountBalance;
    }
    public long getDbID() { return dbID; }

    public void setDbID(long dbID) { this.dbID = dbID; }

    /**
     * Setter for setting the account balance
     * @param accountBalance
     */
    public void setAccountBalance(double accountBalance) { this.accountBalance = accountBalance; }

    /**
     * method used to get the array list of transactions
     * @return
     */
    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    /**
     * method used to add a payment
     * @param payee
     * @param amount
     */
    public void addPaymentTransaction (String payee, double amount) {
        accountBalance -= amount;
        Transaction payment = new Transaction("P-T" + (transactions.size() + 1), payee, amount);
        transactions.add(payment);
    }

    public void addDepositTransaction(double amount) {
        accountBalance += amount;
        //TODO: Loop through transactions, find how many deposits there are: Trans ID: T1-D1 (# of trans - # of deposits)
        Transaction deposit = new Transaction("D-T" + (transactions.size() + 1), amount);
        transactions.add(deposit);
    }

    /**
     * method used to show the account as a string
     * @return
     */
    public String toString() {
        return (accountName + " ($" + String.format("%.2f",accountBalance) + ")");
    }

    /**
     * method used to show the account as a string for transactions
     * @return
     */
    public String toTransactionString() { return (accountName + " (" + accountNo + ")"); }

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }
}
