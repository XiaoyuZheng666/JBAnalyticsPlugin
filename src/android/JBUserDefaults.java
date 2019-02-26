package com.zhaoyin.analytics;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JBUserDefaults {

    public static JBUserDefaults userDefaults = null;
    private static SharedPreferences sp = null;
    private static Context con = null;
    private static String name = "JBUserDefaults";

    private static String launchesKey = "launchesKey";
    private static String eventKey = "eventKey";

    private JBUserDefaults() {
    }

    public static JBUserDefaults getInstance(Context ctx) {
        if (null == userDefaults) {
            userDefaults = new JBUserDefaults();
            con = ctx;
            sp = con.getSharedPreferences(name, Context.MODE_PRIVATE);
        }
        return userDefaults;
    }

    public  ArrayList getLaunchs(){
        String json = sp.getString(launchesKey, null);
        if (json != null)
        {
            Gson gson = new Gson();
            Type type = new TypeToken<List<EventModel>>(){}.getType();

            List<EventModel> alterSamples = new ArrayList<EventModel>();
            alterSamples = gson.fromJson(json, type);

            return (ArrayList) alterSamples;
        }

        return (new ArrayList<EventModel>());
    }


    public  void setLaunchs(ArrayList<EventModel> list){
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(launchesKey, json);
        editor.commit();
    }

    public  ArrayList getEventIds(){
        String json = sp.getString(eventKey, null);
        if (json != null)
        {
            Gson gson = new Gson();
            Type type = new TypeToken<List<String>>(){}.getType();

            List<String> alterSamples = new ArrayList<String>();
            alterSamples = gson.fromJson(json, type);

            return (ArrayList) alterSamples;
        }

        return new ArrayList<String>();
    }

    public  void setEventIds(ArrayList<String> list){
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(eventKey, json);
        editor.commit();
    }

    public  ArrayList getRecordsWithKey(String key){
        String json = sp.getString(key, null);
        if (json != null)
        {
            Gson gson = new Gson();
            Type type = new TypeToken<List<EventModel>>(){}.getType();

            List<EventModel> models = new ArrayList<EventModel>();
            models = gson.fromJson(json, type);

            return (ArrayList) models;
        }

        return new ArrayList<EventModel>();
    }

    public  void setRecordsWithKey(ArrayList<EventModel> list,String key){
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.commit();
    }
}
