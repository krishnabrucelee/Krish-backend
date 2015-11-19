/**
 *
 */
package ck.panda.service;

import java.util.List;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.ResourceLimitProject;
import ck.panda.util.domain.CRUDService;

/**
 * @author Assistanz
 *
 */
@Service
public interface ResourceLimitProjectService extends CRUDService<ResourceLimitProject> {

      /**
      * To get list of Volume from cloudstack server.
      *
      * @param projectId project id.
      * @return ResourceLimit list from server
      * @throws Exception unhandled errors.
      */
     List<ResourceLimitProject> findAllFromCSServerProject(String projectId) throws Exception;
}
