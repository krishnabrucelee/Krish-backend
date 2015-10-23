package ck.panda.service;

import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Application;
import ck.panda.util.domain.CRUDService;

/**
 * Service class for Application.
 *
 * This service provides basic CRUD and essential api's for Application related
 * business actions.
 */
@Service
public interface ApplicationService extends CRUDService<Application> {

    /**
     * Method to find type of the application.
     *
     * @param type of the department
     * @return application type
     * @throws Exception if error occurs
     */
    Application findByType(String type) throws Exception;

}
