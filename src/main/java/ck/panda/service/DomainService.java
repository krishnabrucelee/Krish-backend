package ck.panda.service;

import java.util.List;

import org.springframework.stereotype.Service;

import ck.panda.domain.entity.Domain;
import ck.panda.util.domain.CRUDService;

/**
 * Service class for Domain.
 * This service provides basic CRUD and essential api's for Domain related business actions.
 *
 */
@Service
public interface DomainService extends CRUDService<Domain> {

    /**
     * To get list of domains from cloudstack server.
     *
     * @return domain list from server
     * @throws Exception unhandled errors.
     */
    List<Domain> findAllFromCSServer() throws Exception;

    /**
     * Find domain by uuid.
     *
     * @param uuid uuid of domain.
     * @return domain object.
     * @throws Exception unhandled errors.
     */
    Domain findbyUUID(String uuid) throws Exception;

}

