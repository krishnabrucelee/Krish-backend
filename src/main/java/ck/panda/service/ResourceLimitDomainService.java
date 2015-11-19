/**
 *
 */
package ck.panda.service;

import java.util.List;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.ResourceLimitDomain;
import ck.panda.util.domain.CRUDService;

/**
 * Resource Limit Service.
 *
 */
@Service
public interface ResourceLimitDomainService extends CRUDService<ResourceLimitDomain> {

    /**
    * To get list of Volume from cloudstack server.
    *
    * @param domainId domain id.
    * @return ResourceLimit list from server
    * @throws Exception unhandled errors.
    */
   List<ResourceLimitDomain> findAllFromCSServerDomain(String domainId) throws Exception;

}
