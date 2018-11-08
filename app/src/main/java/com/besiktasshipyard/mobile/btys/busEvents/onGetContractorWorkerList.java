package com.besiktasshipyard.mobile.btys.busEvents;

/**
 * Created by aliarin on 28.6.2017.
 */

public class onGetContractorWorkerList implements iBusEvent {
    private Object _data, _error;

    @Override
    public Object getData() {
        return _data;
    }

    @Override
    public void setData(Object data) {
        _data = data;
    }

    @Override
    public Object getError() {
        return _error;
    }

    @Override
    public void setError(Object error) {
        _error = error;
    }
}
