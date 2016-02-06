package ck.panda.util;

import java.util.HashMap;
import java.util.LinkedList;
import org.apache.commons.httpclient.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * CloudStack Template service for cloudStack server connectivity with template.
 *
 */
@Service
public class CloudStackTemplateService {

    /** Cloud stack server for connectivity. */
    @Autowired
    private CloudStackServer server;

    /**
     * Set API key, Secret key and URL.
     *
     * @param server sets these values.
     */
    public void setServer(CloudStackServer server) {
        this.server = server;
    }

    /**
     * Creates a template of a virtual machine. The virtual machine must be in a STOPPED state. A template created from
     * this command is automatically designated as a private template visible to the account that created it.
     *
     * @param displayText - The display text of the template. This is usually used for display purposes
     * @param templateName - The name of the template
     * @param osTypeId - The ID of the OS Type that best represents the OS of this template
     * @param response - Response format as json
     * @param optional - List of optional values
     * @return - Json string response
     * @throws Exception - Raise if any error
     */
    public String createTemplate(String displayText, String templateName, String osTypeId, String response,
            HashMap<String, String> optional) throws Exception {
        LinkedList<NameValuePair> arguments = server.getDefaultQuery("createTemplate", optional);
        arguments.add(new NameValuePair("displaytext", displayText));
        arguments.add(new NameValuePair("name", templateName));
        arguments.add(new NameValuePair("ostypeid", osTypeId));
        arguments.add(new NameValuePair("response", response));
        String responseJson = server.request(arguments);
        return responseJson;
    }

    /**
     * Registers an existing template into the cloud.
     *
     * @param displayText - The display text of the template. This is usually used for display purposes
     * @param format - The format for the template. Possible values include QCOW2, RAW, and VHD
     * @param hypervisor - The target hypervisor for the template
     * @param templateName - The name of the template
     * @param osTypeId - The ID of the OS Type that best represents the OS of this template
     * @param url - The URL of where the template is hosted. Possible URL include http:// and https://
     * @param zoneId - The ID of the zone the template is to be hosted on
     * @param response - Response format as json
     * @param optional - List of optional values
     * @return - Json string response
     * @throws Exception - Raise if any error
     */
    public String registerTemplate(String displayText, String format, String hypervisor, String templateName,
            String osTypeId, String url, String zoneId, String response, HashMap<String, String> optional)
                    throws Exception {
        LinkedList<NameValuePair> arguments = server.getDefaultQuery("registerTemplate", optional);
        arguments.add(new NameValuePair("displaytext", displayText));
        arguments.add(new NameValuePair("format", format));
        arguments.add(new NameValuePair("hypervisor", hypervisor));
        arguments.add(new NameValuePair("name", templateName));
        arguments.add(new NameValuePair("ostypeid", osTypeId));
        arguments.add(new NameValuePair("url", url));
        arguments.add(new NameValuePair("zoneid", zoneId));
        arguments.add(new NameValuePair("response", response));
        String responseJson = server.request(arguments);
        return responseJson;
    }

    /**
     * Registers an existing ISO template into the cloud.
     *
     * @param displayText - The display text of the template. This is usually used for display purposes
     * @param templateName - The name of the template
     * @param url - The URL of where the template is hosted. Possible URL include http:// and https://
     * @param zoneId - The ID of the zone the template is to be hosted on
     * @param response - Response format as json
     * @param optional - List of optional values
     * @return - Json string response
     * @throws Exception - Raise if any error
     */
    public String registerIso(String displayText, String templateName,
            String url, String zoneId, String response, HashMap<String, String> optional)
                    throws Exception {
        LinkedList<NameValuePair> arguments = server.getDefaultQuery("registerIso", optional);
        arguments.add(new NameValuePair("displaytext", displayText));
        arguments.add(new NameValuePair("name", templateName));
        arguments.add(new NameValuePair("url", url));
        arguments.add(new NameValuePair("zoneid", zoneId));
        arguments.add(new NameValuePair("response", response));
        String responseJson = server.request(arguments);
        return responseJson;
    }

    /**
     * Updates attributes of a template.
     *
     * @param fileId - The ID of the file
     * @param response - Response format as json
     * @param optional - List of optional values
     * @return - Json string response
     * @throws Exception - Raise if any error
     */
    public String updateTemplate(String fileId, String response, HashMap<String, String> optional)
            throws Exception {
        LinkedList<NameValuePair> arguments = server.getDefaultQuery("updateTemplate", optional);
        arguments.add(new NameValuePair("id", fileId));
        arguments.add(new NameValuePair("response", response));
        String responseJson = server.request(arguments);
        return responseJson;
    }

