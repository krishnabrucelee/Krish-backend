package ck.panda.util;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.json.JSONObject;

/**
 * JSON validator to handle NULL validations.
 */
public abstract class JsonUtil {
    /**
     * @param object JSON array
     * @param key value
     * @return string value
     * @throws Exception raise if error
     */
    public static String getStringValue(JSONObject object, String key) throws Exception {
        if (object.has(key)) {
            return object.getString(key);
        } else {
            return null;
        }
    }

    /**
     * @param object JSON array
     * @param key value
     * @return boolean status
     * @throws Exception  raise if error
     */
    public static Boolean getBooleanValue(JSONObject object, String key) throws Exception {
        if (object.has(key)) {
            return object.getBoolean(key);
        } else {
            return false;
        }
    }

    /**
     * @param object JSON array
     * @param key value
     * @return integer value.
     * @throws Exception  raise if error
     */
    public static Integer getIntegerValue(JSONObject object, String key) throws Exception {
        if (object.has(key)) {
            return Integer.valueOf(object.getInt(key));
        } else {
            return Integer.valueOf(0);
        }
    }

    /**
     * Converting date time validation.
     *
     * @param object json object.
     * @param key key value
     * @return date time.
     * @throws Exception error.
     */
    public static ZonedDateTime jsonZonedDateTimeValidation(JSONObject object, String key) throws Exception {
        if (object.has(key)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            ZonedDateTime dateTime = ZonedDateTime.parse(key, formatter);
            return dateTime;
        } else {
            return null;
        }
    }

//    /**
//     * @param object JSON array
//     * @param key value
//     * @return string value
//     * @throws Exception raise if error
//     */
//    public static String getEnumValue(JSONObject object, String key) throws Exception {
//        if (object.has(key)) {
//            return object.optString(key);
//        } else {
//            return null;
//        }
//    }
}
