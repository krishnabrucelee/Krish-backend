package ck.panda.domain.repository.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import ck.panda.domain.entity.NetworkOffering;

/**
 * JPA repository for NetworkOffering entity.
 */
public interface NetworkOfferingRepository extends PagingAndSortingRepository<NetworkOffering, Long> {

    /**
     * Get the Network offering based on the name.
     * @param pageable
     * @return
     */
    @Query(value = "select networkOffer from NetworkOffering networkOffer where networkOffer.guestIpType = 'Isolated'")
    Page<NetworkOffering> findAllByIsolated(Pageable pageable);


}
