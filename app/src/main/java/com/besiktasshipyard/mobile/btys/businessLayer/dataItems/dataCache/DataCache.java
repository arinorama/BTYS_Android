package com.besiktasshipyard.mobile.btys.businessLayer.dataItems.dataCache;

/**
 * Created by aliarin on 11.7.2017.
 */

public class DataCache {
    private static final DataCache ourInstance = new DataCache();

    public static DataCache getInstance() {
        return ourInstance;
    }

    private DataCache() {
    }

    public void clearDataCache(){
        set_userListData(null);
        set_contractorWorkerListData(null);
        set_vehicleListData(null);
    }

    //region userList
    private Object _userListData = null;

    public Object get_userListData() {
        return _userListData;
    }

    public void set_userListData(Object _userListData) {
        this._userListData = _userListData;
    }
    //endregion

    //region contractorWorkerList
    private Object _contractorWorkerListData = null;

    public Object get_contractorWorkerListData() {
        return _contractorWorkerListData;
    }

    public void set_contractorWorkerListData(Object _contractorWorkerListData) {
        this._contractorWorkerListData = _contractorWorkerListData;
    }
    //endregion

    //region vehicleList
    private Object _vehicleListData = null;

    public Object get_vehicleListData() {
        return _vehicleListData;
    }

    public void set_vehicleListData(Object vehicleListData) {
        this._vehicleListData = vehicleListData;
    }
    //endregion

    //region reportList
    private Object _reportListData = null;

    public Object get_reportListData() {
        return _reportListData;
    }

    public void set_reportListData(Object _reportListData) {
        this._reportListData = _reportListData;
    }
    //endregion

    //region repairProjectsList
    private Object _repairProjectsListData = null;

    public Object get_repairProjectsListData() {
        return _repairProjectsListData;
    }

    public void set_repairProjectsListData(Object repairProjectsListData) {
        this._repairProjectsListData = repairProjectsListData;
    }
    //endregion
}
