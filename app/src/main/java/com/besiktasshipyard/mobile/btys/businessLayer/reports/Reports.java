package com.besiktasshipyard.mobile.btys.businessLayer.reports;

import android.content.Context;
import android.view.View;

import com.besiktasshipyard.mobile.btys.busEvents.iBusEvent;
import com.besiktasshipyard.mobile.btys.busEvents.onGetGenericReportResultData;
import com.besiktasshipyard.mobile.btys.busEvents.onGetReportListData;
import com.besiktasshipyard.mobile.btys.helpers.ApplicationErrorData;
import com.besiktasshipyard.mobile.btys.helpers.ApplicationHelpers;
import com.besiktasshipyard.mobile.btys.helpers.DataService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by aliarin on 20.7.2017.
 */

public class Reports {
    private iBusEvent reportListBusEvent, genericReportBusEvent;

    private static Reports mInstance;
    private static Context _context;
    private static View _view;

    private Reports(Context context)
    {
        _context = context;
    }

    public static synchronized Reports getInstance(Context context, View... view) {
        if (mInstance == null) {
            mInstance = new Reports(context);
        }

        if(view.length > 0)
            _view = view[0];

        return mInstance;
    }

    @Override
    public void finalize() throws Throwable {
        super.finalize();
        EventBus.getDefault().unregister(this);
    }




    //region getReportList
    /**
     * kullanici listesini getiren fonk.
     * @param busEvent listenin donecegi callback fonk.
     * @param getOnlySelfReports sadece kendi raporlarını mı getirsin arg.
     */
    public void getReportList(iBusEvent busEvent, boolean getOnlySelfReports){


        //event busa kaydol
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        reportListBusEvent = busEvent;

        String _aid="1", _pid="100000", _urlParams="";
        DataService _dataService = new DataService(_context);


        JSONObject _dataJSON = new JSONObject();
        if (getOnlySelfReports)
        {
            try {
                _dataJSON.put("report_customer_id", ApplicationHelpers.getInstance(_context).get_currentUserId());
            } catch (JSONException e) {
                ApplicationHelpers.getInstance(_context).handleError(
                        new ApplicationErrorData(ApplicationErrorData.ApplicationErrorType.JSON_ERROR, "", this.getClass().getName() + " : HRUsers: getUserById: Hata: JSON hatasi")
                );
            }
            _urlParams = _dataJSON.toString();
        }

        _dataService.execDataService (_aid, _pid, _urlParams, new onGetReportListData());
    }

    /**
     * getUserListData sonucu gelen eventi dinleyen fonk.
     * getUserListData ile datayı DB den alır ve
     * getUserList ile post eder
     * @param event
     */
    @Subscribe
    public void onEvent(onGetReportListData event){
        //gelen event te error varsa isle
        if (event.getError() != null)
        {
            ApplicationHelpers.getInstance(_context).handleError(
                    new ApplicationErrorData(ApplicationErrorData.ApplicationErrorType.JSON_ERROR, event.getError().toString(), this.getClass().getName() + ": hata: onGetReportListData - " + event.getError().toString())
            );
            return;
        }

        //gelen event te data yoksa hata ver
        if (event.getData() == null)
        {
            ApplicationHelpers.getInstance(_context).handleError(
                    new ApplicationErrorData(ApplicationErrorData.ApplicationErrorType.EVENT_DATA_ERROR, event.getError().toString(), this.getClass().getName() + ": hata: onGetReportListData - DATA yok")
            );
            return;
        }

        //gelen datayi al
        LinkedHashMap _lhm = (LinkedHashMap) event.getData();
        String _reportArrayString = (String) _lhm.get("report_definitions");
        JSONArray _jaReports = new JSONArray();
        try {
            _jaReports = new JSONArray(_reportArrayString);
        } catch (JSONException e) {
            reportListBusEvent.setError(e);
        }


        reportListBusEvent.setData(_jaReports);
        EventBus.getDefault().post(reportListBusEvent);
    }
    //endregion

