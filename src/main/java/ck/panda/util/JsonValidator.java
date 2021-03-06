package ck.panda.util;

import org.json.JSONObject;

/**
 * JSON validator to handle NULL validations.
 *
 */
public abstract class JsonValidator {

    /**
     * @param object JSON array
     * @param key value
     * @return string value
     * @throws Exception raise if error
     */
    public static String jsonStringValidation(JSONObject object, String key) throws Exception {
        if (object.has(key)) {
            return object.getString(key);
        } else {
            return "";
        }
    }

    /**
     * @param object JSON array
     * @param key value
     * @return boolean status
     * @throws Exception raise if error
     */
    public static Boolean jsonBooleanValidation(JSONObject object, String key) throws Exception {
        if (object.has(key)) {
            return object.getBoolean(key);
        } else {
            return false;
        }
    }

    /**
     *
     * @param object JSON array
     * @param key value
     * @return integer value
     * @throws Exception raise if error.
     */
    public static Integer jsonIntegerValidation(JSONObject object, String key) throws Exception {
        if (object.has(key)) {
            return object.getInt(key);
        } else {
            return 0;
        }
    }

    /**
    *
    * @param key value
    * @return integer value
    * @throws Exception raise if error.
    */
   public static Integer stringToIntegerConvertion(String key) throws Exception {
       if (key != null) {
           return Integer.getInteger(key);
       } else {
           return 0;
       }
   }

   /**
   *
   * @param key value
   * @return integer value
   * @throws Exception raise if error.
   */
  public static Double stringToDoubleConvertion(String key) throws Exception {
      if (key != null) {
          return Double.valueOf(key);
      } else {
          return 0.0;
      }
  }
}
