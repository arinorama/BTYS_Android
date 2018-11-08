package com.besiktasshipyard.mobile.btys.businessLayer.hr;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.besiktasshipyard.mobile.btys.busEvents.iBusEvent;
import com.besiktasshipyard.mobile.btys.busEvents.onGetContractorWorkerByIdData;
import com.besiktasshipyard.mobile.btys.busEvents.onGetContractorWorkerById;
import com.besiktasshipyard.mobile.btys.busEvents.onGetContractorWorkerListData;
import com.besiktasshipyard.mobile.btys.busEvents.onGetContractorWorkerPhotoURL;
import com.besiktasshipyard.mobile.btys.helpers.ApplicationErrorData;
import com.besiktasshipyard.mobile.btys.helpers.ApplicationHelpers;
import com.besiktasshipyard.mobile.btys.helpers.ApplicationVariables;
import com.besiktasshipyard.mobile.btys.helpers.DataService;
import com.besiktasshipyard.mobile.btys.helpers.RequestQueueSingleton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;


/**
 * Created by aliarin on 16.6.2017.
 */

public class ContractorWorkers {

    private Context _context;
    private iBusEvent contractorWorkerImageBusEvent;
    private iBusEvent contractorWorkerListBusEvent;
    private iBusEvent contractorWorkerDetailsBusEvent;

    private static ContractorWorkers mInstance;
    private static Context mCtx;
    private static View _view;

    private ContractorWorkers(Context context)
    {
        mCtx = context;
    }

