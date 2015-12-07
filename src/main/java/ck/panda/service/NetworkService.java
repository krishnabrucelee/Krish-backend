package ck.panda.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Network;
import ck.panda.util.domain.CRUDService;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Service interface for GuestNetwork entity.
 *
 */
@Service
public interface NetworkService extends CRUDService<Network> {

    List<Network> findAllFromCSServerByDomain() throws Exception;

    /**
     * To get network from cloudstack server.
     *
     * @param uuid network uuid.
     * @return zone from server
     * @throws Exception unhandled errors.
     */
    Network findByUUID(String uuid) throws Exception;

    /**
     * To get list of networks from department.
     *
     * @param department department.
     * @return network list from server.
     * @throws Exception unhandled errors.
     */
    List<Network> findByDepartment(Long department) throws Exception;

    /**
     * Soft delete for Network.
     *
     * @param network get Network id.
     * @return network
     * @throws Exception exception
     */
    Network softDelete(Network network) throws Exception;

    /**
     * Paging and Sorting for displaying more number of elements in list which
     * are active.
     *
     * @param page pagination
     * @return sorted values.
     * @throws Exception unhandled errors.
     */
    Page<Network> findAllByActive(PagingAndSorting page) throws Exception;
}
