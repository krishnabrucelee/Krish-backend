package ck.panda.service;

import java.util.HashMap;
import java.util.List;
import ck.panda.domain.entity.CloudStackConfiguration;
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Domain;
import ck.panda.domain.entity.GuestNetwork;
import ck.panda.domain.entity.User;
import ck.panda.domain.entity.VmInstance;
import ck.panda.domain.repository.jpa.DepartmentReposiory;
import ck.panda.domain.repository.jpa.DomainRepository;
import ck.panda.domain.repository.jpa.GuestNetworkRepository;
import ck.panda.domain.repository.jpa.UserRepository;
import ck.panda.domain.repository.jpa.VirtualMachineRepository;
import ck.panda.rabbitmq.util.EventTypes;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackInstanceService;
import ck.panda.util.CloudStackServer;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 *
 * Virtual Machine creation, update, start, reboot, stop all operations are handled by this controller.
 *
 */
@Service
public class VirtualMachineServiceImpl implements VirtualMachineService {

	/** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(VirtualMachineServiceImpl.class);

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** Virtual Machine repository reference. */
    @Autowired
    VirtualMachineRepository virtualmachinerepository;

    /** Domain repository reference. */
    @Autowired
    DomainRepository domainRepository;

    /** Guest network repository reference. */
    @Autowired
    GuestNetworkRepository gNetworkRepository;

    /** Department repository reference. */
    @Autowired
    DepartmentReposiory departmentReposiory;

    /** User repository reference. */
    @Autowired
    UserRepository userRepository;

    /** CloudStack connector reference for instance. */
    @Autowired
    CloudStackInstanceService cloudStackInstanceService;

    /** CloudStack connector. */
    @Autowired
    private CloudStackServer server;

    /** CloudStack vm event. */
    private String VmEvent;

    /** CloudStack configuration . */
    @Autowired
    private CloudStackConfigurationService cloudConfigService;

