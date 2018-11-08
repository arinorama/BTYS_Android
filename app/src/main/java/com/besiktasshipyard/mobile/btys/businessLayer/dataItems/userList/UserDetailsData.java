package com.besiktasshipyard.mobile.btys.businessLayer.dataItems.userList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 */
public class UserDetailsData {

    //private Context _context;

    /**
     * An array of sample (dummy) items.
     */
    public static final List<UserListItem> ITEMS = new ArrayList<UserListItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, UserListItem> ITEM_MAP = new HashMap<String, UserListItem>();

    private static final int COUNT = 25;


//    public UserListData(Context context) {
//        _context = context;
//        HRUsers _hrUsers = new HRUsers(_context);
//        _hrUsers.getUserList(new onGetUserList());
//    }


    public UserDetailsData() {
    }

    public UserDetailsData(JSONArray jaListData) {
        String _id = "";
        String _name_surname = "";
        String _profession_name = "";
int j = 0;
        for (int i = 0; i < jaListData.length(); i++) {
            JSONObject _joListItem = null;
            try {
                _joListItem = jaListData.getJSONObject(i);

                _id = _joListItem.getString("id");
                _name_surname = _joListItem.getString("name") + " " + _joListItem.getString("surname");
                _profession_name =_joListItem.getString("plate_number");
                UserListItem _userListItem =
                        new UserListItem
                        (
                            _id
                            ,_name_surname
                            ,_profession_name
                        );

                addItem(_userListItem);

            } catch (JSONException e) {
                _id = "";
                _name_surname = "";
                _profession_name = "";
            }

            _id = null;
            _name_surname = null;
            _profession_name = null;
        }
    }




    private static void addItem(UserListItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static UserListItem createDummyItem(int position) {
        return new UserListItem(String.valueOf(position), "Item " + position, makeDetails(position));
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class UserListItem {
        public final String id;
        public final String name_surname;
        public final String profession_name;

        public UserListItem(String id, String name_surname, String profession_name) {
            this.id = id;
            this.name_surname = name_surname;
            this.profession_name = profession_name;
        }

        @Override
        public String toString() {
            return name_surname;
        }
    }
}
