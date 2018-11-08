package com.besiktasshipyard.mobile.btys.businessLayer.dataItems.contractorWorkers;

import com.besiktasshipyard.mobile.btys.helpers.StringHelpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContractorWorkerListData {

    //private Context _context;

    /**
     * An array of sample (dummy) items.
     */
    public static final List<ContractorWorkerListItem> ITEMS = new ArrayList<ContractorWorkerListItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, ContractorWorkerListItem> ITEM_MAP = new HashMap<String, ContractorWorkerListItem>();

    public ContractorWorkerListData() {
    }



    public ContractorWorkerListData(JSONArray jaListData) {
        clearItems();

        String _id = "";
        String _name_surname = "";
        String _profession_name = "";
        String _contractor_name = "";

        for (int i = 0; i < jaListData.length(); i++) {
            JSONObject _joListItem = null;
            try {
                _joListItem = jaListData.getJSONObject(i);

                _id = _joListItem.getString("id");
                _name_surname = _joListItem.getString("name") + " " + _joListItem.getString("surname");
                _profession_name =_joListItem.getString("meslek_adi");
                _contractor_name =_joListItem.getString("contractor_name");
                ContractorWorkerListItem _contractorWorkerListItem =
                        new ContractorWorkerListItem
                        (
                            _id
                            ,_name_surname
                            ,_profession_name
                            ,_contractor_name
                        );

                addItem(_contractorWorkerListItem);

            } catch (JSONException e) {
                _id = "";
                _name_surname = "";
                _profession_name = "";
                _contractor_name = "";
            }

            _id = null;
            _name_surname = null;
            _profession_name = null;
            _contractor_name = null;
        }
    }

    private static void addItem(ContractorWorkerListItem item) {
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
    public static class ContractorWorkerListItem {
        public final String id;
        public final String name_surname;
        public final String profession_name;
        public final String contractor_name;

        public ContractorWorkerListItem(String id, String name_surname, String profession_name, String contractor_name) {
            this.id = id;
            this.name_surname = name_surname;
            this.profession_name = StringHelpers.getCapitalizedString_Fully(profession_name, true);
            this.contractor_name = StringHelpers.getCapitalizedString_Fully(contractor_name, true);
        }

        @Override
        public String toString() {
            return name_surname;
        }
    }
}
