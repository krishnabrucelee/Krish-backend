package ck.panda.payment.util;

import org.apache.commons.httpclient.Header;
import ck.panda.constants.PaymentConstants;
import java.io.UnsupportedEncodingException;

/**
 * The following code is sample code to test for the convenience of business and provided businesses as needed for your
 * site, according to technical writing , not have to use the code. The code for study and research Alipay interface ,
 * only provide a reference.
 */
public class HttpResponse {

    /**
     * Back in the Header information
     */
    private Header[] responseHeaders;

    /**
     * String type result.
     */
    private String stringResult;

    /**
     * byte type of result
     */
    private byte[] byteResult;

    public Header[] getResponseHeaders() {
        return responseHeaders;
    }

    public void setResponseHeaders(Header[] responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public byte[] getByteResult() {
        if (byteResult != null) {
            return byteResult;
        }
        if (stringResult != null) {
            return stringResult.getBytes();
        }
        return null;
    }

    public void setByteResult(byte[] byteResult) {
        this.byteResult = byteResult;
    }

    public String getStringResult() throws UnsupportedEncodingException {
        if (stringResult != null) {
            return stringResult;
        }
        if (byteResult != null) {
            return new String(byteResult, PaymentConstants.INPUT_CHARSET);
        }
        return null;
    }

    public void setStringResult(String stringResult) {
        this.stringResult = stringResult;
    }

}
