package ck.panda.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Project;
import ck.panda.domain.repository.jpa.ProjectRepository;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Project service implementation used to get list of project and save ,delete, update the project in application
 * database.
 *
 */
@Service
public class ProjectServiceImpl implements ProjectService {

    /** project repository reference. */
    @Autowired
    private ProjectRepository projectRepository;

    @Override
    public Project save(Project project) throws Exception {
        return projectRepository.save(project);
    }

    @Override
    public Project update(Project project) throws Exception {
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
        return projectRepository.findOne(id);
    }

    @Override
    public Page<Project> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return null;
    }

    @Override
    public List<Project> findAll() throws Exception {
        return (List<Project>) projectRepository.findAll();
    }
}
