package ck.panda.payment.util;

import org.apache.commons.httpclient.NameValuePair;

/**
 * The following code is sample code to test for the convenience of business and provided businesses as needed for your
 * site, according to technical writing , not have to use the code. The code for study and research Alipay interface ,
 * only provide a reference .
 */
public class HttpRequest {

    /** HTTP GET method */
    public static final String METHOD_GET = "GET";

    /** HTTP POST method */
    public static final String METHOD_POST = "POST";

    /**
     * It is requested url
     */
    private String url = null;

    /**
     * The default request method
     */
    private String method = METHOD_POST;

    private int timeout = 0;

    private int connectionTimeout = 0;

    /**
     * Post manner assembled request parameter values
     */
    private NameValuePair[] parameters = null;

    /**
     * Request parameters corresponding to Get Information
     */
    private String queryString = null;

    /**
     * Default request encoding
     */
    private String charset = "GBK";

    /**
     * Request initiator ip address
     */
    private String clientIp;

    /**
     * Back way request
     */
    private HttpResultType resultType = HttpResultType.BYTES;

    public HttpRequest(HttpResultType resultType) {
        super();
        this.resultType = resultType;
    }

    /**
     * @return Returns the clientIp.
     */
    public String getClientIp() {
        return clientIp;
    }

    /**
     * @param clientIp The clientIp to set.
     */
    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public NameValuePair[] getParameters() {
        return parameters;
    }

    public void setParameters(NameValuePair[] parameters) {
        this.parameters = parameters;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * @return Returns the charset.
     */
    public String getCharset() {
        return charset;
    }

    /**
     * @param charset The charset to set.
     */
    public void setCharset(String charset) {
        this.charset = charset;
    }

    public HttpResultType getResultType() {
        return resultType;
    }

    public void setResultType(HttpResultType resultType) {
        this.resultType = resultType;
    }

}
