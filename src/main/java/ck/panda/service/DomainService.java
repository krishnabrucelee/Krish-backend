package ck.panda.service;

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

}

