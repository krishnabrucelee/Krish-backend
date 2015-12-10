package ck.panda.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Domain;
import ck.panda.domain.entity.Hypervisor;
import ck.panda.domain.entity.OsCategory;
import ck.panda.domain.entity.OsType;
import ck.panda.domain.entity.StorageOffering;
import ck.panda.domain.entity.Template;
import ck.panda.domain.entity.Template.Status;
import ck.panda.domain.entity.Template.Type;
import ck.panda.domain.entity.Zone;
import ck.panda.domain.repository.jpa.DepartmentReposiory;
import ck.panda.domain.repository.jpa.DomainRepository;
import ck.panda.domain.repository.jpa.HypervisorRepository;
import ck.panda.domain.repository.jpa.OsCategoryRepository;
import ck.panda.domain.repository.jpa.OsTypeRepository;
import ck.panda.domain.repository.jpa.TemplateRepository;
import ck.panda.domain.repository.jpa.ZoneRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackTemplateService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.TokenDetails;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;
import ck.panda.util.error.exception.EntityNotFoundException;

/**
 * Service implementation for Template entity.
 *
 */
@Service
public class TemplateServiceImpl implements TemplateService {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(VirtualMachineServiceImpl.class);

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

    /** Os category repository reference. */
    @Autowired
    private OsCategoryRepository osCategoryRepository;

    /** Zone repository reference. */
    @Autowired
    private ZoneRepository zoneRepo;

    /** Token details repository reference. */
    @Autowired
    private TokenDetails tokenDetails;

    /** Department repository reference. */
    @Autowired
    private DepartmentReposiory departmentReposiory;

    /** Domain repository reference. */
    @Autowired
    private DomainRepository domainRepository;

    /** Hypervisor repository reference. */
    @Autowired
    private HypervisorRepository hypervisorRepository;

