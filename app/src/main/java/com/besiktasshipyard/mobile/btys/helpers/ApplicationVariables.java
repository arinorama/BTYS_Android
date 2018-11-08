package com.besiktasshipyard.mobile.btys.helpers;
import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

/**
 * Created by aliarin on 14.6.2017.
 */

public class ApplicationVariables
{
    private static ApplicationVariables instance = null;

    private static String _serverIP_TEST = "192.168.25.51";
    private static String _serverIP_PRODUCTION_LOCAL_IP = "192.168.20.9";
    private static String _serverIP_PRODUCTION_GLOBAL_IP = "95.0.89.78";//"88.248.234.124";

    private static String _serverIP = _serverIP_PRODUCTION_LOCAL_IP;

    private static String _serverURL = "http://" + _serverIP + "/vias/";

    private static String _testLoginURLData_JSON = "{'username':'aliarin', 'password':'VZlSXRVVONlUspFVXxmWaN2aKVVVB1TP'}";
    private static String _busEventsProjectPath = "com.example.myfirstapp.busEvents";

    public static int get_terminal_uid() {
        return _terminal_uid;
    }

    public static void set_terminal_uid(int _terminal_uid) {
        ApplicationVariables._terminal_uid = _terminal_uid;
    }

    private static int _terminal_uid = 0;

    //a private constructor so no instances can be made outside this class
    private ApplicationVariables(Context context)
    {

    }

    //Everytime you need an instance, call this
    public static synchronized ApplicationVariables getInstance(Context context) {
        if(instance == null)
            instance = new ApplicationVariables(context);

        return instance;
    }

    public static String get_busEventsProjectPath(){
        return _busEventsProjectPath;
    }


    public static String get_serverIP() {
        return _serverIP;
    }

    public static String get_serverURL() {
        return _serverURL;
    }

    public String getJSONFromString(String inputString) {
        JSONObject _resultJSON = null;
        try {
            _resultJSON = new JSONObject(inputString);
        }
        catch (org.json.JSONException e)
        {
            Log.e("ali-hata", "getJSONFromString: ", e);
            return "";
        }
        finally {
            return _resultJSON.toString();
        }

        //return _testLoginURLData;
    }

    public String getRequestURL(String _urlParams, String _aid, String _pid)
    {
        String _returnString = get_serverURL();
        int _terminal_uid = ApplicationVariables.get_terminal_uid();

        _returnString += "?terminaluid=" + String.valueOf(_terminal_uid) + "&aid=" + _aid + "&pid=" + _pid + "&data=" + _urlParams;
        return _returnString;
    }

}
