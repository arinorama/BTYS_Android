package com.besiktasshipyard.mobile.btys.businessLayer.dataItems.reports;

import android.util.Log;

import com.besiktasshipyard.mobile.btys.helpers.StringHelpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GenericReportResultData {

    //private Context _context;

    /**
     * An array of sample (dummy) items.
     */
    public static final List<GenericReportResultItem> ITEMS = new ArrayList<GenericReportResultItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, GenericReportResultItem> ITEM_MAP = new HashMap<String, GenericReportResultItem>();

    public int reportDate = 0;

    public GenericReportResultData() {
    }



    public GenericReportResultData(JSONArray jaListData) {
        clearItems();

        JSONObject _joListItem = null;
        /*
        ArrayList _joListItemProperties = new ArrayList();
                try {
                    _joListItem = jaListData.getJSONObject(i);
                    Iterator<String> keys = _joListItem.keys();
                    while ( keys.hasNext() ){
                        _joListItemProperties.add((String)keys.next()); // First key in your json object
                    }
                } catch (JSONException e) {
                    Log.i("ali", "GenericReportResultData: JSON");
                }
         */

        for (int i = 0; i < jaListData.length(); i++) {
            try {
                _joListItem = jaListData.getJSONObject(i);

                GenericReportResultItem _GenericReportResultItem = new GenericReportResultItem(_joListItem);
                reportDate = Integer.valueOf(_joListItem.getString("RAPOR_TARIHI"));
                addItem(_GenericReportResultItem);

            } catch (JSONException e) {
                Log.i("ali", "GenericReportResultData: JSON Hatasi");
            }
        }

//
//        String _id = "";
//        String _title = "";
//        String _notes = "";
//
//        for (int i = 0; i < jaListData.length(); i++) {
//            JSONObject _joListItem = null;
//            try {
//                _joListItem = jaListData.getJSONObject(i);
//
//                _id = _joListItem.getString("id");
//                _title = _joListItem.getString("title");
//                _notes =_joListItem.getString("notes");
//                GenericReportResultItem _GenericReportResultItem =
//                        new GenericReportResultItem
//                        (
//                            _id
//                            ,_title
//                            ,_notes
//                        );
//
//                addItem(_GenericReportResultItem);
//
//            } catch (JSONException e) {
//                _id = "";
//                _title = "";
//                _notes = "";
//            }
//
//            _id = null;
//            _title = null;
//            _notes = null;
//        }
    }

    private static void addItem(GenericReportResultItem item) {
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

    public static class GenericReportResultItem {
        public final JSONObject _genericJSONObject;
        public final String id;
        public static int _lastId = 0;

        public GenericReportResultItem(JSONObject genericItem) {
            this._genericJSONObject = genericItem;
            _lastId += 1;
            this.id = String.valueOf(_lastId);
        }

        @Override
        public String toString() {
            return "JSON Object";
        }
    }
}
