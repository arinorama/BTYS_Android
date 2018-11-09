package com.besiktasshipyard.mobile.btys.helpers;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.besiktasshipyard.mobile.btys.MainActivity;
import com.besiktasshipyard.mobile.btys.businessLayer.dataItems.dataCache.DataCache;
import com.besiktasshipyard.mobile.btys.fragments.reports.ReportsMainActivity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;

import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;
/**
 * Created by aliarin on 14.6.2017.
 */

public class ApplicationHelpers
{
    private static ApplicationHelpers instance = null;
    private static Context _context = null;

    private static final String MESSAGE_TYPE_ERROR = "error";
    private static final String MESSAGE_TYPE_INFO = "info";

    private String _currentUserId;

    //a private constructor so no instances can be made outside this class
    private ApplicationHelpers(Context context)
    {
        set_context(context);
    }

    /**
     * SINGLETON icin gerekli fonk.
     * bunun sayesinde, her seferinde bu fonksiyon = new ile init edilmiyor
     * @param context
     * @return
     */
    public static synchronized ApplicationHelpers getInstance(Context context) {
        if(instance == null)
            instance = new ApplicationHelpers(context);

        return instance;
    }

    public int getSavedLoggedInUserID(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(_context);
        return preferences.getInt("loggedInUserID", 0);
    }

    public void saveSharedPreferencesData(int loggedInUserID)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(_context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("loggedInUserID",loggedInUserID);
        editor.apply();
    }

    public void clearSharedPreferencesData(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(_context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }


    /**
     * DATACACHE icinde sakladigimiz bilgileri siliyor
     * cunku sinirlamalardan dolayi baska bir kullanicinin gormemesi gereken bilgiler olabilir, cachi temizlemek lazim
     */
    public void clearCache(){
        DataCache.getInstance().clearDataCache();
    }


    /**
     * network cagrilarinin hepsinde kullanilan fonk.
     * phpden gelen cevabi alir, fazlaliklari atar, response icinden
     * DATA ve ERROR nesnelerini bulur ve bu ikisini geri cevirir
     * @param rawResponse :phpden gelen cevap
     * @return data ve error nesnelerini iceren map cevirir
     */
    public HashMap<String, Object> getResponseData_fromRawResponse(String rawResponse)
    {
        //bazen gelen cevapta notice vb. oluyor, bunlari cikart
        if(rawResponse.indexOf("<?xml version") > -1)
            rawResponse = rawResponse.substring(rawResponse.indexOf("<?xml version"));

        //gelen cevaptan xml version yazan gereksiz yerleri cikart
        rawResponse = rawResponse.replaceAll("<?xml version=\"1.0\" encoding=\"UTF-8\"?>","");
        JSONObject _rawJSON = null;

        XmlToJson xmlToJson = new XmlToJson.Builder(rawResponse).build();
        // JSON nesnesine cevir
        _rawJSON = xmlToJson.toJson();

        // JSON nesnesini hashmap e cevir (key-value map) -> responsemap
        final String json = _rawJSON.toString();//"{}";
        final ObjectMapper mapper = new ObjectMapper();
        final MapType type = mapper.getTypeFactory().constructMapType(
                Map.class, String.class, Object.class);
        Map<String, Object> _responseMapOuter = null;
        try {
            _responseMapOuter = mapper.readValue(json, type);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //response icinden data ve error u al
        Map<String, Object> _responseMap = (Map<String, Object>) _responseMapOuter.get("response");
        HashMap<String, Object> _results = new HashMap<String, Object>();
        if(_responseMap != null){
            Object _data = _responseMap.get("data");
            Object _error = _responseMap.get("error");

            //data ve error u results diye map e yerlestir ve geri dondur (ikisini aynı anda geri cevirmek icin yaptim)
            _results.put("data", _data);
            _results.put("error", _error);
        }
        else {
            handleError(
                    new ApplicationErrorData(ApplicationErrorData.ApplicationErrorType.JSON_ERROR, "e_general_061", "hata")
            );
            if(_context instanceof MainActivity)
                ((MainActivity) _context).hideLoadingPanel();
            else if (_context instanceof ReportsMainActivity)
                ((ReportsMainActivity) _context).hideLoadingPanel();
        }
        return _results;
    }

    /**
     * herhangi bir JSON'ı mape cevirir
     * @param jsonString JSON string
     * @return <String, Object> tipi map cevirir
     */
    public Map<String, Object> getMapFromJSONString(String jsonString)
    {
        jsonString = jsonString.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>","");
        JSONObject _json = null;


        // JSON nesnesini hashmap e cevir (key-value map) -> _resultMappingObject
        final ObjectMapper mapper = new ObjectMapper();
        final MapType type = mapper.getTypeFactory().constructMapType(
                Map.class,  String.class, Object.class);
        Map<String, Object> _resultMappingObject = null;
        try {
            _resultMappingObject = mapper.readValue(jsonString, type);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return _resultMappingObject;
    }

    /**
     * herhangi bir stringi sifreler
     * password, DB de saklanirken, boyle sifreleniyor
     * login icin kullaniyoruz
     * @param rawString
     * @return
     */
    public String encryptString(String rawString)
    {
        String _encryptedString = "";
        byte[] _rawData;
        String _base64 = rawString;
        try {

            for (int i = 0; i < 5; i++) {
                _rawData = _base64.getBytes("UTF-8");
                _base64 = Base64.encodeToString(_rawData, Base64.DEFAULT).replace("\n","");
                _base64  = new StringBuffer(_base64).reverse().toString();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        _encryptedString = _base64;
        return _encryptedString;
    }





    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        try {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }catch (NullPointerException e){}
    }

    public static void setFragmentTitle(AppCompatActivity activity, String title){
        ActionBar _actionBar = activity.getSupportActionBar();
        if(_actionBar != null)
            _actionBar.setTitle(title);
    }

    /**
     * hata gösterimi yapan fonk.
     * @param errorData
     */
    public void handleError(ApplicationErrorData errorData)
    {
        Log.i("ali-hata", errorData.getErrorMessage()); //error code yok, dogrudan mesaji yaz

        if(!errorData.getErrorDescription().equals(""))
            showMessage(errorData.getErrorDescription());
        else
            showMessage(errorData.getErrorMessage());

    }

    public void showMessage(String message){
        Toast.makeText(_context, message, Toast.LENGTH_SHORT).show();
    }

    public Context get_context() {
        return _context;
    }

    public void set_context(Context _context) {
        this._context = _context;
    }

    public String get_currentUserId() {
        return _currentUserId;
    }

    public void set_currentUserId(String _currentUserId) {
        this._currentUserId = _currentUserId;
    }

    public String getDateFromUnixTimestamp(int unixtimestamp){
        String _resultDateString = "";

        Date date = new Date(unixtimestamp*1000L); // *1000 milisaniye icin
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm"); // tarih formati
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-3")); // referans timezone
        _resultDateString = sdf.format(date);

        return _resultDateString;
    }
}
