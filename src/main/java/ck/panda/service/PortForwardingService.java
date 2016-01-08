/**
 *
 */
package ck.panda.service;

import org.springframework.stereotype.Service;
import ck.panda.domain.entity.PortForwarding;
import ck.panda.util.domain.CRUDService;

/**
 * Port Forwarding Service.
 *
 */
@Service
public interface PortForwardingService extends CRUDService<PortForwarding> {

}
