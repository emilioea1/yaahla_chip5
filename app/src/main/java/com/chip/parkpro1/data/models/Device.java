package com.chip.parkpro1.data.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Device extends RealmObject {

  @PrimaryKey
  private String keyGuid;
  private String name;
  private String type;
  private boolean ble;
  private String macAddress;
  private String bleIdentifier;

  public Device(){

  }

  public Device(String keyGuid, String name, String type, boolean ble, String macAddress, String bleIdentifier) {
    this.keyGuid = keyGuid;
    this.name = name;
    this.type = type;
    this.ble = ble;
    this.macAddress = macAddress;
    this.bleIdentifier = bleIdentifier;
  }

  public Device(String name, String mac){
    this.name = name;
    this.macAddress = mac;
  }

  public String getMacAddress() {
    return macAddress;
  }

  public String getKeyGuid() {
    return keyGuid;
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

  public boolean isBle() {
    return ble;
  }

  public String getBleIdentifier() {
    return bleIdentifier;
  }

  public void setKeyGuid(String keyGuid) {
    this.keyGuid = keyGuid;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setBle(boolean ble) {
    this.ble = ble;
  }

  public void setMacAddress(String macAddress) {
    this.macAddress = macAddress;
  }

  public void setBleIdentifier(String bleIdentifier) {
    this.bleIdentifier = bleIdentifier;
  }
}
