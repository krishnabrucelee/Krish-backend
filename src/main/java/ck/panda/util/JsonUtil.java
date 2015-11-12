package ck.panda.util;

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
     * @throws Exception raise if error
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
     * @throws Exception raise if error
     */
    public static Integer getIntegerValue(JSONObject object, String key) throws Exception {
        if (object.has(key)) {
            return Integer.valueOf(object.getInt(key));
        } else {
            return Integer.valueOf(0);
        }
    }
}
