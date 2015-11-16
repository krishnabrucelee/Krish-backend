package ck.panda.service;

import java.util.List;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.VmInstance;
import ck.panda.util.domain.CRUDService;

/**
 * Service class for Virtual Machine. This service provides basic CRUD and essential api's for Virtual Machine
 * related business actions.
 */
@Service
public interface VirtualMachineService extends CRUDService<VmInstance> {

    /**
     * Find vm instance by uuid.
     *
     * @param uuid instance uuid.
     * @return instance.
     */
    VmInstance findByUUID(String uuid);

    /**
     * VM related events are handled.
     *
     * @param vmId Virtual machine Id.
     * @param event event message.
     * @return instance.
     * @throws Exception if error occurs.
     */
    VmInstance vmEventHandle(String vmId, String event) throws Exception;

    /**
     * To get list of instance from cloudstack server.
     *
     * @return domain list from server
     * @throws Exception unhandled errors.
     */
    List<VmInstance> findAllFromCSServer() throws Exception;


}
