package com.zhaoyin.analytics;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by mac005 on 2018/6/5.
 */

public class JBAnalyticsPlugin  extends CordovaPlugin{

    private CallbackContext mCallbackContext;
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.mCallbackContext = callbackContext;
            callbackContext.success("success");
            return true;
    }    
}
