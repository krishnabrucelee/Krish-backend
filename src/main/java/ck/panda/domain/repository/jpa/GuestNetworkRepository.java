package ck.panda.domain.repository.jpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.GuestNetwork;

/**
 * JPA repository for GuestNetwork entity.
 */
public interface GuestNetworkRepository extends PagingAndSortingRepository<GuestNetwork, Long> {

    /**
     * Find GuestNetwork by uuid.
     * @param uuid GuestNetwork uuid.
     * @return uuid.
     */
    @Query(value = "select net from GuestNetwork net where net.uuid LIKE :uuid ")
    GuestNetwork findByUUID(@Param("uuid") String uuid);
}
