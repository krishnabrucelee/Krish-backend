package ck.panda.domain.repository.jpa;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.NetworkOffering;

/**
 * JPA repository for NetworkOffering entity.
 */
public interface NetworkOfferingRepository extends PagingAndSortingRepository<NetworkOffering, Long> {

    /**
     * Find NetworkOffering by the guestTpType is Isolated.
     * @param pageable Isolated
     * @return guestIpType
     */
    @Query(value = "select networkOffer from NetworkOffering networkOffer where networkOffer.guestIpType = 'Isolated'")
    Page<NetworkOffering> findAllByIsolated(Pageable pageable);

    /**
     * Find Network by uuid.
     * @param uuid Network uuid.
     * @return uuid
     */
    @Query(value = "select net from NetworkOffering net where net.uuid LIKE :uuid ")
    NetworkOffering findByUUID(@Param("uuid") String uuid);

    /**
     * Find NetworkOffering by the guestTpType is Isolated without Pagination.
     * @return guestIpType
     */
    @Query(value = "select networkOffer from NetworkOffering networkOffer where networkOffer.guestIpType = 'Isolated' AND networkOffer.availability = 'Required'" )
    List<NetworkOffering> findIsolated();

}
