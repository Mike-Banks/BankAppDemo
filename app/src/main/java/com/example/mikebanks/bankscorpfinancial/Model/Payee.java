package com.example.mikebanks.bankscorpfinancial.Model;

/**
 * Created by mikebanks on 2018-01-04.
 */

public class Payee {

    private String payeeID;
    private String payeeName;
    private long dbId;

    /**
     * Constructor used to initialize all of the values
     * @param payeeID
     * @param payeeName
     */
    public Payee (String payeeID, String payeeName) {
        this.payeeID = payeeID;
        this.payeeName = payeeName;
    }

    public Payee (String payeeID, String payeeName, long dbId) {
        this(payeeID, payeeName);
        this.dbId = dbId;
    }

    /**
     * getter used to get the name of the payee
     * @return
     */
    public String getPayeeName() {
        return payeeName;
    }
    public String getPayeeID() { return payeeID; }
    public long getDbId() { return dbId; }

    public void setDbId(long dbId) {
        this.dbId = dbId;
    }

    /**
     * method used to show the payee as a string
     * @return
     */
    public String toString() { return (payeeName + " (" + payeeID + ")"); }
}
