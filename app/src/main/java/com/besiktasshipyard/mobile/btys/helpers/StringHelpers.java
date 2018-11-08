package com.besiktasshipyard.mobile.btys.helpers;

import android.text.TextUtils;
import android.util.Log;

import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Normalizer;
import java.util.Iterator;

/**
 * Created by aliarin on 30.6.2017.
 */

public class StringHelpers {
    private static final StringHelpers ourInstance = new StringHelpers();

    static StringHelpers getInstance() {
        return ourInstance;
    }

    private StringHelpers() {
    }

    /**
     * string bos ise belirtilen ile degistirilir
     * @param inputString
     * @param outputString
     * @return
     */
    public static String checkAndReplaceEmptyStringWith(String inputString, String outputString){
        return isEmptyString(inputString) ? outputString : inputString;
    }

    /**
     * string null mu bos mu yoksa "null" mu kontrol eder
     * @param inputString
     * @return
     */
    public static Boolean isEmptyString(String inputString){
//        Boolean _result = false;
//
//        if(TextUtils.isEmpty(inputString) || inputString == "null")
//            _result = true;

//        return _result;
        return (TextUtils.isEmpty(inputString) || inputString == "null");
    }

    /**
     * türkçe harfleri arindirarak, ingilizce alfabeye cevirir
     * @param inputString
     * @param conversionType
     * @return
     */
    public static String getNormalizedString(String inputString, ConversionType conversionType){
        String _normalizedFilterPattern = Normalizer.normalize(inputString, Normalizer.Form.NFD);
        _normalizedFilterPattern = _normalizedFilterPattern.replaceAll("[^\\p{ASCII}]", "");

        if(conversionType == ConversionType.TO_LOWERCASE)
            _normalizedFilterPattern = _normalizedFilterPattern.toLowerCase();
        else
            _normalizedFilterPattern = _normalizedFilterPattern.toUpperCase();

        return _normalizedFilterPattern;
    }

    /**
     * bir stringdeki tüm kelimelerin bas harflerini buyuk, diger harflerini kucuk getirir
     * eger normalizeFirst gonderilirse, once kelimedeki turkce harfleri ingilizce ile degistirir
     * @param inputString
     * @return
     */
    public static String getCapitalizedString_Fully(String inputString, boolean normalizeFirst){
        inputString = StringHelpers.checkAndReplaceEmptyStringWith(inputString, "");
        String _returnString = inputString;
        if (normalizeFirst)
            _returnString = getNormalizedString(inputString, ConversionType.TO_LOWERCASE);

        _returnString = WordUtils.capitalizeFully(inputString);

        return _returnString;

    }

    public static Boolean stringContains(String stringToBeSearched, String search){
        boolean _result = false;

        String _normalizedStringToBeSearched = StringHelpers.getNormalizedString(stringToBeSearched, StringHelpers.ConversionType.TO_UPPERCASE);
        String _normalizedSearch = StringHelpers.getNormalizedString(search, StringHelpers.ConversionType.TO_UPPERCASE);
        if(_normalizedStringToBeSearched.contains(_normalizedSearch))
            _result = true;

        return _result;
    }

    public enum ConversionType{
        TO_UPPERCASE
        ,TO_LOWERCASE
    }

    public static boolean checkIfJSONObjectContainsString(JSONObject jsonObject, String strSearchText){
        boolean _result = false;

        for(Iterator<String> keys = jsonObject.keys(); keys.hasNext();) {
            try {
                String _property = keys.next();
                String _value = String.valueOf(jsonObject.get(_property));
                if (stringContains(_value, strSearchText))
                    _result = true;

            } catch (JSONException e) {
                Log.i("ali", "checkIfJSONObjectContainsString: JSON");
            }
        }

        return _result;
    }
}
