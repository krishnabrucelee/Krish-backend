package ck.panda.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Project;
import ck.panda.domain.repository.jpa.ProjectRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;
import ck.panda.util.error.exception.EntityNotFoundException;

/**
 * Project service implementation used to get list of project and save ,delete, update the project in application
 * database.
 *
 */
@Service
public class ProjectServiceImpl implements ProjectService {

   /** Validator attribute. */
   @Autowired
   private AppValidator validator;

   /** project repository reference. */
   @Autowired
   private ProjectRepository projectRepository;

   @Override
   public Project save(Project project) throws Exception {
      Errors errors = validator.rejectIfNullEntity("project", project);
      errors = validator.validateEntity(project, errors);
      errors = this.validateByName(errors, project.getName(), project.getDepartment(), 0L);
      // Validation
      if (errors.hasErrors()) {
         throw new ApplicationException(errors);
      } else {
         project.setIsActive(true);
         return projectRepository.save(project);
      }
   }

   @Override
   public Project update(Project project) throws Exception {
      Errors errors = validator.rejectIfNullEntity("project", project);
      errors = validator.validateEntity(project, errors);
      errors = this.validateByName(errors, project.getName(), project.getDepartment(), project.getId());
      // Validation
      if (errors.hasErrors()) {
         throw new ApplicationException(errors);
      } else {
         return projectRepository.save(project);
      }
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
      return projectRepository.findAllByActive(pagingAndSorting.toPageRequest());

   }

   @Override
   public List<Project> findAll() throws Exception {
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
      if (projectRepository.findByNameAndDepartment(name, department, projectId) != null) {
         errors.addFieldError("name", "project.already.exist.for.same.department");
      }
      return errors;
   }

   @Override
   public Project softDelete(Project project) throws Exception {
      return projectRepository.save(project);
   }

   @Override
   public Page<Project> findAllByActive(PagingAndSorting pagingAndSorting) throws Exception {
      return projectRepository.findAllByActive(pagingAndSorting.toPageRequest());
   }

   @Override
   public List<Project> findByName(String query) throws Exception {
      return (List<Project>) projectRepository.findByName(query);
   }

   @Override
   public List<Project> findAllByActive() throws Exception {
      return projectRepository.findAllByActive();
   }
}
