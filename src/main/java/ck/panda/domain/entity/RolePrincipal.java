package ck.panda.domain.entity;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ck.panda.domain.entity.User.Type;

/**
 * Role permission for login user.
 *
 */
public class RolePrincipal {

    /** User name attributes. */
    private String username;

    /** User role attributes. */
    private Role role;

    /** User type attributes. */
    private Type type;

    /** User domain name attributes. */
    private String domainname;

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
    public RolePrincipal(String username, Role role, Type type, String domainname) {
        this.username = username;
        this.role = role;
        this.type = type;
        this.domainname = domainname;
    }

    @Override
    public String toString() {
        Authentication token = SecurityContextHolder.getContext().getAuthentication();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", token.getDetails().toString());
            jsonObject.put("userName", username);
            jsonObject.put("type", type);
            jsonObject.put("domainName", domainname);
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