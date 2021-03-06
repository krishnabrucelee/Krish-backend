package ck.panda.domain.repository.jpa;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.NetworkOffering;
import ck.panda.domain.entity.NetworkOffering.Status;

/**
 * JPA repository for NetworkOffering entity.
 */
public interface NetworkOfferingRepository extends PagingAndSortingRepository<NetworkOffering, Long> {

    /**
     * Find NetworkOffering by the guest Ip Type is Isolated.
     *
     * @param pageable Isolated
     * @return guestIpType
     */
    @Query(value = "SELECT networkOffer FROM NetworkOffering networkOffer WHERE networkOffer.guestIpType = :guestIpType")
    Page<NetworkOffering> findAllByIsolated(Pageable pageable, @Param("guestIpType") String guestIpType );

    /**
     * Find Network by uuid.
     *
     * @param uuid Network uuid.
     * @return uuid
     */
    @Query(value = "SELECT net FROM NetworkOffering net WHERE net.uuid LIKE :uuid ")
    NetworkOffering findByUUID(@Param("uuid") String uuid);

    /**
     * Find Network by id.
     *
     * @param id Network id.
     * @return id
     */
    @Query(value = "SELECT net FROM NetworkOffering net WHERE net.id LIKE :id ")
    NetworkOffering findById(@Param("id") Long id);

    /**
     * Find NetworkOffering by the guestTpType is Isolated without Pagination.
     *
     * @return guestIpType
     */
    @Query(value = "SELECT networkOffer FROM NetworkOffering networkOffer WHERE networkOffer.guestIpType = :guestIpType AND networkOffer.availability = :availability")
    List<NetworkOffering> findByIsolatedAndRequired(@Param("guestIpType") String guestIpType, @Param("availability") String availability);

    /**
     * Find NetworkOffering by VPC.
     *
     * @param forVpc Network VPC status
     * @param status Network Enabled/Disabled status
     * @return Network offering list
     */
    @Query(value = "SELECT networkOffer FROM NetworkOffering networkOffer WHERE networkOffer.forVpc = :forVpc AND networkOffer.status = :status")
    List<NetworkOffering> findVpcList(@Param("forVpc") Boolean forVpc, @Param("status") Status status);
}