    //region getReportResult
    /**
     * herhangi bir rapor calistirir.
     * @param busEvent rapor sonucunun donecegi callback fonk.
     * @param reportId rapor id
     */
    public void getGenericReportResult(iBusEvent busEvent, int reportId){


        //event busa kaydol
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        genericReportBusEvent = busEvent;

        String _aid="3", _pid="100000", _urlParams="";
        DataService _dataService = new DataService(_context);


        JSONObject _dataJSON = new JSONObject();

        try {
            _dataJSON.put("report_id", reportId);
        } catch (JSONException e) {
            ApplicationHelpers.getInstance(_context).handleError(
                    new ApplicationErrorData(ApplicationErrorData.ApplicationErrorType.JSON_ERROR, "", this.getClass().getName() + " : Reports: getGenericReportResult: Hata: JSON hatasi")
            );
        }
        _urlParams = _dataJSON.toString();

        _dataService.execDataService (_aid, _pid, _urlParams, new onGetGenericReportResultData());
    }

    /**
     * herhangi bir rapor calistirir.
     * @param busEvent rapor sonucunun donecegi callback fonk.
     * @param pid rapor sayfasinin pid i
     * @param aid rapor sayfasinin aid i
     */
    public void getGenericReportResult(iBusEvent busEvent, String pid, String aid, HashMap<String,String> reportParams){


        //event busa kaydol
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        genericReportBusEvent = busEvent;

        String _aid=aid, _pid=pid, _urlParams="";
        DataService _dataService = new DataService(_context);


        JSONObject _dataJSON = new JSONObject();

        try {
            //reportParams ile gonderilen parametreleri istege ekliyor
            Iterator it = reportParams.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
//                System.out.println(pair.getKey() + " = " + pair.getValue());
                _dataJSON.put(pair.getKey().toString(), pair.getValue().toString());
                it.remove(); // avoids a ConcurrentModificationException
            }

        } catch (JSONException e) {
            ApplicationHelpers.getInstance(_context).handleError(
                    new ApplicationErrorData(ApplicationErrorData.ApplicationErrorType.JSON_ERROR, "", this.getClass().getName() + " : Reports: getGenericReportResult: Hata: JSON hatasi")
            );
        }
        _urlParams = _dataJSON.toString();

        _dataService.execDataService (_aid, _pid, _urlParams, new onGetGenericReportResultData());
    }

    /**
     * getUserListData sonucu gelen eventi dinleyen fonk.
     * getUserListData ile datayı DB den alır ve
     * getUserList ile post eder
     * @param event
     */
    @Subscribe
    public void onEvent(onGetGenericReportResultData event){
        //gelen event te error varsa isle
        if (event.getError() != null)
        {
            ApplicationHelpers.getInstance(_context).handleError(
                    new ApplicationErrorData(ApplicationErrorData.ApplicationErrorType.JSON_ERROR, event.getError().toString(), this.getClass().getName() + ": hata: onGetGenericReportResultData - " + event.getError().toString())
            );
            return;
        }

        //gelen event te data yoksa hata ver
        if (event.getData() == null)
        {
            ApplicationHelpers.getInstance(_context).handleError(
                    new ApplicationErrorData(ApplicationErrorData.ApplicationErrorType.EVENT_DATA_ERROR, event.getError().toString(), this.getClass().getName() + ": hata: onGetGenericReportResultData - DATA yok")
            );
            return;
        }

        //gelen datayi al
        LinkedHashMap _lhm = (LinkedHashMap) event.getData();
        String _reportArrayString = (String) _lhm.get("report_result");
        JSONArray _jaReports = new JSONArray();
        try {
            _jaReports = new JSONArray(_reportArrayString);
        } catch (JSONException e) {
            genericReportBusEvent.setError(e);
        }


        genericReportBusEvent.setData(_jaReports);
        EventBus.getDefault().post(genericReportBusEvent);
    }
    //endregion

}
