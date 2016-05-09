package ck.panda.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ck.panda.domain.entity.IpAddress;
import ck.panda.domain.entity.ResourceLimitDepartment;
import ck.panda.rabbitmq.util.EmailEvent;
import ck.panda.domain.entity.ResourceLimitDomain;
import ck.panda.domain.entity.ResourceLimitProject;
import ck.panda.domain.entity.VmInstance;
import ck.panda.domain.entity.Volume;
import ck.panda.email.util.Resource;
import ck.panda.constants.CloudStackConstants;
import ck.panda.constants.EmailConstants;
import ck.panda.constants.GenericConstants;
import ck.panda.util.CloudStackResourceCapacity;
import ck.panda.util.ConfigUtil;
import ck.panda.util.error.exception.CustomGenericException;

@Service
public class QuotaValidationServiceImpl implements QuotaValidationService{

    /** Reference of the convert entity service. */
    @Autowired
    private ConvertEntityService convertEntityService;

    /** CloudStack Resource Capacity Service. */
    @Autowired
    private CloudStackResourceCapacity cloudStackResourceCapacity;

    @Autowired
    private IpaddressService ipaddressService;

    /** Resource Limit Department service reference. */
    @Autowired
    private ResourceLimitDepartmentService resourceLimitDepartmentService;

    /** Resource Limit Project service reference. */
    @Autowired
    private ResourceLimitProjectService resourceLimitProjectService;

    /** Resource Limit Department service reference. */
    @Autowired
    private ResourceLimitDomainService resourceLimitDomainService;

    /** Email Job service. */
    @Autowired
    private EmailJobService emailJobService;

    /** Cloud stack configuration utility class. */
    @Autowired
    private ConfigUtil config;

