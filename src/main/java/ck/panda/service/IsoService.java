package ck.panda.service;

import java.util.List;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Iso;
import ck.panda.util.domain.CRUDService;

/**
 * Service class for Iso.
 * This service provides basic CRUD and essential api's for Iso related business actions.
 *
 */
@Service
public interface IsoService extends CRUDService<Iso> {

    /**
     * To get list of domains from cloudstack server.
     *
     * @return domain list from server
     * @throws Exception unhandled errors.
     */
    List<Iso> findAllFromCSServer() throws Exception;

    /**
     * Find iso by uuid.
     *
     * @param uuid of iso.
     * @return iso object.
     * @throws Exception unhandled errors.
     */
    Iso findbyUUID(String uuid) throws Exception;

    /**
     * Soft delete for iso.
     *
     * @param iso object
     * @return iso
     * @throws Exception unhandled errors.
     */
    Iso softDelete(Iso iso) throws Exception;
}
