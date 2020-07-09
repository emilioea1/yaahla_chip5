package com.chip.parkpro1.data.models;

import androidx.annotation.NonNull;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;

public class Transaction extends RealmObject {

    private double amount;
    private String type;
    @PrimaryKey
    private int walletId;


    public double getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }

    public int getWalletId() {
        return walletId;
    }

    public static void createOrUpdateTransactions(final JSONArray jsonArray){
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.createOrUpdateAllFromJson(Transaction.class, jsonArray);
            }
        });
    }

    public static List<Transaction> getAllWalletTransactions() {
        List<Transaction> transactions;
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Transaction> transactionsResults = realm.where(Transaction.class).findAll();
        if (transactionsResults.size() > 0)
            transactions = new ArrayList<>(transactionsResults);
        else
            return new ArrayList<>();
        return transactions;
    }
}
