package ck.panda.domain.repository.jpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.Network;

/**
 * JPA repository for Network entity.
 */
public interface NetworkRepository extends PagingAndSortingRepository<Network, Long> {

    /**
     * Find Network by uuid.
     * @param uuid Network uuid.
     * @return uuid
     */
    @Query(value = "select net from Network net where net.uuid LIKE :uuid ")
    Network findByUUID(@Param("uuid") String uuid);
}
