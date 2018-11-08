package com.besiktasshipyard.mobile.btys.businessLayer.repair;

import android.content.Context;
import android.view.View;

import com.besiktasshipyard.mobile.btys.busEvents.iBusEvent;
import com.besiktasshipyard.mobile.btys.busEvents.onGetRepairProjectsListData;
import com.besiktasshipyard.mobile.btys.helpers.ApplicationErrorData;
import com.besiktasshipyard.mobile.btys.helpers.ApplicationHelpers;
import com.besiktasshipyard.mobile.btys.helpers.DataService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.LinkedHashMap;


/**
 * Created by aliarin on 16.6.2017.
 */

public class RepairProjects {

    private iBusEvent repairProjectsListBusEvent;

    private static RepairProjects mInstance;
    private static Context _context;
    private static View _view;

    private RepairProjects(Context context)
    {
        _context = context;
    }

    public static synchronized RepairProjects getInstance(Context context, View... view) {
        if (mInstance == null) {
            mInstance = new RepairProjects(context);
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

    //region getRepairProjectsList
    /**
     * proje listesini getiren fonk.
     * @param busEvent listenin donecegi callback fonk.
     * @param view eger bir hata olursa hatanin gosterilecegi view (snackbar icin gerekli)
     */
    public void getRepairProjectsList(iBusEvent busEvent, View... view){
        if(view.length > 0 && view[0] != null)
            _view = view[0];

        //event busa kaydol
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        repairProjectsListBusEvent = busEvent;

        String _aid="49", _pid="4000";
        DataService _dataService = new DataService(_context);

//        JSONObject _dataJSON = new JSONObject();
//        try {
//            _dataJSON.put("get_list_data_only",1); //resmi almasin, sadece esas datayi alsin diye
//        } catch (JSONException e) {
//            ApplicationHelpers.getInstance(_context).handleError("HRUsers: getRepairProjectsList: Hata: JSON hatasi");
//        }

//        _dataService.execDataService (_aid, _pid, _dataJSON.toString(), new onGetRepairProjectsListData());
        _dataService.execDataService (_aid, _pid, "", new onGetRepairProjectsListData());
    }

    /**
     * getUserListData sonucu gelen eventi dinleyen fonk.
     * getUserListData ile datayı DB den alır ve
     * getUserList ile post eder
     * @param event
     */
    @Subscribe
    public void onEvent(onGetRepairProjectsListData event){
        //gelen event te error varsa isle
        if (event.getError() != null)
        {
//            ApplicationHelpers.getInstance(_context).handleError(this.getClass().getName() + ": hata: onGetRepairProjectsListData - " + event.getError().toString(), _view);
            ApplicationHelpers.getInstance(_context).handleError(
                    new ApplicationErrorData(ApplicationErrorData.ApplicationErrorType.EVENT_DATA_ERROR, event.getError().toString(), this.getClass().getName() + " : hata: onGetRepairProjectsListData")
            );
            return;
        }

        //gelen event te data yoksa hata ver
        if (event.getData() == null)
        {
//            ApplicationHelpers.getInstance(_context).handleError(this.getClass().getName() + ": hata: onGetRepairProjectsListData - DATA yok", _view);
            ApplicationHelpers.getInstance(_context).handleError(
                    new ApplicationErrorData(ApplicationErrorData.ApplicationErrorType.EVENT_DATA_ERROR, "", this.getClass().getName() + " : onGetRepairProjectsListData - DATA yok")
            );
            return;
        }

        //gelen datayi al
        LinkedHashMap _lhm = (LinkedHashMap) event.getData();
        String _projectsArrayString = (String) _lhm.get("projects");
        JSONArray _jaProjects = new JSONArray();
        try {
            _jaProjects = new JSONArray(_projectsArrayString);
        } catch (JSONException e) {
            repairProjectsListBusEvent.setError(e);
        }


        repairProjectsListBusEvent.setData(_jaProjects);
        EventBus.getDefault().post(repairProjectsListBusEvent);
    }
    //endregion
}
