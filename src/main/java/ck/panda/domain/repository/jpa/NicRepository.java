package ck.panda.domain.repository.jpa;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Nic;

/**
 * Jpa Repository for nic entity.
 *
 */
@Service
public interface NicRepository extends PagingAndSortingRepository<Nic, Long> {

    /**
     * Find nic by uuid.
     *
     * @param uuid of nic.
     * @return nic object.
     */
    @Query(value = "select nic from Nic nic where nic.uuid = :uuid")
    Nic findByUUID(@Param("uuid") String uuid);

    /**
     * Find all by Instance Id.
     *
     * @param isActive get the nic list based on active/inactive status.
     * @param vmInstanceId from nic
     * @return nic.
     */
    @Query(value = "select nic from Nic nic where  nic.vmInstanceId=:vmInstanceId AND nic.isActive =:isActive ORDER BY nic.isDefault DESC")
    List<Nic> findByInstanceAndIsActive(@Param("vmInstanceId") Long vmInstanceId, @Param("isActive") Boolean isActive);

    /**
     * Find all the active or inactive nics with pagination.
     *
     * @param pageable to get the list with pagination.
     * @param isActive get the nic list based on active/inactive status.
     * @return list of nics.
     */
    @Query(value = "select nic from Nic nic where nic.isActive =:isActive")
    Page<Nic> findAllByIsActive(Pageable pageable, @Param("isActive") Boolean isActive);

    /**
    * Find by Instance Id.
    *
    * @param vmInstanceId from nic
    * @param isDefault true/false.
    * @return nic.
    */
    @Query(value = "select nic from Nic nic where  nic.vmInstanceId=:vmInstanceId AND nic.isDefault =:isDefault")
    Nic findByInstanceIdAndIsDefault(@Param("vmInstanceId") Long vmInstanceId, @Param("isDefault") Boolean isDefault);

    /**
     * Find Nic by id.
     *
     * @param id Nic id.
     * @return id
     */
    @Query(value = "select nic from Nic nic where nic.id LIKE :id ")
    Nic findById(@Param("id") Long id);

    /**
     * List nic by Network.
     *
     * @param networkId for that network.
     * @param isActive get the nic list based on active/inactive status.
     * @return nics.
     */
    @Query(value = "select nic from Nic nic where  nic.networkId=:networkId AND nic.isActive =:isActive")
    List<Nic> findByNetworkIdAndIsActive(@Param("networkId") Long networkId, @Param("isActive") Boolean isActive);

    /**
     * List nic by Network.
     *
     * @param networkId for that network.
     * @param isActive get the nic list based on active/inactive status.
     * @return nics.
     */
    @Query(value = "select nic from Nic nic where  nic.networkId=:networkId AND nic.vmInstanceId=:vmInstanceId AND nic.isActive =:isActive")
    Nic findAllNetworkByIsActive(@Param("networkId") Long networkId, @Param("vmInstanceId") Long vmInstanceId, @Param("isActive") Boolean isActive);

    /**
     * Find by Instance Id and isActive status.
     *
     * @param isActive get the nic list based on active/inactive status.
     * @param vmInstanceId from nic
     * @return nic.
     */
    @Query(value = "select nic from Nic nic where  nic.vmInstanceId=:vmInstanceId AND nic.isActive =:isActive")
     Nic findByVmInstanceAndIsActiveStatus(@Param("vmInstanceId") Long vmInstanceId, @Param("isActive") Boolean isActive);
}
