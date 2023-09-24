package com.example.expensestracker.Model;

public class data {

    private int amount;
    private String purpose;
    private String note;
    private  String id;
    private String data;

    public data(int amount, String purpose, String note, String id, String data) {
        this.amount = amount;
        this.purpose = purpose;
        this.note = note;
        this.id = id;
        this.data = data;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getAmount() {
        return amount;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getNote() {
        return note;
    }

    public String getId() {
        return id;
    }

    public String getData() {
        return data;
    }

    public data(){

    }
}
