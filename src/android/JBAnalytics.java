package com.zhaoyin.analytics;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.google.gson.Gson;

import org.apache.cordova.device.JBDevice;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class JBAnalytics {

    private static String currentDateStr;
    public static final String JBLaunchSuccessNotify="android.intent.action.launchsuccess";//启动成功通知


    private static String eventUrl="https://app.goldrock.cn/metis/put/event";

    public static String getCurrentDateStr() {
        return currentDateStr;
    }

    public static void setCurrentDateStr(String currentDateStr) {
        JBAnalytics.currentDateStr = currentDateStr;
    }

    public static void initAnalytics(Context context){

        String cordovaStr ="7.1.4";
        JBDevice device= new JBDevice();
        AppDataModel appDataModel=new AppDataModel();

        appDataModel.setUniqueid(getUuid(context));

        setCurrentDateStr(getDateStrFromDate(new Date()));
        appDataModel.setTriggerTime(getCurrentDateStr());

        appDataModel.setVersion(device.getOSVersion());
        appDataModel.setPlatform(device.getPlatform());
        appDataModel.setModel(device.getModel());
        appDataModel.setManufacturer(device.getManufacturer());
        appDataModel.setCordovaVersion(cordovaStr);
        appDataModel.setStatus(false);

        EventModel eventModel=new EventModel();
        eventModel.setAppData(appDataModel);
        String channelNumber = getAppMetaData(context, "ZHIKU_CHANNEL");//获取app当前的渠道号
        eventModel.setAppChannel(channelNumber);
        eventModel.setAppVersion(getVersionName(context));
        eventModel.setEventId("launch_firstActivatedUser");
        eventModel.setProjectId("02");
        eventModel.setSource("APP");

        ArrayList list=JBUserDefaults.getInstance(context).getFailedLaunchs();
        if (list==null){
            list=new ArrayList();
        }
        list.add(eventModel);
        JBUserDefaults.getInstance(context).setFailedLaunchs(list);
    }

    public static void reportErrorLaunches(Context context){
        ArrayList list=  JBUserDefaults.getInstance(context).getFailedLaunchs();
        if ((list != null)&&(list.size()>0)) {
            EventModel eventModel= (EventModel) list.get(list.size()-1);
            AppDataModel dataModel=eventModel.getAppData();

            //如果不是这次触发的，那都是之前启动失败的
            if (getCurrentDateStr().equals(dataModel.getTriggerTime())){
                return;
            }

            Gson gson = new Gson();
            String paramJson = gson.toJson(eventModel);

            try {
                JBHttpUtil.doPostAsyn(eventUrl, paramJson, new JBHttpUtil.CallBack() {
                    @Override
                    public void onRequestComplete(String result) {

                        if (result!=null){
                                list.remove(eventModel);
                                JBUserDefaults.getInstance(context).setFailedLaunchs(list);
                                if (list.size()>0){
                                    reportErrorLaunches(context);
                                }
                        }

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String getDateStrFromDate(Date date){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return sdf.format(date);
    }

    public static void reportSuccessLaunches(Context context){

        ArrayList list=JBUserDefaults.getInstance(context).getFailedLaunchs();

        if (list!=null){
            if (list.size()==0)
                return;

            EventModel eventModel= (EventModel) list.get(list.size()-1);
            AppDataModel dataModel=eventModel.getAppData();

            //是这次触发的才算成功启动
            if (!getCurrentDateStr().equals(dataModel.getTriggerTime())){
                return;
            }

            dataModel.setStatus(true);

            Gson gson = new Gson();
            String paramJson = gson.toJson(eventModel);

            try {
                JBHttpUtil.doPostAsyn(eventUrl, paramJson, new JBHttpUtil.CallBack() {
                    @Override
                    public void onRequestComplete(String result) {

                        if (result!=null){

                            if (getCurrentDateStr().equals(dataModel.getTriggerTime())){
                                list.remove(eventModel);
                                JBUserDefaults.getInstance(context).setFailedLaunchs(list);

                                Intent contractlistIntent = new Intent(JBLaunchSuccessNotify);
                                LocalBroadcastManager.getInstance(context).sendBroadcast(contractlistIntent);
                            }
                        }

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String getAppMetaData(Context context, String key) {
        if (context == null || TextUtils.isEmpty(key)) {
            return null;
        }
        String channelNumber = null;
        try {
            PackageManager packageManager = context.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        channelNumber = applicationInfo.metaData.getString(key);
                        channelNumber=channelNumber.substring(3);
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return channelNumber;
    }

    public static String getVersionName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(),
                    0);
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String version = packInfo.versionName;
        return version;
    }

    public static String getUuid(Context context) {
        String uuid = Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        return uuid;
    }
}