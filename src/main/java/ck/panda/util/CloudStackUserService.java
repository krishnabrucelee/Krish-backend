package ck.panda.util;

import java.util.HashMap;
import java.util.LinkedList;

import org.apache.commons.httpclient.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * CloudStack User service for connectivity with CloudStack server.
 *
 */
@Service
public class CloudStackUserService {

    /**
     * Cloudstack server for connectivity.
     */
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
     * Creates a user for an account that already exists.
     *
     * @param accountName Creates the user under the specified account. If no account is specified, the username will be
     *            used as the account name.
     * @param emailId email ID
     * @param firstName first name
     * @param lastName last name
     * @param userName Unique user name.
     * @param password Hashed password (Default is MD5). If you wish to use any other hashing algorithm, you would need
     *            to write a custom authentication adapter See Docs section.
     * @param optional
     * @return user
     * @throws Exception
     */
    public String createUser(String accountName, String emailId, String firstName, String lastName, String userName,
            String password, String response, HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("createUser", optional);
        arguments.add(new NameValuePair("account", accountName));
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
     * Delete user from the account.
     *
     * @param userId user id
     * @param response response
     * @return deleted user
     * @throws Exception exception
     */
    public String deleteUser(String userId, String response) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("deleteUser", null);
        arguments.add(new NameValuePair("id", userId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);

        return responseDocument;

    }

    /**
     * Updates a user account.
     *
     * @param userId the User id
     * @param optional values
     * @param response response
     * @return response response
     * @throws Exception exception
     */
    public String updateUser(String userId, HashMap<String, String> optional, String response) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("updateUser", optional);
        arguments.add(new NameValuePair("id", userId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);

        return responseDocument;
    }

    /**
     * Lists user accounts.
     *
     * @param optional values
     * @param response response
     * @return user
     * @throws Exception exception
     */

    public String listUsers(HashMap<String, String> optional, String response) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("listUsers", optional);
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);

        return responseDocument;
    }

    /**
     * Locks user.
     *
     * @param userId the user id
     * @param response responser
     * @return user
     * @throws Exception exception
     */

    public String lockUser(String userId, String response) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("lockUser", null);
        arguments.add(new NameValuePair("id", userId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);

        return responseDocument;

    }

    /**
     * Disables user.
     *
     * @param userId the user id
     * @param response response
     * @return user
     * @throws Exception exception
     */

    public String disableUser(String userId, String response) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("disableUser", null);
        arguments.add(new NameValuePair("id", userId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);

        return responseDocument;

    }

    /**
     * Enables User.
     *
     * @param userId the user id
     * @return enabled user
     * @param response response
     * @throws Exception exception
     */
     public String enableUser(String userId, String response) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("enableUser", null);
        arguments.add(new NameValuePair("id", userId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);

        return responseDocument;

    }

    /**
     * Finds user account by API key.
     *
     * @param userApiKey the user api key
     * @return user
     * @param response response
     * @throws Exception exception
     */
    public String GetUser(String userApiKey, String response) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("getUser", null);
        arguments.add(new NameValuePair("userapikey", userApiKey));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);

        return responseDocument;

    }

    /**
     * This command allows a user to register for the developer API, returning a secret key and an API key. This request
     * is made through the integration API port, so it is a privileged command and must be made on behalf of a user. It
     * is up to the implementer just how the username and password are entered, and then how that translates to an
     * integration API request. Both secret key and API key should be returned to the user
     *
     * @param userId The user id
     * @return user
     * @param response response
     * @throws Exception exception
     */
    public String registerUserKeys(String userId, String response) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("registerUserKeys", null);
        arguments.add(new NameValuePair("id", userId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);

        return responseDocument;

    }

  /** @param asychronousJobid the ID of the asychronous job
    * @param response json or xml.
    * @return response.
    * @throws Exception if error occurs.
    */
   public String associatedJobResult(String asychronousJobid, String response)
           throws Exception {

       LinkedList<NameValuePair> arguments
               = server.getDefaultQuery("queryAsyncJobResult", null);
       arguments.add(new NameValuePair("jobid", asychronousJobid));
        arguments.add(new NameValuePair("response",response));
       String responseDocument = server.request(arguments);
       return responseDocument;
   }
}
