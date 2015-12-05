package ck.panda.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Permission;
import ck.panda.domain.repository.jpa.PermissionRepository;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Permission service implementation class.
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    /** Autowired permissionRepository. */
    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public Permission save(Permission permission) throws Exception {
    	return permissionRepository.save(permission);
    }

    @Override
    public Permission update(Permission t) throws Exception {
       return null;
    }

    @Override
    public void delete(Permission t) throws Exception {

    }

    @Override
    public void delete(Long id) throws Exception {

    }

    @Override
    public Permission find(Long id) throws Exception {
       return null;
    }

    @Override
    public Page<Permission> findAll(PagingAndSorting pagingAndSorting) throws Exception {
       return null;
    }

    @Override
    public List<Permission> findAll() throws Exception {
       return (List<Permission>) permissionRepository.findAll();
    }

    /**
     * List the permission.
     * @return list of permission.
     * @throws Exception error occurs.
     */
    @Override
    public List<Permission> getPermissionList() throws Exception {
       return permissionRepository.getPermissionList();
    }

}
