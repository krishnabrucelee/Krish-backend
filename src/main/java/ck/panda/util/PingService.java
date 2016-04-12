package ck.panda.util;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.LinkedList;
import org.apache.commons.httpclient.NameValuePair;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;

/**
 * MR.ping service connectivity for IP cost update.
 *
 */
@Service
public class PingService {

    /** MR.ping server for connectivity. */
    @Autowired
    private PingServer server;

    /** Admin role. */
    @Value("${mrping.url}")
    private String apiURL;

    /**
     * Set MR.ping connection URL.
     *
     * @param server sets these values.
     */
    public void setServer(PingServer server) {
        this.server = server;
    }

    /**
     * Add offering plan cost for usage calculation.
     *
     * @param requestJson - request json value
     * @return - Json string response
     * @throws Exception - Raise if any error
     */
    public String addPlanCost(JSONObject requestJson) throws Exception {
        server.setServer(apiURL + "/offeringCost");
        String arguments = server.getJsonToString(requestJson);
        String responseJson = server.postRequest(arguments);
        return responseJson;
    }

    /**
     * Add domain to ping for usage calculation.
     *
     * @param requestJson - request json value
     * @return - Json string response
     * @throws Exception - Raise if any error
     */
    public String addDomainToPing(JSONObject requestJson) throws Exception {
        server.setServer(apiURL + "/domain");
        String arguments = server.getJsonToString(requestJson);
        String responseJson = server.postRequest(arguments);
        return responseJson;
    }

    /**
     * Add department to ping for usage calculation.
     *
     * @param requestJson - request json value
     * @return - Json string response
     * @throws Exception - Raise if any error
     */
    public String addDepartmentToPing(JSONObject requestJson) throws Exception {
        server.setServer(apiURL + "/department");
        String arguments = server.getJsonToString(requestJson);
        String responseJson = server.postRequest(arguments);
        return responseJson;
    }

    /**
     * Add project to ping for usage calculation.
     *
     * @param requestJson - request json value
     * @return - Json string response
     * @throws Exception - Raise if any error
     */
    public String addProjectToPing(JSONObject requestJson) throws Exception {
        server.setServer(apiURL + "/project");
        String arguments = server.getJsonToString(requestJson);
        String responseJson = server.postRequest(arguments);
        return responseJson;
    }

    /**
     * Add instance custom offering to ping for usage calculation.
     *
     * @param requestJson - request json value
     * @return - Json string response
     * @throws Exception - Raise if any error
     */
    public String updateTemplateSize(JSONObject requestJson) throws Exception {
        server.setServer(apiURL + "/usage/templateSize");
        String arguments = server.getJsonToString(requestJson);
        String responseJson = server.postRequest(arguments);
        return responseJson;
    }

    /**
     * Add organization to ping.
     *
     * @param requestJson - request json value
     * @return - Json string response
     * @throws Exception - Raise if any error
     */
    public String addOraganizationToPing(JSONObject requestJson) throws Exception {
        server.setServer(apiURL + "/organization");
        String arguments = server.getJsonToString(requestJson);
        String responseJson = server.postRequest(arguments);
        return responseJson;
    }

    /**
     * Update organization to ping.
     *
     * @param requestJson - request json value
     * @return - Json string response
     * @throws Exception - Raise if any error
     */
    public String updateOraganizationToPing(JSONObject requestJson) throws Exception {
        server.setServer(apiURL + "/organization");
        String arguments = server.getJsonToString(requestJson);
        String responseJson = server.putRequest(arguments);
        return responseJson;
    }

    /**
     * Update invoice to ping.
     *
     * @param requestJson - request json value
     * @return - Json string response
     * @throws Exception - Raise if any error
     */
    public String updateInvoiceToPing(JSONObject requestJson) throws Exception {
        server.setServer(apiURL + "/invoice");
        String arguments = server.getJsonToString(requestJson);
        String responseJson = server.postRequest(arguments);
        return responseJson;
    }

    /**
     * Get the invoice by invoice id.
     *
     * @param invoiceId invoice id
     * @return - Json string response
     * @throws Exception - Raise if any error
     */
    public String getInvoiceById(String id) throws Exception {
        server.setServer(apiURL + "/invoice/"+id);
        LinkedList<NameValuePair> arguments = new LinkedList<NameValuePair>();
        String responseJson = server.request(arguments);
        return responseJson;
    }

    /**
     * Initial sync for domain, department and project.
     *
     * @param requestJson - request json value
     * @return - Json string response
     * @throws Exception - Raise if any error
     */
    public String pingInitialSync(JSONObject requestJson) throws Exception {
        server.setServer(apiURL + "/domain/sync");
        String arguments = server.getJsonToString(requestJson);
        String responseJson = server.postRequest(arguments);
        return responseJson;
    }

    /**
     * Check if an IP port is open or not.
     *
     * @param errors error check
     * @return connection status
     * @throws Exception - Raise if any error
     */
    public Boolean apiConnectionCheck(Errors errors) throws Exception {
        Boolean status = false;
        try {
            Socket socket = new Socket();
            URL aURL = new URL(apiURL);
            socket.connect(new InetSocketAddress(aURL.getHost(), aURL.getPort()), 5);
            socket.close();
            status = true;
        } catch (Exception ex) {
            if (errors == null) {
                return status;
            }
            errors.addGlobalError("Mr.Ping Server is not Reachable");
            throw new ApplicationException(errors);
        }
        return status;
    }
}

