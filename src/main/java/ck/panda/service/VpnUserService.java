package ck.panda.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.VpnUser;
import ck.panda.util.domain.CRUDService;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Service interface for VPN user.
 *
 */
@Service
public interface VpnUserService extends CRUDService<VpnUser> {

    /**
     * Find all VPN User by department and domain.
     *
     * @param departmentId department id
     * @param domainId domain id
     * @param isActive status Active/Inactive
     * @return list of VPN users.
     * @throws Exception if error occurs.
     */
    List<VpnUser> findAllByDepartmentAndDomainAndIsActive(Long departmentId, Long domainId, Boolean isActive) throws Exception;

    /**
     * Paging and Sorting for displaying more number of elements in list.
     *
     * @param pagingAndSorting sortable method.
     * @return sorted values of VPN users.
     * @throws Exception unhandled errors.
     */
    Page<VpnUser> findAllByActive(PagingAndSorting pagingAndSorting) throws Exception;

    /**
     * Find all VPN user from CloudStack.
     *
     * @return list of VPN users.
     * @throws Exception unhandled errors.
     */
    List<VpnUser> findAllFromCSServer() throws Exception;

    /**
     * Soft delete method for VPN user.
     *
     * @param vpnUser for network
     * @return vpn user.
     * @throws Exception unhandled errors.
     */
    VpnUser softDelete(VpnUser vpnUser) throws Exception;

    /**
     * Find VPN user by uuid.
     *
     * @param uuid of VPN user.
     * @return VPN user object.
     * @throws Exception unhandled errors.
     */
    VpnUser findbyUUID(String uuid) throws Exception;

    /**
     * Find VPN user by by domain and department.
     *
     * @param domainId of VPN user.
     * @param departmentId of VPN user.
     * @return VPN user object.
     * @throws Exception unhandled errors.
     */
    List<VpnUser> findByDomainWithDepartment(Long domainId, Long departmentId) throws Exception;

    /**
     * Find VPN user by account, user and domainid.
     *
     * @param userName of VPN user.
     * @param account of VPN user.
     * @param domainUUid of VPN user.
     * @return VPN user object.
     * @throws Exception unhandled errors.
     */
    VpnUser findbyDomainWithAccountAndUser(String userName, String account, String domainUUid) throws Exception;
}
