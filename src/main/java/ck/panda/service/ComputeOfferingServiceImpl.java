package ck.panda.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.constants.CloudStackConstants;
import ck.panda.constants.GenericConstants;
import ck.panda.domain.entity.ComputeOffering;
import ck.panda.domain.entity.ComputeOfferingCost;
import ck.panda.domain.entity.Domain;
import ck.panda.domain.entity.User;
import ck.panda.domain.entity.VmInstance;
import ck.panda.domain.entity.Zone;
import ck.panda.domain.repository.jpa.ComputeOfferingRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackComputeOffering;
import ck.panda.util.CloudStackOptionalUtil;
import ck.panda.util.ConfigUtil;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;

/**
 *  Compute Offering service aid user to create compute offers in Cloud Stack Server.
 *
 */

@Service
public class ComputeOfferingServiceImpl implements ComputeOfferingService {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ComputeOfferingServiceImpl.class);

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** ComputeOffering repository reference. */
    @Autowired
    private ComputeOfferingRepository computeRepo;

    /** object(server) created for CloudStackServer. */
    @Autowired
    private ConfigUtil configServer;

    /** Service method for establishing connection to CloudStack. */
    @Autowired
    private CloudStackComputeOffering cscomputeOffering;

    /** Virtual Machine service reference. */
    @Autowired
    private VirtualMachineService vmService;

    /** Compute offering cost service reference. */
    @Autowired
    private ComputeOfferingCostService costService;

    /** Convert Entity service for reference. */
    @Autowired
    private ConvertEntityService convertEntityService;

    /** Constant for service offering id. */
    private static final String CS_SERVICE_OFFERING = "serviceoffering";

    /** Constant for memory. */
    private static final String CS_MEMORY = "memory";

    /** Constant for compute. */
    private static final String CS_COMPUTE = "compute";

    /** Constant for cpu number. */
    private static final String CS_CPU_CORE = "cpunumber";

    /** Constant for cpu speed. */
    private static final String CS_SPEED = "cpuspeed";

    /** Constant for limit cpu use. */
    private static final String CS_CAPACITY = "limitcpuuse";

    /** Constant for network rate. */
    private static final String CS_NETWORK_RATE = "networkrate";

    /** Constant for offer ha. */
    private static final String CS_OFFER_HA = "offerha";

    /** Constant for host tags. */
    private static final String CS_HOST_TAGS = "hosttags";

    /** Constant for create compute offering .*/
    private static final String CS_COMPUTEOFFERING = "createserviceofferingresponse";

    /** Constant for update compute offering .*/
    private static final String CS_UPDATE_COMPUTEOFFERING = "updateserviceofferingresponse";

    /** Constant for list compute offering. */
    private static final String CS_LIST_COMPUTEOFFERING = "listserviceofferingsresponse";

    @Override
    public ComputeOffering save(ComputeOffering compute) throws Exception {

        if (compute.getIsSyncFlag()) {
            this.validateComputeOffering(compute);
            Errors errors = validator.rejectIfNullEntity(CS_COMPUTE, compute);
            errors = validator.validateEntity(compute, errors);
            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
                // set server for maintain session with configuration values
                cscomputeOffering.setServer(configServer.setServer(1L));
                // create compute offering in ACS and getting response in Json String.
                String createComputeResponse = cscomputeOffering.createComputeOffering(compute.getName(),
                        compute.getDisplayText(),CloudStackConstants.JSON, addOptionalValues(compute));
                // convert json string to json object
                JSONObject createComputeResponseJSON = new JSONObject(createComputeResponse)
                        .getJSONObject(CS_COMPUTEOFFERING);
                if (createComputeResponseJSON.has(CloudStackConstants.CS_ERROR_CODE)) {
                    errors = this.validateEvent(errors, createComputeResponseJSON.getString(CloudStackConstants.CS_ERROR_TEXT));
                    throw new ApplicationException(errors);
                }
                JSONObject serviceOffering = createComputeResponseJSON.getJSONObject(CS_SERVICE_OFFERING);
                compute.setUuid((String) serviceOffering.get(CloudStackConstants.CS_ID));
                compute.setIsActive(true);
                // Get compute offering cost.
                ComputeOfferingCost cost = compute.getComputeCost().get(0);
                // Get the total cost
                Double totalCost = costService.totalcost(cost);
                // storing in our DB.
                cost.setTotalCost(totalCost);
                cost.setZoneId(cost.getZone().getId());
                ComputeOffering persistCompute = computeRepo.save(compute);
                cost.setComputeId(persistCompute.getId());
                return computeRepo.save(persistCompute);
            }
        } else {
            LOGGER.debug(compute.getUuid());
            return computeRepo.save(compute);
        }
    }

    @Override
    public ComputeOffering update(ComputeOffering compute) throws Exception {
        if (compute.getIsSyncFlag()) {
            this.validateComputeOffering(compute);
            Errors errors = validator.rejectIfNullEntity(CS_COMPUTE, compute);
            errors = validator.validateEntity(compute, errors);
            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
                HashMap<String, String> optionalParams = new HashMap<String, String>();
                cscomputeOffering.setServer(configServer.setServer(1L));
                // update compute offering in ACS and getting response in Json String.
                String editComputeResponse = cscomputeOffering.updateComputeOffering(compute.getUuid(),
                        compute.getName(), compute.getDisplayText(), CloudStackConstants.JSON, optionalParams);
                // convert json string to json object
                JSONObject editComputeJSON = new JSONObject(editComputeResponse)
                        .getJSONObject(CS_UPDATE_COMPUTEOFFERING).getJSONObject(CS_SERVICE_OFFERING);
                //cost calculation for compute offering.
                if(compute.getComputeCost().size() !=0) {
                    this.costCalculation(compute);
                }
            }
        }
       return computeRepo.save(compute);
    }

    @Override
    public void delete(ComputeOffering compute) throws Exception {
        computeRepo.delete(compute);
    }

    @Override
    public void delete(Long id) throws Exception {
        computeRepo.delete(id);
    }

    @Override
    public ComputeOffering softDelete(ComputeOffering compute) throws Exception {
        compute.setIsActive(false);
        if (compute.getIsSyncFlag()) {
            Errors errors = validator.rejectIfNullEntity(CS_COMPUTE, compute);
            errors = validator.validateEntity(compute, errors);
            // set server for finding value in configuration
            cscomputeOffering.setServer(configServer.setServer(1L));
            List<VmInstance> vmResponse = vmService.findAllByComputeOfferingIdAndVmStatus(compute.getId(),
                    VmInstance.Status.EXPUNGING);
            if (vmResponse.size() != 0) {
                errors.addGlobalError("plan.delete.confirmation");
            }
            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
                compute.setIsActive(false);
                // update compute offering in ACS.
                cscomputeOffering.deleteComputeOffering(compute.getUuid(),CloudStackConstants.JSON);
            }
        }
        return computeRepo.save(compute);
    }

    @Override
    public Page<ComputeOffering> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return computeRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public ComputeOffering find(Long id) throws Exception {
        return computeRepo.findOne(id);
    }

    /**
     * Check the compute offering CS error handling.
     *
     * @param errors error creating status.
     * @param errmessage error message.
     * @return errors.
     * @throws Exception if error occurs.
     */
    private Errors validateEvent(Errors errors, String errmessage) throws Exception {
        errors.addGlobalError(errmessage);
        return errors;
    }

    @Override
    public List<ComputeOffering> findAll() throws Exception {
        return (List<ComputeOffering>) computeRepo.findAll();
    }

    /**
     * Method to add optional values from cloud stack.
     *
     * @param compute object for compute offering
     * @return optional values.
     * @throws Exception if error occurs.
     */
    public HashMap<String, String> addOptionalValues(ComputeOffering compute) throws Exception {
        HashMap<String, String> computeMap = new HashMap<String, String>();

         CloudStackOptionalUtil.updateOptionalStringValue(CloudStackConstants.CS_TAGS, compute.getStorageTags(), computeMap);
         CloudStackOptionalUtil.updateOptionalStringValue(CS_HOST_TAGS, compute.getStorageTags(), computeMap);
         CloudStackOptionalUtil.updateOptionalBooleanValue(CS_CAPACITY, compute.getCpuCapacity(), computeMap);
         CloudStackOptionalUtil.updateOptionalIntegerValue(CS_SPEED, compute.getClockSpeed(), computeMap);
         CloudStackOptionalUtil.updateOptionalIntegerValue(CS_CPU_CORE, compute.getNumberOfCores(), computeMap);
         CloudStackOptionalUtil.updateOptionalIntegerValue(CS_MEMORY, compute.getMemory(), computeMap);
         CloudStackOptionalUtil.updateOptionalIntegerValue(CloudStackConstants.CS_BYTES_READ, compute.getDiskBytesReadRate(), computeMap);
         CloudStackOptionalUtil.updateOptionalIntegerValue(CloudStackConstants.CS_BYTES_WRITE, compute.getDiskBytesWriteRate(), computeMap);
         CloudStackOptionalUtil.updateOptionalIntegerValue(CloudStackConstants.CS_IOPS_READ, compute.getDiskIopsReadRate(), computeMap);
         CloudStackOptionalUtil.updateOptionalIntegerValue(CloudStackConstants.CS_IOPS_WRITE, compute.getDiskIopsWriteRate(), computeMap);
         CloudStackOptionalUtil.updateOptionalBooleanValue(CloudStackConstants.CS_CUSTOM_IOPS, compute.getCustomizedIops(), computeMap);
         CloudStackOptionalUtil.updateOptionalBooleanValue(CloudStackConstants.CS_CUSTOM_OFFER, compute.getCustomized(), computeMap);
         CloudStackOptionalUtil.updateOptionalIntegerValue(CloudStackConstants.CS_MIN_IOPS, compute.getMinIops(), computeMap);
         CloudStackOptionalUtil.updateOptionalIntegerValue(CloudStackConstants.CS_MAX_IOPS, compute.getMaxIops(), computeMap);
         CloudStackOptionalUtil.updateOptionalIntegerValue(CS_NETWORK_RATE, compute.getNetworkRate(), computeMap);
         CloudStackOptionalUtil.updateOptionalBooleanValue(CS_OFFER_HA, compute.getIsHighAvailabilityEnabled(), computeMap);
         if (compute.getDomainId() != null) {
             Domain domain = convertEntityService.getDomainById(compute.getDomainId());
             CloudStackOptionalUtil.updateOptionalBooleanValue(domain.getUuid(), compute.getIsHighAvailabilityEnabled(), computeMap);
         }
         return computeMap;
    }

    @Override
    public List<ComputeOffering> findAllFromCSServer() throws Exception {

        List<ComputeOffering> computeOfferingList = new ArrayList<ComputeOffering>();
        HashMap<String, String> computeOfferingMap = new HashMap<String, String>();
        computeOfferingMap.put(CloudStackConstants.CS_LIST_ALL, CloudStackConstants.STATUS_ACTIVE);
        // 1. Get the list of ComputeOffering from CS server using CS connector
        cscomputeOffering.setServer(configServer.setServer(1L));
        String response = cscomputeOffering.listComputeOfferings(CloudStackConstants.JSON, computeOfferingMap);
        JSONArray computeOfferingListJSON = null;
        JSONObject responseObject = new JSONObject(response).getJSONObject(CS_LIST_COMPUTEOFFERING);
        if (responseObject.has(CS_SERVICE_OFFERING)) {
            computeOfferingListJSON = responseObject.getJSONArray(CS_SERVICE_OFFERING);
            // 2. Iterate the json list, convert the single json entity to
            // domain
            for (int i = 0, size = computeOfferingListJSON.length(); i < size; i++) {
                // 2.1 Call convert by passing JSONObject to ComputeOffering entity and Add
                // the converted ComputeOffering entity to list
                computeOfferingList.add(ComputeOffering.convert(computeOfferingListJSON.getJSONObject(i)));
            }
        }
        return computeOfferingList;
    }

    @Override
    public ComputeOffering findByUUID(String uuid) {
        return computeRepo.findByUUID(uuid);
    }

    /**
     * Find all the compute offering with pagination.
     *
     * @throws Exception application errors.
     * @param pagingAndSorting do pagination with sorting for computeofferings.
     * @return list of compute offerings.
     */
    public Page<ComputeOffering> findAllByActive(PagingAndSorting pagingAndSorting) throws Exception {
        return computeRepo.findAllByIsActive(pagingAndSorting.toPageRequest(), true);

    }

    /**
     * Validate the compute.
     *
     * @param compute reference of the compute offering.
     * @throws Exception error occurs
     */
    private void validateComputeOffering(ComputeOffering compute) throws Exception {
        Errors errors = validator.rejectIfNullEntity(CS_COMPUTE, compute);
        errors = validator.validateEntity(compute, errors);
        ComputeOffering computeOffering = computeRepo.findNameAndIsActive(compute.getName(), true);
        if (computeOffering != null && compute.getId() != computeOffering.getId()) {
            errors.addFieldError(GenericConstants.NAME, "error.computeoffering.already.exist");
        }
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        }
    }

    @Override
    public ComputeOffering findByName(String name) {
        return (ComputeOffering) computeRepo.findNameAndIsActive(name, true);
    }

    @Override
    public List<ComputeOffering> findByIsActive(Boolean isActive, Long userId) throws Exception {
        User user = convertEntityService.getOwnerById(userId);
        // Check the user is not a root and admin and set the domain value from login detail
        if (user.getType().equals(User.UserType.ROOT_ADMIN)) {
            return computeRepo.findByIsActive(true);
        } else {
            return computeRepo.findByDomainAndIsActive(user.getDomainId(), true);
        }
    }

    /**
     * Compute offering cost calculation base on different plans.
     *
     * @param compute object of the compute offering.
     * @return compute offering cost.
     * @throws Exception if error occurs.
     */
    private ComputeOffering costCalculation(ComputeOffering compute) throws Exception {
        List<ComputeOfferingCost> computeCost = new ArrayList<ComputeOfferingCost>();
        ComputeOffering persistCompute = find(compute.getId());
        ComputeOfferingCost cost = compute.getComputeCost().get(0);
        Zone zone = compute.getComputeCost().get(0).getZone();
        Double totalCost = costService.totalcost(cost);
        ComputeOfferingCost computeOfferingcost = costService.findByCostAndId(compute.getId(),totalCost);
        if (computeOfferingcost == null) {
             computeOfferingcost = new ComputeOfferingCost();
             computeOfferingcost.setComputeId(compute.getId());
             computeOfferingcost.setInstanceRunningCostMemory(cost.getInstanceRunningCostMemory());
             computeOfferingcost.setInstanceRunningCostVcpu(cost.getInstanceRunningCostVcpu());
             computeOfferingcost.setInstanceStoppageCostVcpu(cost.getInstanceStoppageCostVcpu());
             computeOfferingcost.setInstanceStoppageCostMemory(cost.getInstanceStoppageCostMemory());
             computeOfferingcost.setInstanceRunningCostPerMB(cost.getInstanceRunningCostPerMB());
             computeOfferingcost.setInstanceRunningCostPerVcpu(cost.getInstanceRunningCostPerVcpu());
             computeOfferingcost.setInstanceStoppageCostPerMB(cost.getInstanceStoppageCostPerMB());
             computeOfferingcost.setInstanceStoppageCostPerVcpu(cost.getInstanceStoppageCostPerVcpu());
             computeOfferingcost.setInstanceStoppageCostPerMhz(cost.getInstanceStoppageCostPerMhz());
             computeOfferingcost.setInstanceRunningCostPerMhz(cost.getInstanceRunningCostPerMhz());
             computeOfferingcost.setTotalCost(totalCost);
             computeOfferingcost.setSetupCost(cost.getSetupCost());
             computeOfferingcost.setZoneId(cost.getZoneId());
             computeOfferingcost = costService.save(computeOfferingcost);
             computeCost.add(computeOfferingcost);
         }
         computeCost.addAll(persistCompute.getComputeCost());
         compute.setComputeCost(computeCost);
         return computeRepo.save(compute);
    }

    @Override
    public List<ComputeOffering> findByDomainAndIsActive(Long domainId, Boolean isActive) throws Exception {
        return computeRepo.findByDomainAndIsActive(domainId, true);
    }

    @Override
    public Page<ComputeOffering> findAllByDomainId(Long domainId, PagingAndSorting pagingAndSorting) throws Exception {
        return computeRepo.findAllByDomainIdAndIsActive(domainId, true, pagingAndSorting.toPageRequest());

    }
}
