package com.chip.parkpro1.data.models;

public class Order {

    private int amount;
    private String type;
    private boolean succeeded;
    private String codeContect;
    private long date;

    private long fromDate;
    private long toDate;

    public Order(){

    }

    public Order(int amount, String type, boolean succeeded, String codeContect, long date) {
        this.amount = amount;
        this.type = type;
        this.succeeded = succeeded;
        this.codeContect = codeContect;
        this.date = date;
    }

    public int getAmmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isSucceeded() {
        return succeeded;
    }

    public void setSucceeded(boolean succeeded) {
        this.succeeded = succeeded;
    }

    public String getCodeContect() {
        return codeContect;
    }

    public void setCodeContect(String codeContect) {
        this.codeContect = codeContect;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getFromDate() {
        return fromDate;
    }

    public long getToDate() {
        return toDate;
    }

    public void setFromDate(long fromDate) {
        this.fromDate = fromDate;
    }

    public void setToDate(long toDate) {
        this.toDate = toDate;
    }
}