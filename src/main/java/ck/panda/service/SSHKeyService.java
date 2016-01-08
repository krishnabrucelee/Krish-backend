package ck.panda.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.SSHKey;
import ck.panda.util.domain.CRUDService;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Service class for SSH key.
 *
 * This service provides basic CRUD and essential api's for SSH key related business actions.
 */
@Service
public interface SSHKeyService extends CRUDService<SSHKey> {

    /**
     * To get list of SSH Key from cloudstack server.
     *
     * @return SSH Key list from server
     * @throws Exception unhandled errors.
     */
    List<SSHKey> findAllFromCSServer() throws Exception;

    /**
     * Delete the SSH Key.
     *
     * @param sshkey SSHKey entity.
     * @return SSHKey.
     * @throws Exception error occurs
     */
    SSHKey softDelete(SSHKey sshkey) throws Exception;

    /**
     * Find all the SSH keys with active status.
     *
     * @param pagingAndSorting pagination and sorting values.
     * @return list of SSH Keys with pagination.
     * @throws Exception error occurs
     */
    Page<SSHKey> findAllByActive(PagingAndSorting pagingAndSorting) throws Exception;

    /**
     * Find all the SSH Keys with active status.
     *
     * @param isActive SSH Key status Active/Inactive
     * @return list of SSH Keys with active status
     * @throws Exception error occurs.
     */
    List<SSHKey> findAllByIsActive(Boolean isActive) throws Exception;

    /**
     * Find all the SSH Keys for sync.
     *
     * @return list of SSH Keys with active status
     * @throws Exception error occurs.
     */
    List<SSHKey> findAllBySync() throws Exception;

}