    public static synchronized ContractorWorkers getInstance(Context context, View... view) {
        if (mInstance == null) {
            mInstance = new ContractorWorkers(context);
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

    /**
     * user details adres alanlarından adresi tek bir stringe çevirir
     * @param userDetails
     * @return
     */
    public static String getAdressString(JSONObject userDetails){
        String _adres_apt = ""
                ,_adres_no = ""
                ,_adres_cadde = ""
                ,_adres_semt = ""
                ,_adres_ilce = ""
                ,_adres_mahalle = ""
                ,_adres_il_adi = ""
                ,_addressString;

        try {
            _adres_apt = userDetails.getString("adres_apt");
        } catch (JSONException e) {
            Log.i("ali", "HRUsers: getAdressString: adres_apt");
        }

        try {
            _adres_no = userDetails.getString("adres_no");
        } catch (JSONException e) {
            Log.i("ali", "HRUsers: getAdressString: _adres_no");
        }

        try {
            _adres_cadde = userDetails.getString("adres_cadde");
        } catch (JSONException e) {
            Log.i("ali", "HRUsers: getAdressString: _adres_cadde");
        }

        try {
            _adres_semt = userDetails.getString("adres_semt");
        } catch (JSONException e) {
            Log.i("ali", "HRUsers: getAdressString: _adres_semt");
        }

        try {
            _adres_ilce = userDetails.getString("adres_ilce");
        } catch (JSONException e) {
            Log.i("ali", "HRUsers: getAdressString: _adres_ilce");
        }

        try {
            _adres_mahalle = userDetails.getString("adres_mahalle");
        } catch (JSONException e) {
            Log.i("ali", "HRUsers: getAdressString: _adres_mahalle");
        }

        try {
            _adres_il_adi = userDetails.getString("adres_il_adi");
        } catch (JSONException e) {
            Log.i("ali", "HRUsers: getAdressString: adres_il_adi");
        }

        _addressString =
                _adres_apt
                        + (_adres_no.equals("") ? _adres_no: ", d-no: " + _adres_no)
                        + (_adres_cadde.equals("") ? _adres_cadde: ", " + _adres_cadde + " cd.")
                        + (_adres_semt.equals("") ? _adres_semt: ", " + _adres_semt)
                        + (_adres_mahalle.equals("") ? _adres_mahalle: ", " + _adres_mahalle)
                        + (_adres_ilce.equals("") ? _adres_ilce: ", " + _adres_ilce)
                        + (_adres_il_adi.equals("") ? _adres_il_adi: ", " + _adres_il_adi);

        //eger adres "," karakteri ile basliyorsa, bunu kaldir
        if(!_addressString.isEmpty() && _addressString.substring(0,1).equals(","))
            _addressString = _addressString.substring(1);

        return _addressString;
    }

    //region getContractorWorkerById
    public void getContractorWorkerById(String contractorWorkerId, iBusEvent busEvent){
        //event busa kaydol
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        contractorWorkerDetailsBusEvent = busEvent;

        //php servise gonderilecek data nesnesi olusturuluyor
        JSONObject _dataJSON = new JSONObject();
        try {
            _dataJSON.put("contractor_worker_id",contractorWorkerId);
        } catch (JSONException e) {
//            ApplicationHelpers.getInstance(_context).handleError("HRUsers: getUserById: Hata: JSON hatasi");
            ApplicationHelpers.getInstance(_context).handleError(
                    new ApplicationErrorData(ApplicationErrorData.ApplicationErrorType.JSON_ERROR, "", "HRUsers: getUserById: Hata: JSON hatasi" )
            );
        }

        String _aid="30", _pid="4100";
        DataService _dataService = new DataService(_context);

        _dataService.execDataService (_aid, _pid, _dataJSON.toString(), new onGetContractorWorkerByIdData());
    }

    /**
     * getUserListData sonucu gelen eventi dinleyen fonk.
     * getUserListData ile datayı DB den alır ve
     * getUserList ile post eder
     * @param event
     */
    @Subscribe
    public void onEvent(onGetContractorWorkerByIdData event){
        //gelen event te error varsa isle
        if (event.getError() != null)
        {
//            ApplicationHelpers.getInstance(_context).handleError(this.getClass().getName() + ": hata: onGetContractorWorkerByIdData - " + event.getError().toString());
            ApplicationHelpers.getInstance(_context).handleError(
                    new ApplicationErrorData(ApplicationErrorData.ApplicationErrorType.EVENT_DATA_ERROR, event.getError().toString(), this.getClass().getName() + ": hata: onGetUserPhoto - DATA yok" )
            );
            contractorWorkerDetailsBusEvent.setError("hata: onGetContractorWorkerByIdData");
            //return;
        }

        //gelen event te data yoksa hata ver
        if (event.getData() == null)
        {
//            ApplicationHelpers.getInstance(_context).handleError(this.getClass().getName() + ": hata: onGetContractorWorkerByIdData - DATA yok");
            ApplicationHelpers.getInstance(_context).handleError(
                    new ApplicationErrorData(ApplicationErrorData.ApplicationErrorType.EVENT_DATA_ERROR, event.getError().toString(), this.getClass().getName() + ": hata: onGetContractorWorkerByIdData - DATA yok" )
            );
            contractorWorkerDetailsBusEvent.setError("onGetContractorWorkerByIdData - DATA yok");
            //return;
        }

        if(event.getError() == null) {
            //gelen datayi al
            LinkedHashMap _lhm = (LinkedHashMap) event.getData();
            String _contractorWorkerDetailsString = (String) _lhm.get("contractor_worker");
            JSONObject _joContractorWorkerDetails = new JSONObject();
            try {
                _joContractorWorkerDetails = new JSONObject(_contractorWorkerDetailsString);
            } catch (JSONException e) {
                contractorWorkerDetailsBusEvent.setError(e);
            }

            contractorWorkerDetailsBusEvent.setData(_joContractorWorkerDetails);
        }
        EventBus.getDefault().post(contractorWorkerDetailsBusEvent);
    }
    //endregion

    //region getContractorWorkerList
    /**
     * kullanici listesini getiren fonk.
     * @param busEvent listenin donecegi callback fonk.
     * @param view eger bir hata olursa hatanin gosterilecegi view (snackbar icin gerekli)
     */
    public void getContractorWorkerList(iBusEvent busEvent, View... view){
        if(view.length > 0 && view[0] != null)
            _view = view[0];

        //event busa kaydol
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        contractorWorkerListBusEvent = busEvent;

        String _aid="21", _pid="4121";
        DataService _dataService = new DataService(_context);

        _dataService.execDataService (_aid, _pid, "", new onGetContractorWorkerListData());
    }

    /**
     * getUserListData sonucu gelen eventi dinleyen fonk.
     * getUserListData ile datayı DB den alır ve
     * getUserList ile post eder
     * @param event
     */
    @Subscribe
    public void onEvent(onGetContractorWorkerListData event){
        //gelen event te error varsa isle
        if (event.getError() != null)
        {
//            ApplicationHelpers.getInstance(_context).handleError(this.getClass().getName() + ": hata: onGetContractorWorkerListData - " + event.getError().toString(), _view);
            ApplicationHelpers.getInstance(_context).handleError(
                    new ApplicationErrorData(ApplicationErrorData.ApplicationErrorType.EVENT_DATA_ERROR, event.getError().toString(), this.getClass().getName() + ": hata: onGetContractorWorkerListData" )
            );
            return;
        }

        //gelen event te data yoksa hata ver
        if (event.getData() == null)
        {
//            ApplicationHelpers.getInstance(_context).handleError(this.getClass().getName() + ": hata: onGetContractorWorkerListData - DATA yok", _view);
            ApplicationHelpers.getInstance(_context).handleError(
                    new ApplicationErrorData(ApplicationErrorData.ApplicationErrorType.EVENT_DATA_ERROR, event.getError().toString(), this.getClass().getName() + ": hata: onGetContractorWorkerListData - DATA yok" )
            );
            return;
        }

        //gelen datayi al
        LinkedHashMap _lhm = (LinkedHashMap) event.getData();
        String _contractorWorkerArrayString = (String) _lhm.get("contractors_personnels");
        JSONArray _jaContractorWorkers = new JSONArray();
        try {
            _jaContractorWorkers = new JSONArray(_contractorWorkerArrayString);
        } catch (JSONException e) {
            contractorWorkerListBusEvent.setError(e);
        }

        contractorWorkerListBusEvent.setData(_jaContractorWorkers);
        EventBus.getDefault().post(contractorWorkerListBusEvent);
    }
    //endregion

    //region getContractorWorkerPhoto
    // TODO: 19.6.2017 su anda image alma islemi, ic ice servislerle devam ediyor. Bunun yerine sirali(senkron) volley cagrisi olsa daha anlasilir olur. Mesela once photo url alinacak, sonra bu url ile photo alinacak
    /**
     * kisi id si alir, user photo gosterir
     * bunu yaparken once photo url bulur, eventName e gonderir
     * sonra bu event icinden image alip gosterme servisi calistirilir
     * ör: getUserPhoto->onGetUserPhotoURL->showImageFile->onGetUserPhoto
     * seklindeki bir akisla gider. yani ic ice iki servis cagirimi
     * bir servisin responseu diger servisi cagiriyor
     * @param contractorWorkerId fotosu alinacak kisinin userId si
     * @param busEvent urlin gonderilecegi callBack metodu (bu metodu hangi aktivite cagiriyorsa, o aktivitede bulunacak)
     */
    public void getContractorWorkerPhoto(String contractorWorkerId, iBusEvent busEvent)
    {
        //event busa kaydol
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        String _contractorWorkerPhotoURL = "";
        contractorWorkerImageBusEvent = busEvent;

        JSONObject _dataJSON = new JSONObject();
        try {
            _dataJSON.put("contractor_worker_id",contractorWorkerId);
        } catch (JSONException e) {
//            ApplicationHelpers.getInstance(_context).handleError("ContractorWorkers: getContractorWorkerPhoto: Hata: JSON hatasi");
            ApplicationHelpers.getInstance(_context).handleError(
                    new ApplicationErrorData(ApplicationErrorData.ApplicationErrorType.JSON_ERROR, "", "ContractorWorkers: getContractorWorkerPhoto: Hata: JSON hatasi" )
            );
        }

        String _aid="31", _pid="4100";

        DataService _dataService = new DataService(_context);

        //onGetUserPhotoURL, HRUsersi cagiran aktivitede, cunku context o aktivite.
        //_dataService.execDataService (_aid, _pid, _dataJSON.toString(), busEvent);
        _dataService.execDataService (_aid, _pid, _dataJSON.toString(), new onGetContractorWorkerPhotoURL());
    }

    /**
     * getUserPhoto sonucu gelen eventi dinleyen fonk.
     * @param event
     */
    @Subscribe
    public void onEvent(onGetContractorWorkerPhotoURL event){

        //gelen event te error varsa isle
        if (event.getError() != null)
        {
//            ApplicationHelpers.getInstance(_context).handleError("ContractorWorkers: hata: onGetContractorWorkerPhotoURL - " + event.getError().toString());
            ApplicationHelpers.getInstance(_context).handleError(
                    new ApplicationErrorData(ApplicationErrorData.ApplicationErrorType.EVENT_DATA_ERROR, event.getError().toString(), this.getClass().getName() + " hata: onGetContractorWorkerPhotoURL - " )
            );
            return;
        }

        //gelen event te data yoksa hata ver
        if (event.getData() == null)
        {
//            ApplicationHelpers.getInstance(_context).handleError("ContractorWorkers: hata: onGetContractorWorkerPhotoURL - DATA yok");
            ApplicationHelpers.getInstance(_context).handleError(
                    new ApplicationErrorData(ApplicationErrorData.ApplicationErrorType.EVENT_DATA_ERROR, event.getError().toString(), this.getClass().getName() + " hata: onGetContractorWorkerListData - DATA yok" )
            );
            return;
        }

        //gelen datayi al ve resim url al
        String _imageURL = "";
        JSONObject _resultURLJSON = new JSONObject((LinkedHashMap) event.getData());
        try {
            _imageURL = _resultURLJSON.getString("worker_photo");
            showImageFile(_imageURL, contractorWorkerImageBusEvent);

        } catch (JSONException e) {
//            ApplicationHelpers.getInstance(_context).handleError("ContractorWorkers: hata: onGetContractorWorkerPhotoURL - URL yok");
            ApplicationHelpers.getInstance(_context).handleError(
                    new ApplicationErrorData(ApplicationErrorData.ApplicationErrorType.EVENT_DATA_ERROR, event.getError().toString(), this.getClass().getName() + " hata: onGetContractorWorkerPhotoURL - URL yok" )
            );
            return;
        }


    }

    public Bitmap showImageFile(String url, final iBusEvent busEvent)
    {
        // TODO: 19.6.2017 sadece test zamanı, burayı eger test ise diye degistir
//        url = url.replace("localhost","192.168.20.61").replace("\\","").replace("\"","");
        url = url.replace("viashipyard", ApplicationVariables.get_serverIP()).replace("\\","").replace("\"","");

        //USER RESIM
        ImageLoader imageLoader = RequestQueueSingleton.getInstance(_context).getImageLoader();

        imageLoader.get(url, new ImageLoader.ImageListener() {
            public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
                if (response.getBitmap() != null) {
                    Bitmap _imageBitmap = response.getBitmap();
                    busEvent.setData(_imageBitmap);
                    EventBus.getDefault().post(busEvent);
                }
            }

            public void onErrorResponse(VolleyError error) {
                busEvent.setError(error);
                EventBus.getDefault().post(busEvent);
            }
        });
        return null;
    }
    //endregion


}
