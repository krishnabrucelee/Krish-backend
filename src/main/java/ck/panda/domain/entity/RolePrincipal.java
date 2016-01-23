package ck.panda.domain.entity;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ck.panda.util.DateConvertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Role permission for login user.
 */
public class RolePrincipal {

	/** Logger constant. */
	private static final Logger LOGGER = LoggerFactory.getLogger(RolePrincipal.class);

    /** User name attributes. */
    private String username;

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
     * @param username to set
     * @param role to set
     * @param buildVersion to set
     */
    public RolePrincipal(User user, String username, Role role, String buildVersion) {
        this.user = user;
        this.username = username;
        this.role = role;
        this.buildVersion = buildVersion;
    }

    @Override
    public String toString() {
        Authentication token = SecurityContextHolder.getContext().getAuthentication();
        JSONObject jsonObject = new JSONObject();
        try {
            TimeZone tz = Calendar.getInstance().getTimeZone();
            jsonObject.put("token", token.getDetails().toString());
            jsonObject.put("id", user.getId());
            jsonObject.put("userName", username);
            jsonObject.put("type", user.getType());
            jsonObject.put("domainName", user.getDomain().getName());
            jsonObject.put("domainId", user.getDomain().getId());
            jsonObject.put("departmentId", user.getDepartment().getId());
            jsonObject.put("buildNumber", buildVersion);
            jsonObject.put("loginTime", DateConvertUtil.getTimestamp());
            jsonObject.put("timeZone", tz.getID());
            JSONArray jsonArray = new JSONArray();
            Map<String, Object> hashList = new HashMap<String, Object>();
            for (int i = 0; i < role.getPermissionList().size(); i++) {
                Permission permission = role.getPermissionList().get(i);
                hashList.put("id", permission.getId());
                hashList.put("action", permission.getAction());
                hashList.put("action_key", permission.getActionKey());
                hashList.put("description", permission.getDescription());
                hashList.put("isActive", permission.getIsActive());
                jsonArray.put(hashList);
                hashList = new HashMap<String, Object>();
            }
            jsonObject.put("permissionList", jsonArray);
        } catch (Exception e) {
            LOGGER.error("ERROR AT GETTING LOGIN USER DETAILS");
        }
        return jsonObject.toString();
    }
}