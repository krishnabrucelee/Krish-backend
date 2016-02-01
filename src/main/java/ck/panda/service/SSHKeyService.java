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
     * Save the SSH Key.
     *
     * @param sshkey SSHKey entity
     * @param id of the login user
     * @return SSHKey
     * @throws Exception error occurs
     */
    SSHKey save(SSHKey sshkey, Long id) throws Exception;

    /**
     * To get list of SSH Key from cloudstack server.
     *
     * @return SSH Key list from server
     * @throws Exception unhandled errors
     */
    List<SSHKey> findAllFromCSServer() throws Exception;

    /**
     * To get list of SSH Key.
     *
     * @param pagingAndSorting parameters
     * @param id of the login user
     * @return SSH Key list with pagination
     * @throws Exception unhandled errors
     */
    Page<SSHKey> findAll(PagingAndSorting pagingAndSorting, Long id) throws Exception;

    /**
     * To get list of SSH Key.
     *
     * @param id of the login user
     * @return SSH Key list
     * @throws Exception unhandled errors
     */
    List<SSHKey> findAll(Long id) throws Exception;

    /**
     * Delete the SSH Key.
     *
     * @param sshkey SSHKey entity
     * @param id of the login user
     * @return SSHKey
     * @throws Exception error occurs
     */
    SSHKey softDelete(SSHKey sshkey, Long id) throws Exception;

    /**
     * Find all the SSH Keys for sync.
     *
     * @return list of SSH Keys with active status
     * @throws Exception error occurs
     */
    List<SSHKey> findAllBySync() throws Exception;
}
