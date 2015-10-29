package ck.panda.service;

import java.util.HashMap;
import java.util.List;
import ck.panda.domain.entity.CloudStackConfiguration;
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Domain;
import ck.panda.domain.entity.User;
import ck.panda.domain.entity.VmInstance;
import ck.panda.domain.repository.jpa.DepartmentReposiory;
import ck.panda.domain.repository.jpa.DomainRepository;
import ck.panda.domain.repository.jpa.UserRepository;
import ck.panda.domain.repository.jpa.VirtualMachineRepository;
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
            optional.put("networkids", "484752d1-7218-4409-94d1-e7cf34ae0a68");
            optional.put("displayvm", "true");
            optional.put("name", vminstance.getName());
            optional.put("displayname", vminstance.getName());
            String instanceResponse = cloudStackInstanceService.deployVirtualMachine(vminstance.getComputeOffering().getUuid(),
            		vminstance.getTemplate().getUuid(),vminstance.getZone().getUuid(),"json",optional);
            JSONObject instance = new JSONObject(instanceResponse).getJSONObject("deployvirtualmachineresponse");
            vminstance.setUuid(instance.getString("id"));
            Domain domain = domainRepository.findOne(vminstance.getDomainId());
            User user = userRepository.findOne(vminstance.getInstanceOwnerId());
            Department department = departmentReposiory.findOne(vminstance.getDepartmentId());
            vminstance.setInstanceOwner(user);
            vminstance.setDomain(domain);
            vminstance.setDepartment(department);
            return virtualmachinerepository.save(vminstance);
        }
    }

    @Override
    public VmInstance update(VmInstance vminstance) throws Exception {
        return virtualmachinerepository.save(vminstance);
    }

    @Override
    public void delete(VmInstance vminstance) throws Exception {
    	virtualmachinerepository.delete(vminstance);
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


