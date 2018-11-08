package com.besiktasshipyard.mobile.btys.businessLayer.dataItems.userList;

import com.besiktasshipyard.mobile.btys.helpers.StringHelpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserListData {

    //private Context _context;

    /**
     * An array of sample (dummy) items.
     */
    public static final List<UserListItem> ITEMS = new ArrayList<UserListItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, UserListItem> ITEM_MAP = new HashMap<String, UserListItem>();

    public UserListData() {
    }



    public UserListData(JSONArray jaListData) {
        clearItems();

        String _id = "";
        String _name_surname = "";
        String _profession_name = "";
        String _sectionName = "";
        String _titleId = "";

        for (int i = 0; i < jaListData.length(); i++) {
            JSONObject _joListItem = null;
            try {
                _joListItem = jaListData.getJSONObject(i);

                _id = _joListItem.getString("id");
                _name_surname = _joListItem.getString("name") + " " + _joListItem.getString("surname");
                _profession_name =_joListItem.getString("profession_name_short");
                _sectionName =_joListItem.getString("section_name");
                _titleId = _joListItem.getString("title_id");

                UserListItem _userListItem =
                        new UserListItem
                        (
                            _id
                            ,_name_surname
                            ,_profession_name
                            ,_sectionName
                            ,_titleId
                        );

                addItem(_userListItem);

            } catch (JSONException e) {
                _id = "";
                _name_surname = "";
                _profession_name = "";
                _sectionName = "";
                _titleId = "";
            }

            _id = null;
            _name_surname = null;
            _profession_name = null;
            _sectionName = null;
            _titleId = null;
        }
    }

    private static void addItem(UserListItem item) {
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
    public static class UserListItem {
        public final String id;
        public final String name_surname;
        public final String profession_name;
        public final String section_name;
        public final String title_id;

        public UserListItem(String id, String name_surname, String profession_name, String section_name, String title_id) {
            this.id = id;
            this.name_surname = name_surname.replace("Null","");
            this.profession_name = StringHelpers.getCapitalizedString_Fully(profession_name.replace("null",""), true);
            this.section_name = StringHelpers.getCapitalizedString_Fully(section_name.replace("null",""), true);
            this.title_id = title_id;
        }

        @Override
        public String toString() {
            return name_surname;
        }
    }
}
