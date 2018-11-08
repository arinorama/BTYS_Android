package com.besiktasshipyard.mobile.btys.businessLayer.dataItems.reports;

import com.besiktasshipyard.mobile.btys.helpers.StringHelpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportListData {

    //private Context _context;

    /**
     * An array of sample (dummy) items.
     */
    public static final List<ReportListItem> ITEMS = new ArrayList<ReportListItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, ReportListItem> ITEM_MAP = new HashMap<String, ReportListItem>();

    public ReportListData() {
    }



    public ReportListData(JSONArray jaListData) {
        clearItems();

        String _id = "";
        String _title = "";
        String _notes = "";

        for (int i = 0; i < jaListData.length(); i++) {
            JSONObject _joListItem = null;
            try {
                _joListItem = jaListData.getJSONObject(i);

                _id = _joListItem.getString("id");
                _title = _joListItem.getString("title");
                _notes =_joListItem.getString("notes");
                ReportListItem _ReportListItem =
                        new ReportListItem
                        (
                            _id
                            ,_title
                            ,_notes
                        );

                addItem(_ReportListItem);

            } catch (JSONException e) {
                _id = "";
                _title = "";
                _notes = "";
            }

            _id = null;
            _title = null;
            _notes = null;
        }
    }

    private static void addItem(ReportListItem item) {
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
    public static class ReportListItem {
        public final String id;
        public final String title;
        public final String notes;

        public ReportListItem(String id, String title, String notes) {
            this.id = id;
            this.title = title.replace("Null","");
            this.notes = StringHelpers.getCapitalizedString_Fully(notes.replace("null",""), true);
        }

        @Override
        public String toString() {
            return title;
        }
    }
}
