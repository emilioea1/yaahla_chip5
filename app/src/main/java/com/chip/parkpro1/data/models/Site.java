package com.chip.parkpro1.data.models;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;

public class Site extends RealmObject{

    @PrimaryKey
    private int siteId;

    private String siteName;
    private Double lon;
    private Double lat;
    private String pKeyGuidSite;
    private int rate;
    private Boolean isFavorite;
    private RealmList<String> siteTypes;
    private RealmList<Device> devices;

    public Site(int siteId, String siteName, Double lon, Double lat, int rate, Boolean isFavorite) {
        this.siteId = siteId;
        this.siteName = siteName;
        this.lon = lon;
        this.lat = lat;
        this.rate = rate;
        this.isFavorite = isFavorite;
    }

    public Site(String siteName) {
        this.siteName = siteName;
    }

    public Site(){

    }

    public RealmList<Device> getDevices() {
        return devices;
    }

    public String getpKeyGuidSite() {
        return pKeyGuidSite;
    }

    public int getSiteId() {
        return siteId;
    }

    public int getRate() {
        return rate;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public void setpKeyGuidSite(String pKeyGuidSite) {
        this.pKeyGuidSite = pKeyGuidSite;
    }

    public Boolean getFavorite() {
        return isFavorite;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public void setFavorite(Boolean favorite) {
        isFavorite = favorite;
    }

    public List<String> getSiteTypes() {
        return new ArrayList<>(this.siteTypes);
    }

    public List<String> getNonNullSiteTypes(){
        if(this.siteTypes != null)
            return new ArrayList<>(this.siteTypes);
        else
            return new ArrayList<>();
    }

    public void setSiteTypes(List<String> siteTypes) {
        RealmList<String> types = new RealmList<>();
        types.addAll(siteTypes);
        this.siteTypes = types;
    }

    //    public static int getSiteId(String siteName) {
//        Realm realm = Realm.getDefaultInstance();
//        realm.beginTransaction();
//        int siteId = Objects.requireNonNull(realm.where(Site.class)
//                .equalTo("siteName", siteName)
//                .findFirst())
//                .getSiteId();
//        realm.commitTransaction();
//        realm.close();
//        return siteId;
//    }

//    public static Site getSite(int siteID) {
//        Realm realm = Realm.getDefaultInstance();
//        realm.beginTransaction();
//        Site site = realm.where(Site.class)
//                .equalTo("siteId", siteID)
//                .findFirst();
//        realm.commitTransaction();
//        return site;
//    }

//    public static RealmResults<Site> getSites() {
//        Realm realm = Realm.getDefaultInstance();
//        realm.beginTransaction();
//        RealmResults<Site> sites = realm.where(Site.class).findAll();
//        realm.commitTransaction();
//        return sites;
//    }

    public static List<Site> getAllSites() {
        List<Site> siteList;
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Site> siteResult = realm.where(Site.class).findAll();
        if (siteResult.size() > 0)
            siteList = new ArrayList<>(siteResult);
        else
            return new ArrayList<>();
        return siteList;
    }

    public static List<Site> getFavoriteSites(){
        List<Site> sitesList;
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Site> siteResult = realm.where(Site.class).equalTo("isFavorite", true).findAll();
        if(siteResult.size() > 0)
            sitesList = new ArrayList<>(siteResult);
        else
            return new ArrayList<>();

        return sitesList;
    }

    public static void setFavorite(final String keyGuid, final boolean favorite){
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Site> sites = realm.where(Site.class).equalTo("pKeyGuidSite", keyGuid).findAll();
                for(Site site: sites){
                    site.setFavorite(favorite);
                }
            }
        });
    }

//    public static boolean isSiteFavorite(int siteId) {
//        Realm realm = Realm.getDefaultInstance();
//        realm.beginTransaction();
//        Site site = realm.where(Site.class)
//                .equalTo("siteId", siteId)
//                .equalTo("isFavorite", true)
//                .findFirst();
//        realm.commitTransaction();
//        realm.close();
//        if (site != null)
//            return true;
//        else
//            return false;
//    }

//    public static RealmResults setSiteTypes(RealmResults<Site> sites, JSONArray sitesArray){
//        Realm realm = Realm.getDefaultInstance();
//        realm.beginTransaction();
//
//        for(Site site: sites){
//
//        }
//
//    }

//    public static void createOrUpdateStite(final JSONObject json) {
//        Realm realm = Realm.getDefaultInstance();
//        realm.executeTransaction(new Realm.Transaction() {
//            @Override
//            public void execute(Realm realm) {
//                realm.createOrUpdateObjectFromJson(Site.class,json);
//            }
//        });
//    }

    public static void createOrUpdateSites(final JSONArray jsonArray) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.createOrUpdateAllFromJson(Site.class, jsonArray);
            }
        });
    }

}