    /**
     * Updates attributes of a ISO template.
     *
     * @param fileId - The ID of the file
     * @param response - Response format as json
     * @param optional - List of optional values
     * @return - Json string response
     * @throws Exception - Raise if any error
     */
    public String updateIso(String fileId, String response, HashMap<String, String> optional)
            throws Exception {
        LinkedList<NameValuePair> arguments = server.getDefaultQuery("updateIso", optional);
        arguments.add(new NameValuePair("id", fileId));
        arguments.add(new NameValuePair("response", response));
        String responseJson = server.request(arguments);
        return responseJson;
    }

    /**
     * Copies a template from one zone to another.
     *
     * @param templateId - Template ID.
     * @param destZoneId - ID of the zone the template is being copied to
     * @param response - Response format as json
     * @param optional - List of optional values
     * @return - Json string response
     * @throws Exception - Raise if any error
     */
    public String copyTemplate(String templateId, String destZoneId, String response, HashMap<String, String> optional)
            throws Exception {
        LinkedList<NameValuePair> arguments = server.getDefaultQuery("copyTemplate", optional);
        arguments.add(new NameValuePair("id", templateId));
        arguments.add(new NameValuePair("destzoneid", destZoneId));
        arguments.add(new NameValuePair("response", response));
        String responseJson = server.request(arguments);
        return responseJson;
    }

    /**
     * Deletes a template from the system. All virtual machines using the deleted template will not be affected.
     *
     * @param templateId - The ID of the template
     * @param response - Response format as json
     * @param optional - List of optional values
     * @return - Json string response
     * @throws Exception - Raise if any error
     */
    public String deleteTemplate(String templateId, String response, HashMap<String, String> optional)
            throws Exception {
        LinkedList<NameValuePair> arguments = server.getDefaultQuery("deleteTemplate", optional);
        arguments.add(new NameValuePair("id", templateId));
        arguments.add(new NameValuePair("response", response));
        String responseJson = server.request(arguments);
        return responseJson;
    }

    /**
     * Deletes a ISO template from the system. All virtual machines using the deleted template will not be affected.
     *
     * @param templateId - The ID of the template
     * @param response - Response format as json
     * @param optional - List of optional values
     * @return - Json string response
     * @throws Exception - Raise if any error
     */
    public String deleteIso(String templateId, String response, HashMap<String, String> optional)
            throws Exception {
        LinkedList<NameValuePair> arguments = server.getDefaultQuery("deleteIso", optional);
        arguments.add(new NameValuePair("id", templateId));
        arguments.add(new NameValuePair("response", response));
        String responseJson = server.request(arguments);
        return responseJson;
    }

    /**
     * List all public, private, and privileged templates.
     *
     * @param templateFilter - Possible values are "featured", "self", "self-executable", "executable", and community".
     *        featured-templates that are featured and are public
     *        self-templates that have been registered/created by the owner
     *        selfexecutable-templates that have been registered/created by the
     *        owner that can be used to deploy a new VM* executable-all templates that can be used to deploy a new VM
     *        community-templates that are public
     * @param response - Response format as json
     * @param optional - List of optional values
     * @return - Json string response
     * @throws Exception - Raise if any error
     */
    public String listTemplates(String templateFilter, String response, HashMap<String, String> optional)
            throws Exception {
        LinkedList<NameValuePair> arguments = server.getDefaultQuery("listTemplates", optional);
        arguments.add(new NameValuePair("templatefilter", templateFilter));
        arguments.add(new NameValuePair("response", response));
        String responseJson = server.request(arguments);
        return responseJson;
    }

    /**
     * List all public, private, and privileged ISO templates.
     *
     * @param isofilter - Possible values are "featured", "self", "self-executable", "executable", and community".
     *        featured-templates that are featured and are public
     *        self-templates that have been registered/created by the owner
     *        selfexecutable-templates that have been registered/created by the
     *        owner that can be used to deploy a new VM* executable-all templates that can be used to deploy a new VM
     *        community-templates that are public
     * @param response - Response format as json
     * @param optional - List of optional values
     * @return - Json string response
     * @throws Exception - Raise if any error
     */
    public String listIsos(String isofilter, String response, HashMap<String, String> optional)
            throws Exception {
        LinkedList<NameValuePair> arguments = server.getDefaultQuery("listIsos", optional);
        arguments.add(new NameValuePair("isofilter", isofilter));
        arguments.add(new NameValuePair("response", response));
        String responseJson = server.request(arguments);
        return responseJson;
    }

