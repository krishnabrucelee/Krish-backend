package ck.panda.domain.repository.jpa;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.SSHKey;

/**
 * JPA Repository for SSHKey entity.
 */
public interface SSHKeyRepository extends PagingAndSortingRepository<SSHKey, Long> {

    /**
     * Find the SSH Key for same department with SSH Key and is active status.
     *
     * @param name name of the SSH Key
     * @param departmentId Department reference
     * @param isActive get the SSH Key list based on active/inactive status
     * @return SSH Key name
     */
    @Query(value = "SELECT ssh FROM SSHKey ssh WHERE ssh.name=:name AND  ssh.departmentId =:departmentId AND "
                 + "ssh.isActive =:isActive")
    SSHKey findByNameAndDepartmentAndIsActive(@Param("name") String name, @Param("departmentId") Long departmentId,
            @Param("isActive") Boolean isActive);

    /**
     * Find all the active or inactive SSH Keys with pagination.
     *
     * @param pageable to get the list with pagination
     * @param isActive get the SSH Key list based on active/inactive status
     * @return list of SSH Keys
     */
    @Query(value = "SELECT ssh FROM SSHKey ssh WHERE ssh.isActive =:isActive")
    Page<SSHKey> findAllByIsActive(Pageable pageable, @Param("isActive") Boolean isActive);

    /**
     * Find all the SSH Key with active status.
     *
     * @param isActive get the SSH Key list based on active/inactive status
     * @return list of SSH Keys
     */
    @Query(value = "SELECT ssh FROM SSHKey ssh WHERE ssh.isActive =:isActive")
    List<SSHKey> findAllByIsActive(@Param("isActive") Boolean isActive);

    /**
     * Find all the SSH Key.
     *
     * @param departmentId to get the SSH Key list
     * @param pageable to get the list with pagination
     * @return list of SSH Keys
     */
    @Query(value = "SELECT ssh FROM SSHKey ssh WHERE ssh.departmentId=:departmentId")
    Page<SSHKey> findAllByDepartment(@Param("departmentId") Long departmentId, Pageable pageable);

    /**
     * Find all the SSH Key with active status.
     *
     * @param departmentId to get the SSH Key list
     * @param isActive get the SSH Key list based on active/inactive status
     * @param pageable to get the list with pagination
     * @return list of SSH Keys
     */
    @Query(value = "SELECT ssh FROM SSHKey ssh WHERE ssh.departmentId=:departmentId AND ssh.isActive =:isActive")
    Page<SSHKey> findAllByDepartmentIsActive(@Param("departmentId") Long departmentId,
            @Param("isActive") Boolean isActive, Pageable pageable);

    /**
     * Find all the SSH Key with active status.
     *
     * @param departmentId to get the SSH Key list
     * @param isActive get the SSH Key list based on active/inactive status
     * @return list of SSH Keys
     */
    @Query(value = "SELECT ssh FROM SSHKey ssh WHERE ssh.departmentId=:departmentId AND ssh.isActive =:isActive AND ssh.projectId = NULL")
    List<SSHKey> findByDepartmentAndIsActive(@Param("departmentId") Long departmentId,
            @Param("isActive") Boolean isActive);

    /**
     * Find all the SSH Key with active status.
     *
     * @param departmentId to get the SSH Key list
     * @param isActive get the SSH Key list based on active/inactive status
     * @return list of SSH Keys
     */
    @Query(value = "SELECT ssh FROM SSHKey ssh WHERE ssh.departmentId=:departmentId AND ssh.isActive =:isActive")
    List<SSHKey> findAllByDepartmentAndIsActive(@Param("departmentId") Long departmentId,
            @Param("isActive") Boolean isActive);

    /**
     * Get the list of SSH Keys by domain and status.
     *
     * @param domainId to get the SSH key list
     * @param isActive get the SSH Key list based on active/inactive status
     * @param pageable to get the list with pagination
     * @return list of SSH Keys
     */
    @Query(value = "SELECT ssh FROM SSHKey ssh WHERE ssh.domainId=:domainId AND ssh.isActive =:isActive")
    Page<SSHKey> findAllByDomainIsActive(@Param("domainId") Long domainId, @Param("isActive") Boolean isActive,
        Pageable pageable);

    /**
     * Find all the SSH Key with active status.
     *
     * @param domainId to get the SSH Key list
     * @param isActive get the SSH Key list based on active/inactive status
     * @return list of SSH Keys
     */
    @Query(value = "SELECT ssh FROM SSHKey ssh WHERE ssh.domainId=:domainId AND ssh.isActive =:isActive")
    List<SSHKey> findAllByDomainIsActive(@Param("domainId") Long domainId, @Param("isActive") Boolean isActive);

    /**
     * Find all the SSH Key with active status.
     *
     * @param projectId to get the SSH Key list
     * @param isActive get the SSH Key list based on active/inactive status
     * @return list of SSH Keys
     */
    @Query(value = "SELECT ssh FROM SSHKey ssh WHERE ssh.projectId=:projectId AND ssh.isActive =:isActive")
    List<SSHKey> findAllByProjectAndIsActive(@Param("projectId") Long projectId,
            @Param("isActive") Boolean isActive);

    /**
     * Find all the domain based active or inactive SSH Keys with pagination.
     *
     * @param domainId domain id of the SSH key
     * @param isActive get the SSH Key list based on active/inactive status
     * @param pageable to get the list with pagination
     * @return list of SSH Keys
     */
    @Query(value = "SELECT ssh FROM SSHKey ssh WHERE ssh.domainId =:domainId AND ssh.isActive =:isActive")
    Page<SSHKey> findAllByDomainIdAndIsActive(@Param("domainId") Long domainId, @Param("isActive") Boolean isActive, Pageable pageable);
}
