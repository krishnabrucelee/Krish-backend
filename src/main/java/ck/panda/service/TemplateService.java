package ck.panda.service;

import java.util.List;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Template;
import ck.panda.util.domain.CRUDService;

/**
 * Service interface for Template entity.
 *
 * This service provides basic CRUD and essential api's for Template related business actions.
 */
@Service
public interface TemplateService extends CRUDService<Template> {

    /**
     * To get list of template from cloudstack server.
     *
     * @return template list from server
     * @throws Exception unhandled errors.
     */
    List<Template> findAllFromCSServer() throws Exception;
}