    /**
     * Updates a template visibility permissions. A public template is visible to all accounts within the same domain. A
     * private template is visible only to the owner of the template. A priviledged template is a private template with
     * account permissions added. Only accounts specified under the template permissions are visible to them.
     *
     * @param templateId - The template ID
     * @param response - Response format as json
     * @param optional - List of optional values
     * @return - Json string response
     * @throws Exception - Raise if any error
     */
    public String updateTemplatePermissions(String templateId, String response, HashMap<String, String> optional)
            throws Exception {
        LinkedList<NameValuePair> arguments = server.getDefaultQuery("updateTemplatePermissions", optional);
        arguments.add(new NameValuePair("id", templateId));
        arguments.add(new NameValuePair("response", response));
        String responseJson = server.request(arguments);
        return responseJson;
    }

    /**
     * Updates a ISO template visibility permissions. A public ISO template is visible to all accounts within the same domain. A
     * private template is visible only to the owner of the template. A priviledged template is a private template with
     * account permissions added. Only accounts specified under the template permissions are visible to them.
     *
     * @param templateId - The template ID
     * @param response - Response format as json
     * @param optional - List of optional values
     * @return - Json string response
     * @throws Exception - Raise if any error
     */
    public String updateIsoPermissions(String templateId, String response, HashMap<String, String> optional)
            throws Exception {
        LinkedList<NameValuePair> arguments = server.getDefaultQuery("updateIsoPermissions", optional);
        arguments.add(new NameValuePair("id", templateId));
        arguments.add(new NameValuePair("response", response));
        String responseJson = server.request(arguments);
        return responseJson;
    }

    /**
     * List template visibility and all accounts that have permissions to view this template.
     *
     * @param templateId - The template ID
     * @param response - Response format as json
     * @return - Json string response
     * @throws Exception - Raise if any error
     */
    public String listTemplatePermissions(String templateId, String response) throws Exception {
        LinkedList<NameValuePair> arguments = server.getDefaultQuery("listTemplatePermissions", null);
        arguments.add(new NameValuePair("id", templateId));
        arguments.add(new NameValuePair("response", response));
        String responseJson = server.request(arguments);
        return responseJson;
    }

    /**
     * Extracts a template.
     *
     * @param templateId - The ID of the template
     * @param mode - The ID of the template
     * @param response - Response format as json
     * @param optional - List of optional values
     * @return - Json string response
     * @throws Exception - Raise if any error
     */
    public String extractTemplate(String templateId, String mode, String response, HashMap<String, String> optional)
            throws Exception {
        LinkedList<NameValuePair> arguments = server.getDefaultQuery("extractTemplate", optional);
        arguments.add(new NameValuePair("id", templateId));
        arguments.add(new NameValuePair("mode", mode));
        arguments.add(new NameValuePair("response", response));
        String responseJson = server.request(arguments);
        return responseJson;
    }

    /**
     * load template into primary storage.
     *
     * @param templateId - Template ID of the template to be prepared in primary storage(s).
     * @param templateZoneId - Zone ID of the template to be prepared in primary storage(s).
     * @param response - Response format as json
     * @return - Json string response
     * @throws Exception - Raise if any error
     */
    public String prepareTemplate(String templateId, String templateZoneId, String response) throws Exception {
        LinkedList<NameValuePair> arguments = server.getDefaultQuery("prepareTemplate", null);
        arguments.add(new NameValuePair("templateid", templateId));
        arguments.add(new NameValuePair("zoneid", templateZoneId));
        arguments.add(new NameValuePair("response", response));
        String responseJson = server.request(arguments);
        return responseJson;
    }

    /**
     * Retrieves the current status of asynchronous job for template.
     *
     * @param response - Response format as json
     * @param optional - List of optional values
     * @return - Json string response
     * @throws Exception - Raise if any error
     */
    public String upgradeRouterTemplate(String response, HashMap<String, String> optional) throws Exception {
        LinkedList<NameValuePair> arguments = server.getDefaultQuery("queryAsyncJobResult", optional);
        arguments.add(new NameValuePair("response", response));
        String responseJson = server.request(arguments);
        return responseJson;
    }

    /**
     * Event status of template.
     *
     * @param asychronousJobid job id
     * @param response response type.
     * @return json response.
     * @throws Exception unhandled exceptions.
     */
    public String queryAsyncJobResult(String asychronousJobid, String response) throws Exception {
        LinkedList<NameValuePair> arguments = server.getDefaultQuery("queryAsyncJobResult", null);
        arguments.add(new NameValuePair("jobid", asychronousJobid));
        arguments.add(new NameValuePair("response", response));
        return server.request(arguments);
    }
}
