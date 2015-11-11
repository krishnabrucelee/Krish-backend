package ck.panda.domain.repository.jpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.Hypervisor;

/**
 * JPA repository for Hypervisor entity.
 */
public interface HypervisorRepository extends PagingAndSortingRepository<Hypervisor, Long> {

    /**
     * Get the hypervisor based on the name.
     *
     * @param name of the hypervisor
     * @return hypervisor
     */
    @Query(value = "select hyper from Hypervisor hyper where hyper.name = :name")
    Hypervisor findByName(@Param("name") String name);
}
