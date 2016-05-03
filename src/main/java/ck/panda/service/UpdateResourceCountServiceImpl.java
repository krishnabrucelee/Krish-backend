package ck.panda.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ck.panda.constants.GenericConstants;
import ck.panda.domain.entity.IpAddress;
import ck.panda.domain.entity.Network;
import ck.panda.domain.entity.ResourceLimitDepartment;
import ck.panda.domain.entity.ResourceLimitProject;
import ck.panda.domain.entity.VmInstance;
import ck.panda.domain.entity.Volume;
import ck.panda.util.CloudStackResourceCapacity;
import ck.panda.util.ConfigUtil;

/**
 * Update resource count service implementation.
 *
 */
@Service
public class UpdateResourceCountServiceImpl implements UpdateResourceCountService {

    /** Reference of the convert entity service. */
    @Autowired
    private ConvertEntityService convertEntityService;

    @Autowired
    private IpaddressService ipaddressService;

    /** CloudStack Resource Capacity Service. */
    @Autowired
    private CloudStackResourceCapacity cloudStackResourceCapacity;

    /** Resource Limit Department service reference. */
    @Autowired
    private ResourceLimitDepartmentService resourceLimitDepartmentService;

    /** Resource Limit Project service reference. */
    @Autowired
    private ResourceLimitProjectService resourceLimitProjectService;

    /** Cloud stack configuration utility class. */
    @Autowired
    private ConfigUtil config;

