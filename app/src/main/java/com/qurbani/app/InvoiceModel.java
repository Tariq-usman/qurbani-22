package com.qurbani.app;

import java.io.Serializable;

public class InvoiceModel implements Serializable {
    private String receiptNo,date,authPerson,authPersonNo,personName,personNo,cowNo,cowShareNo,time,day,amount,receivedBy;

    public InvoiceModel(String receiptNo, String date, String authPerson, String authPersonNo, String personName, String personNo, String cowNo, String cowShareNo, String time, String day, String amount, String receivedBy) {
        this.receiptNo = receiptNo;
        this.date = date;
        this.authPerson = authPerson;
        this.authPersonNo = authPersonNo;
        this.personName = personName;
        this.personNo = personNo;
        this.cowNo = cowNo;
        this.cowShareNo = cowShareNo;
        this.time = time;
        this.day = day;
        this.amount = amount;
        this.receivedBy = receivedBy;
    }

    public String getReceiptNo() {
        return receiptNo;
    }

    public void setReceiptNo(String receiptNo) {
        this.receiptNo = receiptNo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAuthPerson() {
        return authPerson;
    }

    public void setAuthPerson(String authPerson) {
        this.authPerson = authPerson;
    }

    public String getAuthPersonNo() {
        return authPersonNo;
    }

    public void setAuthPersonNo(String authPersonNo) {
        this.authPersonNo = authPersonNo;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getPersonNo() {
        return personNo;
    }

    public void setPersonNo(String personNo) {
        this.personNo = personNo;
    }

    public String getCowNo() {
        return cowNo;
    }

    public void setCowNo(String cowNo) {
        this.cowNo = cowNo;
    }

    public String getCowShareNo() {
        return cowShareNo;
    }

    public void setCowShareNo(String cowShareNo) {
        this.cowShareNo = cowShareNo;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getReceivedBy() {
        return receivedBy;
    }

    public void setReceivedBy(String receivedBy) {
        this.receivedBy = receivedBy;
    }
}
