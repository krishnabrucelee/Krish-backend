package ck.panda.service;

import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Project;
import ck.panda.util.domain.CRUDService;

/**
 * Service interface for Project.
 * This service provides basic crud functions of projects.
 */
@Service
public interface ProjectService extends CRUDService<Project> {

}
