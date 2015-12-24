package ck.panda.domain.repository.jpa;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import ck.panda.domain.entity.Domain;
import ck.panda.domain.entity.Nic;
import ck.panda.domain.entity.Volume;

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
     * Find by Instance Id.
     * 
     * @param vmInstanceId from nic
     * @return nic.
     */
    @Query(value = "select nic from Nic nic where  nic.vmInstanceId=:vmInstanceId AND nic.isActive =:isActive" )
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

    @Query(value = "select nic from Nic nic where  nic.vmInstanceId=:vmInstanceId AND nic.isDefault =:isDefault" )
	Nic findByInstanceIdAndIsDefault(@Param("vmInstanceId") Long vmInstanceId, @Param("isDefault") Boolean isDefault);
}