    @Override
    public VmInstance save(VmInstance vminstance) throws Exception {
        Errors errors = validator.rejectIfNullEntity("vminstance", vminstance);
        errors = validator.validateEntity(vminstance, errors);
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
            HashMap<String, String> optional = new HashMap<String, String>();
            optional.put("displayvm", vminstance.getName());
            optional.put("name", vminstance.getName());
            CloudStackConfiguration cloudConfig = cloudConfigService.find(1L);
            server.setServer(cloudConfig.getApiURL(), cloudConfig.getSecretKey(), cloudConfig.getApiKey());
            cloudStackInstanceService.setServer(server);
            System.out.println( vminstance.getNetworkUuid());
            System.out.println( vminstance.getNetworkOffering());
            optional.put("networkids", vminstance.getNetworkUuid());
            optional.put("displayvm", "true");
            optional.put("name", vminstance.getName());
            optional.put("displayname", vminstance.getName());
            try {
            String instanceResponse = cloudStackInstanceService.deployVirtualMachine(vminstance.getComputeOffering().getUuid(),
            		vminstance.getTemplate().getUuid(),vminstance.getZone().getUuid(),"json",optional);
            JSONObject instance = new JSONObject(instanceResponse).getJSONObject("deployvirtualmachineresponse");
            vminstance.setUuid(instance.getString("id"));
            if(instance.has("id")){
            	vminstance.setStatus(EventTypes.EVENT_STATUS_RUNNING);
            }
            else {
            	vminstance.setStatus(EventTypes.EVENT_ERROR);
            }
            }
            catch(Exception e){
            	LOGGER.error("ERROR AT VM Creation", e);
            }
            Domain domain = domainRepository.findOne(vminstance.getDomainId());
            User user = userRepository.findOne(vminstance.getInstanceOwnerId());
            Department department = departmentReposiory.findOne(vminstance.getDepartmentId());
            GuestNetwork guestNetwork = gNetworkRepository.findByUUID(vminstance.getNetworkUuid());
            vminstance.setInstanceOwner(user);
            vminstance.setDomain(domain);
            vminstance.setDepartment(department);
            vminstance.setNetwork(guestNetwork);
            return virtualmachinerepository.save(vminstance);
        }
    }

    @Override
    public VmInstance update(VmInstance vminstance) throws Exception {
    	Errors errors = validator.rejectIfNullEntity("vminstance", vminstance);
        errors = validator.validateEntity(vminstance, errors);
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
             return virtualmachinerepository.save(vminstance);
    }
    }

    @Override
    public void delete(VmInstance vminstance) throws Exception {
    	virtualmachinerepository.delete(vminstance);
    }

 	@Override
	public void vmEventHandle(String vmId, String event) throws Exception {
 		VmInstance vminstance = virtualmachinerepository.findByUUID(vmId);
 		HashMap<String, String> optional = new HashMap<String, String>();
 		 if(EventTypes.EVENT_VM_START.equals(event)){
 			String instanceResponse;
			try {

	            CloudStackConfiguration cloudConfig = cloudConfigService.find(1L);
	            server.setServer(cloudConfig.getApiURL(), cloudConfig.getSecretKey(), cloudConfig.getApiKey());
	            cloudStackInstanceService.setServer(server);
				instanceResponse = cloudStackInstanceService.startVirtualMachine(vminstance.getUuid(),"json");
				 JSONObject instance = new JSONObject(instanceResponse).getJSONObject("startvirtualmachineresponse");
		         System.out.println(instance);
				 if(instance.has("jobid")){
		            	vminstance.setStatus(EventTypes.EVENT_STATUS_RUNNING);
		            	virtualmachinerepository.save(vminstance);
		            }
			} catch (Exception e) {
				LOGGER.error("ERROR AT VM START", e);
			}

    	 }
    	 if(EventTypes.EVENT_VM_STOP.equals(event)){
    		 String instanceResponse;
 			try {

 	            CloudStackConfiguration cloudConfig = cloudConfigService.find(1L);
 	            server.setServer(cloudConfig.getApiURL(), cloudConfig.getSecretKey(), cloudConfig.getApiKey());
 	            cloudStackInstanceService.setServer(server);
 				instanceResponse = cloudStackInstanceService.stopVirtualMachine(vminstance.getUuid(),"json",optional);
 				 JSONObject instance = new JSONObject(instanceResponse).getJSONObject("stopvirtualmachineresponse");
 				   System.out.println(instance);
 				 if(instance.has("jobid")){
 		            	vminstance.setStatus(EventTypes.EVENT_STATUS_STOPPED);
 		            	virtualmachinerepository.save(vminstance);
 		            }
 			} catch (Exception e) {
 				LOGGER.error("ERROR AT VM STOP", e);
 			}

    	 }
    	 if(EventTypes.EVENT_VM_REBOOT.equals(event)){
    		 String instanceResponse;
 			try {

 	            CloudStackConfiguration cloudConfig = cloudConfigService.find(1L);
 	            server.setServer(cloudConfig.getApiURL(), cloudConfig.getSecretKey(), cloudConfig.getApiKey());
 	            cloudStackInstanceService.setServer(server);
 				instanceResponse = cloudStackInstanceService.rebootVirtualMachine(vminstance.getUuid(),"json");
 				 JSONObject instance = new JSONObject(instanceResponse).getJSONObject("rebootvirtualmachineresponse");
 				   System.out.println(instance);
 				 if(instance.has("jobid")){
 		            	vminstance.setStatus(EventTypes.EVENT_STATUS_RUNNING);
 		            	virtualmachinerepository.save(vminstance);
 		            }
 			} catch (Exception e) {
 				LOGGER.error("ERROR AT VM REBOOT", e);
 			}

    	 }
	}

	@Override
    public void delete(Long id) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public VmInstance find(Long id) throws Exception {
        // TODO Auto-generated method stub
        return virtualmachinerepository.findOne(id);
    }

    @Override
    public Page<VmInstance> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

   	@Override
	public List<VmInstance> findAll() throws Exception {
		// TODO Auto-generated method stub
		return (List<VmInstance>) virtualmachinerepository.findAll();
	}

	@Override
	public VmInstance findByUUID(String uuid) {
		return virtualmachinerepository.findByUUID(uuid);
	}



}


