package ck.panda.constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import ck.panda.domain.entity.Permission;
import ck.panda.domain.entity.Permission.Module;

/**
 *
 * Prepare permission list.
 */
public final class PermissionUtil {

    /**
     * PermissionUtil constructor.
     */
    private PermissionUtil() {

    }

    /**
     *
     * @param instance - Name of instance
     * @param storage - Name of storage
     * @param network - Name of network
     * @param sshkey - Name of ssh key
     * @param quotaLimit - Name of quotaLimit
     * @param vpc - Name of vpc
     * @param temp - Name of template
     * @param addService - Name of addService
     * @param project - Name of project
     * @param application - Name of application
     * @param dept - Name of department
     * @param roles - Name of roles
     * @param user - Name of user
     * @param report - Name of report
     * @return Permission list - Name of Permission
     */

    @SuppressWarnings("rawtypes")
    public static List<Permission> createPermissions(String instance, String storage, String network, String sshkey,
            String quotaLimit, String vpc, String temp, String addService, String project, String application,
            String dept, String roles, String user, String report) {
        List<String> stringList = new ArrayList<String>();
        Map<Module, List<String>> moduleActionMap = new HashMap<Module, List<String>>();
        List<Module> moduleList = new ArrayList<Module>();
        stringList.add(instance);
        stringList.add(storage);
        stringList.add(network);
        stringList.add(sshkey);
        stringList.add(quotaLimit);
        stringList.add(vpc);
        stringList.add(temp);
        stringList.add(addService);
        stringList.add(project);
        stringList.add(application);
        stringList.add(dept);
        stringList.add(roles);
        stringList.add(user);
        stringList.add(report);

        for (String string : stringList) {
            List<String> actionList = new ArrayList<String>();
            String[] stringArray = string.split("-");
            String[] actions = stringArray[1].split(",");
            for (String action : actions) {
                actionList.add(action);
            }
            moduleList.add(Module.valueOf(stringArray[0]));
            moduleActionMap.put(Module.valueOf(stringArray[0]), actionList);
        }

        List<Permission> permissionList = new ArrayList<Permission>();
        Map<Module, String> moduleDesc = new HashMap<Module, String>();
        Map<String, Module> moduleAction = new HashMap<String, Module>();
        moduleAction = PermissionUtil.prepareActionsModule(moduleActionMap);
        moduleDesc = PermissionUtil.prepareModuleDesc(Permission.Module.values(), moduleDesc);

        Iterator<Entry<String, Module>> it = moduleAction.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry actionsModuleEntry = (Map.Entry) it.next();
            Permission permission = new Permission();
            String[] action_keys = actionsModuleEntry.getKey().toString().split(",");
            permission.setAction(action_keys[0]);
            permission.setActionKey(action_keys[1].toUpperCase());
            permission.setModule((Module) actionsModuleEntry.getValue());
            permission.setDescription(moduleDesc.get(actionsModuleEntry.getValue()));
            permission.setIsActive(true);
            permission.setStatus(Permission.Status.ENABLED);
            permissionList.add(permission);
        }
        return permissionList;
    }

    /**
     *
     * @param instance - Name of instance
     * @param storage - Name of storage
     * @param network - Name of network
     * @param sshkey - Name of sshkey
     * @param quotaLimit - Name of quotaLimit
     * @param vpc - Name of vpc
     * @param temp - Name of template
     * @param addService - Name of addService
     * @param project - Name of project
     * @param application - Name of application
     * @param dept - Name of department
     * @param roles - Name of roles
     * @param user - Name of user
     * @param report - Name of report
     * @return Permission list - Name of Permission
     */
    public static List<Permission> updatePermissions(String instance, String storage, String network, String sshkey,
            String quotaLimit, String vpc, String temp, String addService, String project, String application,
            String dept, String roles, String user, String report) {
        List<Permission> newList = PermissionUtil.createPermissions(instance, storage, network, sshkey, quotaLimit, vpc,
                temp, addService, project, application, dept, roles, user, report);
        return newList;
    }

    /**
     *
     * @param modules - Module array
     * @param moduleDesc - Module and description map
     * @return Module map
     */
    private static Map<Module, String> prepareModuleDesc(Module[] modules, Map<Module, String> moduleDesc) {
        moduleDesc.put(Module.INSTANCE, "Instance");
        moduleDesc.put(Module.STORAGE, "Storage");
        moduleDesc.put(Module.NETWORK, "Network");
        moduleDesc.put(Module.SSH_KEYS, "SSH Keys");
        moduleDesc.put(Module.QUOTA_LIMIT, "Quota Limit");
        moduleDesc.put(Module.VPC, "VPC");
        moduleDesc.put(Module.TEMPLATES, "Templates");
        moduleDesc.put(Module.ADDITIONAL_SERVICE, "Additional Services");
        moduleDesc.put(Module.PROJECTS, "Projects");
        moduleDesc.put(Module.APPLICATION, "Application");
        moduleDesc.put(Module.DEPARTMENT, "Department");
        moduleDesc.put(Module.ROLES, "Roles");
        moduleDesc.put(Module.USER, "User");
        moduleDesc.put(Module.REPORT, "Reports");
        return moduleDesc;
    }

    /**
     *
     * @param moduleAction - moduleAction
     * @return Actions and module map
     */
    private static Map<String, Module> prepareActionsModule(Map<Module, List<String>> moduleAction) {

        Map<String, Module> actionsModule = new HashMap<String, Module>();
        List<String> tempAction = new ArrayList<String>();

        for (Map.Entry<Module, List<String>> entry : moduleAction.entrySet()) {
            tempAction = entry.getValue();
            for (String action : tempAction) {

                String[] formKeyArray = action.split(" ");
                String tempStr = "";
                for (int i = 0; i < formKeyArray.length; i++) {
                    if (i != formKeyArray.length - 1) {
                        tempStr += formKeyArray[i].concat("_");
                    } else {
                        tempStr += formKeyArray[i];
                    }
                }
                actionsModule.put(action.concat(",").concat(tempStr), entry.getKey());
            }
        }
        return actionsModule;
    }

}
