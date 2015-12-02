package ck.panda.service;

import java.util.List;

import org.springframework.stereotype.Service;

import ck.panda.domain.entity.Domain;
import ck.panda.domain.entity.Zone;
import ck.panda.util.domain.CRUDService;

/**
 * Service interface for Zone entity.
 *
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

    /**
     * To get zone from cloudstack server.
     *
     * @param uuid uuid of zone.
     * @return zone from server
     * @throws Exception unhandled errors.
     */
    Zone findByUUID(String uuid) throws Exception;

    /**
     * Soft delete for zone.
     *
     * @param zone object
     * @return zone
     * @throws Exception unhandled errors.
     */
	Zone softDelete(Zone zone) throws Exception;

}
