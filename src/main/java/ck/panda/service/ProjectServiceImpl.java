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
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.CloudStackConfiguration;
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Domain;
import ck.panda.domain.entity.Project;
import ck.panda.domain.repository.jpa.DomainRepository;
import ck.panda.domain.repository.jpa.ProjectRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackProjectService;
import ck.panda.util.CloudStackServer;
import ck.panda.util.ConvertUtil;
import ck.panda.util.TokenDetails;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;
import ck.panda.util.error.exception.EntityNotFoundException;

/**
 * Project service implementation used to get list of project and save ,delete, update the project in
 * application database.
 */
@Service
public class ProjectServiceImpl implements ProjectService {
    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectServiceImpl.class);

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** project repository reference. */
    @Autowired
    private ProjectRepository projectRepository;

    /** CloudStack connector reference for instance. */
    @Autowired
    private CloudStackProjectService cloudStackProjectService;

    /** CloudStack connector. */
    @Autowired
    private CloudStackServer server;

    /** CloudStack configuration . */
    @Autowired
    private CloudStackConfigurationService cloudConfigService;

    /** Convert entity repository reference. */
    @Autowired
    private ConvertUtil entity;

    /** Autowired TokenDetails */
    @Autowired
    TokenDetails tokenDetails;

    /** Domain repository reference. */
    @Autowired
    private DomainRepository domainRepository;

    @Override
    public Project save(Project project) throws Exception {
        if (project.getSyncFlag()) {
        Errors errors = validator.rejectIfNullEntity("project", project);
        errors = validator.validateEntity(project, errors);
        errors = this.validateByName(errors, project.getName(), project.getDepartment(), 0L);
        // Validation
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
            HashMap<String, String> optional = new HashMap<String, String>();
            optional.put("domainid", project.getDepartment().getDomain().getUuid());
            optional.put("account", project.getDepartment().getUserName());
            CloudStackConfiguration cloudConfig = cloudConfigService.find(1L);
            server.setServer(cloudConfig.getApiURL(), cloudConfig.getSecretKey(), cloudConfig.getApiKey());
            cloudStackProjectService.setServer(server);
            LOGGER.debug("Cloud stack connectivity at VM", cloudConfig.getApiKey());
            LOGGER.debug("Cloud stack connectivity at domain", project.getDepartment().getDomain().getUuid());
            String csResponse = cloudStackProjectService.createProject("json", project.getName(),
                    project.getDescription(), optional);
            JSONObject csProject = new JSONObject(csResponse).getJSONObject("createprojectresponse");
            if (csProject.has("errorcode")) {
                errors = this.validateEvent(errors, csProject.getString("errortext"));
                throw new ApplicationException(errors);
            } else {
                LOGGER.debug("Project UUID", csProject.getString("id"));
                project.setUuid(csProject.getString("id"));
                String instancePro = cloudStackProjectService.queryAsyncJobResult(csProject.getString("jobid"), "json");
                JSONObject resProject = new JSONObject(instancePro).getJSONObject("queryasyncjobresultresponse");
                if (resProject.getString("jobstatus").equals("2")) {
                    errors = this.validateEvent(errors, csProject.getString("errortext"));
                    project.setIsActive(false);
                } else {
                    project.setIsActive(true);
                    project.setStatus(Project.Status.ENABLED);
                }
             }
          }
        }
           return projectRepository.save(project);
    }

    @Override
    public Project update(Project project) throws Exception {
        if (project.getSyncFlag()) {
            Errors errors = validator.rejectIfNullEntity("project", project);
            errors = validator.validateEntity(project, errors);
            errors = this.validateByName(errors, project.getName(), project.getDepartment(), project.getId());
            // Validation
            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
                CloudStackConfiguration cloudConfig = cloudConfigService.find(1L);
                server.setServer(cloudConfig.getApiURL(), cloudConfig.getSecretKey(), cloudConfig.getApiKey());
                cloudStackProjectService.setServer(server);
                HashMap<String, String> optional = new HashMap<String, String>();
                optional.put("domainid", project.getDepartment().getDomain().getUuid());
                optional.put("account", project.getDepartment().getUserName());
                String csResponse = cloudStackProjectService.updateProject(project.getUuid(), project.getDescription(),
                        "json", optional);
                JSONObject csProject = new JSONObject(csResponse).getJSONObject("updateprojectresponse");
                if (csProject.has("errorcode")) {
                    errors = this.validateEvent(errors, csProject.getString("errortext"));
                    throw new ApplicationException(errors);
                }
            }

        }
        return projectRepository.save(project);
    }


    @Override
    public void delete(Project project) throws Exception {
        projectRepository.delete(project);
    }

    @Override
    public void delete(Long id) throws Exception {
        projectRepository.delete(id);
    }

    @Override
    public Project find(Long id) throws Exception {
        Project project = projectRepository.findOne(id);
        // find validation
        if (project == null) {
            throw new EntityNotFoundException("project.not.found");
        }
        return projectRepository.findOne(id);
    }

    @Override
    public Page<Project> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return projectRepository.findAllByActive(pagingAndSorting.toPageRequest(), true, Project.Status.ENABLED);
    }

    @Override
    public List<Project> findAll() throws Exception {
        return (List<Project>) projectRepository.findAll();
    }

    @Override
    public List<Project> findByAll() throws Exception {
        Domain domain = domainRepository.findOne(Long.valueOf(tokenDetails.getTokenDetails("domainid")));
        if(domain != null && !domain.getName().equals("ROOT")) {
            return (List<Project>) projectRepository.findbyDomain(domain.getId());
        }
        return (List<Project>) projectRepository.findAll();
    }

    /**
     * Check the Project name already exist or not for same department.
     *
     * @param errors already existing error list.
     * @param name name of the project.
     * @param department department object.
     * @param projectId project id.
     * @return errors.
     * @throws Exception if error occurs.
     */
    private Errors validateByName(Errors errors, String name, Department department, Long projectId) throws Exception {
        if (projectRepository.findByNameAndDepartment(true, name, department, projectId) != null) {
            errors.addFieldError("name", "project.already.exist.for.same.department");
        }
        return errors;
    }

    /**
     * Check the virtual machine CS error handling.
     *
     * @param errors error creating status.
     * @param errmessage error message.
     * @return errors.
     * @throws Exception if error occurs.
     */
    private Errors validateEvent(Errors errors, String errmessage) throws Exception {
        errors.addGlobalError(errmessage);
        return errors;
    }

    @Override
    public Project softDelete(Project project) throws Exception {
        Errors errors = validator.rejectIfNullEntity("project", project);
        // Validation
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
            CloudStackConfiguration cloudConfig = cloudConfigService.find(1L);
            server.setServer(cloudConfig.getApiURL(), cloudConfig.getSecretKey(), cloudConfig.getApiKey());
            cloudStackProjectService.setServer(server);
            HashMap<String, String> optional = new HashMap<String, String>();
            optional.put("domainid", project.getDepartment().getDomain().getUuid());
            optional.put("account", project.getDepartment().getUserName());
            String csResponse = cloudStackProjectService.deleteProject(project.getUuid());
            JSONObject csProject = new JSONObject(csResponse).getJSONObject("deleteprojectresponse");
            if (csProject.has("errorcode")) {
                errors = this.validateEvent(errors, csProject.getString("errortext"));
                throw new ApplicationException(errors);
            } else {
                project.setIsActive(false);
                project.setStatus(Project.Status.DELETED);
            }
        }
        return projectRepository.save(project);
    }

    @Override
    public Page<Project> findAllByActive(Boolean isActive, PagingAndSorting pagingAndSorting) throws Exception {
        Domain domain = domainRepository.findOne(Long.valueOf(tokenDetails.getTokenDetails("domainid")));
        if(domain != null && !domain.getName().equals("ROOT")) {
            return projectRepository.findAllProjectByDomain(domain.getId(), pagingAndSorting.toPageRequest(), isActive, Project.Status.ENABLED);
        }
        return projectRepository.findAllByActive(pagingAndSorting.toPageRequest(), isActive, Project.Status.ENABLED);
    }

    @Override
    public List<Project> findAllByActive(Boolean isActive) throws Exception {
        return projectRepository.findAllByActive(isActive);
    }

    @Override
    public List<Project> findbyDomain(Long id) {
        return projectRepository.findbyDomain(id);
    }

    @Override
    public List<Project> findAllFromCSServerByDomain() throws Exception {
        List<Project> projectList = new ArrayList<Project>();
        HashMap<String, String> projectMap = new HashMap<String, String>();
        projectMap.put("listall", "true");
        // 1. Get the list of Project from CS server using CS connector
        String response = cloudStackProjectService.listProjects("json", projectMap);
        JSONArray projectListJSON = null;
        JSONObject responseObject = new JSONObject(response).getJSONObject("listprojectsresponse");
        if (responseObject.has("project")) {
            projectListJSON = responseObject.getJSONArray("project");
            // 2. Iterate the json list, convert the single json entity to
            // Project
            for (int i = 0, size = projectListJSON.length(); i < size; i++) {
                // 2.1 Call convert by passing JSONObject to Project entity
                // and Add
                // the converted Project entity to list
                projectList.add(Project.convert(projectListJSON.getJSONObject(i), entity));
            }
        }
        return projectList;
    }

    @Override
    public Project findByUuidAndIsActive(String uuid, Boolean isActive) throws Exception {
        return projectRepository.findByUuidAndIsActive(uuid, isActive);
    }

    @Override
    public Project findByUuid(String uuid) throws Exception {
        return projectRepository.findByUuid(uuid);
    }

    @Override
    public List<Project> findByDepartmentAndIsActive(Long id, Boolean isActive) throws Exception {
        return projectRepository.findByDepartmentAndIsActive(id, true);
    }

}
