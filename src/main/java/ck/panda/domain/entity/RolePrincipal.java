package ck.panda.domain.entity;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ck.panda.domain.entity.User.UserType;

/**
 * Role permission for login user.
 */
public class RolePrincipal {

    /** User name attributes. */
    private String username;

    /** User role attributes. */
    private Role role;

    /** User type attributes. */
    private UserType type;

    /** User domain name attributes. */
    private String domainname;

    /** User domain id attributes. */
    private Long domainId;

    /** User id attributes. */
    private Long id;

    /** User department id attributes. */
    private Long departmentid;

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
     * @param username to set
     * @param role to set
     * @param type to set
     * @param domainname to set
     */
    public RolePrincipal(String username, Long id, Role role, UserType type, String domainname, Long domainId,
            Long departmentid, String buildVersion) {
        this.username = username;
        this.id = id;
        this.role = role;
        this.type = type;
        this.domainname = domainname;
        this.departmentid = departmentid;
        this.buildVersion = buildVersion;
        this.domainId = domainId;
    }

    @Override
    public String toString() {
        Authentication token = SecurityContextHolder.getContext().getAuthentication();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", token.getDetails().toString());
            jsonObject.put("id", id);
            jsonObject.put("userName", username);
            jsonObject.put("type", type);
            jsonObject.put("domainName", domainname);
            jsonObject.put("domainId", domainId);
            jsonObject.put("departmentId", departmentid);
            jsonObject.put("buildNumber", buildVersion);
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
