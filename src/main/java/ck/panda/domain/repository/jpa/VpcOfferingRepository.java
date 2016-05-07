package ck.panda.domain.repository.jpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.VpcOffering;

/**
 * JPA repository for VPC offering entity.
 */
public interface VpcOfferingRepository extends PagingAndSortingRepository<VpcOffering, Long> {

	/**
     * Find VpcOffering by uuid.
     *
     * @param uuid VpcOffering's uuid.
     * @return uuid
     */
    @Query(value = "SELECT vpc FROM VpcOffering vpc WHERE vpc.uuid LIKE :uuid ")
    VpcOffering findByUUID(@Param("uuid") String uuid);
}
