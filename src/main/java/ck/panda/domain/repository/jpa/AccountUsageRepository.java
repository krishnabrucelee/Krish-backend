package ck.panda.domain.repository.jpa;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import ck.panda.domain.entity.AccountUsage;
import ck.panda.domain.entity.Department;

/**
 * JPA repository for GuestNetwork entity.
 */
public interface AccountUsageRepository extends PagingAndSortingRepository<AccountUsage, Long> {


    /**
     * Find the department by Domain Id and IsActive.
     *
     * @param domainId for each domain.
     * @param isActive get the department list based on active/inactive status.
     * @return Department.
     */
    @Query(value = "select au from AccountUsage au where au.usageId =:usageId AND au.offeringId=:offeringId AND au.endDate=:endDate AND au.startDate=:startDate")
    List<AccountUsage> findByUsageIdAndOfferingId(@Param("usageId") String usageId, @Param("offeringId") String offeringId,
            @Param("endDate") String endDate, @Param("startDate") String startDate);
}

