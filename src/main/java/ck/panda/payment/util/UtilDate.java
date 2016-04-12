package ck.panda.payment.util;

import java.util.Date;
import java.util.Random;
import java.text.SimpleDateFormat;
import java.text.DateFormat;

/**
 * The following code is sample code to test for the convenience of business and provided businesses as needed for your site, according to technical writing , not have to use the code.
 * The code for study and research Alipay interface , only provide a reference .
 */
public class UtilDate {

    /** The year, month , day, hour ( not underlined ) yyyyMMddHHmmss. */
    public static final String dtLong = "yyyyMMddHHmmss";

    /** The full time yyyy-MM-dd HH: mm: ss. */
    public static final String simple = "yyyy-MM-dd HH:mm:ss";

    /** The date ( not underlined ) yyyyMMdd. */
    public static final String dtShort = "yyyyMMdd";

    /**
     * Returns the current system time ( accurate to the millisecond ) , as a unique order number
     *
     * @return In yyyyMMddHHmmss format of the current system time
     */
    public static String getOrderNum() {
        Date date = new Date();
        DateFormat df = new SimpleDateFormat(dtLong);
        return df.format(date);
    }

    /**
     * Get the current system date ( to the millisecond ) , format : yyyy-MM-dd HH: mm: ss.
     *
     * @return
     */
    public static String getDateFormatter() {
        Date date = new Date();
        DateFormat df = new SimpleDateFormat(simple);
        return df.format(date);
    }

    /**
     * Get the current system date ( to the nearest day ) , format : yyyyMMdd.
     *
     * @return
     */
    public static String getDate() {
        Date date = new Date();
        DateFormat df = new SimpleDateFormat(dtShort);
        return df.format(date);
    }

    /**
     * Generates a random three-digit.
     *
     * @return
     */
    public static String getThree() {
        Random rad = new Random();
        return rad.nextInt(1000) + "";
    }

}
