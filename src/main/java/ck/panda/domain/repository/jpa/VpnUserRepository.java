package ck.panda.domain.repository.jpa;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.VpnUser;

/**
 * JPA repository for VPN user.
 */
public interface VpnUserRepository extends PagingAndSortingRepository<VpnUser, Long> {

    /**
     * Find all the active or inactive VPN user with pagination.
     *
     * @param pageable to get the list with pagination.
     * @param isActive get the VPN user list based on active/inactive status.
     * @return list of VPN users.
     */
    @Query(value = "SELECT vpn FROM VpnUser vpn WHERE vpn.isActive =:isActive")
    Page<VpnUser> findAllByIsActive(Pageable pageable, @Param("isActive") Boolean isActive);

    /**
     * Find VPN user by uuid.
     *
     * @param uuid of VPN user.
     * @return VPN user object.
     */
    @Query(value = "SELECT vpn FROM VpnUser vpn WHERE vpn.uuid = :uuid")
    VpnUser findByUUID(@Param("uuid") String uuid);

    /**
     * Find VPN user by uuid.
     *
     * @param domainId of VPN user.
     * @param departmentId of VPN user.
     * @param isActive of VPN user.
     * @return VPN user object.
     */
    @Query(value = "SELECT vpn FROM VpnUser vpn WHERE vpn.domainId = :domainId AND vpn.departmentId = :departmentId AND vpn.isActive =:isActive")
    List<VpnUser> findByDomainWithDepartment(@Param("domainId") Long domainId, @Param("departmentId") Long departmentId, @Param("isActive") Boolean isActive);

    /**
     * Find VPN user by uuid.
     *
     * @param userName of VPN user.
     * @param departmentId of VPN user.
     * @param domainId of VPN user.
     * @param isActive of VPN user.
     * @return VPN user object.
     */
    @Query(value = "SELECT vpn FROM VpnUser vpn WHERE vpn.userName = :userName AND vpn.departmentId = :departmentId AND vpn.domainId = :domainId AND vpn.isActive =:isActive")
    VpnUser findbyDomainWithAccountAndUser(@Param("userName") String userName, @Param("departmentId") Long departmentId, @Param("domainId") Long domainId, @Param("isActive") Boolean isActive);
}
