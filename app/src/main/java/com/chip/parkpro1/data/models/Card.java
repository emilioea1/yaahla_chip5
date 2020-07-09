package com.chip.parkpro1.data.models;

import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;

public class Card extends RealmObject {

    private static String TAG = Card.class.getSimpleName();

    @PrimaryKey
    private int id;
    private String cardHolderName;
    private String cardNumber;
    private String expiryMonth;
    private String expiryYear;
    private String type;
//    private CardType cardType;
    private String issuer;
    private int defaultCard;

    public Card(){

    }

    public Card(int id, String cardHolderName, String cardNumber, String expiryMonth, String expiryYear, String type, String issuer, int defaultCard) {
        this.id = id;
        this.cardHolderName = cardHolderName;
        this.cardNumber = cardNumber;
        this.expiryMonth = expiryMonth;
        this.expiryYear = expiryYear;
        this.type = type;
//        this.cardType = cardType;
        this.issuer = issuer;
        this.defaultCard = defaultCard;
    }

    public static String getTAG() {
        return TAG;
    }

    public int getId() {
        return id;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getExpiryMonth() {
        return expiryMonth;
    }

    public String getExpiryYear() {
        return expiryYear;
    }

    public String getType() {
        return type;
    }

    public String getIssuer() {
        return issuer;
    }

    public int getmDefault() {
        return defaultCard;
    }

    public static void setTAG(String TAG) {
        Card.TAG = TAG;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public void setExpiryMonth(String expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    public void setExpiryYear(String expiryYear) {
        this.expiryYear = expiryYear;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public void setmDefault(int mDefault) {
        this.defaultCard = mDefault;
    }



    public static void addOrUpdateCard(final JSONArray jsonArray) {
        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NotNull Realm realm) {
                try {
                    realm.createOrUpdateAllFromJson(Card.class, jsonArray);
                }catch (Exception e) {
                    Log.e(TAG, "Error: "+ e.getLocalizedMessage());
                }
            }
        });
    }

    public static List<Card> getAllCards() {
        List<Card> CardList;
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Card> cardsResult = realm.where(Card.class).findAll();
        if (cardsResult.size() > 0)
            CardList = new ArrayList<>(cardsResult);
        else
            return new ArrayList<>();
        return CardList;
    }

}
