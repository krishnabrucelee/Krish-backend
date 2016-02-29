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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import ck.panda.constants.CloudStackConstants;
import ck.panda.domain.entity.Network;
import ck.panda.domain.entity.Nic;
import ck.panda.domain.entity.VmInstance;
import ck.panda.domain.entity.VmIpaddress;
import ck.panda.domain.entity.VmIpaddress.IpType;
import ck.panda.domain.repository.jpa.NicRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackInstanceService;
import ck.panda.util.CloudStackNicService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;

/**
 * Nic service implementation class.
 */
@Service
public class NicServiceImpl implements NicService {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DomainServiceImpl.class);

    /** Department repository reference. */
    @Autowired
    private NicRepository nicRepo;

    /** object(server) created for CloudStackServer. */
    @Autowired
    private ConfigUtil configServer;

    /** CloudStack connector reference for instance. */
    @Autowired
    private CloudStackInstanceService cloudStackInstanceService;

    /** CloudStack connector reference for instance. */
    @Autowired
    private CloudStackNicService cloudStackNicService;

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** Virtual Machine service for vminstance reference. */
    @Autowired
    private VirtualMachineService vmService;

    /** Network Service for network reference . */
    @Autowired
    private NetworkService networkService;

    /** Reference of the convert entity service. */
    @Autowired
    private ConvertEntityService convertEntityService;

    /** Virtual Machine service for vminstance reference. */
    @Autowired
    private VmIpaddressService vmIpService;

    /** Referrence of sync service.*/
    @Autowired
    private SyncService sync;

    /** Constant for add ip to nic. */
    public static final String CS_ADD_IPTONIC = "addiptovmnicresponse";

    /** Constant for remove ip to nic. */
    public static final String CS_REMOVE_IPTONIC = "removeipfromnicresponse";

    /** Constant for list of nic secondary ip. */
    public static final String CS_NIC_SECONDARYIP = "secondaryip";

    /** Constant for nic secondary ip. */
    public static final String CS_SECONDARYIP = "nicsecondaryip";

    /** Constant for nic object. */
    public static final String CS_NIC = "nic";

    /** Constant for nic uuid. */
    public static final String CS_NIC_UUID = "nicuuid";

    /** Constant for nic list. */
    public static final String CS_NIC_LIST = "listnicsresponse";

    /** Constant for removing nic response. */
    public static final String CS_REMOVE_NIC = "removenicfromvirtualmachineresponse";

    @Override
    @PreAuthorize("hasPermission(#nic.getSyncFlag(), 'ADD_NETWORK_TO_VM')")
    public Nic save(Nic nic) throws Exception {
        if (nic.getSyncFlag()) {
            Errors errors = validator.rejectIfNullEntity(CS_NIC , nic);
            errors = validator.validateEntity(nic, errors);
            configServer.setUserServer();
            Network network = convertEntityService.getNetworkById(nic.getNetworkId());
            HashMap<String, String> optional = new HashMap<String, String>();
            String createNicResponse = cloudStackInstanceService.addNicToVirtualMachine(network.getUuid(),
                    nic.getVmInstance().getUuid(), optional, "json");
            JSONObject addNicResponse = new JSONObject(createNicResponse)
                    .getJSONObject("addnictovirtualmachineresponse");
            //TODO: temporarily adding thread to get asynchronous job status. This will be removed when web socket is done.
            Thread.sleep(6000);
            if (addNicResponse.has("jobid")) {
                String jobResponse = cloudStackInstanceService.queryAsyncJobResult(addNicResponse.getString("jobid"),
                        "json");
                JSONObject jobresult = new JSONObject(jobResponse).getJSONObject("queryasyncjobresultresponse");
                if (jobresult.getString("jobstatus").equals("2")) {
                    JSONObject jobresponse = jobresult.getJSONObject("jobresult");
                    if (jobresponse.has("errorcode")) {
                        errors = this.validateEvent(errors, jobresponse.getString("errortext"));
                        throw new ApplicationException(errors);
                    }
                }
                if (jobresult.getString(CloudStackConstants.CS_JOB_STATUS).equals(CloudStackConstants.SUCCEEDED_JOB_STATUS)) {
                    this.assignNicTovM(nic.getVmInstance());
                    // to update acquired ipaddress while attaching a network in our DB.
                    sync.syncIpAddress();
                } else if (jobresult.getString(CloudStackConstants.CS_JOB_STATUS).equals(CloudStackConstants.PROGRESS_JOB_STATUS)) {
                     // to update acquired ipaddress while attaching a network in our DB.
                      this.assignNicTovM(nic.getVmInstance());
                      sync.syncIpAddress();
                 }
              }
              return nic;
          }else {
              nic = nicRepo.save(nic);
              if (nic.getVmIpAddress() != null) {
                  updateNicToVmIpaddress(nic);
              }
              return nic;
          }
    }

    /**
     * Update Nic id in Vm Ip address.
     *
     * @param nic id in Secondary Ip address or vm Ip address.
     * @throws Exception if error occurs.
     */
    private void updateNicToVmIpaddress(Nic nic) throws Exception {
        //Iterating vm ipaddress in Nic to get nic id, vm instance and primary ip address.
        for (VmIpaddress vmIp :  nic.getVmIpAddress()) {
            vmIp.setNicId(nic.getId());
            vmIp.setVmInstanceId(convertEntityService.getNic(nic.getUuid()).getVmInstanceId());
            vmIpService.update(vmIp);
        }
    }

    /**
     * Assign nic to given instance.
     *
     * @param vmInstance instance object.
     * @throws Exception unhandled exception.
     */
    private void assignNicTovM(VmInstance vmInstance) throws Exception {
        HashMap<String, String> optional = new HashMap<String, String>();
        optional.put("virtualmachineid", vmInstance.getUuid());
        configServer.setServer(1L);
        String listNic = cloudStackNicService.listNics(optional, "json");
        JSONArray nicListJSON = new JSONObject(listNic).getJSONObject("listnicsresponse").getJSONArray("nic");
        for (int i = 0; i < nicListJSON.length(); i++) {
            Nic nic = findbyUUID(nicListJSON.getJSONObject(i).getString("id"));
            if (nic == null) {
                 nic = new Nic();
            }
            nic.setUuid(nicListJSON.getJSONObject(i).getString("id"));
            nic.setVmInstanceId(
                        vmService.findByUUID(nicListJSON.getJSONObject(i).getString("virtualmachineid")).getId());
            nic.setNetworkId(
                        networkService.findByUUID(nicListJSON.getJSONObject(i).getString("networkid")).getId());
            nic.setNetMask(nicListJSON.getJSONObject(i).getString("netmask"));
            nic.setGateway(nicListJSON.getJSONObject(i).getString("gateway"));
            nic.setIpAddress(nicListJSON.getJSONObject(i).getString("ipaddress"));
            nic.setIsDefault(nicListJSON.getJSONObject(i).getBoolean("isdefault"));
            nic.setIsActive(true);
            if (nicRepo.findByUUID(nic.getUuid()) == null) {
                nicRepo.save(nic);
            }
              }
            }



    /**
     * Check the nic CS error handling.
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
    @PreAuthorize("hasPermission(#nic.getSyncFlag(), 'UPDATE_NETWORK_TO_VM')")
    public Nic update(Nic nic) throws Exception {
        if (nic.getSyncFlag()) {
            Errors errors = validator.rejectIfNullEntity("nic", nic);
            errors = validator.validateEntity(nic, errors);
            configServer.setUserServer();
            HashMap<String, String> optional = new HashMap<String, String>();
            VmInstance instance = vmService.findById(nic.getVmInstanceId());
            String updateNicResponse = cloudStackInstanceService.updateDefaultNicForVirtualMachine(nic.getUuid(),
                    instance.getUuid(), "json", optional);
            JSONObject defaultNicResponse = new JSONObject(updateNicResponse)
                    .getJSONObject("updatedefaultnicforvirtualmachineresponse");
            //TODO: temporarily adding thread to get asynchronous job status. This will be removed when web socket is done.
            Thread.sleep(6000);
            if (defaultNicResponse.has("jobid")) {
                String jobResponse = cloudStackInstanceService
                        .queryAsyncJobResult(defaultNicResponse.getString("jobid"), "json");
                JSONObject jobresult = new JSONObject(jobResponse).getJSONObject("queryasyncjobresultresponse");
                    if (jobresult.getString("jobstatus").equals("1")) {
                    Nic nicDefault = nicRepo.findByInstanceIdAndIsDefault(nic.getVmInstanceId(), true);
                    nicDefault.setIsDefault(false);
                    //JSONObject jobresponse = jobresult.getJSONObject("jobresult");
                    nic.setIsDefault(true);
                    } else {
                        if (jobresult.getString("jobstatus").equals("2")) {
                            if (jobresult.has("errorcode")) {
                            errors = this.validateEvent(errors, jobresult.getString("errortext"));
                            throw new ApplicationException(errors);
                        }
                    }

                }
            }
        }
        if (nic.getVmIpAddress() != null) {
            updateNicToVmIpaddress(nic);
        }
        nic = nicRepo.save(nic);
        return nic;
    }

    @Override
    public void delete(Nic nic) throws Exception {
        nicRepo.delete(nic);
    }

    @Override
    public void delete(Long id) throws Exception {
        nicRepo.delete(id);
    }

    @Override
    public Nic find(Long id) throws Exception {
        Nic nic = nicRepo.findOne(id);
        return nic;
    }

    @Override
    public Page<Nic> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return nicRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<Nic> findAll() throws Exception {
        return (List<Nic>) nicRepo.findAll();
    }

    @Override
    public Nic findbyUUID(String uuid) throws Exception {
        return nicRepo.findByUUID(uuid);
    }

    @Override
    @PreAuthorize("hasPermission(#nic.getSyncFlag(), 'DELETE_NETWORK_TO_VM')")
    public Nic softDelete(Nic nic) throws Exception {
        nic.setIsActive(false);
        if (nic.getSyncFlag()) {
            Errors errors = validator.rejectIfNullEntity(CS_NIC, nic);
            HashMap<String, String> optional = new HashMap<String, String>();
            configServer.setUserServer();
            VmInstance instance = vmService.findById(nic.getVmInstanceId());
            String removeNicResponse = cloudStackInstanceService.removeNicFromVirtualMachine(nic.getUuid(),
                    instance.getUuid(), optional, CloudStackConstants.JSON);
            JSONObject deleteNicResponse = new JSONObject(removeNicResponse)
                    .getJSONObject(CS_REMOVE_NIC);
            //TODO: temporarily adding thread to get asynchronous job status. This will be removed when web socket is done.
            Thread.sleep(6000);
            if (deleteNicResponse.has(CloudStackConstants.CS_JOB_ID)) {
                String jobResponse = cloudStackInstanceService.queryAsyncJobResult(deleteNicResponse.getString(CloudStackConstants.CS_JOB_ID),
                        CloudStackConstants.JSON);
                JSONObject queryasyncjobresult = new JSONObject(jobResponse).getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE);
                if (queryasyncjobresult.getString(CloudStackConstants.CS_JOB_STATUS).equals(CloudStackConstants.ERROR_JOB_STATUS)) {
                    JSONObject jobResponseResult = queryasyncjobresult.getJSONObject(CloudStackConstants.CS_JOB_RESULT);
                    if (jobResponseResult.has(CloudStackConstants.CS_ERROR_CODE)) {
                            errors = this.validateEvent(errors, jobResponseResult.getString(CloudStackConstants.CS_ERROR_TEXT));
                            throw new ApplicationException(errors);
                    }
              }
                if (queryasyncjobresult.getString(CloudStackConstants.CS_JOB_STATUS).equals(CloudStackConstants.PROGRESS_JOB_STATUS)) {
                    nic.setIsActive(false);
                }

            }
        }
        return nicRepo.save(nic);
    }

    @Override
    public List<Nic> findByInstance(Long nic) throws Exception {
        return nicRepo.findByInstanceAndIsActive(nic, true);
    }

    @Override
    public List<VmIpaddress> findByVMInstance(Long nic) throws Exception {
        return vmIpService.findByVMInstance(nic);
    }

    @Override
    public List<Nic> findAllFromCSServer() throws Exception {
        List<VmInstance> vmInstanceList = vmService.findAllByExceptStatus(VmInstance.Status.EXPUNGING);
        List<Nic> nicList = new ArrayList<Nic>();
        LOGGER.debug("VM size" + vmInstanceList.size());
        for (VmInstance vm : vmInstanceList) {
            HashMap<String, String> nicMap = new HashMap<String, String>();
            // Set virtual machine id to know nic belongs to which vm instance.
            nicMap.put(CloudStackConstants.CS_VIRTUAL_MACHINE_ID, vm.getUuid());
            configServer.setServer(1L);
            // 1. Get the list of nics from CS server using CS connector
            String response = cloudStackNicService.listNics(nicMap, CloudStackConstants.JSON);
            JSONArray nicListJSON = new JSONObject(response).getJSONObject(CS_NIC_LIST).getJSONArray(CS_NIC);
            // 2. Iterate the json list, convert the single json entity to nic
            for (int i = 0, size = nicListJSON.length(); i < size; i++) {
                 List<VmIpaddress> vmIpList = new ArrayList<VmIpaddress>();
                 if (nicListJSON.getJSONObject(i).has("ipaddress")) {
                     VmIpaddress vms = new VmIpaddress();
                      vms.setGuestIpAddress(nicListJSON.getJSONObject(i).getString("ipaddress"));
                      vms.setIpType(IpType.primaryIpAddress);
                      vms.setUuid(nicListJSON.getJSONObject(i).getString("id"));
                      vms.setIsActive(true);
                      vms.setSyncFlag(false);
                      if (vmIpService.findByUUID(vms.getUuid()) == null) {
                          vmIpList.add(vmIpService.save(vms));
                      } else {
                          VmIpaddress vmObject = vmIpService.findByUUID(vms.getUuid());
                          vmObject.setUuid(vms.getUuid());
                          vmObject.setGuestIpAddress(vms.getGuestIpAddress());
                          vmObject.setIpType(vms.getIpType());
                          vmObject.setTransNicId(vms.getTransNicId());
                          vmObject.setIsActive(true);
                          vmObject.setSyncFlag(false);
                          vmIpList.add(vmIpService.save(vmObject));
                      }
                 }
                // 2.1 Call convert by passing JSONObject to nic entity and Add
                // the converted nic entity to list
                 Nic nic = Nic.convert(nicListJSON.getJSONObject(i));
                 nic.setVmInstanceId(convertEntityService.getVmInstanceId(nic.getTransvmInstanceId()));
                 nic.setNetworkId(convertEntityService.getNetworkId(nic.getTransNetworkId()));
                 // Get secondary ip address from nic.
                  if (nicListJSON.getJSONObject(i).has(CS_NIC_SECONDARYIP)) {
                        // Get JSON array secondary ip.
                        JSONArray secondaryIpJSON = nicListJSON.getJSONObject(i).getJSONArray(CS_NIC_SECONDARYIP);
                        for (int j = 0, sizes = secondaryIpJSON.length(); j < sizes; j++) {
                            JSONObject json = (JSONObject)secondaryIpJSON.get(j);
                            json.put(CS_NIC_UUID, nicListJSON.getJSONObject(i).getString(CloudStackConstants.CS_ID));
                            // 2.2  Call convert by passing JSONObject to Vmipaddress entity and Add
                            // the converted vm ipaddress entity to list
                            VmIpaddress vmIp = VmIpaddress.convert(json);
                            if (vmIpService.findByUUID(vmIp.getUuid()) == null) {
                                VmIpaddress persistVmIp = vmIpService.save(vmIp);
                                vmIpList.add(persistVmIp);
                            } else {
                                VmIpaddress vmObject = vmIpService.findByUUID(vmIp.getUuid());
                                vmObject.setUuid(vmIp.getUuid());
                                vmObject.setGuestIpAddress(vmIp.getGuestIpAddress());
                                vmObject.setIpType(vmIp.getIpType());
                                vmObject.setTransNicId(vmIp.getTransNicId());
                                vmObject.setIsActive(vmIp.getIsActive());
                                vmObject.setSyncFlag(vmIp.getSyncFlag());
                                vmIpService.update(vmObject);
                            }

                        }
                }
                if (vmIpList.size() > 0) {
                    nic.setVmIpAddress(vmIpList);
                }
                nicList.add(nic);
            }
        }
        return nicList;
    }

    @Override
    public Nic updatebyResourceState(Nic nic) throws Exception {
        return nicRepo.save(nic);
    }

    /**
     * Find all the nic with pagination.
     *
     * @throws Exception application errors.
     * @param pagingAndSorting do pagination with sorting for domains.
     * @return list of domains.
     */
    public Page<Nic> findAllByActive(PagingAndSorting pagingAndSorting) throws Exception {
        return nicRepo.findAllByIsActive(pagingAndSorting.toPageRequest(), true);
    }

    @Override
    @PreAuthorize("hasPermission(#nic.getSyncFlag(), 'ACQUIRE_SECONDARY_IP_ADDRESS')")
    public Nic acquireSecondaryIP(Nic nic) throws Exception  {
         Errors errors = validator.rejectIfNullEntity(CS_NIC, nic);
         errors = validator.validateEntity(nic, errors);
         configServer.setUserServer();
         Nic nics = convertEntityService.getNicById(nic.getId());
         HashMap<String, String> nicMap = new HashMap<String, String>();
         nicMap.put(CloudStackConstants.CS_IP_ADDRESS, nic.getSecondaryIpAddress());
         String acquireIPResponse = cloudStackNicService.addIpToNic(nics.getUuid(), CloudStackConstants.JSON, nicMap);
         JSONObject csacquireIPResponseJSON = new JSONObject(acquireIPResponse)
                 .getJSONObject(CS_ADD_IPTONIC);
         if (csacquireIPResponseJSON.has(CloudStackConstants.CS_ERROR_CODE)) {
             errors = this.validateEvent(errors, csacquireIPResponseJSON.getString(CloudStackConstants.CS_ERROR_TEXT));
             throw new ApplicationException(errors);
         } else if (csacquireIPResponseJSON.has(CloudStackConstants.CS_JOB_ID)) {
             Thread.sleep(5000);
             String jobResponse = cloudStackNicService.AcquireIpJobResult(csacquireIPResponseJSON.getString(CloudStackConstants.CS_JOB_ID), CloudStackConstants.JSON);
             JSONObject jobresult = new JSONObject(jobResponse).getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE).getJSONObject(CloudStackConstants.CS_JOB_RESULT);
             JSONObject secondaryIP = jobresult.getJSONObject(CS_SECONDARYIP);
                 VmIpaddress vm = new VmIpaddress();
                 vm.setUuid((String) secondaryIP.get(CloudStackConstants.CS_ID));
                 vm.setGuestIpAddress(((String) secondaryIP.get(CloudStackConstants.CS_IP_ADDRESS)));
                 vm.setNicId(convertEntityService.getNic(secondaryIP.getString(CloudStackConstants.CS_NIC_ID)).getId());
                 vm.setVmInstanceId(convertEntityService.getNic(secondaryIP.getString(CloudStackConstants.CS_NIC_ID)).getVmInstanceId());
                 vm.setIsActive(true);
                 vmIpService.save(vm);
         }
        return nic;
    }

    @Override
    public Nic releaseSecondaryIP(Nic nic, Long vmIpaddressId)throws Exception {
         try {
             // Get vm ipaddress object by id.
             VmIpaddress vm = convertEntityService.getVmIpaddressById(vmIpaddressId);
             // Set api key, secret key and ACS URL for individual user.
             configServer.setUserServer();
             // Establishing connectivity with ACS to release secondary Ip from Nic.
             String deleteResponse = cloudStackNicService.removeIpFromNic(vm.getUuid(),CloudStackConstants.JSON);
             vm.setIsActive(false);
             // Converting Json String to Json object.
             JSONObject jobResponse = new JSONObject(deleteResponse).getJSONObject(CS_REMOVE_IPTONIC);
             if (jobResponse.has(CloudStackConstants.CS_ERROR_CODE)) {
                 Errors errors = validator.sendGlobalError(jobResponse.getString(CloudStackConstants.CS_ERROR_TEXT));
                 if (errors.hasErrors()) {
                     throw new BadCredentialsException(jobResponse.getString(CloudStackConstants.CS_ERROR_TEXT));
                 }
             }
             if (jobResponse.has(CloudStackConstants.CS_JOB_ID)) {
                 String jobResponseResult = cloudStackNicService.AcquireIpJobResult(jobResponse.getString(CloudStackConstants.CS_JOB_ID), CloudStackConstants.JSON);
                 JSONObject queryJobresults = new JSONObject(jobResponseResult).getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE);
             }
             vmIpService.save(vm);
         } catch (BadCredentialsException e) {
                 throw new BadCredentialsException(e.getMessage());
         }
         return nic;
    }

    @Override
    public List<Nic> findAllByNetworkAndIsActive(Long networkId, Boolean isActive) throws Exception {
        return (List<Nic>) nicRepo.findByNetworkIdAndIsActive(networkId, true);
    }

    @Override
    public Nic findById(Long id) throws Exception {
        return nicRepo.findById(id);
    }
}
