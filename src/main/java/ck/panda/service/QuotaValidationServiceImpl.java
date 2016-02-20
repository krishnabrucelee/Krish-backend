package ck.panda.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import ck.panda.domain.entity.Network;
import ck.panda.domain.entity.ResourceLimitDepartment;
import ck.panda.domain.entity.ResourceLimitDepartment.ResourceType;
import ck.panda.domain.entity.ResourceLimitDomain;
import ck.panda.domain.entity.ResourceLimitProject;
import ck.panda.domain.entity.VmInstance;
import ck.panda.domain.entity.Volume;
import ck.panda.constants.GenericConstants;
import ck.panda.util.CloudStackResourceCapacity;
import ck.panda.util.error.exception.CustomGenericException;

@Service
public class QuotaValidationServiceImpl implements QuotaValidationService{

    /** Reference of the convert entity service. */
    @Autowired
    private ConvertEntityService convertEntityService;

    /** CloudStack Resource Capacity Service. */
    @Autowired
    private CloudStackResourceCapacity cloudStackResourceCapacity;

    /** Resource Limit Department service reference. */
    @Autowired
    private ResourceLimitDepartmentService resourceLimitDepartmentService;

    /** Resource Limit Project service reference. */
    @Autowired
    private ResourceLimitProjectService resourceLimitProjectService;

    /** Resource Limit Department service reference. */
    @Autowired
    private ResourceLimitDomainService resourceLimitDomainService;

    @Override
    public String QuotaLimitCheckByResourceObject(Object resourceObject, String resourceType,
            Long accountTypeId, String accountType) throws Exception {
        /** Used for setting optional values for resource usage. */
        HashMap<String, Long> resourceUsageMap = new HashMap<String, Long>();
        List<String> resourceList = new ArrayList<String>();
        switch(resourceType) {
        case "Instance":
            VmInstance vmInstance = (VmInstance)resourceObject;
            resourceList.add(ConvertEntityService.CS_INSTANCE);
            resourceUsageMap.put(ConvertEntityService.CS_INSTANCE, 1L);
            resourceList.add(ConvertEntityService.CS_CPU);
            if(convertEntityService.getComputeOfferById(vmInstance.getComputeOfferingId()).getCustomized()) {
                resourceUsageMap.put(ConvertEntityService.CS_INSTANCE, Long.valueOf(vmInstance.getCpuCore()));
                resourceUsageMap.put(ConvertEntityService.CS_MEMORY, Long.valueOf(vmInstance.getMemory()));
            } else {
                resourceUsageMap.put(ConvertEntityService.CS_INSTANCE, Long.valueOf(convertEntityService.getComputeOfferById(vmInstance.getComputeOfferingId()).getNumberOfCores()));
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
            resourceUsageMap.put(ConvertEntityService.CS_INSTANCE, 1L);
            resourceList.add(ConvertEntityService.CS_CPU);
            if(convertEntityService.getComputeOfferById(restoreInstance.getComputeOfferingId()).getCustomized()) {
                resourceUsageMap.put(ConvertEntityService.CS_INSTANCE, Long.valueOf(restoreInstance.getCpuCore()));
                resourceUsageMap.put(ConvertEntityService.CS_MEMORY, Long.valueOf(restoreInstance.getMemory()));
            } else {
                resourceUsageMap.put(ConvertEntityService.CS_INSTANCE, Long.valueOf(convertEntityService.getComputeOfferById(restoreInstance.getComputeOfferingId()).getNumberOfCores()));
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

    public void updateResourceCountByDomain(String domainUuid, HashMap<String, String> domainCountMap)
            throws Exception {
        // Resource count for domain
        String csResponse = cloudStackResourceCapacity.updateResourceCount(domainUuid, domainCountMap, "json");
        convertEntityService.resourceCount(csResponse);
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
                validateResponse = " User "+ resource +" Limit exceeded in project " + convertEntityService.getProjectById(accountTypeId).getName();
            }
        } else if(accountType.equals("Department")){
            ResourceLimitDepartment departmentLimit = getMaxByDepartmentAndResourceType(accountTypeId, resource);
            if(((departmentLimit.getMax() < (EmptytoLong(departmentLimit.getUsedLimit()) + EmptytoLong(resourceUsageMap.get(resources)))) && (departmentLimit.getMax() != (EmptytoLong(departmentLimit.getUsedLimit()) + EmptytoLong(resourceUsageMap.get(resources))))) && departmentLimit.getMax() != -1) {
                //TODO apply internalization.
                validateResponse = " User "+ resource +" Limit exceeded in department " + convertEntityService.getDepartmentById(accountTypeId).getUserName();
            }
        } else {
            // Resource count for domain.
            updateResourceCountByDomain(convertEntityService.getDomainById(accountTypeId).getUuid(), null);
            ResourceLimitDomain domainLimit = getMaxByDomainAndResourceType(accountTypeId, resource);
            if(((domainLimit.getMax() < (EmptytoLong(domainLimit.getUsedLimit()) + EmptytoLong(resourceUsageMap.get(resources)))) && (domainLimit.getMax() != (EmptytoLong(domainLimit.getUsedLimit()) + EmptytoLong(resourceUsageMap.get(resources))))) && domainLimit.getMax() != -1) {
                //TODO apply internalization.
                validateResponse = " User "+ resource +" Limit exceeded in domain " + convertEntityService.getDomainById(accountTypeId).getName();
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
