package com.besiktasshipyard.mobile.btys.helpers;

import android.content.res.Resources;

import com.besiktasshipyard.mobile.btys.MainActivity;

/**
 * Created by aliarin on 18.7.2017.
 */

public class ApplicationErrorData {

    private final String errorType;
    private final String errorMessage;
    private String errorDescription;
    private final String errorCode;

    public ApplicationErrorData(String errorType, String errorCode, String errorMessage){
        this.errorType = errorType;
        this.errorCode = errorCode.replace("[","").replace("]","").replace("'","").replace("\"","");
        this.errorMessage = errorMessage;
    }

    public String getErrorType() {
        return errorType;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getErrorDescription() {
        String _packageName = MainActivity._context.getPackageName();
        Resources _resources = MainActivity._context.getResources();

        this.errorDescription = "Bilinmeyen Hata" ;
        if (!errorCode.equals("")) {
            int _resourceID = _resources.getIdentifier(errorCode, "string", _packageName);
            try {
                this.errorDescription = _resources.getString(_resourceID);
            } catch (Resources.NotFoundException e){
                this.errorDescription = errorCode;
            }

        }
        return this.errorDescription;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public class ApplicationErrorType{
        public static final String APPLICATION_ERROR = "application_error";
        public static final String COMMINICATION_ERROR = "communication_error";
        public static final String JSON_ERROR = "json_error";
        public static final String EVENT_DATA_ERROR = "event_data_error";
    }
}


