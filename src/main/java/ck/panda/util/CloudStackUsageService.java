package ck.panda.util;

import java.util.HashMap;
import java.util.LinkedList;
import org.w3c.dom.Document;
import java.util.List;
import org.apache.commons.httpclient.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * CloudStack Usage service for cloudStack server connectivity to list usages of cpu, memory etc.
 */
@Service
public class CloudStackUsageService {

	 /** Cloudstack server for connectivity. */
    @Autowired
    private CloudStackServer server;

    /** Sets api key , secret key and url.
     *
     * @param server sets these values.
     */
    public void setServer(CloudStackServer server) {
        this.server = server;
    }


    /**
     * Adds traffic type to a physical network
     *
     * @param physicalNetworkId The Physical Network ID
     * @param trafficType The trafficType to be added to the physical network
     * @param optional
     * @return
     * @throws Exception
     */
    public String addTrafficType(String physicalNetworkId, String trafficType,String response,
            HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("addTrafficType", optional);
        arguments.add(new NameValuePair("physicalnetworkid", physicalNetworkId));
        arguments.add(new NameValuePair("traffictype", trafficType));
        arguments.add(new NameValuePair("response", response));

        String responseDocument = server.request(arguments);

        return responseDocument;
    }


    /**
     * Deletes traffic type of a physical network
     *
     * @param trafficTypeId traffic type id
     * @return
     * @throws Exception
     */
    public String deleteTrafficType(String trafficTypeId, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("deleteTrafficType", null);
        arguments.add(new NameValuePair("id", trafficTypeId));

        String responseDocument = server.request(arguments);

        return  responseDocument;
    }


    /**
     * Lists traffic types of a given physical network.
     *
     * @param physicalNetworkId the Physical Network ID
     * @param optional
     * @return
     * @throws Exception
     */
    public String listTrafficTypes(String physicalNetworkId,String response,
            HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("listTrafficTypes", optional);
        arguments.add(new NameValuePair("physicalnetworkid", physicalNetworkId));

        String responseDocument = server.request(arguments);

        return  responseDocument;
    }


    /**
     * Updates traffic type of a physical network
     *
     * @param TrafficTypeId The traffic type id
     * @param optional
     * @return
     * @throws Exception
     */
    public String updateTrafficType(String TrafficTypeId,String response,
            HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("updateTrafficType", optional);
        arguments.add(new NameValuePair("id", TrafficTypeId));

       String  responseDocument = server.request(arguments);

        return  responseDocument;
    }



    /**
     * Lists implementors of implementor of a network traffic type or implementors of all network traffic types
     *
     * @param optional
     * @return
     * @throws Exception
     */
    public String listTrafficTypeImplementors(String response,
            HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("listTrafficTypeImplementors", optional);

       String responseDocument = server.request(arguments);

        return  responseDocument;
    }


    /**
     * Generates usage records. This will generate records only if there any records to be generated, i.e if the
     * scheduled usage job was not run or failed
     *
     * @param endDate End date range for usage record query. Use yyyy-MM-dd as the date format, e.g.
     * startDate=2009-06-03
     *
     * @param startDate Start date range for usage record query. Use yyyy-MM-dd as the date format, e.g.
     * startDate=2009-06-01.
     *
     * @param optional
     * @return
     * @throws Exception
     */
    public String generateUsageRecords(String endDate, String response,
            String startDate, HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("generateUsageRecords", optional);
        arguments.add(new NameValuePair("enddate", endDate));
        arguments.add(new NameValuePair("startdate", startDate));
        arguments.add(new NameValuePair("response", response));

        String responseDocument = server.request(arguments);

        return  responseDocument;
    }



    /**
     * Lists usage records for accounts
     *
     * @param endDate End date range for usage record query. Use yyyy-MM-dd as the date format, e.g.
     * startDate=2009-06-03.
     *
     * @param startDate Start date range for usage record query. Use yyyy-MM-dd as the date format, e.g.
     * startDate=2009-06-01.
     *
     * @param optional
     * @return
     * @throws Exception
     */
    public String listUsageRecords(String endDate,
            String startDate, String response, HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("listUsageRecords", optional);
        arguments.add(new NameValuePair("enddate", endDate));
        arguments.add(new NameValuePair("startdate", startDate));
        arguments.add(new NameValuePair("response", response));

       String  responseDocument = server.request(arguments);

        return responseDocument;
    }

    /**
     * List Usage Types
     *
     * @param optional
     * @return
     * @throws Exception
     */
    public String listUsageTypes(String response) throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("listUsageTypes", null);
        arguments.add(new NameValuePair("response", response));

        String responseDocument = server.request(arguments);

        return  responseDocument;
    }


    /**
     * Adds Traffic Monitor Host for Direct Network Usage
     *
     * @param url URL of the traffic monitor Host
     * @param zoneId Zone in which to add the external firewall appliance.
     * @return
     * @throws Exception
     */
    public String addTrafficMonitor(String url,
            String zoneId, String response) throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("addTrafficMonitor", null);
        arguments.add(new NameValuePair("url", url));
        arguments.add(new NameValuePair("zoneid", zoneId));
        arguments.add(new NameValuePair("response", response));


        String responseDocument = server.request(arguments);

        return responseDocument;
    }


    /**
     * Deletes an traffic monitor host.
     *
     * @param TrafficMonitorId The Id of the Traffic Monitor Host.
     * @return
     * @throws Exception
     */
    public String deleteTrafficMonitor(String TrafficMonitorId, String response
    ) throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("deleteTrafficMonitor", null);
        arguments.add(new NameValuePair("id", TrafficMonitorId));
        arguments.add(new NameValuePair("response", response));

        String responseDocument = server.request(arguments);

        return responseDocument;
    }


    /**
     * Retrieves the current status of asynchronous job for usage.
     *
     * @param asychronousJobid the ID of the asychronous job
     * @return
     * @throws Exception
     */
    public String usageJobResult(String asychronousJobid, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("queryAsyncJobResult", null);
        arguments.add(new NameValuePair("jobid", asychronousJobid));
        arguments.add(new NameValuePair("response", response));

        String responseDocument = server.request(arguments);

        return responseDocument;
    }


    /**
     * List traffic monitor Hosts.
     *
     * @param zoneId zone Id
     * @param optional
     * @return
     * @throws Exception
     */
    public String listTrafficMonitors(String zoneId,String response,
            HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("listTrafficMonitors", optional);
        arguments.add(new NameValuePair("zoneid", zoneId));
        arguments.add(new NameValuePair("response", response));

        String responseDocument = server.request(arguments);

        return  responseDocument;
    }

}