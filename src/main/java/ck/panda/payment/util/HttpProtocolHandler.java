package ck.panda.payment.util;

import org.apache.commons.httpclient.HttpException;
import java.io.IOException;
import java.net.UnknownHostException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.util.IdleConnectionTimeoutThread;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.FilePartSource;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * The following code is sample code to test for the convenience of business and provided businesses as needed for your
 * site, according to technical writing , not have to use the code. The code for study and research Alipay interface ,
 * only provide a reference.
 */
public class HttpProtocolHandler {

    private static String DEFAULT_CHARSET = "GBK";

    /** Connection timeout , the bean factory settings, the default is 8 seconds. */
    private int defaultConnectionTimeout = 8000;

    /** Response timeout , the bean factory settings, the default is 30 seconds. */
    private int defaultSoTimeout = 30000;

    /** Idle connection timeout set by the bean factory, the default is 60 seconds. */
    private int defaultIdleConnTimeout = 60000;

    private int defaultMaxConnPerHost = 30;

    private int defaultMaxTotalConn = 80;

    /**
     * Default wait HttpConnectionManager return connection timeout ( only when it reaches the maximum number of
     * connections to function ) : 1 sec.
     */
    private static final long defaultHttpConnectionManagerTimeout = 3 * 1000;

    /**
     * HTTP connection manager , the connection manager must be thread safe .
     */
    private HttpConnectionManager connectionManager;

    private static HttpProtocolHandler httpProtocolHandler = new HttpProtocolHandler();

    /**
     * Factory Method
     *
     * @return
     */
    public static HttpProtocolHandler getInstance() {
        return httpProtocolHandler;
    }

    /**
     * Private constructor.
     */
    private HttpProtocolHandler() {
        // Create a thread-safe HTTP connection pool.
        connectionManager = new MultiThreadedHttpConnectionManager();
        connectionManager.getParams().setDefaultMaxConnectionsPerHost(defaultMaxConnPerHost);
        connectionManager.getParams().setMaxTotalConnections(defaultMaxTotalConn);

        IdleConnectionTimeoutThread ict = new IdleConnectionTimeoutThread();
        ict.addConnectionManager(connectionManager);
        ict.setConnectionTimeout(defaultIdleConnTimeout);

        ict.start();
    }

    /**
     * Http request execution
     *
     * @param request Request data
     * @param strParaFileName File Type parameter name
     * @param strFilePath file path.
     * @return
     * @throws HttpException, IOException
     */
    public HttpResponse execute(HttpRequest request, String strParaFileName, String strFilePath)
            throws HttpException, IOException {
        HttpClient httpclient = new HttpClient(connectionManager);

        // Setting connection timeout.
        int connectionTimeout = defaultConnectionTimeout;
        if (request.getConnectionTimeout() > 0) {
            connectionTimeout = request.getConnectionTimeout();
        }
        httpclient.getHttpConnectionManager().getParams().setConnectionTimeout(connectionTimeout);

        // Setting the timeout response.
        int soTimeout = defaultSoTimeout;
        if (request.getTimeout() > 0) {
            soTimeout = request.getTimeout();
        }
        httpclient.getHttpConnectionManager().getParams().setSoTimeout(soTimeout);

        // Set the wait time ConnectionManager release connection
        httpclient.getParams().setConnectionManagerTimeout(defaultHttpConnectionManagerTimeout);

        String charset = request.getCharset();
        charset = charset == null ? DEFAULT_CHARSET : charset;
        HttpMethod method = null;

        // get with mode without uploading files
        if (request.getMethod().equals(HttpRequest.METHOD_GET)) {
            method = new GetMethod(request.getUrl());
            method.getParams().setCredentialCharset(charset);

            // parseNotifyConfig will ensure that the use GET method , request certain use QueryString
            method.setQueryString(request.getQueryString());
        } else if (strParaFileName.equals("") && strFilePath.equals("")) {
            // not upload files with a post pattern
            method = new PostMethod(request.getUrl());
            ((PostMethod) method).addParameters(request.getParameters());
            method.addRequestHeader("Content-Type", "application/x-www-form-urlencoded; text/html; charset=" + charset);
        } else {
            // post mode and upload files with
            method = new PostMethod(request.getUrl());
            List<Part> parts = new ArrayList<Part>();
            for (int i = 0; i < request.getParameters().length; i++) {
                parts.add(new StringPart(request.getParameters()[i].getName(), request.getParameters()[i].getValue(),
                        charset));
            }
            // Increase the file parameters , strParaFileName parameter is the name , using a local file
            parts.add(new FilePart(strParaFileName, new FilePartSource(new File(strFilePath))));

            // Set request body
            ((PostMethod) method)
                    .setRequestEntity(new MultipartRequestEntity(parts.toArray(new Part[0]), new HttpMethodParams()));
        }

        // Http Header set the User-Agent Properties
        method.addRequestHeader("User-Agent", "Mozilla/4.0");
        HttpResponse response = new HttpResponse();

        try {
            httpclient.executeMethod(method);
            if (request.getResultType().equals(HttpResultType.STRING)) {
                response.setStringResult(method.getResponseBodyAsString());
            } else if (request.getResultType().equals(HttpResultType.BYTES)) {
                response.setByteResult(method.getResponseBody());
            }
            response.setResponseHeaders(method.getResponseHeaders());
        } catch (UnknownHostException ex) {

            return null;
        } catch (IOException ex) {

            return null;
        } catch (Exception ex) {

            return null;
        } finally {
            method.releaseConnection();
        }
        return response;
    }

    /**
     * The NameValuePairs array into a string
     *
     * @param nameValues
     * @return
     */
    protected String toString(NameValuePair[] nameValues) {
        if (nameValues == null || nameValues.length == 0) {
            return "null";
        }

        StringBuffer buffer = new StringBuffer();

        for (int i = 0; i < nameValues.length; i++) {
            NameValuePair nameValue = nameValues[i];

            if (i == 0) {
                buffer.append(nameValue.getName() + "=" + nameValue.getValue());
            } else {
                buffer.append("&" + nameValue.getName() + "=" + nameValue.getValue());
            }
        }

        return buffer.toString();
    }
}
