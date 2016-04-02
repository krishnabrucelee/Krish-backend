package ck.panda.service;

import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Organization;
import ck.panda.util.domain.CRUDService;

/**
 * Service interface for organization entity.
 *
 */
@Service
public interface OrganizationService extends CRUDService<Organization> {

     /**
     * Find Organization by isActive.
     *
     * @param isActive status of the compute offer
     * @return list Organization.
     * @throws Exception if error occurs.
     */
    Organization findByIsActive(Boolean isActive) throws Exception;

}
