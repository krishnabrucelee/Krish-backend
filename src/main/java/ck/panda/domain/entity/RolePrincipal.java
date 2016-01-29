package ck.panda.domain.entity;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ck.panda.constants.CloudStackConstants;
import ck.panda.util.DateConvertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Role permission for login user.
 */
public class RolePrincipal {

    /** Logger constant. */
    private static final Logger LOGGER = LoggerFactory.getLogger(RolePrincipal.class);

    /** Login user token. */
    public static final String LOGIN_USER_TOKEN = "token";

    /** Login user name. */
    public static final String LOGIN_USER_NAME = "userName";

    /** Login user type. */
    public static final String LOGIN_USER_TYPE = "type";

    /** Login user domain name. */
    public static final String LOGIN_USER_DOMAIN_NAME = "domainName";

    /** Login user domain id. */
    public static final String LOGIN_USER_DOMAIN_ID = "domainId";

    /** Login user department id. */
    public static final String LOGIN_USER_DEPARTMENT_ID = "departmentId";

    /** Build number. */
    public static final String BUILD_NUMBER = "buildNumber";

    /** Login time . */
    public static final String LOGIN_TIME = "loginTime";

    /** Login time zone . */
    public static final String TIME_ZONE = "timeZone";

    /** Login user role permission action. */
    public static final String ROLE_ACTION = "action";

    /** Login user role permission action key. */
    public static final String ROLE_ACTION_KEY = "action_key";

    /** Login user role permission description. */
    public static final String ROLE_DESCRIPTION = "description";

    /** Login user role permission status. */
    public static final String ROLE_STATUS = "isActive";

    /** Login user role permission list. */
    public static final String ROLE_PERMISSION_LIST = "permissionList";

    /** User name attributes. */
    private String userName;

    /** User role attributes. */
    private Role role;

    /** User attributes. */
    private User user;

    /** Build Version number. */
    private String buildVersion;

    /**
     * Default constructor.
     */
    public RolePrincipal() {
    }

    /**
     * Parameterized constructor.
     *
     * @param user to set
     * @param userName to set
     * @param role to set
     * @param buildVersion to set
     */
    public RolePrincipal(User user, String userName, Role role, String buildVersion) {
        this.user = user;
        this.userName = userName;
        this.role = role;
        this.buildVersion = buildVersion;
    }

    @Override
    public String toString() {
        Authentication token = SecurityContextHolder.getContext().getAuthentication();
        JSONObject jsonObject = new JSONObject();
        try {
            TimeZone timeZone = Calendar.getInstance().getTimeZone();
            jsonObject.put(LOGIN_USER_TOKEN, token.getDetails().toString());
            jsonObject.put(CloudStackConstants.CS_ID, user.getId());
            jsonObject.put(LOGIN_USER_NAME, userName);
            jsonObject.put(LOGIN_USER_TYPE, user.getType());
            jsonObject.put(LOGIN_USER_DOMAIN_NAME, user.getDomain().getName());
            jsonObject.put(LOGIN_USER_DOMAIN_ID, user.getDomain().getId());
            jsonObject.put(LOGIN_USER_DEPARTMENT_ID, user.getDepartment().getId());
            jsonObject.put(BUILD_NUMBER, buildVersion);
            jsonObject.put(LOGIN_TIME, DateConvertUtil.getTimestamp());
            jsonObject.put(TIME_ZONE, timeZone.getID());
            JSONArray jsonArray = new JSONArray();
            Map<String, Object> hashList = new HashMap<String, Object>();
            for (int i = 0; i < role.getPermissionList().size(); i++) {
                Permission permission = role.getPermissionList().get(i);
                hashList.put(CloudStackConstants.CS_ID, permission.getId());
                hashList.put(ROLE_ACTION, permission.getAction());
                hashList.put(ROLE_ACTION_KEY, permission.getActionKey());
                hashList.put(ROLE_DESCRIPTION, permission.getDescription());
                hashList.put(ROLE_STATUS, permission.getIsActive());
                jsonArray.put(hashList);
                hashList = new HashMap<String, Object>();
            }
            jsonObject.put(ROLE_PERMISSION_LIST, jsonArray);
        } catch (Exception e) {
            LOGGER.error("ERROR AT GETTING LOGIN USER DETAILS");
        }
        return jsonObject.toString();
    }
}
