package ck.panda.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Hypervisor;
import ck.panda.domain.entity.OsType;
import ck.panda.domain.entity.Template;
import ck.panda.domain.entity.Template.Status;
import ck.panda.domain.entity.Template.Type;
import ck.panda.domain.entity.Zone;
import ck.panda.domain.repository.jpa.HypervisorRepository;
import ck.panda.domain.repository.jpa.OsTypeRepository;
import ck.panda.domain.repository.jpa.TemplateRepository;
import ck.panda.domain.repository.jpa.ZoneRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackTemplateService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;
import ck.panda.util.error.exception.EntityNotFoundException;
import ck.panda.util.infrastructure.security.AuthenticationFilter;
import groovy.json.JsonException;

/**
 * Service implementation for Template entity.
 *
 */
@Service
public class TemplateServiceImpl implements TemplateService {

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** Template repository reference. */
    @Autowired
    private TemplateRepository templateRepository;

    /** Cloud stack template service. */
    @Autowired
    private CloudStackTemplateService cloudStackTemplateService;

    /** Cloud stack configuration reference. */
    @Autowired
    private ConfigUtil configUtil;

    /** Os type repository reference. */
    @Autowired
    private OsTypeRepository osTypeRepository;

    /** Zone repository reference. */
    @Autowired
    private ZoneRepository zoneRepo;

    /** Hypervisor repository reference. */
    @Autowired
    private HypervisorRepository hypervisorRepository;

    /** Logger constant. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationFilter.class);

    @Override
    @PreAuthorize("hasAuthority('ROLE_DOMAIN_USER')")
    public Template save(Template template) throws Exception {

        if(template.getSyncFlag() == true) {
        Errors errors = validator.rejectIfNullEntity("template", template);
        errors = validator.validateEntity(template, errors);

        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
        	try {
            configUtil.setServer(1L);
            HashMap<String, String> optional = new HashMap<String, String>();

            String resp = cloudStackTemplateService.registerTemplate(template.getDescription(), template.getFormat().name(),
                    template.getHypervisor().getName(), template.getName(),
                    template.getOsType().getUuid(), template.getUrl(),
                    template.getZone().getUuid(), "json", templateFieldNullValidation(template, optional));

            JSONArray templateJSON = new JSONObject(resp).getJSONObject("registertemplateresponse")
                    .getJSONArray("template");
            for (int i = 0; i < templateJSON.length(); ++i) {
                JSONObject rec = templateJSON.getJSONObject(i);
                template.setUuid(rec.getString("id"));
                template.setStatus(Status.valueOf("Active"));
                template.setType(Type.valueOf(rec.getString("templatetype")));
                template.setDisplayText(template.getDescription());
            }
        	} catch (JsonException jsonException) {
        		LOGGER.error("Cloud stack template creation exception", jsonException);
        	}
        	return templateRepository.save(template);
        }
        } else {
            return templateRepository.save(template);
        }
    }

    @Override
    public Template update(Template template) throws Exception {
        if(template.getSyncFlag() == true) {
        Errors errors = validator.rejectIfNullEntity("template", template);
        errors = validator.validateEntity(template, errors);

        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
            configUtil.setServer(1L);
            HashMap<String, String> optional = new HashMap<String, String>();
            templateFieldNullValidation(template, optional);
            optional.put("name", template.getName());
            optional.put("displaytext", template.getDescription());

            cloudStackTemplateService.updateTemplate(template.getUuid(), "json", optional);
            return templateRepository.save(template);
        }
        } else {
            return templateRepository.save(template);
        }
    }

    @Override
    public void delete(Template template) throws Exception {
        if(template.getSyncFlag() == true) {
        configUtil.setServer(1L);
        HashMap<String, String> optional = new HashMap<String, String>();
        cloudStackTemplateService.deleteTemplate(template.getId().toString(), "json", optional);
        templateRepository.delete(template);
        } else {
            templateRepository.delete(template);
        }
    }

    @Override
    public void delete(Long id) throws Exception {
        configUtil.setServer(1L);
        HashMap<String, String> optional = new HashMap<String, String>();
        cloudStackTemplateService.deleteTemplate(id.toString(), "json", optional);
        templateRepository.delete(id);
    }

    @Override
    public Template find(Long id) throws Exception {
        Template template = templateRepository.findOne(id);

        if (template == null) {
            throw new EntityNotFoundException("Template not found");
        }
        return template;
    }

    @Override
    public Page<Template> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return templateRepository.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<Template> findAll() throws Exception {
        return (List<Template>) templateRepository.findAll();
    }

    @Override
    public List<Template> findAllFromCSServer() throws Exception {
        List<Template> templateList = new ArrayList<Template>();
        HashMap<String, String> templateMap = new HashMap<String, String>();

        // 1. Get the list of templates from CS server using CS connector
        String response = cloudStackTemplateService.listTemplates("all", "json", templateMap);

        JSONArray templateListJSON = new JSONObject(response).getJSONObject("listtemplatesresponse")
                .getJSONArray("template");
        // 2. Iterate the json list, convert the single json entity to template
        for (int i = 0, size = templateListJSON.length(); i < size; i++) {
            // 2.1 Call convert by passing JSONObject to Template entity and Add
            // the converted Template entity to list

            Template template = Template.convert(templateListJSON.getJSONObject(i));
            OsType osType = osTypeRepository.findByUUID(template.getTransOsType());
            template.setOsType(osType);
            template.setDisplayText(osType.getDescription());
            Zone zone = zoneRepo.findByUUID(template.getTransZone());
            template.setZone(zone);
            Hypervisor hypervisor = hypervisorRepository.findByName(template.getTransHypervisor());
            template.setHypervisor(hypervisor);

            templateList.add(template);
        }
        return templateList;
    }

    public HashMap<String, String> templateFieldNullValidation(Template template, HashMap<String, String> optional) {
        if(template.getDynamicallyScalable() != null) { optional.put("isdynamicallyscalable", template.getDynamicallyScalable().toString()); }
        if(template.getExtractable() != null) { optional.put("isextractable", template.getExtractable().toString()); }
        if(template.getFeatured() != null) { optional.put("isfeatured", template.getFeatured().toString()); }
        if(template.getShare() != null) { optional.put("ispublic", template.getShare().toString()); }
        if(template.getRouting() != null) { optional.put("isrouting", template.getRouting().toString()); }
        if(template.getPasswordEnabled() != null) { optional.put("passwordenabled", template.getPasswordEnabled().toString()); }
        if(template.getHvm() != null) { optional.put("requireshvm", template.getHvm().toString()); }
        return optional;
    }
}
