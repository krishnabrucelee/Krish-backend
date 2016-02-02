<<<<<<< HEAD


=======
>>>>>>> ddf963b11ff0a99b2a6a8f3c56119804093f74a6
package ck.panda.util;

import java.util.HashMap;

<<<<<<< HEAD
public class CloudStackOptionalUtil {

     /**
     * Update the Cloudstack API optional values.
=======
/**
 * In order to handle optional values for ACS.
 *
 */
public abstract class CloudStackOptionalUtil {

     /**
     * Update the Cloudstack API optional String values.
>>>>>>> ddf963b11ff0a99b2a6a8f3c56119804093f74a6
     *
     * @param key of the property
     * @param value of the property
     * @param optionalMap Hashmap of the optional parameters
     * @return Hashmap
     * @throws Exception error occurs.
     */
    public static HashMap<String, String> updateOptionalStringValue(String key, String value, HashMap<String, String> optionalMap) throws Exception {
        if (value != null) {
            optionalMap.put(key, value);
        }
        return optionalMap;
    }

    /**
<<<<<<< HEAD
     * Update the Cloudstack API optional values.
=======
     * Update the Cloudstack API Integer optional values.
>>>>>>> ddf963b11ff0a99b2a6a8f3c56119804093f74a6
     *
     * @param key of the property
     * @param value of the property
     * @param optionalMap Hashmap of the optional parameters
     * @return Hashmap
     * @throws Exception error occurs.
     */
    public static HashMap<String, String> updateOptionalIntegerValue(String key, Integer value, HashMap<String, String> optionalMap) throws Exception {
        if (value != null) {
            optionalMap.put(key, value.toString());
        }
        return optionalMap;
    }

    /**
<<<<<<< HEAD
     * Update the Cloudstack API optional values.
=======
     * Update the Cloudstack API optional Boolean values.
>>>>>>> ddf963b11ff0a99b2a6a8f3c56119804093f74a6
     *
     * @param key of the property
     * @param value of the property
     * @param optionalMap Hashmap of the optional parameters
     * @return Hashmap
     * @throws Exception error occurs.
     */
    public static HashMap<String, String> updateOptionalBooleanValue(String key, Boolean value, HashMap<String, String> optionalMap) throws Exception {
        if (value != null) {
            optionalMap.put(key, value.toString());
        }
        return optionalMap;
    }

    /**
     * Update the Cloudstack API optional values.
     *
     * @param key of the property
     * @param value of the property
     * @param optionalMap Hashmap of the optional parameters
     * @return Hashmap
     * @throws Exception error occurs.
     */
    public static HashMap<String, String> updateOptionalLongValue(String key, Long value, HashMap<String, String> optionalMap) throws Exception {
        if (value != null) {
            optionalMap.put(key, value.toString());
        }
        return optionalMap;
    }
}
<<<<<<< HEAD

=======
>>>>>>> ddf963b11ff0a99b2a6a8f3c56119804093f74a6
