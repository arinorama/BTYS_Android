package com.besiktasshipyard.mobile.btys.helpers;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.besiktasshipyard.mobile.btys.busEvents.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

/**
 * Created by aliarin on 16.6.2017.
 */

public class DataService {

    private static DataService instance = null;
    private  Context _context = null;

    public DataService(Context context){
        set_context(context);
    }

    public Context get_context() {
        return _context;
    }

    public void set_context(Context _context) {
        this._context = _context;
    }

    /**
     * Generic Network Servisinin temeli
     * Bu metod, gonderilen URL e bir cagri yapip
     * sonucunu gonderilen Event adiyla broadcast edecek bir request tanimi geri dondurur
     * Bu request kullanilarak, volley cagrisi yapilir
     * Bu eventi dinleyen Subscriber lar ise eventi alir ve datayi yakalamis olur
     * cunku event icinde hem data hem de error bulunur
     * @param busEvent broadcast edilecek event adi
     * @param _urlParams cagrilacak URL
     * @return
     */
    public StringRequest getVolleyRequest(final iBusEvent busEvent, String _urlParams, String _aid, String _pid){
        //bilgiyi alacagimiz url
        String url = ApplicationVariables.getInstance(get_context()).getRequestURL(_urlParams, _aid, _pid);

        // url istegi yapan ve sonucunu ceviren servis tanimi
        // url e istegi gonderir, cevabi gelince, onResponse veya onError calistirir
        // onResponse ve onError, EventBus ile broadcast edilir, kim dinliyorsa bu eventleri, onlara ulasir
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {

                    //Volley Network cagrisi yapilip response dondugunde calisacak metod
                    @Override
                    public void onResponse(String response) {
                        String _response = response;

                        //gelen response dan data ve error bilgilerini ayristirip aliyoruz
                        HashMap<String,Object> _responseData = ApplicationHelpers.getInstance(get_context()).getResponseData_fromRawResponse(_response);
                        Object _error = _responseData.get("error");
                        Object _data = _responseData.get("data");

                        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
                        //eger error bos ise, hata yok, data da doluysa devam
                        if(_error == null && _data != null) {
                            //responseEvent in setData metodunu calistirir ve datayi eventin icine yerlestirir
                            busEvent.setData(_data);
                            //EventBus, eventi post ediyor
                            EventBus.getDefault().post(busEvent);
                        }
                        else
                        {
                            busEvent.setError(_error);
                            EventBus.getDefault().post(busEvent);
                            //ApplicationHelpers.getInstance(get_context()).handleError(_error.toString());
                        }
                    }

                }
                //Volley Network cagrisi yapilip error dondugunde calisacak metod
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("ali", "onErrorResponse: " + error.getMessage());
                busEvent.setError(error);
                EventBus.getDefault().post(busEvent);
            }
        });

        //olusturulan Volley Requesti gei cevriliyor
        return stringRequest;

    }

    /**
     * gonderilen AID, PID ve JSON formarindaki data nesnesi ile (_urlParamsDataString) ile
     * sorgu yapar ve cevabini EVENTNAME ile gonderdilen event ile broadcast eder
     * bu eventi dinleyenler datayi alir
     * @param _urlParamsDataString
     * @param busEvent
     */
    public void execDataService(String _aid, String _pid, String _urlParamsDataString, iBusEvent busEvent){
        //url parametrelerini olustur
        String _urlParams = "";
        if(!_urlParamsDataString.equals(""))
            _urlParams = ApplicationVariables.getInstance(get_context()).getJSONFromString(_urlParamsDataString);

        //Volley Request olustur - serviste cagiracagiz
        StringRequest stringRequest = getVolleyRequest(busEvent,_urlParams, _aid, _pid);

        //servisi cagir - yukarida tanimlanan string requesti ile cagirir
        if(stringRequest == null)
        {
            ApplicationHelpers.getInstance(_context).handleError(
                    new ApplicationErrorData(ApplicationErrorData.ApplicationErrorType.COMMINICATION_ERROR, "", this.getClass().getName() + " : stringRequest null dondu")
            );
        }
        RequestQueueSingleton.getInstance(get_context()).addToRequestQueue(stringRequest);
    }

}
