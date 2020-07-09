package com.chip.parkpro1.data.models;

import androidx.annotation.NonNull;

import org.json.JSONArray;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;

public class Service extends RealmObject {

    @PrimaryKey
    private String pkeyGuidInvoice;
    private String amount;
    private String fromTime;
    private String pkeyGuidSite;
    private String ticket;
    private String toTime;
    private String type;
    private String check;
    private String siteName;
    private String pkeyGuid;
    private String lisencePlate;
    private String datetime;
    private String location;


    public static void createOrUpdateService(final JSONArray jsonArray){
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.createOrUpdateAllFromJson(Service.class, jsonArray);
            }
        });
    }

    public static RealmResults<Service> getService(String pkeyGuidSite){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmResults<Service> services = realm.where(Service.class).equalTo("pkeyGuidSite", pkeyGuidSite).findAll();
        realm.commitTransaction();
        return services;
    }

    public static void deleteService(final String id){
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Service> rows = realm.where(Service.class).equalTo("pkeyGuidInvoice", id).findAll();
                rows.deleteAllFromRealm();
            }
        });
    }

    public static RealmResults<Service> getServicesById(String carId) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmResults<Service> servicesResult = realm.where(Service.class).equalTo("lisencePlate", carId).findAll();
        realm.commitTransaction();
        return servicesResult;
    }

}
