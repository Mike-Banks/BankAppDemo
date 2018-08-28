package com.example.mikebanks.bankscorpfinancial.Model;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by mikebanks on 2017-12-05.
 */


public class Profile {

    private String firstName;
    private String lastName;
    private String country;
    private String username;
    private String password;
    private ArrayList<Account> accounts;
    private ArrayList<Payee> payees;
    private long dbId;

    /**
     * Constructor used to initialize all of the values
     * @param firstName
     * @param lastName
     * @param country
     * @param username
     * @param password
     */
    public Profile (String firstName, String lastName, String country, String username, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.country = country;
        this.username = username;
        this.password = password;
        accounts = new ArrayList<>();
        payees = new ArrayList<>();
    }

    public Profile (String firstName, String lastName, String country, String username, String password, long dbId) {
        this(firstName, lastName, country, username, password);
        this.dbId = dbId;
    }

    /**
     * getters used to access the private fields of the profile
     */
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getCountry() {
        return country;
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public ArrayList<Account> getAccounts() { return accounts; }
    public ArrayList<Payee> getPayees() { return payees; }
    public long getDbId() { return dbId; }
    public void setDbId(long dbId) { this.dbId = dbId; }

    /**
     * method used to add an account to the profile
     * @param accountName
     * @param accountBalance
     */
    public void addAccount(String accountName, double accountBalance) {
        String accno = "A" + (accounts.size() + 1); //TODO: Make AccNo, ProfileID and TransID's more sophisticated- ie. add the profile id to account no, add the account no and profile id to trans id, etc
        Account account = new Account(accountName, accno, accountBalance);
        accounts.add(account);
    }

    /**
     * method used to add a payee to the profile
     * @param payeeName
     */
    public void addPayee(String payeeName) {
        String payeeID = "P" + (payees.size() + 1);
        Payee payee = new Payee(payeeID, payeeName);
        payees.add(payee);
    }
}
