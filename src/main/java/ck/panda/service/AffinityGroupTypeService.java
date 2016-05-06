package ck.panda.service;

import java.util.List;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.AffinityGroupType;
import ck.panda.util.domain.CRUDService;

/**
 * Service interface for affinity group type. This service provides basic list and save business actions.
 */
@Service
public interface AffinityGroupTypeService extends CRUDService<AffinityGroupType> {

    /**
     * To get list of affinity group type from cloudstack server.
     *
     * @return affinity group type list from server
     * @throws Exception unhandled errors.
     */
    List<AffinityGroupType> findAllFromCSServer() throws Exception;

    /**
     * To get the affinity group by type.
     *
     * @param type affinity group type
     * @return affinity group type
     * @throws Exception unhandled errors.
     */
    AffinityGroupType findByType(String type) throws Exception;

}
