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
import ck.panda.constants.CloudStackConstants;
import ck.panda.constants.GenericConstants;
import ck.panda.domain.entity.Domain;
import ck.panda.domain.entity.Hypervisor;
import ck.panda.domain.entity.OsType;
import ck.panda.domain.entity.Template;
import ck.panda.domain.entity.Template.Format;
import ck.panda.domain.entity.Template.Status;
import ck.panda.domain.entity.Template.TemplateType;
import ck.panda.domain.entity.User;
import ck.panda.domain.entity.Zone;
import ck.panda.domain.repository.jpa.TemplateRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackTemplateService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;

/**
 * Service implementation for Template entity.
 *
 */
@Service
public class TemplateServiceImpl implements TemplateService {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateServiceImpl.class);

    /** Cloud stack configuration reference. */
    @Autowired
    private ConfigUtil configUtil;

    /** Reference of the convert entity service. */
    @Autowired
    private ConvertEntityService convertEntityService;

    /** Cloud stack template service. */
    @Autowired
    private CloudStackTemplateService cloudStackTemplateService;

    /** OS type service reference. */
    @Autowired
    private OsTypeService osTypeService;

    /** Zone service reference. */
    @Autowired
    private ZoneService zoneService;

    /** Domain service reference. */
    @Autowired
    private DomainService domainService;

    /** Hypervisor service reference. */
    @Autowired
    private HypervisorService hypervisorService;

    /** Template repository reference. */
    @Autowired
    private TemplateRepository templateRepository;

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** Template name. */
    public static final String TEMPLATE_NAME = "template";

    /** ISO template name. */
    public static final String ISO_TEMPLATE_NAME = "iso";

    /** List all template. */
    public static final String ALL_TEMPLATE = "ALL";

    /** Root domain admin template. */
    public static final String DOMAIN_ROOT_ADMIN = "ROOT";

    /** Windows template. */
    public static final String WINDOWS_TEMPLATE = "Windows";

    /** Ready state. */
    public static final String IS_READY = "isready";

    /** Template size. */
    public static final String TEMPLATE_SIZE = "size";

    /** Template type. */
    public static final String TEMPLATE_TYPE = "templatetype";

    @Override
    @PreAuthorize("hasPermission(#template.getSyncFlag(), 'REGISTER_TEMPLATE')")
    public Template save(Template template) throws Exception {
        template.setIsActive(true);
        if (template.getSyncFlag()) {
            Errors errors = validator.rejectIfNullEntity(TEMPLATE_NAME, template);
            errors = validator.validateEntity(template, errors);
            errors = customValidateEntity(template, errors, true);

            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
                if (template.getBootable() == null) {
                    template.setBootable(true);
                }
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
            Errors errors = validator.rejectIfNullEntity(TEMPLATE_NAME, template);
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
        template.setIsActive(false);
        template.setStatus(Template.Status.INACTIVE);
        templateRepository.save(template);
    }

    @Override
    @PreAuthorize("hasPermission(null, 'DELETE_MY_TEMPLATE')")
    public void delete(Long id) throws Exception {
        csDeleteTemplate(id);
        templateRepository.delete(id);
    }

    @Override
    public Template find(Long id) throws Exception {
        Template template = templateRepository.findOne(id);
        return template;
    }

    @Override
    public Page<Template> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        csPrepareTemplate(templateRepository.findByTemplate(ALL_TEMPLATE, TemplateType.SYSTEM, Status.INACTIVE, true));
        return templateRepository.findAllByType(TemplateType.SYSTEM, Template.Format.ISO, pagingAndSorting.toPageRequest(), true);
    }

    @Override
    public Page<Template> findAllIso(PagingAndSorting pagingAndSorting) throws Exception {
        return templateRepository.findAllByFormat(TemplateType.SYSTEM, Template.Format.ISO, pagingAndSorting.toPageRequest(), true);
    }

    @Override
    public List<Template> findAll() throws Exception {
        return (List<Template>) templateRepository.findAll();
    }

    @Override
    public List<Template> findAllFromCSServer() throws Exception {
        List<Template> templateList = new ArrayList<Template>();
        HashMap<String, String> templateMap = new HashMap<String, String>();
        templateMap.put(CloudStackConstants.CS_LIST_ALL, CloudStackConstants.STATUS_ACTIVE);
        String response = cloudStackTemplateService.listTemplates(ALL_TEMPLATE.toLowerCase(), CloudStackConstants.JSON, templateMap);
        JSONArray templateListJSON = null;
        JSONObject responseObject = new JSONObject(response).getJSONObject("listtemplatesresponse");
        if (responseObject.has(TEMPLATE_NAME)) {
            templateListJSON = responseObject.getJSONArray(TEMPLATE_NAME);
            for (int i = 0, size = templateListJSON.length(); i < size; i++) {
                Template template = Template.convert(templateListJSON.getJSONObject(i));
                OsType osType = osTypeService.findByUUID(template.getTransOsType());
                template.setOsTypeId(osType.getId());
                template.setOsCategoryId(osType.getOsCategoryId());
                if (osType.getDescription().contains(GenericConstants.TEMPLATE_ARCHITECTURE[0])) {
                    template.setArchitecture(GenericConstants.TEMPLATE_ARCHITECTURE[0]);
                } else if (osType.getDescription().contains(GenericConstants.TEMPLATE_ARCHITECTURE[1])) {
                    template.setArchitecture(GenericConstants.TEMPLATE_ARCHITECTURE[1]);
                }
                template.setDisplayText(osType.getDescription());
                Zone zone = zoneService.findByUUID(template.getTransZone());
                template.setZoneId(zone.getId());
                Hypervisor hypervisor = hypervisorService.findByName(template.getTransHypervisor());
                template.setHypervisorId(hypervisor.getId());
                templateList.add(template);
            }
        }
        getIsoTemplateList(templateList, templateMap);
        return templateList;
    }

    /**
     * Get all the ISO template list from CS.
     *
     * @param templateList template list
     * @param templateMap template map
     * @throws Exception unhandled errors.
     */
    public void getIsoTemplateList(List<Template> templateList, HashMap<String, String> templateMap) throws Exception {
        //Getting the ISO template list from CS
        String isoResponse = cloudStackTemplateService.listIsos(ALL_TEMPLATE.toLowerCase(), CloudStackConstants.JSON, templateMap);
        JSONArray isoTemplateListJSON = null;
        JSONObject isoResponseObject = new JSONObject(isoResponse).getJSONObject("listisosresponse");
        if (isoResponseObject.has(ISO_TEMPLATE_NAME)) {
            isoTemplateListJSON = isoResponseObject.getJSONArray(ISO_TEMPLATE_NAME);
            for (int i = 0, size = isoTemplateListJSON.length(); i < size; i++) {
                Template template = Template.convert(isoTemplateListJSON.getJSONObject(i));
                OsType osType = osTypeService.findByUUID(template.getTransOsType());
                template.setOsTypeId(osType.getId());
                template.setOsCategoryId(osType.getOsCategoryId());
                if (osType.getDescription().contains(GenericConstants.TEMPLATE_ARCHITECTURE[0])) {
                    template.setArchitecture(GenericConstants.TEMPLATE_ARCHITECTURE[0]);
                } else if (osType.getDescription().contains(GenericConstants.TEMPLATE_ARCHITECTURE[1])) {
                    template.setArchitecture(GenericConstants.TEMPLATE_ARCHITECTURE[1]);
                }
                template.setDisplayText(osType.getDescription());
                if (!template.getTransZone().equals("")) {
                    template.setZoneId(zoneService.findByUUID(template.getTransZone()).getId());
                }
                template.setHypervisorId(8L);
                templateList.add(template);
            }
        }
    }

    @Override
    public List<Template> findByTemplate(Long id) throws Exception {
        User user = convertEntityService.getOwnerById(id);
        Domain domain = domainService.find(user.getDomainId());
        if (domain != null && domain.getName().equals(DOMAIN_ROOT_ADMIN)) {
            return csPrepareTemplate((List<Template>) templateRepository.findByTemplateAndFeature(ALL_TEMPLATE,
                   TemplateType.SYSTEM, Status.ACTIVE, true));
        }
        return csPrepareTemplate(templateRepository.findByTemplate(ALL_TEMPLATE, TemplateType.SYSTEM, Status.ACTIVE, true));
    }

    @Override
    public List<Template> findTemplateByFilters(Template template, Long id) throws Exception {
        User user = convertEntityService.getOwnerById(id);
        Domain domain = domainService.find(user.getDomainId());
        if (template.getArchitecture() == null) {
            template.setArchitecture(ALL_TEMPLATE);
        }
        if (template.getOsCategoryId() == null) {
            if (domain != null && domain.getName().equals(DOMAIN_ROOT_ADMIN)) {
                return csPrepareTemplate((List<Template>) templateRepository.findByTemplateAndFeature(
                       template.getArchitecture(), TemplateType.SYSTEM, Status.ACTIVE, true));
            }
            return (List<Template>) templateRepository.findByTemplate(template.getArchitecture(), TemplateType.SYSTEM,
                    Status.ACTIVE, true);
        } else {
            if (domain != null && domain.getName().equals(DOMAIN_ROOT_ADMIN)) {
                return csPrepareTemplate(templateRepository.findAllByOsCategoryAndArchitectureAndType(template.getOsCategoryId(),
                       template.getArchitecture(), TemplateType.SYSTEM, Status.ACTIVE, true));
            }
            return csPrepareTemplate(templateRepository.findAllByOsCategoryAndArchitectureAndTypeAndStatus(
                   template.getOsCategoryId(), template.getArchitecture(), TemplateType.SYSTEM, Status.ACTIVE, true));
        }
    }

    @Override
    public List<Template> findIsoByFilters(Template templateIso, Long id) throws Exception {
        User user = convertEntityService.getOwnerById(id);
        Domain domain = domainService.find(user.getDomainId());
        List<Template.Format> format = new ArrayList<Template.Format>();
        format.add(Format.ISO);
        if (templateIso.getArchitecture() == null) {
            templateIso.setArchitecture(ALL_TEMPLATE);
        }
        if (templateIso.getOsCategoryId() == null) {
            if (domain != null && domain.getName().equals(DOMAIN_ROOT_ADMIN)) {
                return csPrepareTemplate((List<Template>) templateRepository.findByIsoAndFeature(
                       templateIso.getArchitecture(), TemplateType.SYSTEM, format, Status.ACTIVE, true));
            }
            return (List<Template>) templateRepository.findByIso(templateIso.getArchitecture(), TemplateType.SYSTEM, format,
                    Status.ACTIVE, true);
        } else {
            if (domain != null && domain.getName().equals(DOMAIN_ROOT_ADMIN)) {
                return csPrepareTemplate(templateRepository.findAllByOsCategoryAndArchitectureAndTypeAndIso(templateIso.getOsCategoryId(),
                       templateIso.getArchitecture(), TemplateType.SYSTEM, format, Status.ACTIVE, true));
            }
            return csPrepareTemplate(templateRepository.findAllByOsCategoryAndArchitectureAndTypeAndStatusAndIso(
                   templateIso.getOsCategoryId(), templateIso.getArchitecture(), TemplateType.SYSTEM, format, Status.ACTIVE, true));
        }
    }

    @Override
    public Template findByUUID(String uuid) {
        return templateRepository.findByUUID(uuid, true);
    }

    @Override
    @PreAuthorize("hasPermission(#template.getSyncFlag(), 'DELETE_MY_TEMPLATE')")
    public Template softDelete(Template template) throws Exception {
    	template.setIsActive(false);
        template.setStatus(Template.Status.INACTIVE);
        if (template.getSyncFlag()) {
            csDeleteTemplate(template.getId());
            return template;
        }
        return templateRepository.save(template);
    }

    @Override
    public String findTemplateCounts() throws Exception {
        List<Template> template = templateRepository.findTemplateCounts(TemplateType.SYSTEM, true);
        Integer windowsCount = 0, linuxCount = 0, totalCount = 0;
        Integer windowsIsoCount = 0, linuxIsoCount = 0, totalIsoCount = 0;
        for (int i = 0; i < template.size(); i++) {
            if (template.get(i).getFormat() == Template.Format.ISO) {
                if (template.get(i).getOsType().getDescription().contains(WINDOWS_TEMPLATE)) {
                    windowsIsoCount++;
                } else {
                    linuxIsoCount++;
                }
                totalIsoCount++;
                } else {
                    if (template.get(i).getOsType().getDescription().contains(WINDOWS_TEMPLATE)) {
                        windowsCount++;
                    } else {
                        linuxCount++;
                    }
                    totalCount++;
                }
            }
        return "{\"windowsCount\":" + windowsCount + ",\"linuxCount\":" + linuxCount + ",\"totalCount\":"
            + totalCount + ",\"windowsIsoCount\":" + windowsIsoCount + ",\"linuxIsoCount\":"
            + linuxIsoCount + ",\"totalIsoCount\":" + totalIsoCount + "}";
    }

    /**
     * Update the template status and size.
     *
     * @param templates list of templates
     * @return template list
     * @throws Exception unhandled errors.
     */
    public List<Template> csPrepareTemplate(List<Template> templates) throws Exception {
        configUtil.setServer(1L);
        List<Template> allTemplate = new ArrayList<Template>();
        for (Template template : templates) {
            if (template.getStatus() == Status.INACTIVE) {
                String csResponse = cloudStackTemplateService.prepareTemplate(template.getUuid(),
                       zoneService.find(template.getZoneId()).getUuid(), CloudStackConstants.JSON);
                try {
                    JSONObject templateJSON = new JSONObject(csResponse).getJSONObject("preparetemplateresponse");
                    if (templateJSON.has(TEMPLATE_NAME)) {
                        JSONArray templateArray = (JSONArray) templateJSON.get(TEMPLATE_NAME);
                        for (int i = 0; i < templateArray.length(); i++) {
                            JSONObject jsonobject = templateArray.getJSONObject(i);
                            if (jsonobject.getBoolean(IS_READY)) {
                                if (template.getSize() == null) {
                                    Template persistTemplate = templateRepository.findOne(template.getId());
                                    persistTemplate.setSize(jsonobject.getLong(TEMPLATE_SIZE));
                                    persistTemplate.setStatus(Status.ACTIVE);
                                    templateRepository.save(persistTemplate);
                                }
                                allTemplate.add(template);
                            } else {
                                LOGGER.debug("Not yet complete");
                            }
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("Error at template getting template status : ", e.getMessage());
                }
            } else {
                allTemplate.add(template);
            }
        }
        return allTemplate;
    }

    /**
     * Register template/ISO in CS.
     *
     * @param template entity object
     * @param errors object for validation
     * @throws Exception unhandled errors.
     */
    public void csRegisterTemplate(Template template, Errors errors) throws Exception {
        configUtil.setServer(1L);
        HashMap<String, String> optional = new HashMap<String, String>();
        String csResponse = "";
        JSONObject templateJSON = null;
        JSONArray templateArray = null;
        if (template.getFormat().equals(Template.Format.ISO)) {
            optional = optionalFieldValidation(template, optional);
            optional.put("ostypeid", osTypeService.find(template.getOsTypeId()).getUuid());
            csResponse = cloudStackTemplateService.registerIso(template.getDescription(),
                    template.getName(), template.getUrl(), zoneService.find(template.getZoneId()).getUuid(), CloudStackConstants.JSON, optional);
            templateJSON = new JSONObject(csResponse).getJSONObject("registerisoresponse");
            if (templateJSON.has(CloudStackConstants.CS_ERROR_CODE)) {
                errors = this.validateCSEvent(errors, templateJSON.getString(CloudStackConstants.CS_ERROR_TEXT));
                throw new ApplicationException(errors);
            }
            templateArray = (JSONArray) templateJSON.get(ISO_TEMPLATE_NAME);
        } else {
            csResponse = cloudStackTemplateService.registerTemplate(template.getDescription(),
                    template.getFormat().name(), hypervisorService.find(template.getHypervisorId()).getName(),
                    template.getName(), osTypeService.find(template.getOsTypeId()).getUuid(), template.getUrl(),
                    zoneService.find(template.getZoneId()).getUuid(), CloudStackConstants.JSON, optionalFieldValidation(template, optional));
            templateJSON = new JSONObject(csResponse).getJSONObject("registertemplateresponse");
            if (templateJSON.has(CloudStackConstants.CS_ERROR_CODE)) {
                errors = this.validateCSEvent(errors, templateJSON.getString(CloudStackConstants.CS_ERROR_TEXT));
                throw new ApplicationException(errors);
            }
            templateArray = (JSONArray) templateJSON.get(TEMPLATE_NAME);
        }
        for (int i = 0; i < templateArray.length(); i++) {
            JSONObject jsonobject = templateArray.getJSONObject(i);
            template.setUuid(jsonobject.getString(CloudStackConstants.CS_ID));
            if (jsonobject.getBoolean(IS_READY)) {
                template.setStatus(Status.ACTIVE);
            } else {
                template.setStatus(Status.INACTIVE);
            }
            if (jsonobject.has(TEMPLATE_TYPE)) {
                template.setType(TemplateType.valueOf(jsonobject.getString(TEMPLATE_TYPE)));
            } else {
                template.setType(TemplateType.USER);
            }
        }
        template.setDisplayText(osTypeService.find(template.getOsTypeId()).getDescription());
    }

    /**
     * Update template/ISO in CS.
     *
     * @param template entity object
     * @throws Exception unhandled errors.
     */
    public void csUpdateTemplate(Template template) throws Exception {
        configUtil.setServer(1L);
        HashMap<String, String> optional = new HashMap<String, String>();
        optionalFieldValidation(template, optional);
        optional.put(CloudStackConstants.CS_NAME, template.getName());
        optional.put(CloudStackConstants.CS_DISPLAY_TEXT, template.getDescription());
        try {
            if (template.getFormat().equals(Template.Format.ISO)) {
                cloudStackTemplateService.updateIso(template.getUuid(), CloudStackConstants.JSON, optional);
                cloudStackTemplateService.updateIsoPermissions(template.getUuid(), CloudStackConstants.JSON, optional);
            } else {
                cloudStackTemplateService.updateTemplate(template.getUuid(), CloudStackConstants.JSON, optional);
                cloudStackTemplateService.updateTemplatePermissions(template.getUuid(), CloudStackConstants.JSON, optional);
            }
        } catch (Exception e) {
            LOGGER.error("Error at template update : ", e.getMessage());
        }
    }

    /**
     * Delete template/ISO in CS.
     *
     * @param id template id
     * @throws Exception unhandled errors.
     */
    public Boolean csDeleteTemplate(Long id) throws Exception {
        configUtil.setServer(1L);
        Errors errors = null;
        HashMap<String, String> optional = new HashMap<String, String>();
        Template template = templateRepository.findOne(id);
        try {
            String templateResponse = "";
            JSONObject templateJson = null;
            if (template.getFormat().equals(Template.Format.ISO)) {
                templateResponse = cloudStackTemplateService.deleteIso(template.getUuid(), CloudStackConstants.JSON, optional);
                templateJson = new JSONObject(templateResponse).getJSONObject("deleteisoresponse");
            } else {
                templateResponse = cloudStackTemplateService.deleteTemplate(template.getUuid(), CloudStackConstants.JSON, optional);
                templateJson = new JSONObject(templateResponse).getJSONObject("deletetemplateresponse");
            }
            if (templateJson.has(CloudStackConstants.CS_JOB_ID)) {
            	Thread.sleep(3000);
                String templateJob = cloudStackTemplateService.queryAsyncJobResult(templateJson.getString(CloudStackConstants.CS_JOB_ID),
                       CloudStackConstants.JSON);
                JSONObject jobresult = new JSONObject(templateJob).getJSONObject("queryasyncjobresultresponse");
                if (jobresult.getString(CloudStackConstants.CS_JOB_STATUS).equals("1")) {
                	Thread.sleep(3000);
                	return true;
                } else if (jobresult.getString(CloudStackConstants.CS_JOB_STATUS).equals("2")) {
                    errors = validator.sendGlobalError(jobresult.getJSONObject(CloudStackConstants.CS_JOB_RESULT)
                             .getString(CloudStackConstants.CS_ERROR_TEXT));
                    throw new ApplicationException(errors);
                }
            }
        } catch (ApplicationException e) {
            throw new ApplicationException(errors);
        }
        return false;
    }

    /**
     * Custom field validation.
     *
     * @param template entity object
     * @param errors object for validation
     * @param validstatus whether need to check validation or not
     * @return errors list
     * @throws Exception unhandled errors.
     */
    public Errors customValidateEntity(Template template, Errors errors, Boolean validstatus) throws Exception {
        if (template.getArchitecture() == null || template.getArchitecture().isEmpty()) {
            errors.addFieldError("architecture", "template.architecture");
        }
        if (template.getOsVersion() == null || template.getOsVersion().isEmpty()) {
            errors.addFieldError("osVersion", "template.osversion.error");
        }
        if (template.getUrl() == null && validstatus) {
            errors.addFieldError("url", "template.url.error");
        }
        if (template.getDetailedDescription() == null || template.getDetailedDescription().isEmpty()) {
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
        if (template.getOsCategoryId() == null) {
            errors.addFieldError("osCategory", "template.oscategory.error");
        }
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        }
        return errors;
    }

    /**
     * Optional field validation.
     *
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
        if (template.getBootable() != null) {
            optional.put("bootable", template.getBootable().toString());
        } else {
            optional.put("bootable", CloudStackConstants.STATUS_ACTIVE);
        }
        return optional;
    }

    /**
     * Check the template CS error handling.
     *
     * @param errors object for validation
     * @param eMessage error message
     * @return global errors list
     * @throws Exception unhandled errors.
     */
    private Errors validateCSEvent(Errors errors, String eMessage) throws Exception {
        errors.addGlobalError(eMessage);
        return errors;
    }
}
