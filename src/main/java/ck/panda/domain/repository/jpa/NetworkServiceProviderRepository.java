package ck.panda.domain.repository.jpa;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.NetworkServiceProvider;

/**
 * JPA repository for network service provider entity.
 */
public interface NetworkServiceProviderRepository extends PagingAndSortingRepository<NetworkServiceProvider, Long> {

    /**
     * Find network service provider by uuid.
     *
     * @param uuid network service provider uuid.
     * @return network service provider
     */
    @Query(value = "SELECT networkServiceProvider FROM NetworkServiceProvider networkServiceProvider WHERE networkServiceProvider.uuid LIKE :uuid")
    NetworkServiceProvider findByUuid(@Param("uuid") String uuid);

    /**
     * Find network service provider by name and physical network id.
     *
     * @param name network service provider name.
     * @param physicalNetworkId physical network id
     * @return network service provider
     */
    @Query(value = "SELECT networkServiceProvider FROM NetworkServiceProvider networkServiceProvider WHERE networkServiceProvider.name =:name AND networkServiceProvider.physicalNetworkId =:physicalNetworkId")
    NetworkServiceProvider findByNameAndPhysicalNetworkId(@Param("name") String name, @Param("physicalNetworkId") Long physicalNetworkId);

    /**
     * Find network service provider by name.
     *
     * @param name network service provider name.
     * @return network service provider list
     */
    @Query(value = "SELECT networkServiceProvider FROM NetworkServiceProvider networkServiceProvider WHERE networkServiceProvider.name =:name")
    List<NetworkServiceProvider> findByName(@Param("name") String name);
}
