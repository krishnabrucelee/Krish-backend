package ck.panda.service;

import java.util.List;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.VPNCustomerGateway;
import ck.panda.util.domain.CRUDService;

/**
 * Service interface for vpn customer gateway entity.
 *
 */
@Service
public interface VPNCustomerGatewayService extends CRUDService<VPNCustomerGateway> {

    /**
     * To get list of vpn customer gateway from cloudstack server.
     *
     * @return vpn customer gateway list from server
     * @throws Exception unhandled errors.
     */
    List<VPNCustomerGateway> findAllFromCSServer() throws Exception;
}
