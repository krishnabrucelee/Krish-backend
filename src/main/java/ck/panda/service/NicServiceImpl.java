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
import ck.panda.domain.entity.Network;
import ck.panda.domain.entity.Nic;
import ck.panda.domain.entity.VmInstance;
import ck.panda.domain.entity.VmIpaddress;
import ck.panda.domain.repository.jpa.NicRepository;
import ck.panda.domain.repository.jpa.VirtualMachineRepository;
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

    /** Virtual Machine repository reference. */
    @Autowired
    private VirtualMachineRepository virtualmachinerepository;

    /** Network Service for network reference . */
    @Autowired
    private NetworkService networkService;

    /** Reference of the convert entity service. */
    @Autowired
    private ConvertEntityService convertEntityService;

    /** Virtual Machine service for vminstance reference. */
    @Autowired
    private VmIpaddressService vmIpService;


    @Override
    @PreAuthorize("hasPermission(#nic.getSyncFlag(), 'ADD_NETWORK_TO_VM')")
    public Nic save(Nic nic) throws Exception {
        if (nic.getSyncFlag()) {
            Errors errors = validator.rejectIfNullEntity("nic", nic);
            errors = validator.validateEntity(nic, errors);
            configServer.setUserServer();
            Network network = convertEntityService.getNetworkById(nic.getNetworkId());
            HashMap<String, String> optional = new HashMap<String, String>();
            String createNicResponse = cloudStackInstanceService.addNicToVirtualMachine(network.getUuid(),
                    nic.getVmInstance().getUuid(), optional, "json");
            JSONObject addNicResponse = new JSONObject(createNicResponse)
                    .getJSONObject("addnictovirtualmachineresponse");
            if (addNicResponse.has("jobid")) {
                Thread.sleep(5000);
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
                if (jobresult.getString("jobstatus").equals("1")) {
                    this.assignNicTovM(nic.getVmInstance());
                } else if (jobresult.getString("jobstatus").equals("0")) {
                      this.assignNicTovM(nic.getVmInstance());
                  }
              }
              return nic;
          } else {
              System.out.println(nic.getVmIpAddress());
              return nicRepo.save(nic);
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
        String listNic = cloudStackNicService.listNics(optional, "json");
        JSONArray nicListJSON = new JSONObject(listNic).getJSONObject("listnicsresponse").getJSONArray("nic");
        for (int i = 0; i < nicListJSON.length(); i++) {
            Nic nic = findbyUUID(nicListJSON.getJSONObject(i).getString("id"));
            if (nic != null) {
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
            } else {
                nic = new Nic();
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
            Thread.sleep(6000);
            if (defaultNicResponse.has("jobid")) {
                String jobResponse = cloudStackInstanceService
                        .queryAsyncJobResult(defaultNicResponse.getString("jobid"), "json");
                JSONObject jobresult = new JSONObject(jobResponse).getJSONObject("queryasyncjobresultresponse");
                if (jobresult.getString("jobstatus").equals("0")) {
                    Thread.sleep(2000);
                }
                if (jobresult.getString("jobstatus").equals("1")) {
                    Nic nicI = nicRepo.findByInstanceIdAndIsDefault(nic.getVmInstanceId(), true);
                    nicI.setIsDefault(false);
                    nic.setIsDefault(true);
                } else {
                    JSONObject jobresponse = jobresult.getJSONObject("jobresult");
                    if (jobresult.getString("jobstatus").equals("2")) {
                        if (jobresponse.has("errorcode")) {
                            errors = this.validateEvent(errors, jobresponse.getString("errortext"));
                            throw new ApplicationException(errors);
                        }
                    }

                }
            }
        }
        System.out.println(nic.getVmIpAddress());
        return nicRepo.save(nic);
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
            Errors errors = validator.rejectIfNullEntity("nic", nic);
            HashMap<String, String> optional = new HashMap<String, String>();
            configServer.setUserServer();
            VmInstance instance = vmService.findById(nic.getVmInstanceId());
            String removeNicResponse = cloudStackInstanceService.removeNicFromVirtualMachine(nic.getUuid(),
                    instance.getUuid(), optional, "json");
            JSONObject deleteNicResponse = new JSONObject(removeNicResponse)
                    .getJSONObject("removenicfromvirtualmachineresponse");
            if (deleteNicResponse.has("jobid")) {
                Thread.sleep(5000);
                String jobResponse = cloudStackInstanceService.queryAsyncJobResult(deleteNicResponse.getString("jobid"),
                    "json");
                JSONObject jobresult = new JSONObject(jobResponse).getJSONObject("queryasyncjobresultresponse");
                if (jobresult.getString("jobstatus").equals("0")) {
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
    public List<Nic> findAllFromCSServer() throws Exception {
        List<VmInstance> vmInstanceList = virtualmachinerepository.findAllByIsActive(VmInstance.Status.Expunging);
        List<Nic> nicList = new ArrayList<Nic>();
        LOGGER.debug("VM size" + vmInstanceList.size());
        for (VmInstance vm : vmInstanceList) {
            HashMap<String, String> nicMap = new HashMap<String, String>();
            nicMap.put("virtualmachineid", vm.getUuid());
            // 1. Get the list of nics from CS server using CS connector
            String response = cloudStackNicService.listNics(nicMap, "json");
            JSONArray nicListJSON = new JSONObject(response).getJSONObject("listnicsresponse").getJSONArray("nic");
            // 2. Iterate the json list, convert the single json entity to nic
            for (int i = 0, size = nicListJSON.length(); i < size; i++) {
                 List<VmIpaddress> vmIpList = new ArrayList<VmIpaddress>();
                // 2.1 Call convert by passing JSONObject to nic entity and Add
                // the converted nic entity to list
                if (nicListJSON.getJSONObject(i).has("secondaryip")) {
                        JSONArray secondaryIpJSON = nicListJSON.getJSONObject(i).getJSONArray("secondaryip");
                        for (int j = 0, sizes = secondaryIpJSON.length(); j < sizes; j++) {
                            JSONObject json = (JSONObject)secondaryIpJSON.get(j);
                            VmIpaddress vmIp = VmIpaddress.convert(json);
                            if (json.getString("id") != null) {
                                if (vmIpService.findByUUID(json.getString("id")) != null){
                                    if (vmIp.getUuid().equals(vmIpService.findByUUID(json.getString("id")).getUuid())) {
                                    vmIpService.update(vmIpService.findByUUID(json.getString("id")));
                                }
                            } else{
                                VmIpaddress guestIp = vmIpService.save(vmIp);
                                vmIpList.add(guestIp);
                            }
                            }
                        }
                }
                Nic nic = Nic.convert(nicListJSON.getJSONObject(i));
                nic.setVmInstanceId(convertEntityService.getVmInstanceId(nic.getTransvmInstanceId()));
                nic.setNetworkId(convertEntityService.getNetworkId(nic.getTransNetworkId()));
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
    public Nic acquireSecondaryIP(Nic nic) throws Exception  {
         Errors errors = validator.rejectIfNullEntity("nic", nic);
         errors = validator.validateEntity(nic, errors);
         configServer.setUserServer();
         Nic nics = convertEntityService.getNicById(nic.getId());
         HashMap<String, String> nicMap = new HashMap<String, String>();
         nicMap.put("ipaddress", nic.getSecondaryIpAddress());
         String acquireIPResponse = cloudStackNicService.addIpToNic(nics.getUuid(), "json", nicMap);
         JSONObject csacquireIPResponseJSON = new JSONObject(acquireIPResponse)
                 .getJSONObject("addiptovmnicresponse");
         if (csacquireIPResponseJSON.has("errorcode")) {
             errors = this.validateEvent(errors, csacquireIPResponseJSON.getString("errortext"));
             throw new ApplicationException(errors);
         } else if (csacquireIPResponseJSON.has("jobid")) {
             String jobResponse = cloudStackNicService.AcquireIpJobResult(csacquireIPResponseJSON.getString("jobid"), "json");
             JSONObject jobresult = new JSONObject(jobResponse).getJSONObject("queryasyncjobresultresponse");
             if (jobresult.getString("jobstatus").equals("1")) {
                 VmIpaddress vm = new VmIpaddress();
                 vm.setUuid((String) csacquireIPResponseJSON.get("id"));
                 vm.setGuestIpAddress(((String) jobresult.get("ipaddress")));
                 vmIpService.save(vm);
            }
         }
        return nic;
    }

    @Override
    public Nic releaseSecondaryIP(Nic nic, Long vmIpaddressId)throws Exception {
         try {
             VmIpaddress vm = convertEntityService.getVmIpaddressById(vmIpaddressId);
             vm.setIsActive(false);
             configServer.setUserServer();
             String deleteResponse = cloudStackNicService.removeIpFromNic(vm.getUuid(), "json");
             JSONObject jobId = new JSONObject(deleteResponse).getJSONObject("removeipfromnicresponse");
             if (jobId.has("errorcode")) {
                 Errors errors = validator.sendGlobalError(jobId.getString("errortext"));
                 if (errors.hasErrors()) {
                     throw new BadCredentialsException(jobId.getString("errortext"));
                 }
             }
             if (jobId.has("jobid")) {
                 String jobResponse = cloudStackNicService.AcquireIpJobResult(jobId.getString("jobid"), "json");
                 JSONObject jobresults = new JSONObject(jobResponse).getJSONObject("queryasyncjobresultresponse");
             }
             } catch (BadCredentialsException e) {
                 throw new BadCredentialsException(e.getMessage());
         }
        return nic;
    }
    @Override
    public Nic findById(Long id) throws Exception {
        return nicRepo.findById(id);
    }
}
