package com.besiktasshipyard.mobile.btys.businessLayer.hr;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.besiktasshipyard.mobile.btys.busEvents.iBusEvent;
import com.besiktasshipyard.mobile.btys.busEvents.onGetUserByIdData;
import com.besiktasshipyard.mobile.btys.busEvents.onGetUserListData;
import com.besiktasshipyard.mobile.btys.busEvents.onGetUserPhotoURL;
import com.besiktasshipyard.mobile.btys.busEvents.onGetVehicleList;
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

public class HRUsers {

    private iBusEvent userImageBusEvent;
    private iBusEvent userListBusEvent;
    private iBusEvent userDetailsBusEvent;
    private iBusEvent vehicleListBusEvent;

    private static HRUsers mInstance;
    private static Context _context;
    private static View _view;

    private HRUsers(Context context)
    {
        _context = context;
    }

    public static synchronized HRUsers getInstance(Context context, View... view) {
        if (mInstance == null) {
            mInstance = new HRUsers(context);
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

    //region getUserById
    public void getUserById(String userId, iBusEvent busEvent, boolean... getLastEnter_LastExit){
        //event busa kaydol
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        userDetailsBusEvent = busEvent;

        //son giriş ve son çıkış bilgisi alınsın mı - default false, eger oyle bir parametre gonderilirse true
        boolean _getLastEnter_LastExit = false;
        if(getLastEnter_LastExit.length > 0){
            _getLastEnter_LastExit = getLastEnter_LastExit[0];
        }

        //php servise gonderilecek data nesnesi olusturuluyor
        JSONObject _dataJSON = new JSONObject();
        try {
            _dataJSON.put("user_id",userId);
            _dataJSON.put("get_image_data",0); //resmi alma
            _dataJSON.put("get_user_education",0); //akademik egitim bilgisini alma
            _dataJSON.put("get_user_experience",0); //tecrube bilgisini alma
            _dataJSON.put("get_user_discipline",0); //disiplin bilgisini alma
            _dataJSON.put("get_user_absence",0); //gelmedigi gunler bilgisini alma
            _dataJSON.put("get_user_last_enter_exit",(_getLastEnter_LastExit?1:0)); //son giris, son cikis bilgisini arg a gore al ya da alma

            _dataJSON.put("get_user_leave",1); //izin bilgisini AL
            _dataJSON.put("get_last_graduated_school",1); //son mezun olunan okul bilgisini AL
        } catch (JSONException e) {
//            ApplicationHelpers.getInstance(_context).handleError("HRUsers: getUserById: Hata: JSON hatasi");
            ApplicationHelpers.getInstance(_context).handleError(
                    new ApplicationErrorData(ApplicationErrorData.ApplicationErrorType.JSON_ERROR, "", this.getClass().getName() + " HRUsers: getUserById: Hata: JSON hatasi" )
            );
        }

        String _aid="16", _pid="130";
        DataService _dataService = new DataService(_context);

        _dataService.execDataService (_aid, _pid, _dataJSON.toString(), new onGetUserByIdData());
    }

    /**
     * getUserListData sonucu gelen eventi dinleyen fonk.
     * getUserListData ile datayı DB den alır ve
     * getUserList ile post eder
     * @param event
     */
    @Subscribe
    public void onEvent(onGetUserByIdData event){
        //gelen event te error varsa isle
        if (event.getError() != null)
        {
//            ApplicationHelpers.getInstance(_context).handleError(this.getClass().getName() + ": hata: onGetUserByIdData - " + event.getError().toString());
            ApplicationHelpers.getInstance(_context).handleError(
                    new ApplicationErrorData(ApplicationErrorData.ApplicationErrorType.EVENT_DATA_ERROR, event.getError().toString(), this.getClass().getName() + " : hata: onGetUserByIdData - " + event.getError().toString() )
            );
            userDetailsBusEvent.setError("hata: onGetUserByIdData");
            //return;
        }

        //gelen event te data yoksa hata ver
        if (event.getData() == null)
        {
//            ApplicationHelpers.getInstance(_context).handleError(this.getClass().getName() + ": hata: onGetUserByIdData - DATA yok");
            ApplicationHelpers.getInstance(_context).handleError(
                    new ApplicationErrorData(ApplicationErrorData.ApplicationErrorType.EVENT_DATA_ERROR, "", this.getClass().getName() + " :hata: onGetUserByIdData - DATA yok")
            );
            userDetailsBusEvent.setError("onGetUserByIdData - DATA yok");
            //return;
        }

        if(event.getError() == null) {
            //gelen datayi al

            //get_user ile gelen kullanici bilgileri
            LinkedHashMap _lhm_get_user = (LinkedHashMap) event.getData();
            String _userDetailsString = (String) _lhm_get_user.get("get_user");
            JSONObject _joUserDetails = new JSONObject();
            try {
                _joUserDetails = new JSONObject(_userDetailsString);
            } catch (JSONException e) {
                userDetailsBusEvent.setError(e);
            }

            userDetailsBusEvent.setData(_joUserDetails);
        }
        EventBus.getDefault().post(userDetailsBusEvent);
    }
    //endregion

    //region getUserList
    /**
     * kullanici listesini getiren fonk.
     * @param busEvent listenin donecegi callback fonk.
     * @param view eger bir hata olursa hatanin gosterilecegi view (snackbar icin gerekli)
     */
    public void getUserList(iBusEvent busEvent, View... view){
        if(view.length > 0 && view[0] != null)
            _view = view[0];

        //event busa kaydol
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        userListBusEvent = busEvent;

        String _aid="0", _pid="130";
        DataService _dataService = new DataService(_context);

        JSONObject _dataJSON = new JSONObject();
        try {
            _dataJSON.put("get_list_data_only",1); //resmi almasin, sadece esas datayi alsin diye
        } catch (JSONException e) {
//            ApplicationHelpers.getInstance(_context).handleError("HRUsers: getUserById: Hata: JSON hatasi");
            ApplicationHelpers.getInstance(_context).handleError(
                new ApplicationErrorData(ApplicationErrorData.ApplicationErrorType.JSON_ERROR, "", this.getClass().getName() + " : HRUsers: getUserById: Hata: JSON hatasi")
            );
        }

        _dataService.execDataService (_aid, _pid, _dataJSON.toString(), new onGetUserListData());
    }

    /**
     * getUserListData sonucu gelen eventi dinleyen fonk.
     * getUserListData ile datayı DB den alır ve
     * getUserList ile post eder
     * @param event
     */
    @Subscribe
    public void onEvent(onGetUserListData event){
        //gelen event te error varsa isle
        if (event.getError() != null)
        {
//            ApplicationHelpers.getInstance(_context).handleError(this.getClass().getName() + ": hata: onGetUserListData - " + event.getError().toString(), _view);
            ApplicationHelpers.getInstance(_context).handleError(
                    new ApplicationErrorData(ApplicationErrorData.ApplicationErrorType.JSON_ERROR, event.getError().toString(), this.getClass().getName() + ": hata: onGetUserListData - " + event.getError().toString())
            );
            return;
        }

        //gelen event te data yoksa hata ver
        if (event.getData() == null)
        {
//            ApplicationHelpers.getInstance(_context).handleError(this.getClass().getName() + ": hata: onGetUserListData - DATA yok", _view);
            ApplicationHelpers.getInstance(_context).handleError(
                    new ApplicationErrorData(ApplicationErrorData.ApplicationErrorType.EVENT_DATA_ERROR, event.getError().toString(), this.getClass().getName() + ": hata: onGetUserListData - DATA yok")
            );
            return;
        }

        //gelen datayi al
        LinkedHashMap _lhm = (LinkedHashMap) event.getData();
        String _userArrayString = (String) _lhm.get("users");
        JSONArray _jaUsers = new JSONArray();
        try {
            _jaUsers = new JSONArray(_userArrayString);
        } catch (JSONException e) {
            userListBusEvent.setError(e);
        }


        userListBusEvent.setData(_jaUsers);
        EventBus.getDefault().post(userListBusEvent);
    }
    //endregion

    //region getUserPhoto
    // TODO: 19.6.2017 su anda image alma islemi, ic ice servislerle devam ediyor. Bunun yerine sirali(senkron) volley cagrisi olsa daha anlasilir olur. Mesela once photo url alinacak, sonra bu url ile photo alinacak
    /**
     * kisi id si alir, user photo gosterir
     * bunu yaparken once photo url bulur, eventName e gonderir
     * sonra bu event icinden image alip gosterme servisi calistirilir
     * ör: getUserPhoto->onGetUserPhotoURL->showImageFile->onGetUserPhoto
     * seklindeki bir akisla gider. yani ic ice iki servis cagirimi
     * bir servisin responseu diger servisi cagiriyor
     * @param userId fotosu alinacak kisinin userId si
     * @param busEvent urlin gonderilecegi callBack metodu (bu metodu hangi aktivite cagiriyorsa, o aktivitede bulunacak)
     */
    public void getUserPhoto(String userId, iBusEvent busEvent)
    {
        //event busa kaydol
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        String _userPhotoURL = "";
        userImageBusEvent = busEvent;

        JSONObject _dataJSON = new JSONObject();
        try {
            _dataJSON.put("user_id",userId);
        } catch (JSONException e) {
//            ApplicationHelpers.getInstance(_context).handleError("HRUsers: getUserPhoto: Hata: JSON hatasi");
            ApplicationHelpers.getInstance(_context).handleError(
                new ApplicationErrorData(ApplicationErrorData.ApplicationErrorType.JSON_ERROR, "", this.getClass().getName() + " : getUserPhoto: Hata: JSON hatasi")
            );
        }

        String _aid="26", _pid="130";

        DataService _dataService = new DataService(_context);

        //onGetUserPhotoURL, HRUsersi cagiran aktivitede, cunku context o aktivite.
        //_dataService.execDataService (_aid, _pid, _dataJSON.toString(), busEvent);
        _dataService.execDataService (_aid, _pid, _dataJSON.toString(), new onGetUserPhotoURL());
    }

    /**
     * getUserPhoto sonucu gelen eventi dinleyen fonk.
     * @param event
     */
    @Subscribe
    public void onEvent(onGetUserPhotoURL event){

        //gelen event te error varsa isle
        if (event.getError() != null)
        {
//            ApplicationHelpers.getInstance(_context).handleError("DisplayMessageActivity: hata: onGetUserPhotoURL - " + event.getError().toString());
            ApplicationHelpers.getInstance(_context).handleError(
                    new ApplicationErrorData(ApplicationErrorData.ApplicationErrorType.EVENT_DATA_ERROR, event.getError().toString(), this.getClass().getName() + " :hata: onGetUserPhotoURL")
            );
            return;
        }

        //gelen event te data yoksa hata ver
        if (event.getData() == null)
        {
//            ApplicationHelpers.getInstance(_context).handleError("DisplayMessageActivity: hata: onGetUserPhotoURL - DATA yok");
            ApplicationHelpers.getInstance(_context).handleError(
                    new ApplicationErrorData(ApplicationErrorData.ApplicationErrorType.EVENT_DATA_ERROR, "", this.getClass().getName() + " hata: onGetUserPhotoURL - DATA yok")
            );
            return;
        }

        //gelen datayi al ve resim url al
        String _imageURL = "";
        JSONObject _resultURLJSON = new JSONObject((LinkedHashMap) event.getData());
        try {
            _imageURL = _resultURLJSON.getString("user_photo");
            showImageFile(_imageURL, userImageBusEvent);

        } catch (JSONException e) {
//            ApplicationHelpers.getInstance(_context).handleError("DisplayMessageActivity: hata: onGetUserPhotoURL - URL yok");
            ApplicationHelpers.getInstance(_context).handleError(
                    new ApplicationErrorData(ApplicationErrorData.ApplicationErrorType.JSON_ERROR, "", this.getClass().getName() + "  hata: onGetUserPhotoURL - URL yok")
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

    //region getVehicleList
    /**
     * kullanici listesini getiren fonk.
     * @param busEvent listenin donecegi callback fonk.
     * @param view eger bir hata olursa hatanin gosterilecegi view (snackbar icin gerekli)
     */
    public void getVehicleList(iBusEvent busEvent, View... view){
        if(view.length > 0 && view[0] != null)
            _view = view[0];

        //event busa kaydol
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        vehicleListBusEvent = busEvent;

        String _aid="6", _pid="160";
        DataService _dataService = new DataService(_context);

//        JSONObject _dataJSON = new JSONObject();
//        try {
//            _dataJSON.put("get_list_data_only",1); //resmi almasin, sadece esas datayi alsin diye
//        } catch (JSONException e) {
//            ApplicationHelpers.getInstance(_context).handleError("HRUsers: getRepairProjectsList: Hata: JSON hatasi");
//        }

//        _dataService.execDataService (_aid, _pid, _dataJSON.toString(), new onGetRepairProjectsListData());
        _dataService.execDataService (_aid, _pid, "", new onGetVehicleList());
    }

    /**
     * getUserListData sonucu gelen eventi dinleyen fonk.
     * getUserListData ile datayı DB den alır ve
     * getUserList ile post eder
     * @param event
     */
    @Subscribe
    public void onEvent(onGetVehicleList event){
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
        String _vehiclesArrayString = (String) _lhm.get("vehicles");
        JSONArray _jaVehicles = new JSONArray();
        try {
            _jaVehicles = new JSONArray(_vehiclesArrayString);
        } catch (JSONException e) {
            vehicleListBusEvent.setError(e);
        }


        vehicleListBusEvent.setData(_jaVehicles);
        EventBus.getDefault().post(vehicleListBusEvent);
    }
    //endregion
}
