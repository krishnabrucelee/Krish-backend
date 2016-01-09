package ck.panda.service;

import java.util.List;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.PortForwarding;
import ck.panda.util.domain.CRUDService;

/**
 * Port Forwarding Service.
 *
 */
@Service
public interface PortForwardingService extends CRUDService<PortForwarding> {

    /**
     * To get list of PortForwarding from cloudstack server.
     *
     * @return PortForwarding list from server
     * @throws Exception unhandled errors.
     */
    List<PortForwarding> findAllFromCSServer() throws Exception;

    /**
     * Soft delete for PortForwarding.
     *
     * @param portForwarding object
     * @return portForwarding
     * @throws Exception unhandled errors.
     */
    PortForwarding softDelete(PortForwarding portForwarding) throws Exception;

    /**
     * To get PortForwarding by uuid.
     *
     * @param uuid uuid of PortForwarding.
     * @return PortForwarding.
     * @throws Exception unhandled errors.
     */
    PortForwarding findByUUID(String uuid);

    /**
     * List by instance attached to portForwarding.
     *
     * @param portForwarding PortForwarding
     * @return PortForwarding from instance.
     * @throws Exception exception
     */
    List<PortForwarding> findByInstance(Long portForwarding) throws Exception;

}
