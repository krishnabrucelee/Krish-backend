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
import ck.panda.constants.PingConstants;
import ck.panda.domain.entity.Domain;
import ck.panda.domain.entity.Hypervisor;
import ck.panda.domain.entity.OsCategory;
import ck.panda.domain.entity.OsType;
import ck.panda.domain.entity.Template;
import ck.panda.domain.entity.Template.Format;
import ck.panda.domain.entity.Template.Status;
import ck.panda.domain.entity.Template.TemplateType;
import ck.panda.domain.entity.TemplateCost;
import ck.panda.domain.entity.User;
import ck.panda.domain.entity.User.UserType;
import ck.panda.domain.entity.Zone;
import ck.panda.domain.repository.jpa.TemplateRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackTemplateService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.PingService;
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

    /** Department service reference. */
    @Autowired
    private DepartmentService departmentService;

    /** Hypervisor service reference. */
    @Autowired
    private HypervisorService hypervisorService;

    /** Template cost service reference. */
    @Autowired
    private TemplateCostService templateCostService;

    /** User service reference. */
    @Autowired
    private UserService userService;

    /** Template repository reference. */
    @Autowired
    private TemplateRepository templateRepository;

    /** Mr.ping service reference. */
    @Autowired
    private PingService pingService;

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** List all template. */
    public static final String ALL_TEMPLATE = "ALL";

    /** Root domain admin template. */
    public static final String DOMAIN_ROOT_ADMIN = "ROOT";

    /** Windows template. */
    public static final String WINDOWS_TEMPLATE = "Windows";

    /** Template architecture. */
    public static final String TEMPLATE_ARCHITECTURE = "architecture";

    /** Template OS version. */
    public static final String TEMPLATE_OSVERSION = "osVersion";

    /** Template URL. */
    public static final String TEMPLATE_URL = "url";

    /** Template detailed description. */
    public static final String TEMPLATE_DETAILED_DESC = "detailedDescription";

    /** Template cost. */
    public static final String TEMPLATE_COST = "cost";

    /** Template minimum memory. */
    public static final String TEMPLATE_MIN_MEMORY = "minimumMemory";

    /** Template minimum core. */
    public static final String TEMPLATE_MIN_CORE = "minimumCore";

    /** Template OS category. */
    public static final String TEMPLATE_OS_CATEGORY = "osCategory";

    /** Template of type featured. */
    public static final String TEMPLATE_FEATURED = "featured";

    /** Template of type community. */
    public static final String TEMPLATE_COMMUNITY = "community";

    /** ISO and Template counts. */
    public static final String WINDOWS_COUNT = "windowsCount", LINUX_COUNT = "linuxCount", TOTAL_COUNT = "totalCount",
        WINDOWS_ISO_COUNT = "windowsIsoCount", LINUX_ISO_COUNT = "linuxIsoCount", TOTAL_ISO_COUNT = "totalIsoCount";

    @Override
    @PreAuthorize("hasPermission(#template.getSyncFlag(), 'REGISTER_TEMPLATE')")
    public Template save(Template template, Long userId) throws Exception {
        template.setIsActive(true);
        if (template.getSyncFlag()) {
            Errors errors = validator.rejectIfNullEntity(CloudStackConstants.TEMPLATE_NAME, template);
            errors = validator.validateEntity(template, errors);
            errors = customValidateEntity(template, errors, true);
            User userDetails = convertEntityService.getOwnerById(userId);

            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
                if (userDetails.getType() == User.UserType.ROOT_ADMIN && template.getTemplateCost() != null) {
                    pingService.apiConnectionCheck(errors);
                }
                if (template.getBootable() == null) {
                    template.setBootable(true);
                }
                csRegisterTemplate(template, errors, userId);
                if(template.getTemplateCost() != null) {
                    List<TemplateCost> templateCostList = saveTemplateCost(template);
                    template.setTemplateCost(templateCostList);
                    Template templateCS = templateRepository.save(template);
                    TemplateCost templateCost = templateCostService.find(templateCS.getTemplateCost().get(0).getId());
                    templateCost.setTemplateCostId(templateCS.getId());
                    templateCostService.save(templateCost);
                    if (userDetails.getType() == User.UserType.ROOT_ADMIN && template.getTemplateCost() != null) {
                        if (pingService.apiConnectionCheck(errors)) {
                            template = templateRepository.save(template);
                            savePingProject(template);
                        }
                    }
                    return templateCS;
                }
            }
            return templateRepository.save(template);

        } else {
            return templateRepository.save(template);
        }
    }

    @Override
    @PreAuthorize("hasPermission(#template.getSyncFlag(), 'EDIT_TEMPLATE')")
    public Template update(Template template, Long userId) throws Exception {
        if (template.getSyncFlag()) {
            Errors errors = validator.rejectIfNullEntity(CloudStackConstants.TEMPLATE_NAME, template);
            errors = validator.validateEntity(template, errors);
            errors = customValidateEntity(template, errors, false);
            User userDetails = convertEntityService.getOwnerById(userId);

            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
                if (userDetails.getType() == User.UserType.ROOT_ADMIN && template.getTemplateCost() != null) {
                    pingService.apiConnectionCheck(errors);
                }
                csUpdateTemplate(template,userId);
                if(template.getTemplateCost().size() > 0 ) {
                    updateTemplateCost(template);
                if (userDetails.getType() == User.UserType.ROOT_ADMIN) {
                    if (pingService.apiConnectionCheck(errors) && template.getTemplateCost() != null) {
                        template = templateRepository.save(template);
                        savePingProject(template);
                    } else {
                        template = templateRepository.save(template);
                    }
                    return template;
                } else {
                    return templateRepository.save(template);
                }
                }
            }
        }
        if (template.getTemplateOwnerId() != null) {
            User user = userService.find(template.getTemplateOwnerId());
            if (user.getType() == UserType.ROOT_ADMIN) {
                template.setTemplateCreationType(false);
            }
        } else {
            template.setTemplateCreationType(true);
        }
        return templateRepository.save(template);
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
        configUtil.setServer(1L);
        List<Template> templateList = new ArrayList<Template>();
        HashMap<String, String> templateMap = new HashMap<String, String>();
        templateMap.put(CloudStackConstants.CS_LIST_ALL, CloudStackConstants.STATUS_ACTIVE);
        String response = cloudStackTemplateService.listTemplates(ALL_TEMPLATE.toLowerCase(), CloudStackConstants.JSON, templateMap);
        JSONArray templateListJSON = null;
        JSONObject responseObject = new JSONObject(response).getJSONObject(CloudStackConstants.CS_LIST_TEMPLATE_RESPONSE);
        if (responseObject.has(CloudStackConstants.TEMPLATE_NAME)) {
            templateListJSON = responseObject.getJSONArray(CloudStackConstants.TEMPLATE_NAME);
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
                template.setTemplateOwnerId(convertEntityService.getUserByName(template.getTransCreatedName(),
                    convertEntityService.getDomain(template.getTransDomain())));
                template.setDomainId(convertEntityService.getDomainId(template.getTransDomain()));
                template.setDepartmentId(convertEntityService.getDepartmentByUsernameAndDomains(
                        template.getTransDepartment(), convertEntityService.getDomain(template.getTransDomain())));
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
        JSONObject isoResponseObject = new JSONObject(isoResponse).getJSONObject(CloudStackConstants.CS_LIST_ISO_RESPONSE);
        if (isoResponseObject.has(CloudStackConstants.ISO_TEMPLATE_NAME)) {
            isoTemplateListJSON = isoResponseObject.getJSONArray(CloudStackConstants.ISO_TEMPLATE_NAME);
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
                template.setTemplateOwnerId(convertEntityService.getUserByName(template.getTransCreatedName(),
                    convertEntityService.getDomain(template.getTransDomain())));
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
        return csPrepareTemplate(templateRepository.findAllTemplateByDomainIdUserTypeAndIsActiveStatus(ALL_TEMPLATE, TemplateType.SYSTEM, Status.ACTIVE, true, UserType.ROOT_ADMIN, user.getDomainId()));
    }

    @Override
    public List<Template> findTemplateByFilters(Template template, Long id) throws Exception {
        User user = convertEntityService.getOwnerById(id);
        Domain domain = domainService.find(user.getDomainId());
        List<User> userList = userService.findByRootAdminUser();
        List<Template> templates = null;
        for(User rootUser: userList) {
            if (template.getArchitecture() == null) {
                template.setArchitecture(ALL_TEMPLATE);
            }
            if (template.getOsCategoryId() == null) {
                if (domain != null && domain.getName().equals(DOMAIN_ROOT_ADMIN)) {
                    templates = csPrepareTemplate((List<Template>) templateRepository.findByTemplateAndFeature(
                       template.getArchitecture(), TemplateType.SYSTEM, Status.ACTIVE, true));
            }
                else {
                    List<Template> templateList = templateRepository.findByTemplateOwnerIdAndIsActive(rootUser.getId(), true);
                    if(templateList.size() != 0)
                            {
                        templates = (List<Template>) templateRepository.findByTemplateAndUserId(template.getArchitecture(), TemplateType.SYSTEM,
                                    Status.ACTIVE, true,user.getDomainId(),rootUser.getId());
                    }
                }
            } else {
                if (domain != null && domain.getName().equals(DOMAIN_ROOT_ADMIN)) {
                    templates = csPrepareTemplate(templateRepository.findAllByOsCategoryAndArchitectureAndType(template.getOsCategoryId(),
                       template.getArchitecture(), TemplateType.SYSTEM, Status.ACTIVE, true));
                }
                else {
                     if(templateRepository.findByTemplateOwnerIdAndIsActive(rootUser.getId(), true) != null) {
                    templates = csPrepareTemplate(templateRepository.findAllByOsCategoryAndArchitectureAndTypeAndStatus(
                            template.getOsCategoryId(), template.getArchitecture(), TemplateType.SYSTEM, Status.ACTIVE, true,user.getDomainId(),rootUser.getId()));
                     }
                 }
             }
        }
        return templates;
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
        }
        return templateRepository.save(template);
    }

    @Override
    public HashMap<String, Integer> findTemplateCounts() throws Exception {
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

        /** Template minimum core. */
        HashMap<String, Integer> templateCount = new HashMap<String, Integer>();
        templateCount.put(WINDOWS_COUNT, windowsCount);
        templateCount.put(LINUX_COUNT, linuxCount);
        templateCount.put(TOTAL_COUNT, totalCount);
        templateCount.put(WINDOWS_ISO_COUNT, windowsIsoCount);
        templateCount.put(LINUX_ISO_COUNT, linuxIsoCount);
        templateCount.put(TOTAL_ISO_COUNT, totalIsoCount);
        return templateCount;
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
                    JSONObject templateJSON = new JSONObject(csResponse).getJSONObject(CloudStackConstants.CS_PREPARE_TEMPLATE_RESPONSE);
                    if (templateJSON.has(CloudStackConstants.TEMPLATE_NAME)) {
                        JSONArray templateArray = (JSONArray) templateJSON.get(CloudStackConstants.TEMPLATE_NAME);
                        for (int i = 0; i < templateArray.length(); i++) {
                            JSONObject jsonobject = templateArray.getJSONObject(i);
                            if (jsonobject.getBoolean(CloudStackConstants.CS_READY_STATE)) {
                                if (template.getSize() == 0L) {
                                    Template persistTemplate = templateRepository.findOne(template.getId());
                                    persistTemplate.setSize(jsonobject.getLong(CloudStackConstants.CS_SIZE));
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
     * @param userId user id
     * @throws Exception unhandled errors.
     */
    public void csRegisterTemplate(Template template, Errors errors, Long userId) throws Exception {
        configUtil.setUserServer();
        HashMap<String, String> optional = new HashMap<String, String>();
        String csResponse = "";
        JSONObject templateJSON = null;
        JSONArray templateArray = null;
        if (template.getFormat().equals(Template.Format.ISO)) {
            optional = optionalFieldValidation(template, optional, userId);
            optional.put("ostypeid", osTypeService.find(template.getOsTypeId()).getUuid());
            csResponse = cloudStackTemplateService.registerIso(template.getDescription(),
                    template.getName(), template.getUrl(), zoneService.find(template.getZoneId()).getUuid(), CloudStackConstants.JSON, optional);
            templateJSON = new JSONObject(csResponse).getJSONObject(CloudStackConstants.CS_REGISTER_ISO_RESPONSE);
            if (templateJSON.has(CloudStackConstants.CS_ERROR_CODE)) {
                errors = this.validateCSEvent(errors, templateJSON.getString(CloudStackConstants.CS_ERROR_TEXT));
                throw new ApplicationException(errors);
            }
            templateArray = (JSONArray) templateJSON.get(CloudStackConstants.ISO_TEMPLATE_NAME);
        } else {
            csResponse = cloudStackTemplateService.registerTemplate(template.getDescription(),
                    template.getFormat().name(), hypervisorService.find(template.getHypervisorId()).getName(),
                    template.getName(), osTypeService.find(template.getOsTypeId()).getUuid(), template.getUrl(),
                    zoneService.find(template.getZoneId()).getUuid(), CloudStackConstants.JSON, optionalFieldValidation(template, optional,userId));
            templateJSON = new JSONObject(csResponse).getJSONObject(CloudStackConstants.CS_REGISTER_TEMPLATE_RESPONSE);
            if (templateJSON.has(CloudStackConstants.CS_ERROR_CODE)) {
                errors = this.validateCSEvent(errors, templateJSON.getString(CloudStackConstants.CS_ERROR_TEXT));
                throw new ApplicationException(errors);
            }
            templateArray = (JSONArray) templateJSON.get(CloudStackConstants.TEMPLATE_NAME);
        }
        for (int i = 0; i < templateArray.length(); i++) {
            JSONObject jsonobject = templateArray.getJSONObject(i);
            template.setUuid(jsonobject.getString(CloudStackConstants.CS_ID));
            template.setDomainId(convertEntityService.getDomainId(jsonobject.getString(CloudStackConstants.CS_DOMAIN_ID)));
            template.setDepartmentId(convertEntityService.getDepartmentByUsernameAndDomains(
                (jsonobject.getString(CloudStackConstants.CS_ACCOUNT)), convertEntityService.getDomain(jsonobject.getString(CloudStackConstants.CS_DOMAIN_ID))));
            if (jsonobject.getBoolean(CloudStackConstants.CS_READY_STATE)) {
                template.setStatus(Status.ACTIVE);
            } else {
                template.setStatus(Status.INACTIVE);
            }
            if (jsonobject.has(CloudStackConstants.CS_TEMPLATE_TYPE)) {
                template.setType(TemplateType.valueOf(jsonobject.getString(CloudStackConstants.CS_TEMPLATE_TYPE)));
            } else {
                template.setType(TemplateType.USER);
            }
        }
        template.setDisplayText(osTypeService.find(template.getOsTypeId()).getDescription());
        template.setSize(0L);
        if(template.getTemplateCreationType()== true) {
            template.setTemplateCreationType(true);
        }
        else {
            template.setTemplateCreationType(false);
        }
    }

    /**
     * Update template/ISO in CS.
     *
     * @param template entity object
     * @param userId user id
     * @throws Exception unhandled errors.
     */
    public void csUpdateTemplate(Template template, Long userId) throws Exception {
        configUtil.setUserServer();
        HashMap<String, String> optional = new HashMap<String, String>();
        optionalFieldValidation(template, optional,userId);
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
     * @return deleted status
     * @throws Exception unhandled errors.
     */
    public Boolean csDeleteTemplate(Long id) throws Exception {
        configUtil.setUserServer();
        Errors errors = null;
        HashMap<String, String> optional = new HashMap<String, String>();
        Template template = templateRepository.findOne(id);
        try {
            String templateResponse = "";
            JSONObject templateJson = null;
            if (template.getFormat().equals(Template.Format.ISO)) {
                templateResponse = cloudStackTemplateService.deleteIso(template.getUuid(), CloudStackConstants.JSON, optional);
                templateJson = new JSONObject(templateResponse).getJSONObject(CloudStackConstants.CS_DELETE_ISO_RESPONSE);
            } else {
                templateResponse = cloudStackTemplateService.deleteTemplate(template.getUuid(), CloudStackConstants.JSON, optional);
                templateJson = new JSONObject(templateResponse).getJSONObject(CloudStackConstants.CS_DELETE_TEMPLATE_RESPONSE);
            }
            Thread.sleep(5000);
            if (templateJson.has(CloudStackConstants.CS_JOB_ID)) {
                String templateJob = cloudStackTemplateService.queryAsyncJobResult(templateJson.getString(CloudStackConstants.CS_JOB_ID),
                       CloudStackConstants.JSON);
                JSONObject jobresult = new JSONObject(templateJob).getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE);
                if (jobresult.getString(CloudStackConstants.CS_JOB_STATUS).equals(CloudStackConstants.SUCCEEDED_JOB_STATUS)) {
                    return true;
                } else if (jobresult.getString(CloudStackConstants.CS_JOB_STATUS).equals(CloudStackConstants.ERROR_JOB_STATUS)) {
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
            errors.addFieldError(TEMPLATE_ARCHITECTURE, "template.architecture");
        }
        if (template.getOsVersion() == null || template.getOsVersion().isEmpty()) {
            errors.addFieldError(TEMPLATE_OSVERSION, "template.osversion.error");
        }
        if (template.getUrl() == null && validstatus) {
            errors.addFieldError(TEMPLATE_URL, "template.url.error");
        }
        if (template.getDetailedDescription() == null || template.getDetailedDescription().isEmpty()) {
            errors.addFieldError(TEMPLATE_DETAILED_DESC, "template.detaileddescription");
        }
        if(template.getTemplateCreationType() == false ) {
            if (template.getTemplateCost() == null) {
            errors.addFieldError(TEMPLATE_COST, "template.cost.error");
            }
        }
        if (template.getMinimumMemory() == null) {
            errors.addFieldError(TEMPLATE_MIN_MEMORY, "template.minimummemory.error");
        }
        if (template.getMinimumCore() == null) {
            errors.addFieldError(TEMPLATE_MIN_CORE, "template.minimumcore.error");
        }
        if (template.getOsCategoryId() == null) {
            errors.addFieldError(TEMPLATE_OS_CATEGORY, "template.oscategory.error");
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
     * @param userId user id
     * @return optional values list
     * @throws Exception raise if error
     */
    public HashMap<String, String> optionalFieldValidation(Template template, HashMap<String, String> optional, Long userId) throws Exception {
        User user = convertEntityService.getOwnerById(userId);

        if (template.getDynamicallyScalable() != null) {
            optional.put(CloudStackConstants.CS_DYNAMIC_SCALABLE, template.getDynamicallyScalable().toString());
        }
        if (template.getExtractable() != null) {
            optional.put(CloudStackConstants.CS_EXTRACTABLE, template.getExtractable().toString());
        }
        if (template.getFeatured() != null) {
            optional.put(CloudStackConstants.CS_FEATURED, template.getFeatured().toString());
        }
        if (template.getShare() != null) {
            optional.put(CloudStackConstants.CS_VISIBILITY, template.getShare().toString());
        }
        if (template.getRouting() != null) {
            optional.put(CloudStackConstants.CS_ROUTING, template.getRouting().toString());
        }
        if (template.getPasswordEnabled() != null) {
            optional.put(CloudStackConstants.CS_PASSWORD_STATUS, template.getPasswordEnabled().toString());
        }
        if (template.getHvm() != null) {
            optional.put(CloudStackConstants.CS_REQUIRES_HVM, template.getHvm().toString());
        }
        if (template.getBootable() != null) {
            optional.put(CloudStackConstants.CS_BOOTABLE, template.getBootable().toString());
        } else {
            optional.put(CloudStackConstants.CS_BOOTABLE, CloudStackConstants.STATUS_ACTIVE);
        }
        if (template.getDepartmentId() != null) {
             optional.put(CloudStackConstants.CS_ACCOUNT,
                     departmentService.find(user.getDepartmentId()).getUserName());
        }
        if (template.getDomainId() != null) {
            optional.put(CloudStackConstants.CS_DOMAIN_ID, convertEntityService.getDomainUuidById(user.getDomainId()));
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

    @Override
    public List<Template> findByTemplateCategory(OsCategory osCategory, String type) throws Exception {
        List<Template> templates;
        if (type.equals(CloudStackConstants.TEMPLATE_NAME)) {
            templates = templateRepository.findByTemplateWithIsoCategory(TemplateType.SYSTEM,
                Status.ACTIVE, osCategory, Format.ISO);
        } else {
            templates = templateRepository.findByTemplateWithoutIsoCategory(TemplateType.SYSTEM,
                Status.ACTIVE, osCategory, Format.ISO);
        }
        return templates;
    }

    @Override
    public Page<Template> findAllByType(PagingAndSorting pagingAndSorting, String type, Boolean featured, Boolean shared,Long userId) throws Exception {
        Page<Template> templates = null;
        User user = convertEntityService.getOwnerById(userId);
        if(user.getType().equals(UserType.ROOT_ADMIN)) {
              if (type.equals(TEMPLATE_FEATURED)) {
                  templates = templateRepository.findAllTemplateByFeatured(TemplateType.SYSTEM, pagingAndSorting.toPageRequest(), featured, shared, true);
              } else if (type.equals(TEMPLATE_COMMUNITY)) {
                  templates = templateRepository.findAllTemplateByCommunity(TemplateType.SYSTEM, pagingAndSorting.toPageRequest(), shared, true);
              }
        }
        else {
            List<User> userList = userService.findByRootAdminUser();
            for(User rootUser: userList) {
                List<Template> templateList = templateRepository.findByTemplateOwnerIdAndIsActive(rootUser.getId(), true);
            if (type.equals(TEMPLATE_FEATURED)) {
                 if(templateList.size() != 0) {
                templates = templateRepository.findTemplateByFeatured(TemplateType.SYSTEM, pagingAndSorting.toPageRequest(), featured, shared, true,user.getDomainId(),rootUser.getId());
                 }
            } else if (type.equals(TEMPLATE_COMMUNITY)) {
                if(templateList.size() != 0) {
                templates = templateRepository.findTemplateByCommunity(TemplateType.SYSTEM, pagingAndSorting.toPageRequest(), shared, true,user.getDomainId(),rootUser.getId());
            }
               }
            }
        }
         return templates;
    }

    @Override
    public Page<Template> findAllByUserIdAndType(PagingAndSorting pagingAndSorting, String type, Long userId) throws Exception {
        User user = convertEntityService.getOwnerById(userId);
        return templateRepository.findTemplateByUserId(TemplateType.SYSTEM, pagingAndSorting.toPageRequest(), user.getId(),user.getDepartmentId(), true);
    }
     /**
     * Add cost for newly created template.
     *
     * @param template entity object
     * @return template cost list
     * @throws Exception unhandled errors.
     */
    public List<TemplateCost> saveTemplateCost(Template template) throws Exception {
        List<TemplateCost> templateCostList = new ArrayList<TemplateCost>();
        Double tempCost = template.getTemplateCost().get(0).getCost();
        TemplateCost templatecost = new TemplateCost();
        templatecost.setCost(tempCost);
        templateCostList.add(templatecost);
        return templateCostList;
    }

    /**
     * Update cost for existing template.
     *
     * @param template entity object
     * @return template cost list
     * @throws Exception unhandled errors.
     */
    public List<TemplateCost> updateTemplateCost(Template template) throws Exception {
        List<TemplateCost> templateCostList = new ArrayList<TemplateCost>();
        Double tempCost = template.getTemplateCost().get(0).getCost();
        Template persistTemplate = find(template.getId());
        List<TemplateCost> templatecostList = templateCostService.findAllByTemplateCost(template.getId());
        if(templatecostList.size() != 0) {
            TemplateCost persistedCost = templatecostList.get(templatecostList.size() - 1);
                int templateGBCost = Double.compare(offeringNullCheck(tempCost),offeringNullCheck(persistedCost.getCost()));
                if(templateGBCost >0 || templateGBCost <0) {
                    this.templateCostSave(template);
                }

        } else {
            this.templateCostSave(template);
        }
        return templateCostList;
    }

    @Override
    public Template save(Template template) throws Exception {
         if (!template.getSyncFlag()) {
             return templateRepository.save(template);
         }
         return template;
    }

    @Override
    public Template update(Template template) throws Exception {
         if (!template.getSyncFlag()) {
             return templateRepository.save(template);
         }
         return template;
    }

    @Override
    public List<Template> findAllTemplatesByIsActiveAndType(Boolean isActive) throws Exception {
        return (List<Template>) templateRepository.findAllTemplatesByIsActiveAndType(TemplateType.SYSTEM, true);
    }

    /**
     * Set optional value for MR.ping api call.
     *
     * @param templateCost template cost
     * @return status
     * @throws Exception raise if error
     */
    public Boolean savePingProject(Template templateCost) throws Exception {
        JSONObject optional = new JSONObject();
        optional.put(PingConstants.PLAN_UUID, templateCost.getUuid());
        optional.put(PingConstants.NAME, templateCost.getName());
        optional.put(PingConstants.REFERENCE_NAME, PingConstants.ADMIN_TEMPLATE);
        optional.put(PingConstants.GROUP_NAME, PingConstants.TEMPLATE);
        optional.put(PingConstants.TOTAL_COST, templateCost.getTemplateCost().get(0).getCost());
        optional.put(PingConstants.ZONE_ID, zoneService.find(templateCost.getZoneId()).getUuid());
        optional.put(PingConstants.ISADMIN, !templateCost.getTemplateCreationType());
        optional.put(PingConstants.ONE_TIME_CHARGEABLE, templateCost.getOneTimeChargeable());
        pingService.addPlanCost(optional);
        return true;
    }

    @Override
    public List<Template> findAllTemplateByType(String type, Boolean featured, Boolean shared,Long userId) throws Exception {
        List<Template> templates = null;
        User user = convertEntityService.getOwnerById(userId);
        if(user.getType().equals(UserType.ROOT_ADMIN)) {
              if (type.equals(TEMPLATE_FEATURED)) {
                  templates = templateRepository.listAllTemplateByFeatured(TemplateType.SYSTEM, featured, shared, true);
              } else if (type.equals(TEMPLATE_COMMUNITY)) {
                  templates = templateRepository.listAllTemplateByCommunity(TemplateType.SYSTEM, shared, true);
              }
        }
        else {
            List<User> userList = userService.findByRootAdminUser();
            for(User rootUser: userList) {
                List<Template> templateList = templateRepository.findByTemplateOwnerIdAndIsActive(rootUser.getId(), true);

            if (type.equals(TEMPLATE_FEATURED)) {
                if(templateList.size() != 0) {
                templates = templateRepository.listTemplateByFeaturedAndDomainId(TemplateType.SYSTEM, featured, shared, true,user.getDomainId(),rootUser.getId());
                }
            } else if (type.equals(TEMPLATE_COMMUNITY)) {
                if(templateList.size() != 0) {
                templates = templateRepository.listTemplateByCommunity(TemplateType.SYSTEM, shared, true,user.getDomainId(),rootUser.getId());
            }
            }
          }
        }
        return templates;
    }

    /**
     * To save template cost.
     *
     * @param template object.
     * @return template.
     * @throws Exception if error occurs.
     */
    private Template templateCostSave(Template template) throws Exception {
         List<TemplateCost> templateCostList = new ArrayList<TemplateCost>();
         Double tempCost = template.getTemplateCost().get(0).getCost();
         Template persistTemplate = find(template.getId());
         TemplateCost templatecost = new TemplateCost();
             templatecost.setCost(tempCost);
             templatecost.setTemplateCostId(template.getId());
             templatecost = templateCostService.save(templatecost);
             templateCostList.add(templatecost);
         templateCostList.addAll(persistTemplate.getTemplateCost());
         template.setTemplateCost(templateCostList);
        return template;
    }

    /**
     * Offering cost null value check.
     *
     * @param value offering cost
     * @return double value
     */
    public Double offeringNullCheck(Double value) {
        if (value == null) {
            value = 0.0;
        }
        return value;
    }

    
	@Override
	public List<Template> findAllByUserIdIsActiveAndShare(TemplateType type, Status status, Boolean isActive,
			Long userId) throws Exception {
		
		User user = convertEntityService.getOwnerById(userId);
        if (user != null && !user.getType().equals(UserType.ROOT_ADMIN)) {
            if (user.getType().equals(UserType.DOMAIN_ADMIN)) {
            	return templateRepository.findAllByDomainIdIsActiveAndShare(type, false, true, Template.Status.ACTIVE, user.getDomainId());
            } else {
            	return templateRepository.findAllByUserId(type, userId, user.getDepartmentId(), true);
            }
        }
    	return templateRepository.findAllByCommunity(type, true, Template.Status.ACTIVE, true);
	}
}
