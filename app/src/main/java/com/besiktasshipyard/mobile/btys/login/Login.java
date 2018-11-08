package com.besiktasshipyard.mobile.btys.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.besiktasshipyard.mobile.btys.MainActivity;
import com.besiktasshipyard.mobile.btys.R;
import com.besiktasshipyard.mobile.btys.busEvents.onLoginEvent;
import com.besiktasshipyard.mobile.btys.helpers.ApplicationErrorData;
import com.besiktasshipyard.mobile.btys.helpers.ApplicationHelpers;
import com.besiktasshipyard.mobile.btys.helpers.ApplicationVariables;
import com.besiktasshipyard.mobile.btys.helpers.DataService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.besiktasshipyard.mobile.btys.MESSAGE";
    public static final String EXTRA_LOGIN_BILGILERI= "login_bilgileri";

    private Context _context;
    private View _view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        _context = MainActivity._context;
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        //login ekranında back butonuna basınca çalışmasın diye
    }

    /**
     * kullanici, send dugmesine bastiginda calisan metod
     * @param v
     */
    public void onBtnLoginClick(View v) {
        _view = v;

        EditText _etUsername = (EditText) findViewById(R.id.etUsername);
        String _username = _etUsername.getText().toString();

        EditText _etPassword = (EditText) findViewById(R.id.etPassword);
        String _password = _etPassword.getText().toString();

        attemptLogin(_username, _password);
    }

    private void attemptLogin(String username, String password)
    {
        //sifreyi encrypt et
        password = ApplicationHelpers.getInstance(_context).encryptString(password);

        // TODO: 22.6.2017 string değil, JSON nesnesi gonder
        //url parametrelerini hazirla
        JSONObject _data = new JSONObject();
        try {
            _data.put("username", username);
            _data.put("password", password);
        } catch (JSONException e) {
            ApplicationHelpers.getInstance(_context).handleError(
                    new ApplicationErrorData(ApplicationErrorData.ApplicationErrorType.JSON_ERROR, "", this.getClass().getName() + " : attemptLogin: Hata: JSON hatasi")
            );
        }

        String _dataString =_data.toString();

//                "{" +
//                        "'username':'" + username + "'," +
//                        "'password':'" + password + "'" +
//                        "}";

        DataService _dataService = new DataService(_context);
        _dataService.execDataService("","",_dataString, new onLoginEvent());
    }

    /**
     * login event dinleyen fonk.
     * @param event
     */
    @Subscribe
    public void onEvent(onLoginEvent event){

        //gelen event te error varsa isle
        if (event.getError() != null)
        {

//            ApplicationHelpers.getInstance(_context).handleError("hata: onLoginEvent - " + event.getError().toString());
            ApplicationHelpers.getInstance(_context).handleError(
                    new ApplicationErrorData(ApplicationErrorData.ApplicationErrorType.EVENT_DATA_ERROR, event.getError().toString(), "hata: onLoginEvent" )
            );

            setResult(RESULT_CANCELED);
            return;
        }

        //gelen event te data yoksa hata ver
        if (event.getData() == null)
        {
//            ApplicationHelpers.getInstance(_context).handleError("MainActivity: hata: onLoginEvent - DATA yok");
            ApplicationHelpers.getInstance(_context).handleError(
                    new ApplicationErrorData(ApplicationErrorData.ApplicationErrorType.EVENT_DATA_ERROR, "", this.getClass().getName() + " : hata: onLoginEvent - DATA yok")
            );
            setResult(RESULT_CANCELED);
            return;
        }

        //gelen datayi al ve baska bir aktivite ac
        JSONObject _intentJSON = new JSONObject((LinkedHashMap) event.getData());

        int _uid = 0;
        try {
            Map<String, Object> _dataMap = ApplicationHelpers.getInstance(_context).getMapFromJSONString(_intentJSON.toString());
            JSONObject _login_bilgileri = new JSONObject(_dataMap.get("login_bilgileri").toString());
            _uid = Integer.parseInt(_login_bilgileri.getString("id"));
            ApplicationVariables.getInstance(_context).set_terminal_uid(_uid);

            ApplicationHelpers.getInstance(_context).saveSharedPreferencesData(_uid);
//            saveSharedPreferencesData(_uid);

        } catch (JSONException e) {
//            ApplicationHelpers.getInstance(_context).handleError("MainActivity: hata: onLoginEvent - UID alinamadi");

            ApplicationHelpers.getInstance(_context).handleError(
                    new ApplicationErrorData(ApplicationErrorData.ApplicationErrorType.JSON_ERROR, "", this.getClass().getName() + " hata: onLoginEvent - UID alinamadi")
            );
            setResult(RESULT_CANCELED);
            return;
        }


//        Intent _intent = new Intent(this, MainActivity.class);
//        _intent.putExtra(EXTRA_MESSAGE, _intentJSON.toString());
//        startActivity(_intent);

        Intent _resultIntent = new Intent();
        _resultIntent.putExtra(EXTRA_LOGIN_BILGILERI, _intentJSON.toString());
        setResult(RESULT_OK, _resultIntent);
        finish();
    }

//    private void saveSharedPreferencesData(int loggedInUserID)
//    {
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(_context);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putInt("loggedInUserID",loggedInUserID);
//        editor.apply();
//    }
}
