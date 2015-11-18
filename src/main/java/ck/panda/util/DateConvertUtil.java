package ck.panda.util;

/**
 * Date convert util to handle all the date conversion.
 */
public abstract class DateConvertUtil {

    /**
     * @return current system time stamp
     * @throws Exception raise if error
     */
    public static Long getTimestamp() throws Exception {
        return System.currentTimeMillis() / 1000;
    }
}
