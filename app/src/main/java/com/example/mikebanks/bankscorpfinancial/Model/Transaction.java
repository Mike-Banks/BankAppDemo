package com.example.mikebanks.bankscorpfinancial.Model;

/**
 * Created by mikebanks on 2018-01-04.
 */

public class Transaction {

    public enum TRANSACTION_TYPE {
        PAYMENT,
        TRANSFER
    }

    private String transactionID;
    private String sendingAccount;
    private String destinationAccount;
    private String payee;
    private double amount;
    private TRANSACTION_TYPE transactionType;
    private long dbId;

    /**
     * Constructor used to initialize all of the values - used for creating payments
     * @param transactionID
     * @param payee
     * @param amount
     */
    public Transaction (String transactionID, String payee, double amount) {
        this.transactionID = transactionID;
        this.payee = payee;
        this.amount = amount;
        transactionType = TRANSACTION_TYPE.PAYMENT;
    }

    public Transaction (String transactionID, String payee, double amount, long dbId) {
        this(transactionID, payee, amount);
        this.dbId = dbId;
    }

    /**
     * Constructor used to initialize all of the values - used for creating transfers
     * @param transactionID
     * @param sendingAccount
     * @param destinationAccount
     * @param amount
     */
    public Transaction(String transactionID, String sendingAccount, String destinationAccount, double amount) {
        this.transactionID = transactionID;
        this.sendingAccount = sendingAccount;
        this.destinationAccount = destinationAccount;
        this.amount = amount;
        transactionType = TRANSACTION_TYPE.TRANSFER;
    }

    public Transaction(String transactionID, String sendingAccount, String destinationAccount, double amount, long dbId) {
        this(transactionID, sendingAccount, destinationAccount, amount);
        this.dbId = dbId;
    }

    /**
     * getters used to access the private fields of the transaction
     */
    public String getTransactionID() { return transactionID; }
    public String getSendingAccount() {
        return sendingAccount;
    }
    public String getDestinationAccount() {
        return destinationAccount;
    }
    public String getPayee() { return payee; }
    public double getAmount() {
        return amount;
    }
    public TRANSACTION_TYPE getTransactionType() {
        return transactionType;
    }

    public long getDbId() { return dbId; }
    public void setDbId(long dbId) { this.dbId = dbId; }

}
