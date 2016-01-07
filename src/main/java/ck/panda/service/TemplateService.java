package ck.panda.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Template;
import ck.panda.util.domain.CRUDService;
import ck.panda.util.domain.vo.PagingAndSorting;

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

    /**
     * To get list of templates without system type from cloudstack server.
     *
     * @return template list from server
     * @throws Exception unhandled errors.
     */
    List<Template> findByTemplate() throws Exception;

    /**
     * To get list of templates by filters.
     *
     * @param template
     * @return template list from server
     * @throws Exception unhandled errors.
     */
    List<Template> findByFilters(Template template) throws Exception;

    /**
     * To get template by uuid.
     *
     * @param uuid uuid of template.
     * @return template.
     * @throws Exception unhandled errors.
     */
    Template findByUUID(String uuid);

    /**
     * Soft delete for template
     *
     * @param template object
     * @return template
     * @throws Exception unhandled errors.
     */
    Template softDelete(Template template) throws Exception;

    /**
     * Get all Iso templates.
     *
     * @param pagingAndSorting page
     * @return iso templates
     * @throws Exception error
     */
    Page<Template> findAllIso(PagingAndSorting pagingAndSorting) throws Exception;

}
