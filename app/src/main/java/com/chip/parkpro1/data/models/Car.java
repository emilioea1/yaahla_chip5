package com.chip.parkpro1.data.models;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;

public class Car extends RealmObject implements Serializable {


    //mode is the brand of the car
    private String id;
    private String model;
    private String color;
    @PrimaryKey
    private String code;
    private String year;
    private boolean isDefault;

    public Car createCar(String id, String model, String color, String plateNumber, String symbolPlate, boolean isDefault, String year, @Nullable byte[] carImage) {
        this.id = id;
        this.model = model;
        this.color = color;
        this.code = plateNumber;
        this.year = year;
        return this;
    }

    public Car(){

    }

    public Car(String id, String model, String color, String code, String year) {
        this.id = id;
        this.model = model;
        this.color = color;
        this.code = code;
        this.year = year;
    }

    public Car(String model) {
        this.model = model;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

//    public String getPlateNumber() {
//        return plateNumber;
//    }

//    public void setPlateNumber(String plateNumber) {
//        this.plateNumber = plateNumber;
//    }

//    public String getSymbolePlate() {
//        return symbolPlate;
//    }

//    public void setSymbolePlate(String symbolePlate) {
//        this.symbolPlate = symbolePlate;
//    }

//    public boolean isDefault() {
//        return isDefault;
//    }

//    public void setDefault(boolean aDefault) {
//        isDefault = aDefault;
//    }

    public void setYear(String year) {this.year = year;}

    public String getYear(){ return this.year;}

//    public void setCarImage(@Nullable ImageView image) {this.carImage = carImage;}

//    @Nullable
//    public byte[] getCarImage() {return this.carImage;}

    public static List<Car> getAllCars() {
        Realm realm = Realm.getDefaultInstance();
        List<Car> carsToReturn = new ArrayList<>();
        RealmResults<Car> cars = realm.where(Car.class).findAll();
        if (cars == null) {
            return carsToReturn;
        }
        carsToReturn.addAll(cars);
        return cars;
    }

    public static Car getFirstCar(){
        Realm realm = Realm.getDefaultInstance();
        return realm.where(Car.class).findFirst();
    }

    public static void insertOrUpdateCar(final Car car) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@NotNull Realm realm) {
                realm.insertOrUpdate(car);
            }
        });
    }

    public void deleteCar(final String plateNumber) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@NotNull Realm realm) {
                RealmResults<Car> results = realm.where(Car.class).equalTo("plateNumber",plateNumber).findAll();
                results.deleteAllFromRealm();
            }
        });
    }

    public void setDefaultCar(final String plateNumber) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                RealmResults<Car> results = realm.where(Car.class).equalTo("plateNumber",plateNumber).findAll();
                if (results == null) {
                    return;
                }
                for (Car car : results) {
                    car.setDefault(true);
                }
            }
        });
    }

    public void uncheckAllDefaultCars() {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@NotNull Realm realm) {
                RealmResults<Car> results = realm.where(Car.class).findAll();
                if (results == null) {
                    return;
                }
                for (Car car : results) {
                    car.setDefault(false);
                }
            }
        });
    }

    public boolean isMoreThanOneDefault() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Car> results = realm.where(Car.class).equalTo("isDefault",true).findAll();
        return (results != null) && (results.size() != 1) && (results.size() != 0);
    }

    public static void deleteAllCars() {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(Car.class).findAll().deleteAllFromRealm();
            }
        });

    }
}
