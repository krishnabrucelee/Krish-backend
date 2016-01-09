package ck.panda.util;

import java.util.HashMap;
import java.util.LinkedList;
import org.apache.commons.httpclient.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * CloudStack cluster service for connectivity with CloudStack server.
 */

@Service
public class CloudStackAccountService {

    /** Cloudstack server for connectivity. */
    @Autowired
    private CloudStackServer server;

    /**
     * Sets api key , secret key and url.
     *
     * @param server sets these values.
     */
    public void setServer(CloudStackServer server) {
        this.server = server;
    }

    /**
     * Creates an account.
     *
     * @param accountType Type of the account. Specify 0 for user, 1 for root admin, and 2 for domain admin
     * @param emailId email
     * @param firstName first name
     * @param lastName last name
     * @param userName Unique username
     * @param password Hashed password (Default is MD5). If you wish to use any other hashing algorithm, you would need
     *            to write a custom authentication adapter See Docs section.Hashed password (Default is MD5). If you
     *            wish to use any other hashing algorithm, you would need to write a custom authentication adapter See
     *            Docs section.
     * @param optional values for mclouds
     * @return
     * @throws Exception
     */
    public String createAccount(String accountType, String emailId, String firstName, String lastName, String userName,
            String password, String response, HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("createAccount", optional);
        arguments.add(new NameValuePair("accounttype", accountType));
        arguments.add(new NameValuePair("email", emailId));
        arguments.add(new NameValuePair("firstname", firstName));
        arguments.add(new NameValuePair("lastname", lastName));
        arguments.add(new NameValuePair("username", userName));
        arguments.add(new NameValuePair("password", password));
        arguments.add(new NameValuePair("response", response));

        String responseDocument = server.request(arguments);

        return responseDocument;
    }

    /**
     * Deletes a account, and all users associated with this account.
     *
     * @param accountId Account id
     * @return
     */
    public String deleteAccount(String accountId, String response) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("deleteAccount", null);
        arguments.add(new NameValuePair("id", accountId));
        arguments.add(new NameValuePair("response", response));

        String responseDocument = server.request(arguments);
        return responseDocument;

    }

    /**
     * Updates account information for the authenticated user
     *
     * @param newName new name for the account
     * @param optional
     * @return
     * @throws Exception
     */
    public String updateAccount(String newName, HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("updateAccount", optional);
        arguments.add(new NameValuePair("newname", newName));
        String responseDocument = server.request(arguments);

        return responseDocument;

    }

    /**
     * Disables an account
     *
     * @param lock If true, only lock the account; else disable the account
     * @param optional
     * @return
     * @throws Exception
     */
    public String disableAccount(String lock, String accountName, String domainId, String response,
            HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("disableAccount", optional);
        arguments.add(new NameValuePair("lock", lock));
        arguments.add(new NameValuePair("account", accountName));
        arguments.add(new NameValuePair("domainid", domainId));
        arguments.add(new NameValuePair("response", response));

        String responseDocument = server.request(arguments);

        return responseDocument;
    }

    /**
     * Enables an account
     *
     * @param optional
     * @return
     * @throws Exception
     */
    public String enableAccount(String accountName, String domainId, String response, HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("enableAccount", optional);
        arguments.add(new NameValuePair("account", accountName));
        arguments.add(new NameValuePair("domainid", domainId));
        arguments.add(new NameValuePair("response", response));

        String responseDocument = server.request(arguments);

        return responseDocument;
    }

    /**
     * Lock accounts locks the specific account
     *
     * @param optional
     * @return
     * @throws Exception
     */
    public String lockAccount(String accountName, String domainId, String response, HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("lockAccount", optional);
        arguments.add(new NameValuePair("account", accountName));
        arguments.add(new NameValuePair("domainid", domainId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);

        return responseDocument;

    }

    /**
     * Lists accounts and provides detailed account information for listed accounts
     *
     * @param optional
     * @return
     * @throws Exception
     */
    public String listAccounts(String response, HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("listAccounts", optional);
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);

        return responseDocument;
    }

    /**
     * Marks a default zone
     *
     * @param accountName Name of the account that is to be marked
     * @param domainId Marks the account that belongs to the specified domain
     * @param zoneId The Zone ID with which the account is to be marked
     * @param optional
     * @return
     * @throws Exception
     */
    public String markDefaultZoneForAccount(String accountName, String domainId, String response, String zoneId,
            HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("markDefaultZoneForAccount", optional);
        arguments.add(new NameValuePair("account", accountName));
        arguments.add(new NameValuePair("domainid", domainId));
        arguments.add(new NameValuePair("zoneid", zoneId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);

        return responseDocument;
    }

    /**
     * Adds account to a project
     *
     * @param projectId id of the project to add the account to
     * @param optional
     * @return
     * @throws Exception
     */
    public String addAccountToProject(String projectId, String account, String response,
            HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("addAccountToProject", optional);
        arguments.add(new NameValuePair("projectid", projectId));
        arguments.add(new NameValuePair("account", account));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);

        return responseDocument;

    }

    /**
     * Retrieves the current status of asynchronous job for account.
     *
     * @param asychronousJobid the ID of the asychronous job
     * @return
     * @throws Exception
     */
    public String accountJobResult(String asychronousJobid, String response) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("queryAsyncJobResult", null);
        arguments.add(new NameValuePair("jobid", asychronousJobid));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);

        return responseDocument;
    }

    /**
     * Deletes account from the project
     *
     * @param accountName name of the account to be removed from the project
     * @param projectId id of the project to remove the account from
     * @return
     * @throws Exception
     */
    public String deleteAccountFromProject(String accountName, String response, String projectId) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("deleteAccountFromProject", null);
        arguments.add(new NameValuePair("account", accountName));
        arguments.add(new NameValuePair("projectid", projectId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);

        return responseDocument;

    }

    /**
     * Lists project's accounts
     *
     * @param projectId id of the project
     * @param optional
     * @return
     * @throws Exception
     */
    public String listProjectAccounts(String projectId, String response, HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("listProjectAccounts", optional);
        arguments.add(new NameValuePair("projectid", projectId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);

        return responseDocument;

    }

    public String getSolidFireAccountId(String accountId, String storageId, String response,
            HashMap<String, String> optional) throws Exception {
        LinkedList<NameValuePair> arguments = server.getDefaultQuery("getSolidFireAccountId", optional);
        arguments.add(new NameValuePair("accountid", accountId));
        arguments.add(new NameValuePair("storageid", storageId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);

        return responseDocument;
    }
}
