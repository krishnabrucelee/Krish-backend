package ck.panda.util;

import java.util.HashMap;
import java.util.LinkedList;
import org.apache.commons.httpclient.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ck.panda.constants.CloudStackConstants;

/**
 * CloudStack Region Service for cloud Stack connectivity with Region.
 */
@Service
public class CloudStackProjectService {

    /** Cloudstack server for connectivity. */
    @Autowired
    private CloudStackServer server;

    /**
     * setServer passes apikey, url, secretkey from UI and aids to establish cloudstack connectivity.
     *
     * @param server sets apikey and url.
     */
    public void setServer(CloudStackServer server) {
        this.server = server;
    }

    /**
     * create new project in cloud stack.
     *
     * @param optional from values cloud stack
     * @param response json.
     * @param name name of project.
     * @param description display text of project.
     * @return response String json
     * @throws Exception unhandled errors.
     */
    public String createProject(String response, String name, String description, HashMap<String, String> optional)
            throws Exception {
        LinkedList<NameValuePair> arguments = server.getDefaultQuery("createProject", optional);
        arguments.add(new NameValuePair("name", name));
        arguments.add(new NameValuePair("displaytext", description));
        // arguments.add(new NameValuePair("account", owner));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * status of project.
     *
     * @param asychronousJobid job id
     * @param response response type.
     * @return json response.
     * @throws Exception unhandled exceptions..
     */
    public String queryAsyncJobResult(String asychronousJobid, String response) throws Exception {
        LinkedList<NameValuePair> arguments = server.getDefaultQuery("queryAsyncJobResult", null);
        arguments.add(new NameValuePair("jobid", asychronousJobid));
        arguments.add(new NameValuePair("response", response));
        return server.request(arguments);
    }

    /**
     * Deletes a project.
     *
     * @param projectId id of the invitation
     * @return json response.
     * @throws Exception unhandled exceptions.
     */
    public String deleteProject(String projectId) throws Exception {
        LinkedList<NameValuePair> arguments = server.getDefaultQuery("deleteProject", null);
        arguments.add(new NameValuePair("id", projectId));
        arguments.add(new NameValuePair("response", "json"));
        return server.request(arguments);
    }

    /**
     * Add account to the project.
     *
     * @param projectId the id of the project.
     * @param account account name.
     * @param response response type.
     * @throws Exception unhandled exceptions.
     */
    public String addAccountToProject(String projectId, String account, String response) throws Exception {
		LinkedList<NameValuePair> arguments = server.getDefaultQuery(CloudStackConstants.CS_PROJECT_ADD_ACCOUNT, null);
		arguments.add(new NameValuePair(CloudStackConstants.CS_PROJECT_RESPONSE_ID, projectId));
		arguments.add(new NameValuePair(CloudStackConstants.CS_RESPONSE, response));
		arguments.add(new NameValuePair(CloudStackConstants.CS_ACCOUNT, account));
		return server.request(arguments);
    }

    /**
     * Delete account from project.
     *
     * @param projectId the id of the project.
     * @param account account name.
     * @param response response type.
     * @throws Exception unhandled exceptions.
     */
    public String deleteAccountFromProject(String projectId, String account, String response) throws Exception {
		LinkedList<NameValuePair> arguments = server.getDefaultQuery(CloudStackConstants.CS_PROJECT_REMOVE_ACCOUNT,
				null);
		arguments.add(new NameValuePair(CloudStackConstants.CS_PROJECT_RESPONSE_ID, projectId));
		arguments.add(new NameValuePair(CloudStackConstants.CS_RESPONSE, response));
		arguments.add(new NameValuePair(CloudStackConstants.CS_ACCOUNT, account));
		return server.request(arguments);
    }

    /**
     * Updates a project.
     *
     * @param projectId the id of the project to be modified.
     * @param optional optional parameters.
     * @param displaytext description.
     * @param response response type.
     * @return json response.
     * @throws Exception unhandled exceptions..
     */
    public String updateProject(String projectId, String displaytext, String response, HashMap<String, String> optional)
            throws Exception {
		// Add account to project.
		if (optional.containsKey(CloudStackConstants.CS_ACCOUNT)) {
			addAccountToProject(projectId, optional.get(CloudStackConstants.CS_ACCOUNT), response);
		}
		// Update project owner and description.
		LinkedList<NameValuePair> arguments = server.getDefaultQuery(CloudStackConstants.CS_PROJECT_UPDATE, optional);
		arguments.add(new NameValuePair(CloudStackConstants.CS_ID, projectId));
		arguments.add(new NameValuePair(CloudStackConstants.CS_DISPLAY_TEXT, displaytext));
		arguments.add(new NameValuePair(CloudStackConstants.CS_RESPONSE, response));
		return server.request(arguments);
    }

    /**
     * Activates a project.
     *
     * @param projectId the id of the project to be Activated.
     * @param response json or xml.
     * @return json response.
     * @throws Exception unhandled exceptions..
     */
    public String activateProject(String projectId, String response) throws Exception {
        LinkedList<NameValuePair> arguments = server.getDefaultQuery("activateProject", null);
        arguments.add(new NameValuePair("id", projectId));
        arguments.add(new NameValuePair("response", response));
        return server.request(arguments);
    }

    /**
     * Suspends a project The id of the project to be suspended.
     *
     * @param projectId project id.
     * @param response json or xml.
     * @return json response.
     * @throws Exception unhandled exceptions..
     */
    public String suspendProject(String projectId, String response) throws Exception {
        LinkedList<NameValuePair> arguments = server.getDefaultQuery("suspendProject", null);
        arguments.add(new NameValuePair("id", projectId));
        arguments.add(new NameValuePair("response", response));
        return server.request(arguments);
    }

    /**
     * Lists projects and provides detailed information for listed projects.
     *
     * @param optional arguments.
     * @param response json or xml.
     * @return json response.
     * @throws Exception unhandled exceptions..
     */
    public String listProjects(String response, HashMap<String, String> optional) throws Exception {
        LinkedList<NameValuePair> arguments = server.getDefaultQuery("listProjects", optional);
        arguments.add(new NameValuePair("response", response));
        String listResponse = server.request(arguments);
        return listResponse;
    }

    /**
     * Lists the project accounts.
     *
     * @param roleType role type.
     * @param projectId project id.
     * @param response json.
     * @return json response.
     * @throws Exception unhandled exceptions..
     */
    public String listProjectAccounts(String response, String roleType, String projectId) throws Exception {
        LinkedList<NameValuePair> arguments = server.getDefaultQuery(CloudStackConstants.CS_PROJECT_ACCOUNT_LIST, null);
        arguments.add(new NameValuePair(CloudStackConstants.CS_RESPONSE, response));
        arguments.add(new NameValuePair(CloudStackConstants.CS_PROJECT_RESPONSE_ID, projectId));
        arguments.add(new NameValuePair(CloudStackConstants.CS_PROJECT_ROLE, roleType));
        String listResponse = server.request(arguments);
        return listResponse;
    }

    /**
     * Lists projects and provides detailed information for listed projects.
     *
     * @param optional arguments.
     * @param response json or xml.
     * @return json response.
     * @throws Exception unhandled exceptions..
     */
    public String listProjectInvitations(String response, HashMap<String, String> optional) throws Exception {
        LinkedList<NameValuePair> arguments = server.getDefaultQuery("listProjectInvitations", optional);
        arguments.add(new NameValuePair("response", response));
        return server.request(arguments);
    }
}
