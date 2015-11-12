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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.ComputeOffering;
import ck.panda.domain.repository.jpa.ComputeOfferingRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackComputeOffering;
import ck.panda.util.ConfigUtil;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;

/**.
 * Compute Offering service aid user to create compute offers in Cloud Stack Server.
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
    private CloudStackComputeOffering computeOffer;

    @Override
    @PreAuthorize("hasAuthority('ROLE_DOMAIN_USER')")
    public ComputeOffering save(ComputeOffering compute) throws Exception {

        if (compute.getIsSyncFlag()) {
        Errors errors = validator.rejectIfNullEntity("compute", compute);
        errors = validator.validateEntity(compute, errors);

        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {

            //set server for maintain session with configuration values
              computeOffer.setServer(configServer.setServer(1L));
            String createComputeResponse = computeOffer.createComputeOffering(compute.getName(), compute.getDisplayText(),
                    "json", addOptionalValues(compute));
            JSONObject createComputeResponseJSON = new JSONObject(createComputeResponse).getJSONObject("createserviceofferingresponse")
                    .getJSONObject("serviceoffering");
            if (createComputeResponseJSON.has("errorcode")) {
                errors = this.validateEvent(errors, createComputeResponseJSON.getString("errortext"));
                throw new ApplicationException(errors);
        }
            System.out.println(createComputeResponseJSON);
            compute.setUuid((String) createComputeResponseJSON.get("id"));
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
        Errors errors = validator.rejectIfNullEntity("compute", compute);
        errors = validator.validateEntity(compute, errors);

        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
            HashMap<String, String> hs = new HashMap<String, String>();
            computeOffer.setServer(configServer.setServer(1L));
            String editComputeResponse = computeOffer.updateComputeOffering(compute.getUuid(),compute.getName(),compute.getDisplayText(),"json", hs);
            JSONObject domainListJSON = new JSONObject(editComputeResponse).getJSONObject("updateserviceofferingresponse")
                    .getJSONObject("serviceoffering");
        }
        return computeRepo.save(compute);
        } else {
             LOGGER.debug(compute.getUuid());
             return computeRepo.save(compute);
        }

    }

    @Override
    public void delete(ComputeOffering compute) throws Exception {
            computeRepo.delete(compute);
    }

    @Override
    public void delete(Long id) throws Exception {
        ComputeOffering compute = this.find(id);
        //set server for finding value in configuration
        computeOffer.setServer(configServer.setServer(1L));
        computeOffer.deleteComputeOffering(compute.getUuid(), "json");
        computeRepo.delete(id);
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
     * @param compute
     *            object for compute offering
     * @return optional values.
     */
    public HashMap<String, String> addOptionalValues(ComputeOffering compute) {
        HashMap<String, String> computeMap = new HashMap<String, String>();

        if (compute.getStorageTags() != null) {
            computeMap.put("tags", compute.getStorageTags().toString());
        }

        if (compute.getHostTags() != null) {
            computeMap.put("hosttags", compute.getHostTags().toString());
        }

        if (compute.getCpuCapacity() != null) {
            computeMap.put("limitcpuuse", compute.getCpuCapacity().toString());
        }

        if (compute.getClockSpeed() != null) {
            computeMap.put("cpuspeed",compute.getClockSpeed().toString());
        }

        if (compute.getNumberOfCores() != null) {
            computeMap.put("cpunumber", compute.getNumberOfCores().toString());
        }

        if (compute.getDiskBytesReadRate() != null) {
            computeMap.put("bytesreadrate", compute.getDiskBytesReadRate().toString());
        }

        if (compute.getDiskBytesWriteRate() != null) {
            computeMap.put("byteswriterate", compute.getDiskBytesWriteRate().toString());
        }

        if (compute.getDiskIopsReadRate() != null) {
            computeMap.put("iopsreadrate", compute.getDiskIopsReadRate().toString());
        }

        if (compute.getDiskIopsWriteRate() != null) {
            computeMap.put("iopswriterate", compute.getDiskIopsWriteRate().toString());
        }

        if (compute.getCustomizedIops() != null) {
            computeMap.put("customizediops", compute.getCustomizedIops().toString());
        }

        if (compute.getMinIops() != null) {
            computeMap.put("miniops", compute.getMinIops().toString());
        }

        if (compute.getMaxIops() != null) {
            computeMap.put("maxiops", compute.getMaxIops().toString());
        }

        if (compute.getNetworkRate() != null) {
            computeMap.put("networkrate", compute.getNetworkRate().toString());
        }

        if (compute.getDomain() != null) {
            computeMap.put("domain", compute.getDomain().getName());
        }

        if (compute.getIsHighAvailabilityEnabled() != null) {
            computeMap.put("offerha", compute.getIsHighAvailabilityEnabled().toString());
        }

        if (compute.getMemory() != null) {
            computeMap.put("memory", compute.getMemory().toString());
        }
        if (compute.getCustomized() != null) {
             computeMap.put("customized", compute.getCustomized().toString());
        }
        return computeMap;
    }

    @Override
    public List<ComputeOffering> findAllFromCSServer() throws Exception {

         List<ComputeOffering> computeOfferingList = new ArrayList<ComputeOffering>();
          HashMap<String, String> computeOfferingMap = new HashMap<String, String>();

          // 1. Get the list of ComputeOffering from CS server using CS connector
          String response = computeOffer.listComputeOfferings("json", computeOfferingMap);

          JSONArray computeOfferingListJSON = new JSONObject(response).getJSONObject("listserviceofferingsresponse")
                  .getJSONArray("serviceoffering");
          // 2. Iterate the json list, convert the single json entity to domain
          for (int i = 0, size = computeOfferingListJSON.length(); i < size; i++) {
              // 2.1 Call convert by passing JSONObject to ComputeOffering entity and Add
              // the converted ComputeOffering entity to list
              computeOfferingList.add(ComputeOffering.convert(computeOfferingListJSON.getJSONObject(i)));
          }
          return computeOfferingList;
      }

    }

