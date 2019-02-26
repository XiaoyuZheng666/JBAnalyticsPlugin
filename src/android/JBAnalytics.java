package com.zhaoyin.analytics;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.text.TextUtils;

import com.google.gson.Gson;

import org.apache.cordova.device.JBDevice;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JBAnalytics {

    private static String currentDateStr;

    private static AppDataModel appData;

   private static String eventUrl="https://app.goldrock.cn/metis/put/event";
// private static String eventUrl="http://testmetis.goldrock.cn:2752/metis/put/event";


    public static String getCurrentDateStr() {
        return currentDateStr;
    }

    public static void setCurrentDateStr(String currentDateStr) {
        JBAnalytics.currentDateStr = currentDateStr;
    }

    public static AppDataModel getAppData() {
        return appData;
    }

    public static void setAppData(AppDataModel appData) {
        JBAnalytics.appData = appData;
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
        appDataModel.setStatus(true);

        JBAnalytics.setAppData(appDataModel);

        EventModel eventModel=new EventModel();
        eventModel.setAppData(getLaunchAppDataModel(context,device,cordovaStr));
        String channelNumber = getAppMetaData(context, "ZHIKU_CHANNEL");//获取app当前的渠道号
        eventModel.setAppChannel(channelNumber);
        eventModel.setAppVersion(getVersionName(context));
        eventModel.setEventId("launch_firstActivatedUser");
        eventModel.setProjectId("02");
        eventModel.setSource("APP");

        ArrayList list=JBUserDefaults.getInstance(context).getLaunchs();
        if (list==null){
            list=new ArrayList();
        }
        list.add(eventModel);
        JBUserDefaults.getInstance(context).setLaunchs(list);
    }

    public static AppDataModel getLaunchAppDataModel(Context context,JBDevice device,String cordovaStr){
        AppDataModel appDataModel=new AppDataModel();
        appDataModel.setUniqueid(getUuid(context));
        appDataModel.setTriggerTime(getCurrentDateStr());
        appDataModel.setVersion(device.getOSVersion());
        appDataModel.setPlatform(device.getPlatform());
        appDataModel.setModel(device.getModel());
        appDataModel.setManufacturer(device.getManufacturer());
        appDataModel.setCordovaVersion(cordovaStr);
        appDataModel.setStatus(false);

        return appDataModel;

    }

    public static void reportSavedEvents(Context context){
        reportSavedLaunchEvents(context);
        reportSavedOtherEvents(context);
    }

    public static void reportSavedLaunchEvents(Context context){
        ArrayList<EventModel> list=  JBUserDefaults.getInstance(context).getLaunchs();
        if ((list != null)&&(list.size()>0)) {
            EventModel eventModel= (EventModel) list.get(list.size()-1);
            AppDataModel dataModel=eventModel.getAppData();


            List<EventModel> reportList = new ArrayList<EventModel>();

            for (EventModel eModel:list) {
                if (((AppDataModel)eModel.getAppData()).getTriggerTime().equals(getCurrentDateStr())){
                    continue;
                }else {
                    reportList.add(eModel);
                }
            }

            if (reportList.size()==0)
                return;

            Gson gson = new Gson();
            String paramJson = gson.toJson(reportList);

            try {
                JBHttpUtil.doPostAsyn(eventUrl, paramJson, new JBHttpUtil.CallBack() {
                    @Override
                    public void onRequestComplete(String result) {

                        if (result!=null){
                            if (list.removeAll(reportList))
                                JBUserDefaults.getInstance(context).setLaunchs(list);
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

    public static void reportSucceededLaunch(Context context){

        ArrayList list=JBUserDefaults.getInstance(context).getLaunchs();

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
            dataModel.setTriggerTime(getDateStrFromDate(new Date()));

            Gson gson = new Gson();
            String paramJson = gson.toJson(eventModel);

            try {
                JBHttpUtil.doPostAsyn(eventUrl, paramJson, new JBHttpUtil.CallBack() {
                    @Override
                    public void onRequestComplete(String result) {

                        if (result!=null){

                                list.remove(eventModel);
                        }

                        JBUserDefaults.getInstance(context).setLaunchs(list);

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void reportSavedOtherEvents(Context context){

        //处理前端过来的保存的发送失败的事件

        List <EventModel>reportTempList=new ArrayList<EventModel>();
        List <String>eventidsTempList=new ArrayList<String>();

        ArrayList <String>array =JBUserDefaults.getInstance(context).getEventIds();

        if (array==null)
            return;

        for (String eventid:array) {

            ArrayList <EventModel>records  =JBUserDefaults.getInstance(context).getRecordsWithKey(eventid);

            reportTempList.addAll(records);
            eventidsTempList.add(eventid);

            if (reportTempList.size()>99){
                reportTempList.removeAll(records);
                eventidsTempList.remove(eventid);
                break;
            }
        }

       if (reportTempList.size()==0)
           return;

            Gson gson = new Gson();
            String paramJson = gson.toJson(reportTempList);

            try {
                JBHttpUtil.doPostAsyn(eventUrl, paramJson, new JBHttpUtil.CallBack() {
                    @Override
                    public void onRequestComplete(String result) {

                        if (result!=null){

                            for (String eventidStr:eventidsTempList) {
                                ArrayList <EventModel>records= JBUserDefaults.getInstance(context).getRecordsWithKey(eventidStr);
                                records.removeAll(records);
                                JBUserDefaults.getInstance(context).setRecordsWithKey(records,eventidStr);
                            }


                            array.removeAll(eventidsTempList);
                            JBUserDefaults.getInstance(context).setEventIds(array);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    public static void reportSucceededEventWith(String eventId,JSONObject dataObj,Context context){
       if (dataObj==null)
           return;
       EventModel eventModel=new EventModel();
        try {
            eventModel.setAppChannel(dataObj.getString("appChannel"));
            eventModel.setAppVersion(dataObj.getString("appVersion"));
            eventModel.setEventId(dataObj.getString("eventId"));
            eventModel.setProjectId(dataObj.getString("projectId"));
            eventModel.setSource(dataObj.getString("source"));
            eventModel.setAppData(getAppData());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        String paramJson = gson.toJson(eventModel);

        try {
            JBHttpUtil.doPostAsyn(eventUrl, paramJson, new JBHttpUtil.CallBack() {
                @Override
                public void onRequestComplete(String result) {

                    if (result!=null){

                        ArrayList <String>eventids =JBUserDefaults.getInstance(context).getEventIds();

                        if (!isContainsStr(eventId,eventids)){
                            eventids.add(eventId);
                        }

                        JBUserDefaults.getInstance(context).setEventIds(eventids);
                        ArrayList <EventModel>records=JBUserDefaults.getInstance(context).getRecordsWithKey(eventId);
                        records.add(eventModel);
                        JBUserDefaults.getInstance(context).setRecordsWithKey(records,eventId);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean  isContainsStr(String str,ArrayList<String>list){

        boolean isContains=false;

        for (String currentStr:list) {
            if (currentStr.equals(str))
                isContains=true;
        }

        return isContains;
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