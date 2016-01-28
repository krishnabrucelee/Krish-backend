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
import ck.panda.domain.entity.ComputeOffering;
import ck.panda.domain.entity.VmInstance;
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

    @Override
    public ComputeOffering save(ComputeOffering compute) throws Exception {

        if (compute.getIsSyncFlag()) {
            this.validateComputeOffering(compute);
            Errors errors = validator.rejectIfNullEntity("compute", compute);
            errors = validator.validateEntity(compute, errors);
            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {

                // set server for maintain session with configuration values
                cscomputeOffering.setServer(configServer.setServer(1L));
                // create compute offering in ACS and getting response in Json String.
                String createComputeResponse = cscomputeOffering.createComputeOffering(compute.getName(),
                        compute.getDisplayText(), "json", addOptionalValues(compute));
                // convert json string to json object
                JSONObject createComputeResponseJSON = new JSONObject(createComputeResponse)
                        .getJSONObject("createserviceofferingresponse");

                if (createComputeResponseJSON.has("errorcode")) {
                    errors = this.validateEvent(errors, createComputeResponseJSON.getString("errortext"));
                    throw new ApplicationException(errors);
                }
                JSONObject serviceOffering = createComputeResponseJSON.getJSONObject("serviceoffering");
                compute.setUuid((String) serviceOffering.get("id"));
                compute.setIsActive(true);
                return computeRepo.save(compute);
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
            Errors errors = validator.rejectIfNullEntity("compute", compute);
            errors = validator.validateEntity(compute, errors);

            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
                HashMap<String, String> hs = new HashMap<String, String>();
                cscomputeOffering.setServer(configServer.setServer(1L));
                // update compute offering in ACS and getting response in Json String.
                String editComputeResponse = cscomputeOffering.updateComputeOffering(compute.getUuid(),
                        compute.getName(), compute.getDisplayText(), "json", hs);
                // convert json string to json object
                JSONObject editComputeJSON = new JSONObject(editComputeResponse)
                        .getJSONObject("updateserviceofferingresponse").getJSONObject("serviceoffering");
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
        if (compute.getIsSyncFlag()) {
            Errors errors = validator.rejectIfNullEntity("compute", compute);
            errors = validator.validateEntity(compute, errors);
            // set server for finding value in configuration
            cscomputeOffering.setServer(configServer.setServer(1L));
            // before deleting a compute offer checking whether a vm instance associated with that offer
            List<VmInstance> vmResponse = vmService.findByComputeOfferingIdAndVmStatus(compute.getId(),
                    VmInstance.Status.Expunging);
            if (vmResponse.size() != 0) {
                errors.addGlobalError(
                        "before.deleting.a.plan.please.delete.all.the.instance.associated.with.this.plan.and.try.again");
            }
            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
                compute.setIsActive(false);
                // update compute offering in ACS .
                cscomputeOffering.deleteComputeOffering(compute.getUuid(), "json");
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
     * @throws Exception
     */
    public HashMap<String, String> addOptionalValues(ComputeOffering compute) throws Exception {
        HashMap<String, String> computeMap = new HashMap<String, String>();

         CloudStackOptionalUtil.updateOptionalStringValue("tags", compute.getStorageTags(), computeMap);
         CloudStackOptionalUtil.updateOptionalStringValue("hosttags", compute.getStorageTags(), computeMap);
         CloudStackOptionalUtil.updateOptionalBooleanValue("limitcpuuse", compute.getCpuCapacity(), computeMap);
         CloudStackOptionalUtil.updateOptionalIntegerValue("cpuspeed", compute.getClockSpeed(), computeMap);
         CloudStackOptionalUtil.updateOptionalIntegerValue("cpunumber", compute.getNumberOfCores(), computeMap);
         CloudStackOptionalUtil.updateOptionalIntegerValue("memory", compute.getMemory(), computeMap);
         CloudStackOptionalUtil.updateOptionalIntegerValue("bytesreadrate", compute.getDiskBytesReadRate(), computeMap);
         CloudStackOptionalUtil.updateOptionalIntegerValue("byteswriterate", compute.getDiskBytesWriteRate(), computeMap);
         CloudStackOptionalUtil.updateOptionalIntegerValue("iopsreadrate", compute.getDiskIopsReadRate(), computeMap);
         CloudStackOptionalUtil.updateOptionalIntegerValue("iopswriterate", compute.getDiskIopsWriteRate(), computeMap);
         CloudStackOptionalUtil.updateOptionalBooleanValue("customizediops", compute.getCustomizedIops(), computeMap);
         CloudStackOptionalUtil.updateOptionalBooleanValue("customized", compute.getCustomized(), computeMap);
         CloudStackOptionalUtil.updateOptionalIntegerValue("miniops", compute.getMinIops(), computeMap);
         CloudStackOptionalUtil.updateOptionalIntegerValue("maxiops", compute.getMaxIops(), computeMap);
         CloudStackOptionalUtil.updateOptionalIntegerValue("networkrate", compute.getNetworkRate(), computeMap);
         CloudStackOptionalUtil.updateOptionalBooleanValue("offerha", compute.getIsHighAvailabilityEnabled(), computeMap);

        return computeMap;
    }

    @Override
    public List<ComputeOffering> findAllFromCSServer() throws Exception {

        List<ComputeOffering> computeOfferingList = new ArrayList<ComputeOffering>();
        HashMap<String, String> computeOfferingMap = new HashMap<String, String>();
        computeOfferingMap.put("listall", "true");
        // 1. Get the list of ComputeOffering from CS server using CS connector
        String response = cscomputeOffering.listComputeOfferings("json", computeOfferingMap);
        JSONArray computeOfferingListJSON = null;
        JSONObject responseObject = new JSONObject(response).getJSONObject("listserviceofferingsresponse");
        if (responseObject.has("serviceoffering")) {
            computeOfferingListJSON = responseObject.getJSONArray("serviceoffering");
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
        Errors errors = validator.rejectIfNullEntity("computes", compute);
        errors = validator.validateEntity(compute, errors);
        ComputeOffering computeOffering = computeRepo.findNameAndIsActive(compute.getName(), true);
        if (computeOffering != null && compute.getId() != computeOffering.getId()) {
            errors.addFieldError("name", "computeoffering.already.exist");
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
    public List<ComputeOffering> findByIsActive(Boolean isActive) throws Exception {
        return computeRepo.findByIsActive(true);
    }

}
