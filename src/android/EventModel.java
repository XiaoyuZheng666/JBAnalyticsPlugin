package com.zhaoyin.analytics;

import java.io.Serializable;

public class EventModel implements Serializable {

    private String appChannel;
    private String appVersion;
    private String eventId;
    private String projectId;
    private String source;
    private String additional;


    public AppDataModel getAppData() {
        return appData;
    }

    public void setAppData(AppDataModel appData) {
        this.appData = appData;
    }

    private AppDataModel appData;

    public String getAppChannel() {
        return appChannel;
    }

    public void setAppChannel(String appChannel) {
        this.appChannel = appChannel;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
    public String getAdditional() {
        return additional;
    }

    public void setAdditional(String additional) {
        this.additional = additional;
    }

}