    @Override
    public String QuotaLimitCheckByResourceObject(Object resourceObject, String resourceType,
            Long accountTypeId, String accountType) throws Exception {
        /** Used for setting optional values for resource usage. */
        HashMap<String, Long> resourceUsageMap = new HashMap<String, Long>();
        List<String> resourceList = new ArrayList<String>();
        // Api call from Root admin.
        config.setServer(1L);
        this.validateListResourceCapacity();
        switch(resourceType) {
        case "Instance":
            VmInstance vmInstance = (VmInstance)resourceObject;
            resourceList.add(ConvertEntityService.CS_INSTANCE);
            resourceList.add(ConvertEntityService.CS_CPU);
            if(convertEntityService.getComputeOfferById(vmInstance.getComputeOfferingId()).getCustomized()) {
                resourceUsageMap.put(ConvertEntityService.CS_INSTANCE, 1L);
                resourceUsageMap.put(ConvertEntityService.CS_MEMORY, Long.valueOf(vmInstance.getMemory()));
                resourceUsageMap.put(ConvertEntityService.CS_CPU, Long.valueOf(vmInstance.getCpuCore()));
            } else {
                resourceUsageMap.put(ConvertEntityService.CS_INSTANCE, 1L);
                resourceUsageMap.put(ConvertEntityService.CS_CPU, Long.valueOf(convertEntityService.getComputeOfferById(vmInstance.getComputeOfferingId()).getNumberOfCores()));
                resourceUsageMap.put(ConvertEntityService.CS_MEMORY, Long.valueOf(convertEntityService.getComputeOfferById(vmInstance.getComputeOfferingId()).getMemory()));
            }
            if(vmInstance.getStorageOfferingId() != null) {
                if(convertEntityService.getStorageOfferById(vmInstance.getStorageOfferingId()).getIsCustomDisk()) {
                    resourceUsageMap.put(ConvertEntityService.CS_PRIMARY_STORAGE, vmInstance.getDiskSize() + isTemplateZero(convertEntityService.getTemplateById(vmInstance.getTemplateId()).getSize()));
                } else {
                    resourceUsageMap.put(ConvertEntityService.CS_PRIMARY_STORAGE, convertEntityService.getStorageOfferById(vmInstance.getStorageOfferingId()).getDiskSize() + isTemplateZero(convertEntityService.getTemplateById(vmInstance.getTemplateId()).getSize()));
                }
                resourceUsageMap.put(ConvertEntityService.CS_VOLUME, 2L);
            } else {
                resourceUsageMap.put(ConvertEntityService.CS_PRIMARY_STORAGE, isTemplateZero(convertEntityService.getTemplateById(vmInstance.getTemplateId()).getSize()));
                resourceUsageMap.put(ConvertEntityService.CS_VOLUME, 1L);
            }
            resourceList.add(ConvertEntityService.CS_VOLUME);
            resourceList.add(ConvertEntityService.CS_MEMORY);
            resourceList.add(ConvertEntityService.CS_PRIMARY_STORAGE);
            resourceList.add(ConvertEntityService.CS_IP);
        	List<IpAddress> ipaddresses = ipaddressService.findByNetwork(vmInstance.getNetworkId());
			Boolean isCheck =false;
			for (IpAddress ipaddress : ipaddresses) {
				if (ipaddress.getIsSourcenat()) {
					isCheck = true;
				}
			}
			if(!isCheck){
				resourceUsageMap.put(ConvertEntityService.CS_IP, 1L);
			}
            if (accountType.equals("Project")) {
                String validateMessage = checkResourceAvailablity(accountTypeId, accountType, resourceList, resourceUsageMap);
                if (validateMessage != null) {
                    throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED, validateMessage);
                }
            } else if (accountType.equals("Department")) {
                String validateMessage = checkResourceAvailablity(accountTypeId, accountType, resourceList, resourceUsageMap);
                if (validateMessage != null) {
                    throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED, validateMessage);
                }
            } else {
                String validateMessage = checkResourceAvailablity(accountTypeId, accountType, resourceList, resourceUsageMap);
                if (validateMessage != null) {
                    throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED, validateMessage);
                }
            }
            break;
        case "Volume":
            resourceList.clear();
            Volume volume = (Volume)resourceObject;
            resourceList.add(ConvertEntityService.CS_VOLUME);
            resourceList.add(ConvertEntityService.CS_PRIMARY_STORAGE);
            resourceList.add(ConvertEntityService.CS_SECONDARY_STORAGE);
            if(volume.getDiskSize() != null) {
                    resourceUsageMap.put(ConvertEntityService.CS_PRIMARY_STORAGE, volume.getDiskSize());
                } else {
                    resourceUsageMap.put(ConvertEntityService.CS_PRIMARY_STORAGE, convertEntityService.getStorageOfferById(volume.getStorageOfferingId()).getDiskSize());
                }
            resourceUsageMap.put(ConvertEntityService.CS_VOLUME, 1L);
            if (accountType.equals("Project")) {
                String validateMessage = checkResourceAvailablity(accountTypeId, accountType, resourceList, resourceUsageMap);
                if (validateMessage != null) {
                    throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED, validateMessage);
                }
            } else if (accountType.equals("Department")) {
                String validateMessage = checkResourceAvailablity(accountTypeId, accountType, resourceList, resourceUsageMap);
                if (validateMessage != null) {
                    throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED, validateMessage);
                }
            } else {
                String validateMessage = checkResourceAvailablity(accountTypeId, accountType, resourceList, resourceUsageMap);
                if (validateMessage != null) {
                    throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED, validateMessage);
                }
            }
            break;
        case "UploadVolume":
            resourceList.clear();
            Volume uploadVolume = (Volume)resourceObject;
            resourceList.add(ConvertEntityService.CS_VOLUME);
            resourceUsageMap.put(ConvertEntityService.CS_VOLUME, 1L);
            if (accountType.equals("Project")) {
                String validateMessage = checkResourceAvailablity(accountTypeId, accountType, resourceList, resourceUsageMap);
                if (validateMessage != null) {
                    throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED, validateMessage);
                }
            } else if (accountType.equals("Department")) {
                String validateMessage = checkResourceAvailablity(accountTypeId, accountType, resourceList, resourceUsageMap);
                if (validateMessage != null) {
                    throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED, validateMessage);
                }
            } else {
                String validateMessage = checkResourceAvailablity(accountTypeId, accountType, resourceList, resourceUsageMap);
                if (validateMessage != null) {
                    throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED, validateMessage);
                }
            }
            break;
        case "Network":
            resourceList.clear();
            resourceList.add(ConvertEntityService.CS_NETWORK);
            resourceUsageMap.put(ConvertEntityService.CS_NETWORK, 1L);
            if (accountType.equals("Project")) {
                String validateMessage = checkResourceAvailablity(accountTypeId, accountType, resourceList, resourceUsageMap);
                if (validateMessage != null) {
                    throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED, validateMessage);
                }
            } else if (accountType.equals("Department")) {
                String validateMessage = checkResourceAvailablity(accountTypeId, accountType, resourceList, resourceUsageMap);
                if (validateMessage != null) {
                    throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED, validateMessage);
                }
            } else {
                String validateMessage = checkResourceAvailablity(accountTypeId, accountType, resourceList, resourceUsageMap);
                if (validateMessage != null) {
                    throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED, validateMessage);
                }
            }
            break;
        case "VPC":
            resourceList.clear();
            resourceList.add(ConvertEntityService.CS_VPC);
            resourceUsageMap.put(ConvertEntityService.CS_VPC, 1L);
            if (accountType.equals("Project")) {
                String validateMessage = checkResourceAvailablity(accountTypeId, accountType, resourceList, resourceUsageMap);
                if (validateMessage != null) {
                    throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED, validateMessage);
                }
            } else if (accountType.equals("Department")) {
                String validateMessage = checkResourceAvailablity(accountTypeId, accountType, resourceList, resourceUsageMap);
                if (validateMessage != null) {
                    throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED, validateMessage);
                }
            } else {
                String validateMessage = checkResourceAvailablity(accountTypeId, accountType, resourceList, resourceUsageMap);
                if (validateMessage != null) {
                    throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED, validateMessage);
                }
            }
            break;
        case "IP":
            resourceList.clear();
            resourceList.add(ConvertEntityService.CS_IP);
            resourceUsageMap.put(ConvertEntityService.CS_IP, 1L);
            if (accountType.equals("Project")) {
                String validateMessage = checkResourceAvailablity(accountTypeId, accountType, resourceList, resourceUsageMap);
                if (validateMessage != null) {
                    throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED, validateMessage);
                }
            } else if (accountType.equals("Department")) {
                String validateMessage = checkResourceAvailablity(accountTypeId, accountType, resourceList, resourceUsageMap);
                if (validateMessage != null) {
                    throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED, validateMessage);
                }
            } else {
                String validateMessage = checkResourceAvailablity(accountTypeId, accountType, resourceList, resourceUsageMap);
                if (validateMessage != null) {
                    throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED, validateMessage);
                }
            }
            break;
        case "RestoreInstance" :
            VmInstance restoreInstance = (VmInstance)resourceObject;
            resourceList.add(ConvertEntityService.CS_INSTANCE);
            resourceList.add(ConvertEntityService.CS_CPU);
            if(convertEntityService.getComputeOfferById(restoreInstance.getComputeOfferingId()).getCustomized()) {
                resourceUsageMap.put(ConvertEntityService.CS_INSTANCE, 1L);
                resourceUsageMap.put(ConvertEntityService.CS_CPU, Long.valueOf(restoreInstance.getCpuCore()));
                resourceUsageMap.put(ConvertEntityService.CS_MEMORY, Long.valueOf(restoreInstance.getMemory()));
            } else {
            	resourceUsageMap.put(ConvertEntityService.CS_INSTANCE, 1L);
                resourceUsageMap.put(ConvertEntityService.CS_CPU, Long.valueOf(convertEntityService.getComputeOfferById(restoreInstance.getComputeOfferingId()).getNumberOfCores()));
                resourceUsageMap.put(ConvertEntityService.CS_MEMORY, Long.valueOf(convertEntityService.getComputeOfferById(restoreInstance.getComputeOfferingId()).getMemory()));
            }
            resourceList.add(ConvertEntityService.CS_MEMORY);
            if (accountType.equals("Project")) {
                String validateMessage = checkResourceAvailablity(accountTypeId, accountType, resourceList, resourceUsageMap);
                if (validateMessage != null) {
                    throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED, validateMessage);
                }
            } else if (accountType.equals("Department")) {
                String validateMessage = checkResourceAvailablity(accountTypeId, accountType, resourceList, resourceUsageMap);
                if (validateMessage != null) {
                    throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED, validateMessage);
                }
            } else {
                String validateMessage = checkResourceAvailablity(accountTypeId, accountType, resourceList, resourceUsageMap);
                if (validateMessage != null) {
                    throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED, validateMessage);
                }
            }
            break;
        }
        return null;
    }

