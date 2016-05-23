package ck.panda.domain.repository.jpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.PhysicalNetwork;

/**
 * JPA repository for physical network entity.
 */
public interface PhysicalNetworkRepository extends PagingAndSortingRepository<PhysicalNetwork, Long> {

    /**
     * Find physical network by uuid.
     *
     * @param uuid physical network uuid.
     * @return physical network
     */
    @Query(value = "SELECT physicalNetwork FROM PhysicalNetwork physicalNetwork WHERE physicalNetwork.uuid LIKE :uuid")
    PhysicalNetwork findByUuid(@Param("uuid") String uuid);

}
