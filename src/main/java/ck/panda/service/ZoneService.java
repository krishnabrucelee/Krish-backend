package ck.panda.service;

import java.util.List;

import org.springframework.stereotype.Service;

import ck.panda.domain.entity.Zone;
import ck.panda.util.domain.CRUDService;

/**
 * Service interface for Zone entity.
 *s
 */
@Service
public interface ZoneService extends CRUDService<Zone> {

    /**
     * To get list of zones from cloudstack server.
     *
     * @return zone list from server
     * @throws Exception unhandled errors.
     */
    List<Zone> findAllFromCSServer() throws Exception;
}
