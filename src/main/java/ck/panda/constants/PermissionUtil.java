package ck.panda.constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import ck.panda.domain.entity.Permission;
import ck.panda.domain.entity.Permission.Module;

public class PermissionUtil {

	@SuppressWarnings("rawtypes")
	public static List<Permission> createPermissions(String instance, String storage, String network, String sshkey, String quotaLimit,
			String vpc, String temp, String addService, String project, String application, String dept, String roles, String user, String report) {
		List<String> stringList = new ArrayList<String>();
		Map<Module,List<String>> moduleActionMap = new HashMap<Module,List<String>>();
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
			String[] stringArray =  string.split("-");
			String[] actions = stringArray[1].split(",");
			for (String action : actions) {
				actionList.add(action);
			}
			moduleList.add(Module.valueOf(stringArray[0]));
			moduleActionMap.put(Module.valueOf(stringArray[0]),actionList);
		}

		List<Permission> permissionList = new ArrayList<Permission>();
		Map<Module,String> moduleDesc = new HashMap<Module,String>();
		Map<String,Module> moduleAction = new HashMap<String,Module>();
		moduleAction = PermissionUtil.prepareActionsModule(moduleActionMap);
		moduleDesc = PermissionUtil.prepareModuleDesc(Permission.Module.values(), moduleDesc);

		Iterator<Entry<String, Module>> it = moduleAction.entrySet().iterator();
        while (it.hasNext()) {
        	Map.Entry actionsModuleEntry = (Map.Entry)it.next();
        	Permission permission = new Permission();
        	permission.setAction(actionsModuleEntry.getKey().toString());
        	permission.setModule((Module) actionsModuleEntry.getValue());
        	permission.setDescription(moduleDesc.get(actionsModuleEntry.getValue()));
        	permission.setIsActive(true);
        	permission.setStatus(Permission.Status.ENABLED);
        	permissionList.add(permission);
   	    }
        return permissionList;
	}

	public static List<Permission> updatePermissions(String instance, String storage, String network, String sshkey, String quotaLimit,
			String vpc, String temp, String addService, String project, String application, String dept, String roles, String user, String report) {
		List<Permission> newList = PermissionUtil.createPermissions(instance, storage, network, sshkey, quotaLimit, vpc, temp,
				addService, project, application, dept, roles, user, report);
		return newList;
	}

	private static Map<Module,String> prepareModuleDesc(Module[] modules, Map<Module,String> moduleDesc) {
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

	private static Map<String, Module> prepareActionsModule(Map<Module, List<String>> moduleAction) {

		Map<String, Module> actionsModule = new HashMap<String, Module>();
		List<String> tempAction = new ArrayList<String>();

		for (Map.Entry<Module, List<String>> entry : moduleAction.entrySet()){
			tempAction = entry.getValue();
			for (String action : tempAction) {
				actionsModule.put(action, entry.getKey());
			}
		}
		return actionsModule;
	}

	}
