package com.besiktasshipyard.mobile.btys.businessLayer.dataItems.vehicleList;

import com.besiktasshipyard.mobile.btys.helpers.StringHelpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class VehicleListData {

    //private Context _context;

    /**
     * An array of sample (dummy) items.
     */
    public static final List<VehicleListItem> ITEMS = new ArrayList<VehicleListItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, VehicleListItem> ITEM_MAP = new HashMap<String, VehicleListItem>();

    public VehicleListData() {
    }



    public VehicleListData(JSONArray jaListData) {
        clearItems();
//mark_model, plate_number, driver, status, lastEnterDate, lastExitDate;
        String _id = "", _mark_model = "", _plate_number = "", _driver = "", _status = "", _lastEnterDate = "", _lastExitDate = "";

        for (int i = 0; i < jaListData.length(); i++) {
            JSONObject _joListItem = null;
            try {
                _joListItem = jaListData.getJSONObject(i);

                _id = _joListItem.getString("id");
                _mark_model = _joListItem.getString("marka") + " " + _joListItem.getString("model");
                _plate_number =_joListItem.getString("plaka");
                _driver =_joListItem.getString("son_surucu");
                _status =_joListItem.getString("durum");
                _lastEnterDate =_joListItem.getString("son_giris_tarihi");
                _lastExitDate =_joListItem.getString("son_cikis_tarihi");

                VehicleListItem _vehicleListItem =
                        new VehicleListItem
                        (
                            _id
                            ,_mark_model
                            ,_plate_number
                            ,_driver
                            ,_status
                            ,_lastEnterDate
                            ,_lastExitDate
                        );

                addItem(_vehicleListItem);

            } catch (JSONException e) {
                _id = "";
                _mark_model = "";
                _plate_number = "";
                _driver = "";
                _status = "";
                _lastEnterDate = "";
                _lastExitDate = "";
            }

            _id = null;
            _mark_model = null;
            _plate_number = null;
            _driver = null;
            _status = null;
            _lastEnterDate = null;
            _lastExitDate = null;
        }
    }

    private static void addItem(VehicleListItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private void clearItems(){
        ITEMS.clear();
        ITEM_MAP.clear();

    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class VehicleListItem {
        public final String id, mark_model, plate_number, driver, status, lastEnterDate, lastExitDate;

        public VehicleListItem(String id, String mark_model, String plate_number, String driver, String status, String lastEnterDate, String lastExitDate) {
            this.id = id;
            this.mark_model = mark_model;
            this.plate_number = plate_number;
            this.driver = StringHelpers.getCapitalizedString_Fully(driver, true);
            this.status = status;
            this.lastEnterDate = lastEnterDate;
            this.lastExitDate = lastExitDate;
        }

        @Override
        public String toString() {
            return mark_model;
        }
    }
}