    @Override
    public Template save(Template template) throws Exception {
        if (template.getSyncFlag()) {
            Errors errors = validator.rejectIfNullEntity("template", template);
            errors = validator.validateEntity(template, errors);
            errors = customValidateEntity(template, errors, true);

            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
                csRegisterTemplate(template, errors);
                return templateRepository.save(template);
            }
        } else {
            return templateRepository.save(template);
        }
    }

    @Override
    public Template update(Template template) throws Exception {
        if (template.getSyncFlag()) {
            Errors errors = validator.rejectIfNullEntity("template", template);
            errors = validator.validateEntity(template, errors);
            errors = customValidateEntity(template, errors, false);

            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
                csUpdateTemplate(template);
                return templateRepository.save(template);
            }
        } else {
            return templateRepository.save(template);
        }
    }

    @Override
    public void delete(Template template) throws Exception {
        templateRepository.delete(template);
    }

    @Override
    public void delete(Long id) throws Exception {
        csDeleteTemplate(id);
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
        return templateRepository.findAllByType(Type.SYSTEM, pagingAndSorting.toPageRequest());
    }

    @Override
    public List<Template> findAll() throws Exception {
        return (List<Template>) templateRepository.findAll();
    }

    @Override
    public List<Template> findAllFromCSServer() throws Exception {
        List<Template> templateList = new ArrayList<Template>();
        HashMap<String, String> templateMap = new HashMap<String, String>();
        templateMap.put("listall", "true");
        String response = cloudStackTemplateService.listTemplates("all", "json", templateMap);
        JSONArray templateListJSON = null;
        JSONObject responseObject = new JSONObject(response).getJSONObject("listtemplatesresponse");
        if (responseObject.has("template")) {
            templateListJSON = responseObject.getJSONArray("template");
            for (int i = 0, size = templateListJSON.length(); i < size; i++) {
                Template template = Template.convert(templateListJSON.getJSONObject(i));
                OsType osType = osTypeRepository.findByUUID(template.getTransOsType());
                template.setOsType(osType);
                template.setOsCategory(osCategoryRepository.findOne(osType.getOsCategoryId()));
                if(osType.getDescription().contains("32")) {
                    template.setArchitecture("32");
                } else if(osType.getDescription().contains("64")) {
                    template.setArchitecture("64");
                }

                template.setDisplayText(osType.getDescription());
                Zone zone = zoneRepo.findByUUID(template.getTransZone());
                template.setZone(zone);
                Hypervisor hypervisor = hypervisorRepository.findByName(template.getTransHypervisor());
                template.setHypervisor(hypervisor);
                templateList.add(template);
            }
        }
        return templateList;
    }

    @Override
    public List<Template> findByTemplate() throws Exception {
        Domain domain = domainRepository.findOne(Long.valueOf(tokenDetails.getTokenDetails("domainid")));
        if(domain != null && domain.getName().equals("ROOT")) {
            return (List<Template>) templateRepository.findByTemplateAndFeature("ALL", Type.SYSTEM, Status.ACTIVE);
        }
        return templateRepository.findByTemplate("ALL", Type.SYSTEM, Status.ACTIVE);
    }


    @Override
    public List<Template> findByFilters(Template template) throws Exception {
        Domain domain = domainRepository.findOne(Long.valueOf(tokenDetails.getTokenDetails("domainid")));
        if(template.getArchitecture() == null) {
            template.setArchitecture("ALL");
        }
        if(template.getOsCategory() == null ) {
            if(domain != null && domain.getName().equals("ROOT")) {
                return (List<Template>) templateRepository.findByTemplateAndFeature(template.getArchitecture(), Type.SYSTEM, Status.ACTIVE);
            }
            return (List<Template>) templateRepository.findByTemplate(template.getArchitecture(), Type.SYSTEM, Status.ACTIVE);
        } else  {
            if(domain != null && domain.getName().equals("ROOT")) {
                return templateRepository.findAllByOsCategoryAndArchitectureAndType(template.getOsCategory(), template.getArchitecture(), Type.SYSTEM, Status.ACTIVE);
            }
            return templateRepository.findAllByOsCategoryAndArchitectureAndTypeAndStatus(template.getOsCategory(), template.getArchitecture(), Type.SYSTEM, Status.ACTIVE);
        }
    }

    /**
     * @param template entity object
     * @param errors object for validation
     * @throws Exception raise if error
     */
    public void csRegisterTemplate(Template template, Errors errors) throws Exception {
        configUtil.setServer(1L);
        HashMap<String, String> optional = new HashMap<String, String>();
        String resp = cloudStackTemplateService.registerTemplate(template.getDescription(), template.getFormat().name(),
            template.getHypervisor().getName(), template.getName(),
            template.getOsType().getUuid(), template.getUrl(),
            template.getZone().getUuid(), "json", optionalFieldValidation(template, optional));
        try {
            JSONObject templateJSON = new JSONObject(resp).getJSONObject("registertemplateresponse");
            if (templateJSON.has("errorcode")) {
                errors = this.validateCSEvent(errors, templateJSON.getString("errortext"));
                throw new ApplicationException(errors);
            } else {
                JSONArray templateArray = (JSONArray) templateJSON.get("template");
                for (int i = 0; i < templateArray.length(); i++) {
                    JSONObject jsonobject = templateArray.getJSONObject(i);
                    LOGGER.debug("Template UUID", jsonobject.getString("id"));
                    template.setUuid(jsonobject.getString("id"));
                    if (jsonobject.getBoolean("isready")) {
                        template.setStatus(Status.valueOf("ACTIVE"));
                    } else {
                        template.setStatus(Status.valueOf("ACTIVE"));
                    }
                    template.setType(Type.valueOf(jsonobject.getString("templatetype")));
                }
                template.setDisplayText(template.getDescription());
             }
        } catch (ApplicationException e) {
            LOGGER.error("ERROR AT TEMPLATE CREATION", e);
            throw new ApplicationException(e.getErrors());
        }
    }

    /**
     * @param template entity object
     * @throws Exception raise if error
     */
    public void csUpdateTemplate(Template template) throws Exception {
        configUtil.setServer(1L);
        HashMap<String, String> optional = new HashMap<String, String>();
        optionalFieldValidation(template, optional);
        optional.put("name", template.getName());
        optional.put("displaytext", template.getDescription());
        try {
            cloudStackTemplateService.updateTemplate(template.getUuid(), "json", optional);
            cloudStackTemplateService.updateTemplatePermissions(template.getUuid(), "json", optional);
        } catch (Exception e) {
            LOGGER.error("ERROR AT TEMPLATE UPDATION", e);
        }
    }

    /**
     * @param id template id
     * @throws Exception raise if error
     */
    public void csDeleteTemplate(Long id) throws Exception {
        configUtil.setServer(1L);
        HashMap<String, String> optional = new HashMap<String, String>();
        Template template = templateRepository.findOne(id);
        try {
            cloudStackTemplateService.deleteTemplate(template.getUuid(), "json", optional);
        } catch (Exception e) {
            LOGGER.error("ERROR AT TEMPLATE DELETE", e);
        }
    }

    /**
     * @param template entity object
     * @param errors object for validation
     * @param validstatus whether need to check validation or not
     * @return errors list
     * @throws Exception raise if error
     */
    public Errors customValidateEntity(Template template, Errors errors, Boolean validstatus) throws Exception {

        if (template.getArchitecture().isEmpty()) {
            errors.addFieldError("architecture", "template.architecture");
        }
        if (template.getOsVersion().isEmpty()) {
            errors.addFieldError("osVersion", "template.osversion.error");
        }
        if (template.getUrl() == null && validstatus) {
            errors.addFieldError("url", "template.url.error");
        }
        if (template.getDetailedDescription().isEmpty()) {
            errors.addFieldError("detailedDescription", "template.detaileddescription");
        }
        if (template.getTemplateCost() == null) {
            errors.addFieldError("cost", "template.cost.error");
        }
        if (template.getMinimumMemory() == null) {
            errors.addFieldError("minimumMemory", "template.minimummemory.error");
        }
        if (template.getMinimumCore() == null) {
            errors.addFieldError("minimumCore", "template.minimumcore.error");
        }
        if (template.getOsCategory() == null) {
            errors.addFieldError("osCategory", "template.oscategory.error");
        }
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        }
        return errors;
    }

    /**
     * @param template entity object
     * @param optional for null value checking
     * @return optional values list
     */
    public HashMap<String, String> optionalFieldValidation(Template template, HashMap<String, String> optional) {
        if (template.getDynamicallyScalable() != null) {
            optional.put("isdynamicallyscalable", template.getDynamicallyScalable().toString());
        }
        if (template.getExtractable() != null) {
            optional.put("isextractable", template.getExtractable().toString());
        }
        if (template.getFeatured() != null) {
            optional.put("isfeatured", template.getFeatured().toString());
        }
        if (template.getShare() != null) {
            optional.put("ispublic", template.getShare().toString());
        }
        if (template.getRouting() != null) {
            optional.put("isrouting", template.getRouting().toString());
        }
        if (template.getPasswordEnabled() != null) {
            optional.put("passwordenabled", template.getPasswordEnabled().toString());
        }
        if (template.getHvm() != null) {
            optional.put("requireshvm", template.getHvm().toString());
        }
        return optional;
    }

    /**
     * Check the template CS error handling.
     *
     * @param errors object for validation
     * @param eMessage error message
     * @return global errors list
     * @throws Exception raise if error
     */
    private Errors validateCSEvent(Errors errors, String eMessage) throws Exception {
        errors.addGlobalError(eMessage);
        return errors;
    }

    @Override
    public Template findByUUID(String uuid) {
        return templateRepository.findByUUID(uuid);
    }

    @Override
       public Template softDelete(Template template) throws Exception {
        template.setIsActive(false);
        template.setStatus(Template.Status.INACTIVE);
             return templateRepository.save(template);
       }

}
