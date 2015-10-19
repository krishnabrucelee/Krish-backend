package ck.panda.service;

import org.springframework.stereotype.Service;

import ck.panda.domain.entity.Region;
import ck.panda.util.domain.CRUDService;

/**
 * Service class for Region.
 *
 * This service provides basic CRUD and essential api's for Region related
 * business actions.
 */
@Service
public interface RegionService extends CRUDService<Region> {

}
