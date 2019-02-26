package com.zhaoyin.analytics;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by mac005 on 2018/6/5.
 */

public class JBAnalyticsPlugin  extends CordovaPlugin{

    private CallbackContext mCallbackContext;
    @Override
    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        this.mCallbackContext = callbackContext;

        if (action.equals("onEventWithData")) {
            onEventWithData(args, callbackContext);
        }

        return true;
    }

    public void onEventWithData(CordovaArgs args, final CallbackContext callbackContext) throws JSONException{

         String params0="";
         JSONObject params1=null;

        try {
            params0 = args.getString(0);
            params1 = args.getJSONObject(1);

        } catch (JSONException e) {

        }

        JBAnalytics.reportSucceededEventWith(params0,params1,cordova.getActivity());
    }
}
