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
import ck.panda.constants.PingConstants;
import ck.panda.util.PingService;

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

    /** Mr.ping service reference. */
    @Autowired
    private PingService pingService;

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
            } else if (pingService.apiConnectionCheck(errors)) {
                // set server for maintain session with configuration values
                configServer.setUserServer();
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
                if (pingService.apiConnectionCheck(errors)) {
                    compute = computeRepo.save(persistCompute);
                    savePlanCostInPing(compute);
                }
                return compute;
            }
            return compute;
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
            } else if (pingService.apiConnectionCheck(errors)) {
                HashMap<String, String> optionalParams = new HashMap<String, String>();
                configServer.setUserServer();
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
            if (pingService.apiConnectionCheck(errors)) {
                compute = computeRepo.save(compute);
                savePlanCostInPing(compute);
            }
            return compute;
        } else {
            return computeRepo.save(compute);
        }
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
                configServer.setUserServer();
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
        ComputeOfferingCost cost = compute.getComputeCost().get(0);
        Double totalCost = costService.totalcost(cost);
        List<ComputeOfferingCost> computeOfferingcostList = costService.findByComputeOfferingId(compute.getId());
        if(computeOfferingcostList.size() != 0) {
            ComputeOfferingCost persistedCost = computeOfferingcostList.get(computeOfferingcostList.size() - 1);
            if (!compute.getCustomized()) {
                int runningMemoryCost = Double.compare(offeringNullCheck(cost.getInstanceRunningCostMemory()),persistedCost.getInstanceRunningCostMemory());
                int runningVcpuCost= Double.compare(offeringNullCheck(cost.getInstanceRunningCostVcpu()),offeringNullCheck(persistedCost.getInstanceRunningCostVcpu()));
                int stoppageVcpuCost =  Double.compare(offeringNullCheck(cost.getInstanceStoppageCostVcpu()),offeringNullCheck(persistedCost.getInstanceStoppageCostVcpu()));
                int stoppageVcpuMemory =  Double.compare(offeringNullCheck(cost.getInstanceStoppageCostMemory()),offeringNullCheck(persistedCost.getInstanceStoppageCostMemory()));
                int setupCost =  Double.compare(offeringNullCheck(cost.getSetupCost()),offeringNullCheck(persistedCost.getSetupCost()));
                if (runningMemoryCost> 0 || runningVcpuCost>0 || stoppageVcpuCost>0 || stoppageVcpuMemory>0 || setupCost >0 || runningMemoryCost< 0 || runningVcpuCost<0 || stoppageVcpuCost<0 || stoppageVcpuMemory<0 || setupCost <0 ) {
                    this.computeCostSave(compute);
                }
            }
            else {
                int runningcostPerMB = Double.compare(offeringNullCheck(cost.getInstanceRunningCostPerMB()),offeringNullCheck(persistedCost.getInstanceRunningCostPerMB()));
                int runningcostPerVcpu = Double.compare(offeringNullCheck(cost.getInstanceRunningCostPerVcpu()),offeringNullCheck(persistedCost.getInstanceRunningCostPerVcpu()));
                int runningcostPerMhz = Double.compare(offeringNullCheck(cost.getInstanceRunningCostPerMhz()),offeringNullCheck(persistedCost.getInstanceRunningCostPerMhz()));
                int stoppagecostPerMB = Double.compare(offeringNullCheck(cost.getInstanceStoppageCostPerMB()),offeringNullCheck(persistedCost.getInstanceStoppageCostPerMB()));
                int stoppagecostPerVcpu = Double.compare(offeringNullCheck(cost.getInstanceStoppageCostPerVcpu()),offeringNullCheck(persistedCost.getInstanceStoppageCostPerVcpu()));
                int stoppagecostPerMhz = Double.compare(offeringNullCheck(cost.getInstanceStoppageCostPerMhz()),offeringNullCheck(persistedCost.getInstanceStoppageCostPerMhz()));
                int setupCost =  Double.compare(offeringNullCheck(cost.getSetupCost()),offeringNullCheck(persistedCost.getSetupCost()));
                if ( runningcostPerMB >0 || runningcostPerVcpu > 0
                    || runningcostPerMhz>0 || stoppagecostPerMB>0 || stoppagecostPerVcpu>0 || stoppagecostPerMhz>0 || setupCost >0 || runningcostPerMB >0 || runningcostPerVcpu <0
                    || runningcostPerMhz<0 || stoppagecostPerMB<0 || stoppagecostPerVcpu<0 || stoppagecostPerMhz<0 || setupCost <0 ) {
                 this.computeCostSave(compute);
               }
            }
        }
        else {
            this.computeCostSave(compute);
        }
         return compute;
    }

    @Override
    public List<ComputeOffering> findByDomainAndIsActive(Long domainId, Boolean isActive) throws Exception {
        return computeRepo.findByDomainAndIsActive(domainId, true);
    }

    @Override
    public Page<ComputeOffering> findAllByDomainId(Long domainId, PagingAndSorting pagingAndSorting) throws Exception {
        return computeRepo.findAllByDomainIdAndIsActive(domainId, true, pagingAndSorting.toPageRequest());
    }

    @Override
    public Page<ComputeOffering> findAllByDomainIdAndSearchText(Long domainId, PagingAndSorting pagingAndSorting, String searchText) throws Exception {
        return computeRepo.findAllByDomainIdAndIsActiveAndSearchText(domainId, true, pagingAndSorting.toPageRequest(),searchText);
    }

    /**
     * Set optional value for MR.ping api call.
     *
     * @param computeOfferingCost compute offering cost
     * @return status
     * @throws Exception raise if error
     */
    public Boolean savePlanCostInPing(ComputeOffering computeOfferingCost) throws Exception {
        JSONObject optional = new JSONObject();
        optional.put(PingConstants.PLAN_UUID, computeOfferingCost.getUuid());
        optional.put(PingConstants.NAME, computeOfferingCost.getName());
        optional.put(PingConstants.IS_CUSTOM, computeOfferingCost.getCustomized());
        optional.put(PingConstants.REFERENCE_NAME, PingConstants.VM);
        optional.put(PingConstants.GROUP_NAME, PingConstants.VM);
        if (computeOfferingCost.getComputeCost().size() != 0) {
            optional.put(PingConstants.COMPUTE_SETUP_COST, offeringNullCheck(computeOfferingCost.getComputeCost().get(0).getSetupCost()));
            if (computeOfferingCost.getCustomized()) {
            optional.put(PingConstants.COMUTE_RUNNING_PER_VCPU_COST, offeringNullCheck(computeOfferingCost.getComputeCost().get(0).getInstanceRunningCostPerVcpu()));
            optional.put(PingConstants.COMUTE_RUNNING_PER_MB_COST, offeringNullCheck(computeOfferingCost.getComputeCost().get(0).getInstanceRunningCostPerMB()));
            optional.put(PingConstants.COMUTE_RUNNING_PER_SPEED_COST, offeringNullCheck(computeOfferingCost.getComputeCost().get(0).getInstanceRunningCostPerMhz()));
            optional.put(PingConstants.COMUTE_STOPPAGE_PER_VCPU_COST, offeringNullCheck(computeOfferingCost.getComputeCost().get(0).getInstanceStoppageCostPerVcpu()));
            optional.put(PingConstants.COMUTE_STOPPAGE_PER_MB_COST, offeringNullCheck(computeOfferingCost.getComputeCost().get(0).getInstanceStoppageCostPerMB()));
            optional.put(PingConstants.COMUTE_STOPPAGE_PER_SPEED_COST, offeringNullCheck(computeOfferingCost.getComputeCost().get(0).getInstanceStoppageCostPerMhz()));
            Double totalCost = optional.getDouble(PingConstants.COMUTE_RUNNING_PER_VCPU_COST) + optional.getDouble(PingConstants.COMUTE_RUNNING_PER_MB_COST)
                   + optional.getDouble(PingConstants.COMUTE_RUNNING_PER_SPEED_COST) + optional.getDouble(PingConstants.COMUTE_STOPPAGE_PER_VCPU_COST)
                   + optional.getDouble(PingConstants.COMUTE_STOPPAGE_PER_MB_COST) + optional.getDouble(PingConstants.COMUTE_STOPPAGE_PER_SPEED_COST)
                   + optional.getDouble(PingConstants.COMPUTE_SETUP_COST);
            optional.put(PingConstants.TOTAL_COST, totalCost);
            } else {
                optional.put(PingConstants.COMUTE_RUNNING_FOR_VCPU_COST, offeringNullCheck(computeOfferingCost.getComputeCost().get(0).getInstanceRunningCostVcpu()));
                optional.put(PingConstants.COMUTE_RUNNING_FOR_MB_COST, offeringNullCheck(computeOfferingCost.getComputeCost().get(0).getInstanceRunningCostMemory()));
                optional.put(PingConstants.COMUTE_STOPPAGE_FOR_VCPU_COST, offeringNullCheck(computeOfferingCost.getComputeCost().get(0).getInstanceStoppageCostVcpu()));
                optional.put(PingConstants.COMUTE_STOPPAGE_FOR_MB_COST, offeringNullCheck(computeOfferingCost.getComputeCost().get(0).getInstanceStoppageCostMemory()));
                Double totalCost = optional.getDouble(PingConstants.COMUTE_RUNNING_FOR_VCPU_COST) + optional.getDouble(PingConstants.COMUTE_RUNNING_FOR_MB_COST)
                   + optional.getDouble(PingConstants.COMUTE_STOPPAGE_FOR_VCPU_COST) + optional.getDouble(PingConstants.COMUTE_STOPPAGE_FOR_MB_COST)
                   + optional.getDouble(PingConstants.COMPUTE_SETUP_COST);
                optional.put(PingConstants.TOTAL_COST, totalCost);
            }
            if (computeOfferingCost.getComputeCost().get(0).getZoneId() != null) {
                Zone zone = convertEntityService.getZoneById(computeOfferingCost.getComputeCost().get(0).getZoneId());
                optional.put(PingConstants.ZONE_ID, zone.getUuid());
            }
        }
        pingService.addPlanCost(optional);
        return true;
    }

    /**
     * Offering cost null value check.
     *
     * @param value offering cost
     * @return double value
     */
    public Double offeringNullCheck(Double value) {
        if (value == null) {
            value = 0.0;
        }
        return value;
    }

    /**
     * To save compute offering cost.
     *
     * @param compute offering object.
     * @throws Exception if error occurs.
     */
    private void computeCostSave(ComputeOffering compute) throws Exception {
         List<ComputeOfferingCost> computeCost = new ArrayList<ComputeOfferingCost>();
         ComputeOffering persistCompute = find(compute.getId());
         ComputeOfferingCost cost = compute.getComputeCost().get(0);
         ComputeOfferingCost computeOfferingcost = new ComputeOfferingCost();
         computeOfferingcost.setComputeId(compute.getId());
         Double totalCost = costService.totalcost(cost);
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
         computeCost.addAll(persistCompute.getComputeCost());
         compute.setComputeCost(computeCost);
         computeRepo.save(compute);
    }
}