//    public void updateResourceCountByDomain(String domainUuid, HashMap<String, String> domainCountMap)
//            throws Exception {
//        // Resource count for domain
//
//        String csResponse = cloudStackResourceCapacity.updateResourceCount(domainUuid, domainCountMap, "json");
//        convertEntityService.resourceCount(csResponse);
//    }

    /**
     * Validate list resource capacity for domain.
     *
     * @throws Exception unhandled exception
     */
    public void validateListResourceCapacity() throws Exception {
        HashMap<String, String> optionalMap = new HashMap<String, String>();
        Resource resourceEmail = new Resource();
        EmailEvent emailEvent = new EmailEvent();
        HashMap<String, String> resourceMap = new HashMap<String, String>();
        // 2. List capacity CS API call.
        String csListResponse = cloudStackResourceCapacity.listCapacity(optionalMap, CloudStackConstants.JSON);
        JSONObject csCapacity = new JSONObject(csListResponse).getJSONObject(CloudStackConstants.CS_CAPACITY_LIST_RESPONSE);
        if (csCapacity.has(CloudStackConstants.CS_CAPACITY)) {
            JSONArray capacityArrayJSON = csCapacity.getJSONArray(CloudStackConstants.CS_CAPACITY);
            for (int i = 0, size = capacityArrayJSON.length(); i < size; i++) {
                String resourceType = capacityArrayJSON.getJSONObject(i).getString(CloudStackConstants.CAPACITY_TYPE);
                Double tempTotalCapacity = Double
                        .valueOf(capacityArrayJSON.getJSONObject(i).getString(CloudStackConstants.CS_CAPACITY_PERCENT));
                if (resourceType.equals(GenericConstants.RESOURCE_MEMORY)) {
                    resourceEmail.setMemory(tempTotalCapacity.toString());
                    resourceMap.put(EmailConstants.EMAIL_Memory, resourceEmail.getMemory());
                }
                if (resourceType.equals(GenericConstants.RESOURCE_CPU)) {
                    resourceEmail.setCpu(tempTotalCapacity.toString());
                    resourceMap.put(EmailConstants.EMAIL_Cpu, resourceEmail.getCpu());
                }
                if (resourceType.equals(GenericConstants.RESOURCE_PRIMARY_STORAGE)) {
                    resourceEmail.setPrimaryStorage(tempTotalCapacity.toString());
                    resourceMap.put(EmailConstants.EMAIL_Primary_storage, resourceEmail.getPrimaryStorage());
                }
                if (resourceType.equals(GenericConstants.RESOURCE_IP_ADDRESS)) {
                    resourceEmail.setIp(tempTotalCapacity.toString());
                    resourceMap.put(EmailConstants.EMAIL_Ip, resourceEmail.getIp());
                }
                if (resourceType.equals(GenericConstants.RESOURCE_SECONDARY_STORAGE)) {
                    resourceEmail.setSecondaryStorage(tempTotalCapacity.toString());
                    resourceMap.put(EmailConstants.EMAIL_Secondary_storage, resourceEmail.getSecondaryStorage());
                }
            }
            for (int i = 0, size = capacityArrayJSON.length(); i < size; i++) {
                String resourceType = capacityArrayJSON.getJSONObject(i).getString(CloudStackConstants.CAPACITY_TYPE);
                String zonename = capacityArrayJSON.getJSONObject(i).getString(EmailConstants.EMAIL_zonename);
                // 2.1 Total capacity in puplic pool for each resource type.
                Double tempTotalCapacity = Double.valueOf(capacityArrayJSON.getJSONObject(i).getString(CloudStackConstants.CS_CAPACITY_PERCENT));
                if (GenericConstants.RESOURCE_CAPACITY.containsKey(resourceType)) {
                    switch (resourceType) {
                    case GenericConstants.RESOURCE_MEMORY:
                        if (tempTotalCapacity > CloudStackConstants.CS_CAPACITY_MAX) {
                            emailEvent.setMessageBody(tempTotalCapacity.toString());
                            emailEvent.setEventType(EmailConstants.EMAIL_CAPACITY);
                            emailEvent.setEvent(EmailConstants.EMAIL_Memory);
                            emailEvent.setResources(resourceMap);
                            emailJobService.sendMessageToQueue(emailEvent);
                        }
                        break;
                    case GenericConstants.RESOURCE_CPU:
                        if (tempTotalCapacity > CloudStackConstants.CS_CAPACITY_MAX) {
                            emailEvent.setMessageBody(tempTotalCapacity.toString());
                            emailEvent.setEventType(EmailConstants.EMAIL_CAPACITY);
                            emailEvent.setEvent(EmailConstants.EMAIL_Cpu);
                            emailEvent.setResources(resourceMap);
                            emailJobService.sendMessageToQueue(emailEvent);
                        }
                        break;
                    case GenericConstants.RESOURCE_PRIMARY_STORAGE:
                        if (tempTotalCapacity > CloudStackConstants.CS_CAPACITY_MAX) {
                            emailEvent.setMessageBody(tempTotalCapacity.toString());
                            emailEvent.setEventType(EmailConstants.EMAIL_CAPACITY);
                            emailEvent.setEvent(EmailConstants.EMAIL_Primary_storage);
                            emailEvent.setResources(resourceMap);
                            emailJobService.sendMessageToQueue(emailEvent);
                        }
                        break;
                    case GenericConstants.RESOURCE_IP_ADDRESS:
                        if (tempTotalCapacity > CloudStackConstants.CS_CAPACITY_MAX) {
                            emailEvent.setMessageBody(tempTotalCapacity.toString());
                            emailEvent.setEventType(EmailConstants.EMAIL_CAPACITY);
                            emailEvent.setResourceUuid(zonename);
                            emailEvent.setEvent(EmailConstants.EMAIL_Ip);
                            emailEvent.setResources(resourceMap);
                            emailJobService.sendMessageToQueue(emailEvent);
                        }
                        break;
                    case GenericConstants.RESOURCE_SECONDARY_STORAGE:
                        if (tempTotalCapacity > CloudStackConstants.CS_CAPACITY_MAX) {
                            emailEvent.setMessageBody(tempTotalCapacity.toString());
                            emailEvent.setEventType(EmailConstants.EMAIL_CAPACITY);
                            emailEvent.setResourceUuid(zonename);
                            emailEvent.setEvent(EmailConstants.EMAIL_Secondary_storage);
                            emailEvent.setResources(resourceMap);
                            emailJobService.sendMessageToQueue(emailEvent);
                        }
                        break;
                    }
                }

            }
        }
    }

    public ResourceLimitDepartment getMaxByDepartmentAndResourceType(Long departmentId, String resourceType) throws Exception {
        ResourceLimitDepartment departmentLimit = resourceLimitDepartmentService
                .findByDepartmentAndResourceType(departmentId, ResourceLimitDepartment.ResourceType.valueOf(resourceType), true);
        return departmentLimit;
    }

    public ResourceLimitProject getMaxByProjectAndResourceType(Long projectId, String resourceType)  throws Exception {
        ResourceLimitProject projectLimit = resourceLimitProjectService.findByProjectAndResourceType(projectId,
                ResourceLimitProject.ResourceType.valueOf(resourceType), true);
        return projectLimit;
    }

    public ResourceLimitDomain getMaxByDomainAndResourceType(Long domainId, String resourceType)  throws Exception {
        ResourceLimitDomain domainLimit = resourceLimitDomainService.findByDomainAndResourceType(domainId,
                ResourceLimitDomain.ResourceType.valueOf(resourceType), true);
        return domainLimit;
    }

    public String validateResourceLimit(Long accountTypeId, String accountType, String resources, HashMap<String, Long> resourceUsageMap, String validateResponse) throws Exception {
        String resource = convertEntityService.getResourceTypeValue().get(resources);
        if(accountType.equals("Project")) {
            ResourceLimitProject projectLimit = getMaxByProjectAndResourceType(accountTypeId, resource);
            if(((projectLimit.getMax() < (EmptytoLong(projectLimit.getUsedLimit()) + EmptytoLong(resourceUsageMap.get(resources)))) && (projectLimit.getMax() != (EmptytoLong(projectLimit.getUsedLimit()) + EmptytoLong(resourceUsageMap.get(resources))))) && projectLimit.getMax() != -1) {
                //TODO apply internalization.
                validateResponse = "There is not enough " + resource + " available for " + convertEntityService.getProjectById(accountTypeId).getName() +". Please update project quota.";
            }
		} else if (accountType.equals("Department")) {
			ResourceLimitDepartment departmentLimit = getMaxByDepartmentAndResourceType(accountTypeId, resource);
			if (((departmentLimit.getMax() < (EmptytoLong(departmentLimit.getUsedLimit())
					+ EmptytoLong(resourceUsageMap.get(resources))))
					&& (departmentLimit.getMax() != (EmptytoLong(departmentLimit.getUsedLimit())
							+ EmptytoLong(resourceUsageMap.get(resources)))))
					&& departmentLimit.getMax() != -1) {
				// TODO apply internalization.
				validateResponse = "There is not enough " + resource + " available for " + convertEntityService.getDepartmentById(accountTypeId).getUserName() + ". Please update department quota.";
			}
		} else {
            // Resource count for domain.
            //updateResourceCountByDomain(convertEntityService.getDomainById(accountTypeId).getUuid(), null);
            ResourceLimitDomain domainLimit = getMaxByDomainAndResourceType(accountTypeId, resource);
            if(((domainLimit.getMax() < (EmptytoLong(domainLimit.getUsedLimit()) + EmptytoLong(resourceUsageMap.get(resources)))) && (domainLimit.getMax() != (EmptytoLong(domainLimit.getUsedLimit()) + EmptytoLong(resourceUsageMap.get(resources))))) && domainLimit.getMax() != -1) {
                //TODO apply internalization.
                validateResponse = "There is not enough " + resource + " available for " + convertEntityService.getDomainById(accountTypeId).getName() + ". Please update domain quota.";
            }
        }
        return validateResponse;
    }

    public Long EmptytoLong(Long value) {
        if (value == null) {
            return 0L;
        }
        return value;
    }

    public Long isTemplateZero(Long value) {
        if (value != 0L) {
            return value / (1024*1024*1024);
        } else {
            return 0L;
        }
    }

    public String checkResourceAvailablity(Long accountTypeId, String accountType, List<String> resourceType,  HashMap<String, Long> resourceUsageMap)
            throws Exception {
        String validateResponse = null ;
        for(String resource : resourceType) {
            switch(resource) {
            case ConvertEntityService.CS_INSTANCE:
                validateResponse = validateResourceLimit(accountTypeId, accountType, ConvertEntityService.CS_INSTANCE, resourceUsageMap,
                        validateResponse);
                if (validateResponse != null) {
                    return validateResponse;
                }
                break;
            case ConvertEntityService.CS_IP:
                validateResponse = validateResourceLimit(accountTypeId, accountType, ConvertEntityService.CS_IP, resourceUsageMap,
                        validateResponse);
                if (validateResponse != null) {
                    return validateResponse;
                }
                break;
            case ConvertEntityService.CS_VOLUME:
                validateResponse = validateResourceLimit(accountTypeId, accountType, ConvertEntityService.CS_VOLUME, resourceUsageMap,
                        validateResponse);
                if (validateResponse != null) {
                    return validateResponse;
                }
                break;
            case ConvertEntityService.CS_SNAPSHOT:
                validateResponse = validateResourceLimit(accountTypeId, accountType, ConvertEntityService.CS_SNAPSHOT, resourceUsageMap,
                        validateResponse);
                if (validateResponse != null) {
                    return validateResponse;
                }
                break;
            case ConvertEntityService.CS_TEMPLATE:
                validateResponse = validateResourceLimit(accountTypeId, accountType, ConvertEntityService.CS_TEMPLATE, resourceUsageMap,
                        validateResponse);
                if (validateResponse != null) {
                    return validateResponse;
                }
                break;
            case ConvertEntityService.CS_NETWORK:
                validateResponse = validateResourceLimit(accountTypeId, accountType, ConvertEntityService.CS_NETWORK, resourceUsageMap,
                        validateResponse);
                if (validateResponse != null) {
                    return validateResponse;
                }
                break;
            case ConvertEntityService.CS_VPC:
                validateResponse = validateResourceLimit(accountTypeId, accountType, ConvertEntityService.CS_VPC, resourceUsageMap,
                        validateResponse);
                if (validateResponse != null) {
                    return validateResponse;
                }
                break;
            case ConvertEntityService.CS_CPU:
                validateResponse = validateResourceLimit(accountTypeId, accountType, ConvertEntityService.CS_CPU, resourceUsageMap,
                        validateResponse);
                if (validateResponse != null) {
                    return validateResponse;
                }
                break;
            case ConvertEntityService.CS_MEMORY:
                validateResponse = validateResourceLimit(accountTypeId, accountType, ConvertEntityService.CS_MEMORY, resourceUsageMap,
                        validateResponse);
                if (validateResponse != null) {
                    return validateResponse;
                }
                break;
            case ConvertEntityService.CS_PRIMARY_STORAGE:
                validateResponse = validateResourceLimit(accountTypeId, accountType, ConvertEntityService.CS_PRIMARY_STORAGE, resourceUsageMap,
                        validateResponse);
                if (validateResponse != null) {
                    return validateResponse;
                }
                break;
            case ConvertEntityService.CS_SECONDARY_STORAGE:
                validateResponse = validateResourceLimit(accountTypeId, accountType, ConvertEntityService.CS_SECONDARY_STORAGE, resourceUsageMap,
                        validateResponse);
                if (validateResponse != null) {
                    return validateResponse;
                }
                break;
            }
        }
        return validateResponse;
    }

}
