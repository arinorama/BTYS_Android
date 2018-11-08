package com.besiktasshipyard.mobile.btys.businessLayer.dataItems.repairProjectsList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RepairProjectsListData {

    //private Context _context;

    /**
     * An array of sample (dummy) items.
     */
    public static final List<RepairProjectsListItem> ITEMS = new ArrayList<RepairProjectsListItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, RepairProjectsListItem> ITEM_MAP = new HashMap<String, RepairProjectsListItem>();

    public RepairProjectsListData() {
    }



    public RepairProjectsListData(JSONArray jaListData) {
        clearItems();
//mark_model, plate_number, driver, status, lastEnterDate, lastExitDate;
//        String _id = "", _mark_model = "", _plate_number = "", _driver = "", _status = "", _lastEnterDate = "", _lastExitDate = "";
        String _id= "", _project_code= "", _project_description= "", _engineer_id= "", _engineer_id2= "", _engineer_name_surname= "", _engineer2_name_surname= "", _estimated_start_date= "", _estimated_finish_date= "", _actual_start_date= "", _actual_finish_date= "", _shipyard_start_date= "", _shipyard_finish_date= "", _status= "", _work_order_count_total= "", _work_order_count_finished= "", _material_price_tl = "";
        for (int i = 0; i < jaListData.length(); i++) {
            JSONObject _joListItem = null;
            try {
                _joListItem = jaListData.getJSONObject(i);

                _id = _joListItem.getString("id");
                _project_code = _joListItem.getString("project_code");//_joListItem.getString("marka") + " " + _joListItem.getString("model");
                _project_description = _joListItem.getString("project_description");
                _engineer_id = _joListItem.getString("engineer_id");
                _engineer_id2 = _joListItem.getString("engineer_id2");
                _engineer_name_surname = _joListItem.getString("engineer_name_surname");
                _engineer2_name_surname = _joListItem.getString("engineer2_name_surname");
                _estimated_start_date =_joListItem.getString("estimated_start_date");
                _estimated_finish_date =_joListItem.getString("estimated_finish_date");
                _actual_start_date =_joListItem.getString("actual_start_date");
                _actual_finish_date = _joListItem.getString("actual_finish_date");
                _status = _joListItem.getString("status");
                _work_order_count_total = _joListItem.getString("work_order_count_total");
                _work_order_count_finished = _joListItem.getString("work_order_count_finished");
                _material_price_tl = _joListItem.getString("material_price_tl");

                RepairProjectsListItem _repairProjectsListItem =
                        new RepairProjectsListItem
                        (
                            _id
                            ,_project_code
                            ,_project_description
                            ,_engineer_id
                            ,_engineer_id2
                            ,_engineer_name_surname
                            ,_engineer2_name_surname
                            ,_estimated_start_date
                            ,_estimated_finish_date
                            ,_actual_start_date
                            ,_actual_finish_date
                            ,_shipyard_start_date
                            ,_shipyard_finish_date
                            ,_status
                            ,_work_order_count_total
                            ,_work_order_count_finished
                            ,_material_price_tl
                        );

                addItem(_repairProjectsListItem);

            } catch (JSONException e) {
                _id = "";
                _project_code = "";
                _project_description = "";
                _engineer_id = "";
                _engineer_id2 = "";
                _engineer_name_surname = "";
                _engineer2_name_surname = "";
                _estimated_start_date = "";
                _estimated_finish_date = "";
                _actual_start_date = "";
                _actual_finish_date = "";
                _shipyard_start_date = "";
                _shipyard_finish_date = "";
                _status = "";
                _work_order_count_total = "";
                _work_order_count_finished = "";
                _material_price_tl = "";
            }

            _id = null;
            _project_code = null;
            _project_description = null;
            _engineer_id = null;
            _engineer_id2 = null;
            _engineer_name_surname = null;
            _engineer2_name_surname = null;
            _estimated_start_date = null;
            _estimated_finish_date = null;
            _actual_start_date = null;
            _actual_finish_date = null;
            _shipyard_start_date = null;
            _shipyard_finish_date = null;
            _status = null;
            _work_order_count_total = null;
            _work_order_count_finished = null;
            _material_price_tl = null;
        }
    }

    private static void addItem(RepairProjectsListItem item) {
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
    public static class RepairProjectsListItem {
        public final String id, project_code, project_description, engineer_id, engineer_id2, engineer_name_surname, engineer2_name_surname, estimated_start_date, estimated_finish_date, actual_start_date, actual_finish_date, shipyard_start_date, shipyard_finish_date, status, work_order_count_total, work_order_count_finished, material_price_tl;

        public RepairProjectsListItem(String id, String  project_code, String  project_description, String  engineer_id, String  engineer_id2, String  engineer_name_surname, String  engineer2_name_surname, String  estimated_start_date, String  estimated_finish_date, String  actual_start_date, String  actual_finish_date, String  shipyard_start_date, String  shipyard_finish_date, String  status, String  work_order_count_total, String  work_order_count_finished, String  material_price_tl
        ) {
            this.id = id;
            /*this.mark_model = mark_model;
            this.plate_number = plate_number;
            this.driver = StringHelpers.getCapitalizedString_Fully(driver, true);
            this.status = status;
            this.lastEnterDate = lastEnterDate;
            this.lastExitDate = lastExitDate;*/
            this.project_code = project_code;
            this.project_description = project_description;
            this.engineer_id = engineer_id;
            this.engineer_id2 = engineer_id2;
            this.engineer_name_surname = engineer_name_surname;
            this.engineer2_name_surname = engineer2_name_surname;
            this.estimated_start_date = estimated_start_date;
            this.estimated_finish_date = estimated_finish_date;
            this.actual_start_date = actual_start_date;
            this.actual_finish_date = actual_finish_date;
            this.shipyard_start_date = shipyard_start_date;
            this.shipyard_finish_date = shipyard_finish_date;
            this.status = status;
            this.work_order_count_total = work_order_count_total;
            this.work_order_count_finished = work_order_count_finished;
            this.material_price_tl = material_price_tl;

        }

        @Override
        public String toString() {
            return project_description;
        }
    }
}
