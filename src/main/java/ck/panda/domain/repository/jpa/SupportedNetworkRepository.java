package ck.panda.domain.repository.jpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.SupportedNetwork;

/**
 * JPA repository for supported network entity.
 */
public interface SupportedNetworkRepository extends PagingAndSortingRepository<SupportedNetwork, Long> {
    /**
     * Find supported network by uuid.
     *
     * @param name supported network name.
     * @return supported network
     */
    @Query(value = "SELECT supportedNet FROM SupportedNetwork supportedNet WHERE supportedNet.name =:name")
    SupportedNetwork findByName(@Param("name") String name);
}
