package com.zhaoyin.analytics;

import java.io.Serializable;

public class AppDataModel implements Serializable {

    private String cordovaVersion;
    private String manufacturer;
    private String model;
    private String platform;
    private boolean status;
    private String triggerTime;
    private String uniqueid;
    private String version;


    public String getCordovaVersion() {
        return cordovaVersion;
    }

    public void setCordovaVersion(String cordovaVersion) {
        this.cordovaVersion = cordovaVersion;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getTriggerTime() {
        return triggerTime;
    }

    public void setTriggerTime(String triggerTime) {
        this.triggerTime = triggerTime;
    }

    public String getUniqueid() {
        return uniqueid;
    }

    public void setUniqueid(String uniqueid) {
        this.uniqueid = uniqueid;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
