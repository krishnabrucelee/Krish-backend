/**
 *
 */
package ck.panda.service;

import java.util.List;

import org.springframework.stereotype.Service;

import ck.panda.domain.entity.ResourceLimitDepartment;
import ck.panda.util.domain.CRUDService;

/**
 * @author Assistanz
 *
 */
@Service
public interface ResourceLimitDepartmentService extends CRUDService<ResourceLimitDepartment> {

       /**
       * To get list of Volume from cloudstack server.
       *
       * @param domainId domain id.
       * @param department department name.
       * @return ResourceLimit list from server
       * @throws Exception unhandled errors.
       */
      List<ResourceLimitDepartment> findAllFromCSServerDepartment(Long domainId, String department) throws Exception;
}
