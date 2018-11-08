package com.besiktasshipyard.mobile.btys.businessLayer.dataItems.general;

import java.io.Serializable;

public class SpinnerDataItem implements Serializable {

    private String _itemId;
    private String _itemName;

    public SpinnerDataItem(String _itemId, String _itemName) {
        this._itemId = _itemId;
        this._itemName = _itemName;
    }

    @Override
    public String toString() {
        return _itemName;
    }

    public String get_itemId() {
        return _itemId;
    }

    public void set_itemId(String _itemId) {
        this._itemId = _itemId;
    }

    public String get_itemName() {
        return _itemName;
    }

    public void set_itemName(String _itemName) {
        this._itemName = _itemName;
    }
}