	@Override
	public String QuotaUpdateByResourceObject(Object resourceObject, String resourceType, Long accountTypeId,
			String accountType, String status) throws Exception {
		/** Used for setting optional values for resource usage. */
		HashMap<String, Long> resourceUsageMap = new HashMap<String, Long>();
		List<String> resourceList = new ArrayList<String>();
		config.setServer(1L);
		switch (resourceType) {
		case GenericConstants.INSTANCE:
			VmInstance vmInstance = (VmInstance) resourceObject;
			resourceList.add(ConvertEntityService.CS_INSTANCE);
			resourceList.add(ConvertEntityService.CS_CPU);
			if (convertEntityService.getComputeOfferById(vmInstance.getComputeOfferingId()).getCustomized()) {
				resourceUsageMap.put(ConvertEntityService.CS_INSTANCE, 1L);
				resourceUsageMap.put(ConvertEntityService.CS_CPU, Long.valueOf(vmInstance.getCpuCore()));
				resourceUsageMap.put(ConvertEntityService.CS_MEMORY, Long.valueOf(vmInstance.getMemory()));
			} else {
				resourceUsageMap.put(ConvertEntityService.CS_INSTANCE, 1L);
				resourceUsageMap.put(ConvertEntityService.CS_CPU, Long.valueOf(convertEntityService
						.getComputeOfferById(vmInstance.getComputeOfferingId()).getNumberOfCores()));
				resourceUsageMap.put(ConvertEntityService.CS_MEMORY, Long.valueOf(
						convertEntityService.getComputeOfferById(vmInstance.getComputeOfferingId()).getMemory()));
			}
			if (vmInstance.getStorageOfferingId() != null) {
				if (convertEntityService.getStorageOfferById(vmInstance.getStorageOfferingId()).getIsCustomDisk()) {
					resourceUsageMap.put(ConvertEntityService.CS_PRIMARY_STORAGE,
							vmInstance.getDiskSize() + isTemplateZero(
									convertEntityService.getTemplateById(vmInstance.getTemplateId()).getSize()));
				} else {
					resourceUsageMap.put(ConvertEntityService.CS_PRIMARY_STORAGE, convertEntityService
							.getStorageOfferById(vmInstance.getStorageOfferingId()).getDiskSize()
							+ isTemplateZero(
									convertEntityService.getTemplateById(vmInstance.getTemplateId()).getSize()));
				}
				resourceUsageMap.put(ConvertEntityService.CS_VOLUME, 2L);
			} else {
				resourceUsageMap.put(ConvertEntityService.CS_PRIMARY_STORAGE,
						isTemplateZero(convertEntityService.getTemplateById(vmInstance.getTemplateId()).getSize()));
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
			if (accountType.equals(GenericConstants.PROJECT)) {
				updateResourceCount(accountTypeId, accountType, resourceList, resourceUsageMap, status);
			} else if (accountType.equals(GenericConstants.DEPARTMENT)) {
				updateResourceCount(accountTypeId, accountType, resourceList, resourceUsageMap, status);
			}
			break;
		case GenericConstants.VOLUME:
			resourceList.clear();
			Volume volume = (Volume) resourceObject;
			resourceList.add(ConvertEntityService.CS_VOLUME);
			resourceList.add(ConvertEntityService.CS_PRIMARY_STORAGE);
			if (volume.getDiskSize() != null) {
				resourceUsageMap.put(ConvertEntityService.CS_PRIMARY_STORAGE,
						volume.getDiskSize() / (1024 * 1024 * 1024));
			} else {
				resourceUsageMap.put(ConvertEntityService.CS_PRIMARY_STORAGE,
						convertEntityService.getStorageOfferById(volume.getStorageOfferingId()).getDiskSize());
			}
			resourceUsageMap.put(ConvertEntityService.CS_VOLUME, 1L);
			if (accountType.equals(GenericConstants.PROJECT)) {
				updateResourceCount(accountTypeId, accountType, resourceList, resourceUsageMap, status);
			} else if (accountType.equals(GenericConstants.DEPARTMENT)) {
				updateResourceCount(accountTypeId, accountType, resourceList, resourceUsageMap, status);
			}
			break;
		case GenericConstants.UPLOAD_VOLUME:
			resourceList.clear();
			Volume uploadVolume = (Volume) resourceObject;
			resourceList.add(ConvertEntityService.CS_VOLUME);
			resourceList.add(ConvertEntityService.CS_SECONDARY_STORAGE);
			if (uploadVolume.getDiskSize() != null) {
				resourceUsageMap.put(ConvertEntityService.CS_SECONDARY_STORAGE,
						uploadVolume.getDiskSize() / (1024 * 1024 * 1024));
			} else {
				resourceUsageMap.put(ConvertEntityService.CS_SECONDARY_STORAGE,
						convertEntityService.getStorageOfferById(uploadVolume.getStorageOfferingId()).getDiskSize());
			}
			resourceUsageMap.put(ConvertEntityService.CS_VOLUME, 1L);
			if (accountType.equals(GenericConstants.PROJECT)) {
				updateResourceCount(accountTypeId, accountType, resourceList, resourceUsageMap, status);
			} else if (accountType.equals(GenericConstants.DEPARTMENT)) {
				updateResourceCount(accountTypeId, accountType, resourceList, resourceUsageMap, status);
			}
			break;
		case GenericConstants.NETWORK:
			Network net = (Network)resourceObject;
			resourceList.clear();
			resourceList.add(ConvertEntityService.CS_NETWORK);
			resourceUsageMap.put(ConvertEntityService.CS_NETWORK, 1L);
			List<IpAddress> ipaddresse = ipaddressService.findByNetwork(net.getId());
			if (ipaddresse.size() > 0) {
				resourceUsageMap.put(ConvertEntityService.CS_IP, Long.valueOf(ipaddresse.size()));
			}
			if (accountType.equals(GenericConstants.PROJECT)) {
				updateResourceCount(accountTypeId, accountType, resourceList, resourceUsageMap, status);
			} else if (accountType.equals(GenericConstants.DEPARTMENT)) {
				updateResourceCount(accountTypeId, accountType, resourceList, resourceUsageMap, status);
			}
			break;
		case GenericConstants.IP:
			resourceList.clear();
			resourceList.add(ConvertEntityService.CS_IP);
			resourceUsageMap.put(ConvertEntityService.CS_IP, 1L);
			if (accountType.equals(GenericConstants.PROJECT)) {
				updateResourceCount(accountTypeId, accountType, resourceList, resourceUsageMap, status);
			} else if (accountType.equals(GenericConstants.DEPARTMENT)) {
				updateResourceCount(accountTypeId, accountType, resourceList, resourceUsageMap, status);
			}
			break;
		case GenericConstants.RESTORE_INSTANCE:
			resourceList.clear();
			VmInstance restoreInstance = (VmInstance) resourceObject;
			resourceList.add(ConvertEntityService.CS_INSTANCE);
			resourceList.add(ConvertEntityService.CS_CPU);
			if (convertEntityService.getComputeOfferById(restoreInstance.getComputeOfferingId()).getCustomized()) {
				resourceUsageMap.put(ConvertEntityService.CS_INSTANCE, 1L);
				resourceUsageMap.put(ConvertEntityService.CS_CPU, Long.valueOf(restoreInstance.getCpuCore()));
				resourceUsageMap.put(ConvertEntityService.CS_MEMORY, Long.valueOf(restoreInstance.getMemory()));
			} else {
				resourceUsageMap.put(ConvertEntityService.CS_INSTANCE, 1L);
				resourceUsageMap.put(ConvertEntityService.CS_CPU, Long.valueOf(convertEntityService
						.getComputeOfferById(restoreInstance.getComputeOfferingId()).getNumberOfCores()));
				resourceUsageMap.put(ConvertEntityService.CS_MEMORY, Long.valueOf(
						convertEntityService.getComputeOfferById(restoreInstance.getComputeOfferingId()).getMemory()));
			}
			resourceList.add(ConvertEntityService.CS_MEMORY);
			if (accountType.equals(GenericConstants.PROJECT)) {
				updateResourceCount(accountTypeId, accountType, resourceList, resourceUsageMap, status);
			} else if (accountType.equals(GenericConstants.DEPARTMENT)) {
				updateResourceCount(accountTypeId, accountType, resourceList, resourceUsageMap, status);
			}
			break;
		case GenericConstants.DESTROY:
			resourceList.clear();
			VmInstance destroyInstance = (VmInstance) resourceObject;
			resourceList.add(ConvertEntityService.CS_INSTANCE);
			resourceList.add(ConvertEntityService.CS_CPU);
			if (convertEntityService.getComputeOfferById(destroyInstance.getComputeOfferingId()).getCustomized()) {
				resourceUsageMap.put(ConvertEntityService.CS_INSTANCE, 1L);
				resourceUsageMap.put(ConvertEntityService.CS_CPU, Long.valueOf(destroyInstance.getCpuCore()));
				resourceUsageMap.put(ConvertEntityService.CS_MEMORY, Long.valueOf(destroyInstance.getMemory()));
			} else {
				resourceUsageMap.put(ConvertEntityService.CS_INSTANCE, 1L);
				resourceUsageMap.put(ConvertEntityService.CS_CPU, Long.valueOf(convertEntityService
						.getComputeOfferById(destroyInstance.getComputeOfferingId()).getNumberOfCores()));
				resourceUsageMap.put(ConvertEntityService.CS_MEMORY, Long.valueOf(
						convertEntityService.getComputeOfferById(destroyInstance.getComputeOfferingId()).getMemory()));

			}
			resourceList.add(ConvertEntityService.CS_MEMORY);
			if (accountType.equals(GenericConstants.PROJECT)) {
				updateResourceCount(accountTypeId, accountType, resourceList, resourceUsageMap, status);
			} else if (accountType.equals(GenericConstants.DEPARTMENT)) {
				updateResourceCount(accountTypeId, accountType, resourceList, resourceUsageMap, status);
			}
			break;
		case GenericConstants.EXPUNGING:
			resourceList.clear();
			VmInstance expungingInstance = (VmInstance) resourceObject;
			resourceList.add(ConvertEntityService.CS_INSTANCE);
			resourceList.add(ConvertEntityService.CS_CPU);
			if (convertEntityService.getComputeOfferById(expungingInstance.getComputeOfferingId()).getCustomized()) {
				resourceUsageMap.put(ConvertEntityService.CS_INSTANCE, 1L);
				resourceUsageMap.put(ConvertEntityService.CS_CPU, Long.valueOf(expungingInstance.getCpuCore()));
				resourceUsageMap.put(ConvertEntityService.CS_MEMORY, Long.valueOf(expungingInstance.getMemory()));
			} else {
				resourceUsageMap.put(ConvertEntityService.CS_INSTANCE, 1L);
				resourceUsageMap.put(ConvertEntityService.CS_CPU, Long.valueOf(convertEntityService
						.getComputeOfferById(expungingInstance.getComputeOfferingId()).getNumberOfCores()));
				resourceUsageMap.put(ConvertEntityService.CS_MEMORY, Long.valueOf(convertEntityService
						.getComputeOfferById(expungingInstance.getComputeOfferingId()).getMemory()));
			}
			resourceUsageMap.put(ConvertEntityService.CS_PRIMARY_STORAGE,
					isTemplateZero(convertEntityService.getTemplateById(expungingInstance.getTemplateId()).getSize()));
			resourceUsageMap.put(ConvertEntityService.CS_VOLUME, 1L);
			resourceList.add(ConvertEntityService.CS_VOLUME);
			resourceList.add(ConvertEntityService.CS_MEMORY);
			resourceList.add(ConvertEntityService.CS_PRIMARY_STORAGE);
			if (accountType.equals(GenericConstants.PROJECT)) {
				updateResourceCount(accountTypeId, accountType, resourceList, resourceUsageMap, status);
			} else if (accountType.equals(GenericConstants.DEPARTMENT)) {
				updateResourceCount(accountTypeId, accountType, resourceList, resourceUsageMap, status);
			}
			break;
		}
		return null;
	}

	/**
	 * Update the department count.
	 *
	 * @param departmentId
	 *            department id
	 * @param resourceType
	 *            of the resource
	 * @param updateResourceCount
	 *            of the department
	 * @param status
	 *            of the department
	 * @throws Exception
	 *             if error occurs
	 */
	public void updateCountByDepartmentAndResourceType(Long departmentId, String resourceType, Long updateResourceCount,
			String status) throws Exception {
		ResourceLimitDepartment departmentLimit = resourceLimitDepartmentService.findByDepartmentAndResourceType(
				departmentId, ResourceLimitDepartment.ResourceType.valueOf(resourceType), true);
		if (status.equalsIgnoreCase(GenericConstants.UPDATE)) {
			departmentLimit.setUsedLimit(EmptytoLong(departmentLimit.getUsedLimit()) + updateResourceCount);
		} else if (status.equalsIgnoreCase(GenericConstants.DELETE)) {
			if (EmptytoLong(departmentLimit.getUsedLimit()) > 0L) {
				departmentLimit.setUsedLimit(departmentLimit.getUsedLimit() - updateResourceCount);
			} else {
				departmentLimit.setUsedLimit(0L);
			}
		}
		departmentLimit.setIsSyncFlag(false);
		resourceLimitDepartmentService.update(departmentLimit);
	}

	/**
	 * Update the project count.
	 *
	 * @param projectId
	 *            project id
	 * @param resourceType
	 *            of the resource
	 * @param updateResourceCount
	 *            of the project
	 * @param status
	 *            of the project
	 * @throws Exception
	 *             if error occurs
	 */
	public void updateCountByProjectAndResourceType(Long projectId, String resourceType, Long updateResourceCount,
			String status) throws Exception {
		ResourceLimitProject projectLimit = resourceLimitProjectService.findByProjectAndResourceType(projectId,
				ResourceLimitProject.ResourceType.valueOf(resourceType), true);
		if (status.equalsIgnoreCase(GenericConstants.UPDATE)) {
			projectLimit.setUsedLimit(EmptytoLong(projectLimit.getUsedLimit()) + updateResourceCount);
		} else if (status.equalsIgnoreCase(GenericConstants.DELETE)) {
			if (EmptytoLong(projectLimit.getUsedLimit()) > 0L) {
				projectLimit.setUsedLimit(projectLimit.getUsedLimit() - updateResourceCount);
			} else {
				projectLimit.setUsedLimit(0L);
			}
		}
		projectLimit.setIsSyncFlag(false);
		resourceLimitProjectService.update(projectLimit);
	}

	/**
	 * update resource limit based on department and project.
	 *
	 * @param accountTypeId
	 *            of the resource
	 * @param accountType
	 *            of the resource
	 * @param resources
	 * @param resourceUsageMap
	 * @param updateResponse
	 *            of the resource
	 * @param status
	 *            of the resource
	 * @throws Exception
	 *             if error occurs
	 */
	public void updateResourceLimit(Long accountTypeId, String accountType, String resources,
			HashMap<String, Long> resourceUsageMap, String updateResponse, String status) throws Exception {
		String resource = convertEntityService.getResourceTypeValue().get(resources);
		if (accountType.equals(GenericConstants.PROJECT)) {
			updateCountByProjectAndResourceType(accountTypeId, resource, EmptytoLong(resourceUsageMap.get(resources)),
					status);
		} else if (accountType.equals(GenericConstants.DEPARTMENT)) {
			updateCountByDepartmentAndResourceType(accountTypeId, resource,
					EmptytoLong(resourceUsageMap.get(resources)), status);
		}
	}

	/**
	 * Empty to long check validation.
	 *
	 * @param value
	 * @return
	 */
	public Long EmptytoLong(Long value) {
		if (value == null) {
			return 0L;
		}
		return value;
	}

	/**
	 * Template value validation.
	 *
	 * @param value
	 * @return
	 */
	public Long isTemplateZero(Long value) {
		if (value != 0L) {
			return value / (1024 * 1024 * 1024);
		} else {
			return 0L;
		}
	}

	/**
	 * Update resource count.
	 *
	 * @param accountTypeId
	 *            of the resource
	 * @param accountType
	 *            of the resource
	 * @param resourceType
	 *            of the resource
	 * @param resourceUsageMap
	 * @param status
	 * @throws Exception
	 *             if error occurs
	 */
	public void updateResourceCount(Long accountTypeId, String accountType, List<String> resourceType,
			HashMap<String, Long> resourceUsageMap, String status) throws Exception {
		String updateResponse = null;
		for (String resource : resourceType) {
			switch (resource) {
			case ConvertEntityService.CS_INSTANCE:
				updateResourceLimit(accountTypeId, accountType, ConvertEntityService.CS_INSTANCE, resourceUsageMap,
						updateResponse, status);
				break;
			case ConvertEntityService.CS_IP:
				updateResourceLimit(accountTypeId, accountType, ConvertEntityService.CS_IP, resourceUsageMap,
						updateResponse, status);
				break;
			case ConvertEntityService.CS_VOLUME:
				updateResourceLimit(accountTypeId, accountType, ConvertEntityService.CS_VOLUME, resourceUsageMap,
						updateResponse, status);
				break;
			case ConvertEntityService.CS_SNAPSHOT:
				updateResourceLimit(accountTypeId, accountType, ConvertEntityService.CS_SNAPSHOT, resourceUsageMap,
						updateResponse, status);
				break;
			case ConvertEntityService.CS_TEMPLATE:
				updateResourceLimit(accountTypeId, accountType, ConvertEntityService.CS_TEMPLATE, resourceUsageMap,
						updateResponse, status);
				break;
			case ConvertEntityService.CS_NETWORK:
				updateResourceLimit(accountTypeId, accountType, ConvertEntityService.CS_NETWORK, resourceUsageMap,
						updateResponse, status);
				break;
			case ConvertEntityService.CS_VPC:
				updateResourceLimit(accountTypeId, accountType, ConvertEntityService.CS_VPC, resourceUsageMap,
						updateResponse, status);
				break;
			case ConvertEntityService.CS_CPU:
				updateResourceLimit(accountTypeId, accountType, ConvertEntityService.CS_CPU, resourceUsageMap,
						updateResponse, status);
				break;
			case ConvertEntityService.CS_MEMORY:
				updateResourceLimit(accountTypeId, accountType, ConvertEntityService.CS_MEMORY, resourceUsageMap,
						updateResponse, status);
				break;
			case ConvertEntityService.CS_PRIMARY_STORAGE:
				updateResourceLimit(accountTypeId, accountType, ConvertEntityService.CS_PRIMARY_STORAGE,
						resourceUsageMap, updateResponse, status);
				break;
			case ConvertEntityService.CS_SECONDARY_STORAGE:
				updateResourceLimit(accountTypeId, accountType, ConvertEntityService.CS_SECONDARY_STORAGE,
						resourceUsageMap, updateResponse, status);
				break;
			}
		}
	}
}
